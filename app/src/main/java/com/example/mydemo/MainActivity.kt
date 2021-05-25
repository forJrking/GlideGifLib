package com.example.mydemo

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.mydemo.MyAdapter.MyHolder
import com.github.forjrking.gifLib.GifLibDrawableTransformation
import kotlinx.android.synthetic.main.activity_main.*
import pl.droidsonroids.gif.GifDrawable


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        recycle.layoutManager = LinearLayoutManager(this)
        val myAdapter = MyAdapter()
        myAdapter.items = arrayListOf(
            "https://upfile.asqql.com/2009pasdfasdfic2009s305985-ts/2019-6/2019621225346994.gif",
            "https://upfile.asqql.com/2009pasdfasdfic2009s305985-ts/2018-1/20181301949831774.gif",
            "https://upfile.asqql.com/2009pasdfasdfic2009s305985-ts/2019-6/20196212312871093.gif",
            "https://upfile.asqql.com/2009pasdfasdfic2009s305985-ts/2019-8/2019821982623812.gif",
            "https://upfile.asqql.com/2009pasdfasdfic2009s305985-ts/2019-7/201972920231262375.gif",
            "https://upfile.asqql.com/2009pasdfasdfic2009s305985-ts/2019-6/20196518155763309.gif",
        )
        recycle.adapter = myAdapter

//        val circleCrop = CircleCrop()
//        IGlideModule.with(this)
//            .load("http://tva2.sinaimg.cn/large/005CjUdnly1g6lwmq0fijg30rs0zu4qp.gif")
//            .placeholder(R.color.colorPrimaryDark)
//            .error(R.color.colorPrimaryDark)
//            .transform(GifDrawable::class.java, GifLibDrawableTransformation(circleCrop))
////            .transform(circleCrop)
//            .into(iv_2)
    }
}

class MyAdapter : RecyclerView.Adapter<MyHolder>() {

    var items = ArrayList<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val also = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(-1, 600)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return MyHolder(also)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val url = items[position]
        IGlideModule.with(holder.itemView).load(url)
            .placeholder(R.color.colorAccent)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?, model: Any?,
                    target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean
                ): Boolean {
                    if (resource is pl.droidsonroids.gif.GifDrawable) {
                        Log.d("TAG", "giflib的 Gifdrawable")
                    } else if (resource is com.bumptech.glide.load.resource.gif.GifDrawable) {
                        Log.d("TAG", "glide的 Gifdrawable")
                    }
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Drawable>?, isFirstResource: Boolean
                ): Boolean = false

            })
            .into(holder.itemView as ImageView)
    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun getItemCount(): Int =
        items.size

}
