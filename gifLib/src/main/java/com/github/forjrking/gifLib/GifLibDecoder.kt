package com.github.forjrking.gifLib

import com.bumptech.glide.load.ImageHeaderParser
import com.bumptech.glide.load.ImageHeaderParserUtils
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool
import com.bumptech.glide.load.resource.gif.GifOptions
import pl.droidsonroids.gif.GifDrawable
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.jvm.Throws

/**
 * @author 岛主
 * 自定义gif 解码器
 * 结合 android-gif-drawable
 * https://github.com/koral--/android-gif-drawable
 * 支持 gif png jpg webp
 */
class GifLibDecoder(private val parsers: List<ImageHeaderParser>,
                    private val byteBufferDecoder: ResourceDecoder<ByteBuffer, GifDrawable>,
                    private val byteArrayPool: ArrayPool) : ResourceDecoder<InputStream, GifDrawable> {

    @Throws(IOException::class)
    override fun handles(source: InputStream, options: Options): Boolean {
        val isAnim = !options.get(GifOptions.DISABLE_ANIMATION)!!
        val isGif = ImageHeaderParserUtils.getType(parsers, source, byteArrayPool) == ImageHeaderParser.ImageType.GIF
        return isAnim && isGif
    }

    @Throws(IOException::class)
    override fun decode(source: InputStream, width: Int, height: Int,
                        options: Options): Resource<GifDrawable>? {
        val data = source.readBytes()
        val byteBuffer = ByteBuffer.wrap(data)
        return byteBufferDecoder.decode(byteBuffer, width, height, options)
    }
}