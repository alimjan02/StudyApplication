package com.sxt.chat.adapter;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.utils.glide.GlideCircleTransformer;
import com.sxt.chat.utils.glide.GlideRoundTransformer;

public class ImageBindingAdapter {

    @BindingAdapter(value = {"app:imageUrl", "app:placeHolder", "app:error"}, requireAll = false)
    public static void bindImageUrl(ImageView img, String url, int placeHolder, int error) {
        Glide.with(img.getContext())
                .load(url)
                .placeholder(placeHolder == 0 ? R.drawable.ic_placeholder : placeHolder)
                .error(error == 0 ? R.drawable.ic_placeholder : error)
                .into(img);
    }

    @BindingAdapter(value = {"app:imageRoundUrl", "app:placeHolder", "app:error", "app:conner"}, requireAll = false)
    public static void bindImageRoundUrl(ImageView img, String imageUrl, int placeHolder, int error, int conner) {
        Glide.with(img.getContext())
                .load(imageUrl)
                .placeholder(placeHolder == 0 ? R.mipmap.ic_no_img : placeHolder)
                .error(error == 0 ? R.mipmap.ic_no_img : error)
                .bitmapTransform(new GlideRoundTransformer(img.getContext(), conner))
                .into(img);
    }

    @BindingAdapter("app:imageCircleUrl")
    public static void bindImageCircleUrl(ImageView img, String url) {
        Glide.with(img.getContext())
                .load(url)
                .error(R.mipmap.ic_no_img)
                .bitmapTransform(new GlideCircleTransformer(img.getContext()))
                .into(img);
    }
}
