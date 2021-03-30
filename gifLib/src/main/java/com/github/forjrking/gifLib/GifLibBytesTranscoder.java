package com.github.forjrking.gifLib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bytes.BytesResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.util.ByteBufferUtil;

import java.nio.ByteBuffer;

import pl.droidsonroids.gif.GifDrawable;


public class GifLibBytesTranscoder implements ResourceTranscoder<GifDrawable, byte[]> {

    @Nullable
    @Override
    public Resource<byte[]> transcode(@NonNull Resource<GifDrawable> toTranscode, @NonNull Options options) {
        if (toTranscode instanceof GifLibDrawableResource) {
            ByteBuffer byteBuffer = ((GifLibDrawableResource) toTranscode).getBuffer();
            return new BytesResource(ByteBufferUtil.toBytes(byteBuffer));
        }
        return null;
    }
}