package com.sxt.banner.loader;

import android.content.Context;
import android.widget.ImageView;


public abstract class ImageLoader implements com.sxt.banner.loader.ImageLoaderInterface<ImageView> {

    @Override
    public ImageView createImageView(Context context) {
        ImageView imageView = new ImageView(context);
        return imageView;
    }

}
