#利用gifLib提升Glide的Gif性能优化卡顿

```
implementation 'com.github.forJrking:GlideGifLib:0.0.1'

例如:
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
```

### 不支持Glide的图片转换处理

`GifLibDrawableTransformation`主要作用就是支持glide各种转换器,但是glide自身gif还有bitmap可以支持转换器,代码为
```
  @NonNull
  T transform(@NonNull Transformation<Bitmap> transformation, boolean isRequired) {
    if (isAutoCloneEnabled) {
      return clone().transform(transformation, isRequired);
    }

    DrawableTransformation drawableTransformation =
        new DrawableTransformation(transformation, isRequired);
    transform(Bitmap.class, transformation, isRequired);
    transform(Drawable.class, drawableTransformation, isRequired);
    // TODO: remove BitmapDrawable decoder and this transformation.
    // Registering as BitmapDrawable is simply an optimization to avoid some iteration and
    // isAssignableFrom checks when obtaining the transformation later on. It can be removed without
    // affecting the functionality.
    transform(BitmapDrawable.class, drawableTransformation.asBitmapDrawable(), isRequired);
    transform(GifDrawable.class, new GifDrawableTransformation(transformation), isRequired);
    return selfOrThrowIfLocked();
  }
```
要插入自己的代码在这些里面,AOP或者使用自己编译,还有就是通过代码每次调用时候插入
```
val circleCrop = CircleCrop()
IGlideModule.with(this)
    .load("http://tva2.sinaimg.cn/large/005CjUdnly1g6lwmq0fijg30rs0zu4qp.gif")
    .placeholder(R.color.colorPrimaryDark)
    .error(R.color.colorPrimaryDark)
    .transform(GifDrawable::class.java, GifLibDrawableTransformation(circleCrop))
    .transform(circleCrop)
    .into(iv_2)
```
#### 目前存在问题 GifLibDrawableTransformation 中绘制Bitmap位置及缩放等还有问题