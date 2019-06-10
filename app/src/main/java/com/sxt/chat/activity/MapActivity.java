package com.sxt.chat.activity;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.AnimationSet;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.sxt.chat.R;
import com.sxt.chat.adapter.LocationAdapter;
import com.sxt.chat.adapter.config.DividerItemDecoration;
import com.sxt.chat.base.BaseActivity;
import com.sxt.chat.json.LocationInfo;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.utils.AnimationUtil;
import com.sxt.chat.utils.ArithTool;
import com.sxt.chat.utils.SimpleTextWatcher;
import com.sxt.chat.utils.Utils;
import com.sxt.chat.ws.BmobRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sxt on 2018/10/26.
 */
public class MapActivity extends BaseActivity implements AMapLocationListener, LocationSource, AMap.OnMarkerClickListener, PoiSearch.OnPoiSearchListener {

    private AMap aMap;
    private MapView mapView;
    private AMapLocation aMapLocation;
    private RecyclerView recyclerView;
    private ViewSwitcher viewSwitcher;
    private NestedScrollView nestedScrollView;
    private CoordinatorLayout coordinatorLayout;
    private LocationAdapter locationAdapter;
    public AMapLocationClient mLocationClient;
    private BottomSheetBehavior bottomSheetBehavior;
    public AMapLocationClientOption mLocationOption;
    private OnLocationChangedListener mLocationListener;

