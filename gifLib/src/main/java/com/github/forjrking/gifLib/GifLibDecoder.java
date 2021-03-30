package com.github.forjrking.gifLib;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

/**
 * @author 岛主
 * 自定义gif 解码器
 * 结合 android-gif-drawable
 * https://github.com/koral--/android-gif-drawable
 * 支持 gif png jpg webp
 */

public class GifLibDecoder implements ResourceDecoder<InputStream, GifDrawable> {

    private static final String TAG = "GifLibDecoder";

    private final List<ImageHeaderParser> parsers;
    private final ResourceDecoder<ByteBuffer, GifDrawable> byteBufferDecoder;
    private final ArrayPool byteArrayPool;

    public GifLibDecoder(List<ImageHeaderParser> parsers, ResourceDecoder<ByteBuffer, GifDrawable> byteBufferDecoder, ArrayPool byteArrayPool) {
        this.parsers = parsers;
        this.byteBufferDecoder = byteBufferDecoder;
        this.byteArrayPool = byteArrayPool;
    }

    @Override
    public boolean handles(@NonNull InputStream source, @NonNull Options options) throws IOException {
//        boolean isAnim = !options.get(GifOptions.DISABLE_ANIMATION);
        boolean isGif = ImageHeaderParserUtils.getType(parsers, source, byteArrayPool) == ImageHeaderParser.ImageType.GIF;
        return isGif;
    }

    @Override
    public Resource<GifDrawable> decode(@NonNull InputStream source, int width, int height,
                                        @NonNull Options options) throws IOException {
        byte[] data = inputStreamToBytes(source);
        if (data == null) {
            return null;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        return byteBufferDecoder.decode(byteBuffer, width, height, options);
    }

    private static byte[] inputStreamToBytes(InputStream is) {
        final int bufferSize = 16384;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bufferSize);
        try {
            int nRead;
            byte[] data = new byte[bufferSize];
            while ((nRead = is.read(data)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
        } catch (IOException e) {
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Error reading data from stream", e);
            }
            return null;
        }
        return buffer.toByteArray();
    }
}
