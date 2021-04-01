package com.github.forjrking.gifLib

import android.content.Context
import android.graphics.*
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.util.Preconditions
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.transforms.Transform
import java.security.MessageDigest

/**
 * 让GifLib可以支持转换器的特效
 * .transform(GifDrawable::class.java, GifLibDrawableTransformation(circleCrop))
 */
class GifLibDrawableTransformation(wrapped: Transformation<Bitmap>) : Transformation<GifDrawable> {

    private val wrapped: Transformation<Bitmap> = Preconditions.checkNotNull(wrapped)

    override fun transform(
        context: Context, resource: Resource<GifDrawable?>, outWidth: Int, outHeight: Int
    ): Resource<GifDrawable?> {

        val drawable = resource.get()
        drawable.transform = object : Transform {
            private val mDstRectF = RectF()
            override fun onBoundsChange(rct: Rect) = mDstRectF.set(rct)

            override fun onDraw(canvas: Canvas, paint: Paint, bitmap: Bitmap) {
                val bitmapPool = Glide.get(context).bitmapPool
                val bitmapResource: Resource<Bitmap> = BitmapResource(bitmap, bitmapPool)
                val transformed = wrapped.transform(context, bitmapResource, outWidth, outHeight)
                val transformedFrame = transformed.get()
                canvas.drawBitmap(transformedFrame, null, mDstRectF, paint)
            }
        }
        return resource
    }

    override fun equals(o: Any?): Boolean {
        if (o is GifLibDrawableTransformation) {
            return wrapped == o.wrapped
        }
        return false
    }

    override fun hashCode(): Int {
        return wrapped.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        wrapped.updateDiskCacheKey(messageDigest)
    }

}