package com.github.forjrking.gifLib;

import androidx.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.load.resource.drawable.DrawableResource;

import java.nio.ByteBuffer;

import pl.droidsonroids.gif.GifDrawable;

public class GifLibDrawableResource extends DrawableResource<GifDrawable> {

    private static final String TAG = "GifLibResource";

    public ByteBuffer getBuffer() {
        return mBuffer;
    }

    // DES: GifDrawable 没有提供数据 编码成 ByteBuffer 或者其他格式的方法，这里利用 Resource包装
    final ByteBuffer mBuffer;

    // Public API.
    @SuppressWarnings("WeakerAccess")
    public GifLibDrawableResource(GifDrawable drawable, ByteBuffer byteBuffer) {
        super(drawable);
        this.mBuffer = byteBuffer;
    }

    @NonNull
    @Override
    public Class<GifDrawable> getResourceClass() {
        return GifDrawable.class;
    }

    @Override
    public int getSize() {
        // DES: getAllocationByteCount 返回占用内存  使用 getFrameByteCount*
        int byteCount = (int) drawable.getAllocationByteCount();
        Log.d(TAG, "getSize ->" + byteCount);
        return byteCount;
    }

    @Override
    public void recycle() {
        Log.d(TAG, "recycle()");
        drawable.stop();
        drawable.recycle();
    }

    @Override
    public void initialize() {
        drawable.seekToFrameAndGet(0).prepareToDraw();
    }
}