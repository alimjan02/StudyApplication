package com.sxt.chat.fragment.bottonsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.sxt.chat.R;
import com.sxt.chat.adapter.config.BottomSheetAdapter;
import com.sxt.chat.adapter.config.DividerItemDecoration;
import com.sxt.chat.base.BaseBottomSheetFragment;
import com.sxt.chat.json.Banner;
import com.sxt.chat.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sxt on 2018/10/22.
 */
public class GallaryBottomSheetFragment extends BaseBottomSheetFragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private String URL_CSDN = "https://me.csdn.net/sxt_zls";

    @Override
    protected View getDisplayView() {
        return LayoutInflater.from(context).inflate(R.layout.item_gallary_bottom_sheet, null, false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setData();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            final Banner banner = (Banner) bundle.getSerializable(Prefs.KEY_BANNER_INFO);
            final List<Banner> bannerList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                bannerList.add(banner);
            }

            progressBar = contentView.findViewById(R.id.progressBar);
            recyclerView = contentView.findViewById(R.id.recyclerView);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.addItemDecoration(new DividerItemDecoration(context, ContextCompat.getDrawable(context, R.drawable.divider_colors)));
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            handler.postDelayed(() -> {
                contentView.findViewById(R.id.line).setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                recyclerView.addItemDecoration(new DividerItemDecoration(context, ContextCompat.getDrawable(context, R.drawable.divider)));
                recyclerView.setAdapter(new BottomSheetAdapter(context, bannerList));
            }, 2000);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
