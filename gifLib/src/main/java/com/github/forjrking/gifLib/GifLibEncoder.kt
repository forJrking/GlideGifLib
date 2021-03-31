package com.github.forjrking.gifLib

import android.util.Log
import com.bumptech.glide.load.EncodeStrategy
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceEncoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.util.ByteBufferUtil
import pl.droidsonroids.gif.GifDrawable
import java.io.File
import java.io.IOException

class GifLibEncoder : ResourceEncoder<GifDrawable?> {

    override fun getEncodeStrategy(options: Options): EncodeStrategy {
        return EncodeStrategy.SOURCE
    }

    override fun encode(data: Resource<GifDrawable?>, file: File, options: Options): Boolean {
        var success = false
        if (data is GifLibDrawableResource) {
            val byteBuffer = data.buffer
            try {
                ByteBufferUtil.toFile(byteBuffer, file)
                success = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
            // DES: 将 resource 编码成文件
            Log.d(TAG, "GifLibEncoder -> $success -> ${file.absolutePath}")
        }
        return success
    }
}