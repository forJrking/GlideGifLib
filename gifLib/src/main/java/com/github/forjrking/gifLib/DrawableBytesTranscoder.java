package com.github.forjrking.gifLib;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

import pl.droidsonroids.gif.GifDrawable;

public final class DrawableBytesTranscoder implements ResourceTranscoder<Drawable, byte[]> {
    private final BitmapPool bitmapPool;
    private final ResourceTranscoder<Bitmap, byte[]> bitmapBytesTranscoder;
    private final ResourceTranscoder<com.bumptech.glide.load.resource.gif.GifDrawable, byte[]> gifDrawableBytesTranscoder;
    // DES: 此处为自己的GifDrawable 插入后提供转换功能
    private final ResourceTranscoder<GifDrawable, byte[]> gifLibTranscoder;

    public DrawableBytesTranscoder(
            @NonNull BitmapPool bitmapPool,
            @NonNull ResourceTranscoder<Bitmap, byte[]> bitmapBytesTranscoder,
            @NonNull ResourceTranscoder<com.bumptech.glide.load.resource.gif.GifDrawable, byte[]> gifDrawableBytesTranscoder,
            @NonNull ResourceTranscoder<GifDrawable, byte[]> gifLibTranscoder) {
        this.bitmapPool = bitmapPool;
        this.bitmapBytesTranscoder = bitmapBytesTranscoder;
        this.gifDrawableBytesTranscoder = gifDrawableBytesTranscoder;
        this.gifLibTranscoder = gifLibTranscoder;
    }

    @Nullable
    @Override
    public Resource<byte[]> transcode(@NonNull Resource<Drawable> toTranscode,
                                      @NonNull Options options) {
        Drawable drawable = toTranscode.get();
        if (drawable instanceof BitmapDrawable) {
            return bitmapBytesTranscoder.transcode(
                    BitmapResource.obtain(((BitmapDrawable) drawable).getBitmap(), bitmapPool), options);
        } else if (drawable instanceof GifDrawable) {
            return gifLibTranscoder.transcode(toGifLibDrawableResource(toTranscode), options);
        } else if (drawable instanceof com.bumptech.glide.load.resource.gif.GifDrawable) {
            return gifDrawableBytesTranscoder.transcode(toGifDrawableResource(toTranscode), options);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private static Resource<GifDrawable> toGifLibDrawableResource(@NonNull Resource<Drawable> resource) {
        return (Resource<GifDrawable>) (Resource<?>) resource;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private static Resource<com.bumptech.glide.load.resource.gif.GifDrawable> toGifDrawableResource(@NonNull Resource<Drawable> resource) {
        return (Resource<com.bumptech.glide.load.resource.gif.GifDrawable>) (Resource<?>) resource;
    }
}