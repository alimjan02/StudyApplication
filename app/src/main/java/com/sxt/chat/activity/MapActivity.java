package com.sxt.chat.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseActivity;

/**
 * Created by sxt on 2018/10/26.
 */
public class MapActivity extends BaseActivity implements AMapLocationListener, LocationSource {

    private AMap map;
    private MapView mapView;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.getUiSettings().setScaleControlsEnabled(true);// 标尺开关
        map.getUiSettings().setZoomControlsEnabled(true);//缩放按钮
        map.setMyLocationEnabled(true);
        map.setLocationSource(this);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
        if (mLocationClient != null) mLocationClient.startLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
        if (mLocationClient != null) mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
    }

    private void releaseClient() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
            mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
            mLocationClient = null;
        }
    }

    private void initLocationOption() {
        if (mLocationClient == null) {
            mLocationOption = new AMapLocationClientOption()
                    .setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)//高精度模式。
                    .setNeedAddress(true)//设置是否返回地址信息（默认返回地址信息）
                    .setInterval(2000)//设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
                    .setHttpTimeOut(20000)//单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
                    .setLocationCacheEnable(true)//缓存机制默认开启，可以通过以下接口进行关闭。
                    // 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
                    .setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);

            mLocationClient = new AMapLocationClient(getApplicationContext());//初始化定位
            mLocationClient.setLocationListener(this);//设置定位回调监听
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.stopLocation();//设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.startLocation();
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        initLocationOption();
    }

    @Override
    public void deactivate() {
        releaseClient();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
        releaseClient();
    }
}
