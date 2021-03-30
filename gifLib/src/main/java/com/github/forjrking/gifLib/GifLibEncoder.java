package com.github.forjrking.gifLib;

import androidx.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.ByteBufferUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import pl.droidsonroids.gif.GifDrawable;

public class GifLibEncoder implements ResourceEncoder<GifDrawable> {

    private static final String TAG = "GifLibEncoder";

    @NonNull
    @Override
    public EncodeStrategy getEncodeStrategy(@NonNull Options options) {
        return EncodeStrategy.SOURCE;
    }

    @Override
    public boolean encode(@NonNull Resource<GifDrawable> data, @NonNull File file,
                          @NonNull Options options) {

        boolean success = false;
        if (data instanceof GifLibDrawableResource) {
            ByteBuffer byteBuffer = ((GifLibDrawableResource) data).getBuffer();
            if (byteBuffer != null) {
                try {
                    ByteBufferUtil.toFile(byteBuffer, file);
                    success = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // DES: 将 resource 编码成文件
            Log.d(TAG, String.format("GifLibEncoder -> %s -> %s", success, file.getAbsolutePath()));
        }
        return success;
    }
}