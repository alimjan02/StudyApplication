package com.sxt.chat.fragment.bottonsheet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseBottomSheetFragment;
import com.sxt.chat.json.Banner;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.glide.GlideRoundTransformer;

/**
 * Created by sxt on 2018/10/22.
 */
public class GallaryBottomSheetFragment extends BaseBottomSheetFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setData();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void setData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Banner banner = (Banner) bundle.getSerializable(Prefs.KEY_BANNER_INFO);
            if (banner != null) {
                Log.i(TAG, "banner : " + banner.getUrl());
                Glide.with(context).load(banner.getUrl()).transform(new CenterCrop(context), new GlideRoundTransformer(context, 8))
                        .placeholder(R.mipmap.ic_placeholder)
                        .error(R.mipmap.ic_banner_placeholder)
                        .into((ImageView) contentView.findViewById(R.id.img));
            }
        }
    }

    @Override
    protected View getDisplayView() {
        return LayoutInflater.from(context).inflate(R.layout.item_gallary_bottom_sheet, null, false);
    }
}
