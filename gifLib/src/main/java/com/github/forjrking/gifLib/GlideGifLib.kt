package com.github.forjrking.gifLib

import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.load.resource.transcode.BitmapBytesTranscoder
import com.bumptech.glide.load.resource.transcode.GifDrawableBytesTranscoder
import pl.droidsonroids.gif.GifDrawable

const val TAG = "GifLib"

object GlideGifLib {

    @JvmStatic
    fun registerGifLib(glide: Glide, registry: Registry) {
        //优先使用gifLib-Gif
        val bufferDecoder = GifLibByteBufferDecoder(registry.imageHeaderParsers)
        val gifLibTranscoder = GifLibBytesTranscoder()
        val bitmapBytesTranscoder = BitmapBytesTranscoder()
        val gifTranscoder = GifDrawableBytesTranscoder()

        registry.prepend(
                Registry.BUCKET_GIF, java.io.InputStream::class.java, GifDrawable::class.java,
                GifLibDecoder(registry.imageHeaderParsers, bufferDecoder, glide.arrayPool)
        ).prepend(
                Registry.BUCKET_GIF,
                java.nio.ByteBuffer::class.java,
                GifDrawable::class.java, bufferDecoder
        ).prepend(
                GifDrawable::class.java, GifLibEncoder()
        ).register(
                Drawable::class.java, ByteArray::class.java,
                DrawableBytesTranscoder(glide.bitmapPool, bitmapBytesTranscoder, gifTranscoder, gifLibTranscoder)
        ).register(
                GifDrawable::class.java, ByteArray::class.java, gifLibTranscoder
        )
    }
}