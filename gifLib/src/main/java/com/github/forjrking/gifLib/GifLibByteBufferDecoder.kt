package com.github.forjrking.gifLib

import android.util.Log
import com.bumptech.glide.gifdecoder.GifDecoder
import com.bumptech.glide.gifdecoder.GifHeader
import com.bumptech.glide.gifdecoder.GifHeaderParser
import com.bumptech.glide.load.ImageHeaderParser
import com.bumptech.glide.load.ImageHeaderParser.ImageType
import com.bumptech.glide.load.ImageHeaderParserUtils
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.resource.gif.GifOptions
import com.bumptech.glide.util.LogTime
import com.bumptech.glide.util.Util
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifDrawableBuilder
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.jvm.Throws

/**
 * An [ResourceDecoder] that decodes [ ] from [java.io] data.
 */
class GifLibByteBufferDecoder constructor(
        private val parsers: List<ImageHeaderParser>,
        private val parserPool: GifHeaderParserPool = PARSER_POOL) : ResourceDecoder<ByteBuffer, GifDrawable> {

    @Throws(IOException::class)
    override fun handles(source: ByteBuffer, options: Options): Boolean {
        val isAnim = !options.get(GifOptions.DISABLE_ANIMATION)!!
        val isGif = ImageHeaderParserUtils.getType(parsers, source) == ImageType.GIF
        // DES: 此日志主要关注 gif图并且 设置了不允许动画的地方
        if (isGif) Log.e(TAG, "gif options anim ->$isAnim")
        return isAnim && isGif
    }

    override fun decode(source: ByteBuffer, width: Int, height: Int,
                        options: Options): GifLibDrawableResource? {
        val parser = parserPool.obtain(source)
        return try {
            decode(source, width, height, parser, options)
        } finally {
            parserPool.release(parser)
        }
    }

    private fun decode(byteBuffer: ByteBuffer, width: Int, height: Int, parser: GifHeaderParser, options: Options): GifLibDrawableResource? {
        val startTime = LogTime.getLogTime()
        return try {
            val header = parser.parseHeader()
            if (header.numFrames <= 0 || header.status != GifDecoder.STATUS_OK) {
                // If we couldn't decode the GIF, we will end up with a frame count of 0.
                return null
            }
            val sampleSize = getSampleSize(header, width, height)
            val builder = GifDrawableBuilder()
            builder.from(byteBuffer)
            builder.sampleSize(sampleSize)
            builder.isRenderingTriggeredOnDraw = true
//            pl.droidsonroids.gif.GifOptions gifOptions = new pl.droidsonroids.gif.GifOptions();
//            DES: 不含透明层可以加速渲染 但是透明的gif会渲染黑色背景
//            gifOptions.setInIsOpaque();
            val gifDrawable = builder.build()
            val loopCount = gifDrawable.loopCount
            if (loopCount <= 1) {
                //循环一次的则矫正为无限循环
                Log.v(TAG, "Decoded GIF LOOP COUNT WARN $loopCount")
                gifDrawable.loopCount = 0
            }
            GifLibDrawableResource(gifDrawable, byteBuffer)
        } catch (e: IOException) {
            Log.v(TAG, "Decoded GIF Error" + e.message)
            null
        } finally {
            Log.v(TAG, "Decoded GIF from stream in " + LogTime.getElapsedMillis(startTime))
        }
    }

    class GifHeaderParserPool {
        private val pool = Util.createQueue<GifHeaderParser>(0)

        @Synchronized
        fun obtain(buffer: ByteBuffer?): GifHeaderParser {
            var result = pool.poll()
            if (result == null) {
                result = GifHeaderParser()
            }
            return result.setData(buffer!!)
        }

        @Synchronized
        fun release(parser: GifHeaderParser) {
            parser.clear()
            pool.offer(parser)
        }
    }

    companion object {
        private val PARSER_POOL = GifHeaderParserPool()

        private fun getSampleSize(gifHeader: GifHeader, targetWidth: Int, targetHeight: Int): Int {
            val exactSampleSize = (gifHeader.height / targetHeight).coerceAtMost(gifHeader.width / targetWidth)
            val powerOfTwoSampleSize = if (exactSampleSize == 0) 0 else Integer.highestOneBit(exactSampleSize)
            // Although functionally equivalent to 0 for BitmapFactory, 1 is a safer default for our code
            // than 0.
            val sampleSize = 1.coerceAtLeast(powerOfTwoSampleSize)
            if (sampleSize > 1) {
                Log.v(TAG, "Downsampling GIF"
                        + ", sampleSize: " + sampleSize
                        + ", 目标 dimens: [" + targetWidth + "x" + targetHeight + "]"
                        + ", 实际 dimens: [" + gifHeader.width + "x" + gifHeader.height + "]")
            }
            return sampleSize
        }
    }

}