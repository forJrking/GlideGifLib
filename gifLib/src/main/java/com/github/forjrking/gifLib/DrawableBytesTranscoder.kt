package com.github.forjrking.gifLib

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder

/**Drawable to Bytes*/
@Suppress("UNCHECKED_CAST")
class DrawableBytesTranscoder(
        private val bitmapPool: BitmapPool,
        private val bitmapBytesTranscoder: ResourceTranscoder<Bitmap, ByteArray>,
        private val gifDrawableBytesTranscoder: ResourceTranscoder<GifDrawable, ByteArray>,
        // DES: 此处为自己的GifDrawable 插入后提供转换功能
        private val gifLibTranscoder: ResourceTranscoder<pl.droidsonroids.gif.GifDrawable, ByteArray>) : ResourceTranscoder<Drawable, ByteArray> {

    override fun transcode(toTranscode: Resource<Drawable?>,
                           options: Options): Resource<ByteArray>? {
        return when (val drawable = toTranscode.get()) {
            is BitmapDrawable -> {
                bitmapBytesTranscoder.transcode(BitmapResource.obtain(drawable.bitmap, bitmapPool)!!, options)
            }
            is pl.droidsonroids.gif.GifDrawable -> {
                gifLibTranscoder.transcode(toGifLibDrawableResource(toTranscode), options)
            }
            is GifDrawable -> {
                gifDrawableBytesTranscoder.transcode(toGifDrawableResource(toTranscode), options)
            }
            else -> null
        }
    }

    companion object {
        private fun toGifLibDrawableResource(resource: Resource<Drawable?>): Resource<pl.droidsonroids.gif.GifDrawable> {
            return resource as Resource<pl.droidsonroids.gif.GifDrawable>
        }

        /** */
        private fun toGifDrawableResource(resource: Resource<Drawable?>): Resource<GifDrawable> {
            return resource as Resource<GifDrawable>
        }
    }

}