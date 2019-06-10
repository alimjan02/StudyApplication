package com.sxt.chat.fragment.bottonsheet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.sxt.chat.R;
import com.sxt.chat.activity.BannerDetailActivity;
import com.sxt.chat.adapter.BottomSheetGridAdapter;
import com.sxt.chat.base.BaseBottomSheetFragment;
import com.sxt.chat.json.Banner;
import com.sxt.chat.json.RoomInfo;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.glide.GlideRoundTransformer;

import java.util.Arrays;

/**
 * Created by sxt on 2018/10/22.
 */
public class GalleryBottomSheetFragment extends BaseBottomSheetFragment {

    @SuppressLint("InflateParams")
    @Override
    protected View getDisplayView() {
        return LayoutInflater.from(context).inflate(R.layout.item_gallary_bottom_sheet, null, false);
    }

    @Override
    protected void initView() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Banner banner = (Banner) bundle.getSerializable(Prefs.KEY_BANNER_INFO);
            ImageView imageView = contentView.findViewById(R.id.image);

            RecyclerView gridRecyclerView = contentView.findViewById(R.id.gridViewRecyclerView);
            gridRecyclerView.setNestedScrollingEnabled(false);
            gridRecyclerView.setLayoutManager(new GridLayoutManager(context, 4));

            Glide.with(context)
                    .load(banner.getUrl())
                    .placeholder(R.mipmap.ic_banner_placeholder)
                    .error(R.mipmap.ic_banner_placeholder)
                    .transform(new CenterCrop(context), new GlideRoundTransformer(context, 8))
                    .into(imageView);

            BottomSheetGridAdapter adapter = new BottomSheetGridAdapter(context, Arrays.asList(getResources().getStringArray(R.array.menus)));
            gridRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener((position, object) -> {
                Intent intent = new Intent(context, BannerDetailActivity.class);
                Bundle b = new Bundle();
                RoomInfo roomInfo = new RoomInfo();
                roomInfo.setRoom_url(banner.getUrl());
                roomInfo.setHome_name(banner.getDescription());
                b.putSerializable(Prefs.ROOM_INFO, roomInfo);
                intent.putExtra(Prefs.ROOM_INFO, b);
                context.startActivity(intent);
            });
        }
    }

}
