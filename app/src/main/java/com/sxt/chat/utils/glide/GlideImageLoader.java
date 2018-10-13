package com.sxt.chat.utils.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.banner.loader.ImageLoader;

/**
 * Created by izhaohu on 2018/5/22.
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        /**
         注意：
         1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
         2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
         传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
         切记不要胡乱强转！
         */
//        eg：
        //Glide 加载图片简单用法
        Glide.with(context).load(path)
                .placeholder(R.mipmap.ic_banner_placeholder)
                .error(R.mipmap.ic_banner_placeholder)
                /*.bitmapTransform(new GlideRoundTransformer(context, 8))*/
                .into(imageView);

        //Picasso 加载图片简单用法
//        Picasso.with(context).load(path).into(imageView);

        //用fresco加载图片简单用法，记得要写下面的createImageView方法
//        Uri uri = Uri.parse((String) path);
//        imageView.setImageURI(uri);
    }

    //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
    @Override
    public ImageView createImageView(Context context) {
        return super.createImageView(context);//可以在这里替换成自定义的ImageView
    }
}