    private PoiSearch poiSearch;
    private EditText searchView;
    private ValueAnimator valueAnimator;
    private float mAnimatorValue;
    private View close;
    private int heightPixels, marginTop;
    private Marker markerPre;
    private int maxRadius = 100, markerIndexPre = -1;
    private int ZOOM_MAP = 15, peekHeight, peekHeightMin = 16;
    private List<Circle> listCircle = new ArrayList<>();
    private Map<Integer, Marker> markers = new HashMap<>();
    private LatLng centerLatLng = new LatLng(31.236255, 121.470231);
    private GpsReceiver gpsReceiver;
    private final String ACTION_GPS_STATE = "android.location.PROVIDERS_CHANGED";
    private String CMD_GET_LOCATION_INTOS = this.getClass().getName() + "CMD_GET_LOCATION_INTOS";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_layout);
        initTitle();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        aMap.getUiSettings().setScaleControlsEnabled(true);// 标尺开关
        aMap.getUiSettings().setZoomControlsEnabled(false);//缩放按钮
        aMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_MAP));
        aMap.setMyLocationEnabled(true);
        aMap.setLocationSource(this);
        aMap.setOnMarkerClickListener(this);
        initView();
        initLocationOption();
        registerReceiver();
    }

    private void registerReceiver() {
        gpsReceiver = new GpsReceiver();
        registerReceiver(gpsReceiver, new IntentFilter(ACTION_GPS_STATE));
    }

    private void initTitle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
            getWindow().setNavigationBarColor(Color.WHITE);
        }
        heightPixels = getResources().getDisplayMetrics().heightPixels;
        final CardView cardView = findViewById(R.id.cardView);
        cardView.post(() -> {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) cardView.getLayoutParams();
            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
            marginTop = cardView.getHeight() + lp.topMargin + lp.bottomMargin / 2 + statusBarHeight;
        });
    }

    private void initView() {
        close = findViewById(R.id.close);
        searchView = findViewById(R.id.search_view);
        viewSwitcher = findViewById(R.id.viewSwitcher);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        final View fabContainer = findViewById(R.id.fab_container);
        final FloatingActionButton fab = findViewById(R.id.fab_my_location);
        final FloatingActionButton fabScrolling = findViewById(R.id.fab_scrolling);
        fabScrolling.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        fab.setOnClickListener(view -> {
            if (mLocationClient != null) {
                mLocationClient.startLocation();
            }
        });
        fabScrolling.setOnClickListener(v -> {
            LatLng latLng;
            if (aMapLocation != null) {
                latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            } else {
                latLng = centerLatLng;
            }
            openGaoDeMap(latLng, getString(R.string.app_name));
        });
        bottomSheetBehavior = BottomSheetBehavior.from(nestedScrollView);
        peekHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        bottomSheetBehavior.setPeekHeight(peekHeight);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState != BottomSheetBehavior.STATE_DRAGGING) {
                    ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                    if (bottomSheet.getHeight() > heightPixels - marginTop) {
                        layoutParams.height = heightPixels - marginTop;
                        bottomSheet.setLayoutParams(layoutParams);
                    }
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        if (nestedScrollView != null && nestedScrollView.getScrollX() != 0) {
                            nestedScrollView.setScrollX(0);//列表滑动到顶端
                        }
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float distance;
                if (slideOffset > 0) {//在peekHeight位置以上 滑动(向上、向下) slideOffset bottomSheet.getHeight() 是展开后的高度的比例
                    distance = bottomSheet.getHeight() * slideOffset;
                } else {//在peekHeight位置以下 滑动(向上、向下)  slideOffset 是PeekHeight的高度的比例
                    distance = bottomSheetBehavior.getPeekHeight() * slideOffset;
                }
                if (distance < 0) {
                    fabContainer.setTranslationY(-distance);
                    mapView.setTranslationY(0);
                } else {
                    if (distance <= peekHeight) {
                        fabContainer.setTranslationY(-distance);
                        mapView.setTranslationY(-distance);
                    }
                }
                Log.e(TAG, String.format("slideOffset -->>> %s bottomSheet.getHeight() -->>> %s heightPixels -->>> %s", slideOffset, bottomSheet.getHeight(), heightPixels));
            }
        });
        close.setOnClickListener(v -> searchView.setText(""));
        searchView.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                AnimationUtil.rotation(close, !TextUtils.isEmpty(s.toString().trim()));
            }
        });
        searchView.setOnFocusChangeListener((v, hasFocus) -> searchView.setCursorVisible(hasFocus));
        searchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideSoft(searchView);
                String keyWord = searchView.getText().toString().trim();
                if (TextUtils.isEmpty(keyWord)) {
                    Toast(coordinatorLayout, R.string.key_word_is_empty);
                    return false;
                }
                PoiSearch.Query query = new PoiSearch.Query(keyWord, "", "");
                if (poiSearch == null) {
                    poiSearch = new PoiSearch(MapActivity.this, query);
                    poiSearch.setOnPoiSearchListener(MapActivity.this);
                } else {
                    poiSearch.setQuery(query);
                }
                poiSearch.searchPOIAsyn();
                searchView.setCursorVisible(false);
            }
            return false;
        });
    }

    //======================Start============POI关键词搜索回调=======================================
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        searchView.setText("");
        LatLng latLng;
        LatLng mPoint;
        LocationInfo locationInfo;
        List<LocationInfo> list = null;
        if (poiResult != null && poiResult.getPois() != null) {
            if (aMapLocation != null) {
                mPoint = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            } else {
                mPoint = centerLatLng;
            }
            for (int j = 0; j < poiResult.getPois().size(); j++) {
                PoiItem poiItem = poiResult.getPois().get(j);
                LatLonPoint latLonPoint = poiItem.getLatLonPoint();
                latLng = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
                locationInfo = new LocationInfo();
                locationInfo.setLatitude(latLonPoint.getLatitude());
                locationInfo.setLongitude(latLonPoint.getLongitude());
                locationInfo.setAddressName(poiItem.getTitle());
                locationInfo.setAddress(poiItem.getAdName());
                double distance = ArithTool.div(AMapUtils.calculateLineDistance(latLng, mPoint) / 1000.0, 1, 2);
                locationInfo.setDistance(distance);
                if (list == null) {
                    list = new ArrayList<>();
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                }
                list.add(locationInfo);
            }
        }
        refresh(list);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
    //======================End============POI关键词搜索回调=========================================

    private void initLocationOption() {
        AMapLocationClientOption.AMapLocationMode mode;
        if (Utils.isGpsOpen(this)) {
            mode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;//高精度模式。
            Log.e(TAG, "当前定位模式为 --->>> 高精度模式");
        } else if (Utils.isGpsNetWorkOpen(this)) {
            mode = AMapLocationClientOption.AMapLocationMode.Battery_Saving;//低功耗模式。
            Log.e(TAG, "当前定位模式为 --->>> 低功耗模式");
        } else {
            mode = AMapLocationClientOption.AMapLocationMode.Device_Sensors;//仅限设备模式。
            Log.e(TAG, "当前定位模式为 --->>> 仅限设备模式");
        }
        if (mLocationOption == null) {
            mLocationOption = new AMapLocationClientOption()
                    .setLocationMode(mode)
                    .setNeedAddress(true)//设置是否返回地址信息（默认返回地址信息）
                    .setInterval(2000)//设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
                    .setHttpTimeOut(8000)//单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
                    .setLocationCacheEnable(true);//缓存机制默认开启，可以通过以下接口进行关闭。
            // 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
            //.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        } else {
            mLocationOption.setLocationMode(mode);
        }
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(getApplicationContext());//初始化定位
            mLocationClient.setLocationListener(this);//设置定位回调监听
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.stopLocation();//设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        } else {
            mLocationClient.setLocationOption(mLocationOption);
        }
        mLocationClient.startLocation();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //step 1 , reset  将上次的marker恢复到初始状态
        if (markerPre != null && !marker.equals(markerPre) && markerIndexPre != -1) {
            markerPre.remove();
            View view = View.inflate(this, R.layout.item_marker, null);
            ((TextView) view.findViewById(R.id.num)).setText(String.valueOf(markerIndexPre));
            ((ImageView) view.findViewById(R.id.img)).setImageResource(R.drawable.ic_location_blue_small);

            markers.put(markerIndexPre, aMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromView(view))
                    .position(markerPre.getPosition())));
        }
        if (!marker.equals(markerPre)) {
            int clickIndex = -1;
            for (Map.Entry<Integer, Marker> entry : markers.entrySet()) {
                if (marker.equals(entry.getValue())) {
                    clickIndex = entry.getKey();
                    break;
                }
            }
            if (clickIndex != -1) {
                marker.remove();
                View view = View.inflate(this, R.layout.item_marker, null);
                ((TextView) view.findViewById(R.id.num)).setText(String.valueOf(clickIndex));
                ((ImageView) view.findViewById(R.id.img)).setImageResource(R.drawable.ic_location_blue_big);
                Marker addMarker = aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromView(view))
                        .position(marker.getPosition()));
                markers.put(clickIndex, addMarker);
                startAnimation(addMarker);
                recyclerView.smoothScrollToPosition(clickIndex - 1);
                locationAdapter.refreshIndex(clickIndex - 1);
                markerPre = addMarker;
                markerIndexPre = clickIndex;
            }
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        return false;
    }

    private void refresh() {
        BmobRequest.getInstance(this).getLocationInfos(CMD_GET_LOCATION_INTOS);
    }

    @Override
    public void onMessage(ResponseInfo resp) {
        if (resp.getCode() == ResponseInfo.OK) {
            if (CMD_GET_LOCATION_INTOS.equals(resp.getCmd())) {
                List<LocationInfo> locationInfos = resp.getLocationInfoList();
                if (locationInfos != null) {
                    LatLng mPoint;
                    if (aMapLocation != null) {//我的位置
                        mPoint = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    } else {
                        mPoint = centerLatLng;
                    }
                    LatLng latLng;//以我的位置为中心,计算距离
                    for (int i = 0; i < locationInfos.size(); i++) {
                        latLng = new LatLng(locationInfos.get(i).getLatitude(), locationInfos.get(i).getLongitude());
                        locationInfos.get(i).setDistance(ArithTool.div(AMapUtils.calculateLineDistance(latLng, mPoint) / 1000.0, 1, 2));
                    }
                }
                refresh(locationInfos);
            }
        } else {
            Toast(coordinatorLayout, resp.getError());
        }
    }

    private void refresh(List<LocationInfo> list) {
        if (list != null) {
            Collections.sort(list, (o1, o2) -> {
                if (o1.getDistance() > o2.getDistance()) {
                    return 1;
                } else if (o1.getDistance() < o2.getDistance()) {
                    return -1;
                } else {
                    return 0;
                }
            });
        }
        if (locationAdapter == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, ContextCompat.getDrawable(this, R.drawable.divider_colors)));
            locationAdapter = new LocationAdapter(this, list);
            locationAdapter.setOnItemClickListener((position, object) -> {
                locationAdapter.refreshIndex(position);
                if (markers.get(position + 1) != null && (markers.get(position + 1).equals(markerPre)))
                    return;//点击的是同一条数据
                if (markerPre != null) {//先将上次的标记reset
                    View view = View.inflate(MapActivity.this, R.layout.item_marker, null);
                    ((TextView) view.findViewById(R.id.num)).setText(String.valueOf(markerIndexPre));
                    ((ImageView) view.findViewById(R.id.img)).setImageResource(R.drawable.ic_location_blue_small);
                    markerPre.remove();
                    markers.put(markerIndexPre, aMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromView(view))
                            .position(markerPre.getPosition())));
                }
                markerIndexPre = position + 1;
                if (markers.get(markerIndexPre) != null) {
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_MAP));
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(markers.get(markerIndexPre).getPosition()));
                    View view = View.inflate(MapActivity.this, R.layout.item_marker, null);
                    ((TextView) view.findViewById(R.id.num)).setText(String.valueOf(markerIndexPre));
                    ((ImageView) view.findViewById(R.id.img)).setImageResource(R.drawable.ic_location_blue_big);
                    markers.get(markerIndexPre).remove();
                    markers.put(markerIndexPre, aMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromView(view))
                            .position(markers.get(markerIndexPre).getPosition())));
                    startAnimation(markers.get(markerIndexPre));
                    markerPre = markers.get(markerIndexPre);
                }
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            });
            recyclerView.setAdapter(locationAdapter);
        } else {
            locationAdapter.resetIndex().notifyDataSetChanged(list);
        }

        LatLng mPoint;
        if (aMapLocation != null) {
            mPoint = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        } else {
            mPoint = centerLatLng;
        }
        refreshMyLocation(mPoint);
        if (list != null && list.size() > 0) {
            markers.clear();
            LocationInfo locationInfo;
            View view;
            for (int i = 0; i < list.size(); i++) {
                locationInfo = list.get(i);
                view = View.inflate(this, R.layout.item_marker, null);
                ((TextView) view.findViewById(R.id.num)).setText(String.valueOf(i + 1));
                Marker addMarker = aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromView(view))
                        .position(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude())));
                markers.put(i + 1, addMarker);
                startAnimation(addMarker);
            }
        }
        if (list == null || list.size() == 0) {
            viewSwitcher.setDisplayedChild(0);
        } else {
            viewSwitcher.setDisplayedChild(1);
        }
        bottomSheetBehavior.setPeekHeight(peekHeight);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void startAnimation(Marker marker) {
        AnimationSet animationSet = new AnimationSet(false);
        Animation scale = new ScaleAnimation(0, 1, 0, 1);
        Animation alpha = new AlphaAnimation(0, 1);
        animationSet.addAnimation(alpha);
        animationSet.addAnimation(scale);
        animationSet.setDuration(300);
        animationSet.setInterpolator(new LinearInterpolator());
        marker.setAnimation(animationSet);
        marker.startAnimation();
    }

    private void refreshMyLocation(LatLng latLng) {
        aMap.clear();
        clearCircle();
        // 绘制不同半径的圆，添加到地图上
        final Circle circleOut = aMap.addCircle(new CircleOptions().center(latLng)
                .radius(maxRadius)
                .strokeColor(ContextCompat.getColor(this, android.R.color.transparent))
                .fillColor(ContextCompat.getColor(this, R.color.blue_shader))
                .strokeWidth(0));

        final Circle circleIn = aMap.addCircle(new CircleOptions().center(latLng)
                .radius(10)
                .strokeColor(ContextCompat.getColor(this, android.R.color.transparent))
                .fillColor(ContextCompat.getColor(this, R.color.green_sharder))
                .strokeWidth(0));

        aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .setFlat(true)//平贴地面
                .icon(BitmapDescriptorFactory.fromView(View.inflate(this, R.layout.item_location_point, null)))
                .position(latLng));

        listCircle.add(circleOut);
        listCircle.add(circleIn);
        startAnimator(circleIn);
    }

    private void clearCircle() {
        if (valueAnimator != null && valueAnimator.isRunning()) valueAnimator.cancel();
        for (int i = 0; i < listCircle.size(); i++) listCircle.get(i).remove();
        listCircle.clear();
    }

    private void startAnimator(final Circle circle) {
        valueAnimator = ValueAnimator.ofFloat(10, maxRadius).setDuration(3000);
        valueAnimator.addUpdateListener(animation -> {
            mAnimatorValue = (float) animation.getAnimatedValue();
            circle.setRadius(mAnimatorValue);
        });
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);//反向重复执行,可以避免抖动
        valueAnimator.start();
    }

    private void openGaoDeMap(LatLng latLng, String describle) {
        try {
            if (!new File("/data/data/" + "com.autonavi.minimap").exists()) {
                Toast(coordinatorLayout, "您还没有安装高德地图哟~");
                return;
            }
            StringBuilder loc = new StringBuilder();
            loc.append("androidamap://viewMap?sourceApplication=XX");
            loc.append("&poiname=");
            loc.append(describle);
            loc.append("&lat=");
            loc.append(latLng.latitude);
            loc.append("&lon=");
            loc.append(latLng.longitude);
            loc.append("&dev=0");
            Intent intent = Intent.getIntent(loc.toString());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            LatLng latLng;
            if (aMapLocation.getErrorCode() == 0) {
                this.aMapLocation = aMapLocation;
                if (mLocationClient != null) {
                    mLocationClient.stopLocation();
                }
                latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            } else {
                latLng = centerLatLng;
                Toast(coordinatorLayout, aMapLocation.getErrorInfo());
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
            refresh();
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        initLocationOption();
        mLocationListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        releaseClient();
    }

    private class GpsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_GPS_STATE.equals(intent.getAction())) {
                Log.e(TAG, "监听到定位模式发生变化");
                initLocationOption();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        heightPixels = getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
        if (mLocationClient != null) mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
    }

    @Override
    public void onBackPressed() {
        hideSoft(searchView);
        if (bottomSheetBehavior != null) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void releaseClient() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
            mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
            mLocationClient = null;
        }
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
        if (gpsReceiver != null) {
            unregisterReceiver(gpsReceiver);
        }
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
    }

}
