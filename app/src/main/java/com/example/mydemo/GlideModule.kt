package com.example.mydemo

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.github.forjrking.gifLib.GlideGifLib

@GlideModule(glideName = "IGlideModule")
class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        //使用C++提升解码性能
        GlideGifLib.registerGifLib(glide, registry)
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}