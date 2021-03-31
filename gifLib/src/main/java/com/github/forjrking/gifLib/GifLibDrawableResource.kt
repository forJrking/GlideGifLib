package com.github.forjrking.gifLib

import android.util.Log
import com.bumptech.glide.load.resource.drawable.DrawableResource
import pl.droidsonroids.gif.GifDrawable
import java.nio.ByteBuffer

// DES: GifDrawable 没有提供数据 编码成 ByteBuffer 或者其他格式的方法，这里利用 Resource包装
class GifLibDrawableResource(drawable: GifDrawable?, val buffer: ByteBuffer) : DrawableResource<GifDrawable>(drawable) {

    override fun getResourceClass(): Class<GifDrawable> {
        return GifDrawable::class.java
    }

    override fun getSize(): Int {
        // DES: getAllocationByteCount 返回占用内存  使用 getFrameByteCount*
        val byteCount = drawable.allocationByteCount.toInt()
        Log.d(TAG, "getSize ->$byteCount")
        return byteCount
    }

    override fun recycle() {
        Log.d(TAG, "recycle()")
        drawable.stop()
        drawable.recycle()
    }

    override fun initialize() {
        drawable.seekToFrameAndGet(0).prepareToDraw()
    }

}