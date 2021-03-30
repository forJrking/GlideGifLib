package com.github.forjrking.gifLib;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.GifHeader;
import com.bumptech.glide.gifdecoder.GifHeaderParser;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParser.ImageType;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.resource.gif.GifOptions;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifDrawableBuilder;

/**
 * An {@link ResourceDecoder} that decodes {@link
 * GifDrawable} from {@link java.io} data.
 */
public class GifLibByteBufferDecoder implements ResourceDecoder<ByteBuffer, GifDrawable> {
    private static final String TAG = "GifLibBufferDecoder";

    private static final GifHeaderParserPool PARSER_POOL = new GifHeaderParserPool();
    private final List<ImageHeaderParser> parsers;
    private final GifHeaderParserPool parserPool;

    public GifLibByteBufferDecoder(
            List<ImageHeaderParser> parsers) {
        this(parsers, PARSER_POOL);
    }

    @VisibleForTesting
    GifLibByteBufferDecoder(
            List<ImageHeaderParser> parsers,
            GifHeaderParserPool parserPool) {
        this.parsers = parsers;
        this.parserPool = parserPool;
    }

    @Override
    @SuppressWarnings("ALL")
    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) throws IOException {
        boolean isAnim = !options.get(GifOptions.DISABLE_ANIMATION);
        boolean isGif = ImageHeaderParserUtils.getType(parsers, source) == ImageType.GIF;
        // DES: 此日志主要关注 gif图并且 设置了不允许动画的地方
        if(isGif)Log.e(TAG, "gif options anim ->" + isAnim );
        return isAnim&&isGif;
    }

    @Override
    public GifLibDrawableResource decode(@NonNull ByteBuffer source, int width, int height,
                                                                          @NonNull Options options) {
        final GifHeaderParser parser = parserPool.obtain(source);
        try {
            return decode(source, width, height, parser, options);
        } finally {
            parserPool.release(parser);
        }
    }

    @Nullable
    private GifLibDrawableResource decode(ByteBuffer byteBuffer, int width, int height, GifHeaderParser parser, Options options) {
        long startTime = LogTime.getLogTime();
        try {
            final GifHeader header = parser.parseHeader();
            if (header.getNumFrames() <= 0 || header.getStatus() != GifDecoder.STATUS_OK) {
                // If we couldn't decode the GIF, we will end up with a frame count of 0.
                return null;
            }
            int sampleSize = getSampleSize(header, width, height);

            GifDrawableBuilder builder = new GifDrawableBuilder();
            builder.from(byteBuffer);
            builder.sampleSize(sampleSize);
            builder.setRenderingTriggeredOnDraw(true);
//            pl.droidsonroids.gif.GifOptions gifOptions = new pl.droidsonroids.gif.GifOptions();
//            DES: 不含透明层可以加速渲染 但是透明的gif会渲染黑色背景
//            gifOptions.setInIsOpaque();
            GifDrawable gifDrawable = builder.build();
            int loopCount = gifDrawable.getLoopCount();
            if (loopCount <= 1) {
                //循环一次的则矫正为无限循环
                Log.v(TAG, "Decoded GIF LOOP COUNT WARN " + loopCount);
                gifDrawable.setLoopCount(0);
            }
            return new GifLibDrawableResource(gifDrawable, byteBuffer);
        } catch (IOException e) {
            Log.v(TAG, "Decoded GIF Error" + e.getMessage());
            return null;
        } finally {
            Log.v(TAG, "Decoded GIF from stream in " + LogTime.getElapsedMillis(startTime));
        }
    }

    private static int getSampleSize(GifHeader gifHeader, int targetWidth, int targetHeight) {
        int exactSampleSize = Math.min(gifHeader.getHeight() / targetHeight,
                gifHeader.getWidth() / targetWidth);
        int powerOfTwoSampleSize = exactSampleSize == 0 ? 0 : Integer.highestOneBit(exactSampleSize);
        // Although functionally equivalent to 0 for BitmapFactory, 1 is a safer default for our code
        // than 0.
        int sampleSize = Math.max(1, powerOfTwoSampleSize);
        if (sampleSize > 1) {
            Log.v(TAG, "Downsampling GIF"
                    + ", sampleSize: " + sampleSize
                    + ", 目标 dimens: [" + targetWidth + "x" + targetHeight + "]"
                    + ", 实际 dimens: [" + gifHeader.getWidth() + "x" + gifHeader.getHeight() + "]");
        }
        return sampleSize;
    }


    @VisibleForTesting
    static class GifHeaderParserPool {
        private final Queue<GifHeaderParser> pool = Util.createQueue(0);

        synchronized GifHeaderParser obtain(ByteBuffer buffer) {
            GifHeaderParser result = pool.poll();
            if (result == null) {
                result = new GifHeaderParser();
            }
            return result.setData(buffer);
        }

        synchronized void release(GifHeaderParser parser) {
            parser.clear();
            pool.offer(parser);
        }
    }

    public static boolean isGIF(byte[] bytes) {
        return bytes.length > 3 && bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F';
    }
}
