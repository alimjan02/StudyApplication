package com.sxt.chat.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
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
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
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
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
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
import com.sxt.chat.dialog.AlertDialogBuilder;
import com.sxt.chat.json.LocationInfo;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.utils.AnimationUtil;
import com.sxt.chat.utils.ArithTool;
import com.sxt.chat.utils.LocationManager;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.SimpleTextWatcher;
import com.sxt.chat.utils.SystemUiStyle;
import com.sxt.chat.ws.BmobRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.systemchannels.PlatformChannel;

/**
 * Created by sxt on 2018/10/26.
 */
public class MapActivity extends BaseActivity implements LocationSource, AMap.OnMarkerClickListener, PoiSearch.OnPoiSearchListener {

    private AMap aMap;
    private MapView mapView;
    private AMapLocation aMapLocation;
    private RecyclerView recyclerView;
    private ViewSwitcher viewSwitcher;
    private NestedScrollView nestedScrollView;
    private View bottomSheet;
    private CoordinatorLayout coordinatorLayout;
    private LocationAdapter locationAdapter;
    private BottomSheetBehavior bottomSheetBehavior;

    private PoiSearch poiSearch;
    private EditText searchView;
    private ValueAnimator valueAnimator;
    private float mAnimatorValue;
    private View close;
    private int marginTop;
    private Marker markerPre;
    private int maxRadius = 100, markerIndexPre = -1;
    private int ZOOM_MAP = 15, peekHeight, maxHeight;
    private List<Circle> listCircle = new ArrayList<>();
    private Map<Integer, Marker> markers = new HashMap<>();
    private LatLng centerLatLng = new LatLng(31.236255, 121.470231);
    private final int REQUEST_CODE_LOCATION = 500;
    private String CMD_GET_LOCATION_INTOS = this.getClass().getName() + "CMD_GET_LOCATION_INTOS";
    private UiSettings uiSettings;
    private CardView cardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_layout);
        SystemUiStyle.fitSystemWindow(this);
        SystemUiStyle.setStatusBarColor(this, android.R.color.transparent);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        //地图的夜间模式跟随app当前的模式
        boolean isNightMode = Prefs.getInstance(this).isNightMode();
        aMap.setMapType(isNightMode ? AMap.MAP_TYPE_NIGHT : AMap.MAP_TYPE_NORMAL);
        requestLocationPermission();
    }

    private void init() {
        if (uiSettings == null) {
            initMap();
            initView();
            initBehavior();
        }
        startLocation();
    }

    private void initMap() {
        uiSettings = aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(true);// 标尺开关
        uiSettings.setZoomControlsEnabled(false);//缩放按钮
        aMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_MAP));
        aMap.setMyLocationEnabled(true);
        aMap.setLocationSource(this);
        aMap.setOnMarkerClickListener(this);
    }

    private void initView() {
        close = findViewById(R.id.close);
        searchView = findViewById(R.id.search_view);
        viewSwitcher = findViewById(R.id.viewSwitcher);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        bottomSheet = findViewById(R.id.bottom_sheet);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
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

    /**
     * 初始化BottomSheet
     */
    private void initBehavior() {
        final View fabContainer = findViewById(R.id.fab_container);
        final FloatingActionButton fab = findViewById(R.id.fab_my_location);
        final FloatingActionButton fabScrolling = findViewById(R.id.fab_scrolling);
        fabScrolling.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.day_night_normal_color)));
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.day_night_normal_color)));
        fab.setOnClickListener(view -> startLocation());
        fabScrolling.setOnClickListener(v -> {
            LatLng latLng;
            if (aMapLocation != null) {
                latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            } else {
                latLng = centerLatLng;
            }
            openGaoDeMap(latLng, getString(R.string.app_name));
        });
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (nestedScrollView != null && nestedScrollView.getScrollY() != 0) {
                        nestedScrollView.setScrollY(0);//列表滑动到顶端
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float distance, result;
                if (slideOffset > 0) {//在peekHeight位置以上 滑动(向上、向下) slideOffset bottomSheet.getHeight() 是展开后的高度的比例
                    distance = bottomSheet.getHeight() * slideOffset;
                } else {//在peekHeight位置以下 滑动(向上、向下)  slideOffset 是PeekHeight的高度的比例
                    distance = bottomSheetBehavior.getPeekHeight() * slideOffset;
                }
                cardView.setTranslationY(-(bottomSheet.getHeight() - distance));
                cardView.setAlpha(slideOffset);
                if (distance < 0) {
                    fabContainer.setTranslationY(-distance);
                    mapView.setTranslationY(0);
                } else {
                    if (distance <= peekHeight) {
                        fabContainer.setTranslationY(-distance);
                        mapView.setTranslationY(-distance);
                    }
                }
            }
        });
        bottomSheet.post(() -> {
            initTitle();
            nestedScrollView.measure(0, 0);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) nestedScrollView.getLayoutParams();
            params.height = maxHeight;
            nestedScrollView.setLayoutParams(params);
            nestedScrollView.requestLayout();
            bottomSheetBehavior.setPeekHeight(peekHeight);
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
    }

    private void initTitle() {
        cardView = findViewById(R.id.cardView);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) cardView.getLayoutParams();
        int statusBarHeight = 0;
        boolean flag = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP;
        if (flag) {
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
            cardView.measure(0, 0);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) cardView.getLayoutParams();
            params.topMargin += statusBarHeight;
            cardView.setLayoutParams(params);
            cardView.requestLayout();
        }
        marginTop = cardView.getHeight() + lp.topMargin / 2 + (flag ? 0 : statusBarHeight);
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        peekHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        maxHeight = heightPixels - marginTop;//展开后最大高度
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
            viewSwitcher.setDisplayedChild(1);
        } else {
            viewSwitcher.setDisplayedChild(0);
        }
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
                .fillColor(ContextCompat.getColor(this, R.color.green_shader))
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

    /**
     * 发起定位
     */
    private void startLocation() {
        LocationManager.getInstance(this).setOnLocationListener(aMapLocation -> {
            if (aMapLocation != null) {
                LatLng latLng;
                if (aMapLocation.getErrorCode() == 0) {
                    this.aMapLocation = aMapLocation;
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
        }).startLocation();
    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        LocationManager.getInstance(this).stopLocation();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        startLocation();
    }

    @Override
    public void deactivate() {
        LocationManager.getInstance(this).releaseClient();
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
        stopLocation();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
        LocationManager.getInstance(this).releaseClient();
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
    }

    @Override
    public void onPermissionsAllowed(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {
            init();
        }
    }

    @Override
    public void onPermissionsRefused(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefused(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {
            requestLocationPermission();
        }
    }

    @Override
    public void onPermissionsRefusedNever(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefusedNever(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {
            String appName = getString(R.string.app_name);
            String message = String.format(getString(R.string.permission_request_LOCATION), appName);
            SpannableString span = new SpannableString(message);
            span.setSpan(new TextAppearanceSpan(this, R.style.text_color_2_15_style), 0, message.indexOf(appName), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int start = message.indexOf(appName) + appName.length();
            span.setSpan(new TextAppearanceSpan(this, R.style.text_color_black_15_bold_style), message.indexOf(appName), start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new TextAppearanceSpan(this, R.style.text_color_2_15_style), start, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            showPermissionRefusedNeverDialog(span);
        }
    }

    /**
     * 请求位置权限
     */
    private void requestLocationPermission() {
        checkPermission(REQUEST_CODE_LOCATION, Manifest.permission_group.LOCATION, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,});
    }

    /**
     * 权限被彻底禁止后 , 弹框提醒用户去开启
     */
    private void showPermissionRefusedNeverDialog(CharSequence message) {
        new AlertDialogBuilder(this)
                .setTitle(R.string.message_alert, true)
                .setMessage(message)
                .setLeftButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setRightButton(R.string.confirm, (dialog, which) -> {
                    dialog.dismiss();
                    goToAppSettingsPage();
                })
                .setShowLine(true)
                .setCanceledOnTouchOutside(false)
                .show();
    }

}
