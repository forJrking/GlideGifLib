package com.github.forjrking.gifLib

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bytes.BytesResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.bumptech.glide.util.ByteBufferUtil
import pl.droidsonroids.gif.GifDrawable

class GifLibBytesTranscoder : ResourceTranscoder<GifDrawable, ByteArray> {

    override fun transcode(toTranscode: Resource<GifDrawable?>, options: Options): Resource<ByteArray>? {
        if (toTranscode is GifLibDrawableResource) {
            val byteBuffer = toTranscode.buffer
            return BytesResource(ByteBufferUtil.toBytes(byteBuffer))
        }
        return null
    }
}