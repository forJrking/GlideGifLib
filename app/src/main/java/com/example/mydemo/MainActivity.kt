package com.example.mydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.github.forjrking.gifLib.GifLibDrawableTransformation
import kotlinx.android.synthetic.main.activity_main.*
import pl.droidsonroids.gif.GifDrawable


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val url = "http://tva1.sinaimg.cn/large/005CjUdnly1g6lwmrjhaog30rs0zu4qq.gif"
        IGlideModule.with(this).load(url)
            .placeholder(R.color.colorPrimaryDark)
            .error(R.color.colorPrimaryDark)
//            .centerCrop()
            .into(iv_1)
        val circleCrop = CircleCrop()
        IGlideModule.with(this)
            .load("http://tva2.sinaimg.cn/large/005CjUdnly1g6lwmq0fijg30rs0zu4qp.gif")
            .placeholder(R.color.colorPrimaryDark)
            .error(R.color.colorPrimaryDark)
            .transform(GifDrawable::class.java, GifLibDrawableTransformation(circleCrop))
            .transform(circleCrop)
            .into(iv_2)
    }
}
