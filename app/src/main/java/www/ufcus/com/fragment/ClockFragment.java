package www.ufcus.com.fragment;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnLongClick;
import me.xiaopan.android.content.res.DimenUtils;
import www.ufcus.com.R;
import www.ufcus.com.activity.MainActivity;
import www.ufcus.com.adapter.ClockAdapter;
import www.ufcus.com.beans.ClockBean;
import www.ufcus.com.event.CanSlideEvent;
import www.ufcus.com.event.SkinChangeEvent;
import www.ufcus.com.event.SwitchFragmentEvent;
import www.ufcus.com.utils.MyMapUtils;
import www.ufcus.com.utils.MyViewUtils;
import www.ufcus.com.utils.PreUtils;
import www.ufcus.com.utils.ThemeUtils;
import www.ufcus.com.utils.Utils;
import www.ufcus.com.utils.WifiOpenHelper;
import www.ufcus.com.widget.WaveRipView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClockFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<ClockBean>> {
    protected View rootView;
    // 定位相关
    LocationClient mLocationClient;
    public MyLocationListener myListener = new MyLocationListener();
    private MyLocationConfiguration.LocationMode mCurrentMode;

    @BindView(R.id.bmapView)
    MapView mMapView;
    BaiduMap mBaiduMap;

    // UI相关
    @BindView(R.id.map_status)
    TextView mStatus;
    @BindView(R.id.list)
    ListView mList;
    @BindView(R.id.tv_clock)
    WaveRipView tvClock;
    @BindView(R.id.ll_btn)
    View btnParent;
    @BindView(R.id.rl_map)
    View mapParent;
    @BindView(R.id.ll_list)
    View listParent;
    boolean isFirstLoc = true; // 是否首次定位
    WifiOpenHelper wifiOpenHelper;
    boolean isInRadius = false;
    private List<ClockBean> clocks = new ArrayList<>();
    private ClockAdapter clockAdapter;


    protected int getLayoutResource() {
        return R.layout.fragment_map;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(getActivity().getApplicationContext());
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutResource(), container, false);
            ButterKnife.bind(this, rootView);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new CanSlideEvent(true));
        initWifi();
        getActivity().getContentResolver().registerContentObserver(ClockBean.ITEMS_URI, true, mContentObserver);
        clockAdapter = new ClockAdapter(getActivity(), clocks);
        mList.setAdapter(clockAdapter);
        initMap();
        setViewColor();
        initData();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        testData();

    }

    /**
     * 测试数据,如果有服务端可删除本方法
     */
    private void testData() {
        //116.240794,40.072816
//        if (TextUtils.isEmpty(PreUtils.getString(this, "j_w", ""))) {
//            PreUtils.putString(this, "phone_number", "18301214392");
//            PreUtils.putString(this, "attend_wifi_ssid", "office");
//            PreUtils.putFloat(this, "work_time", 8);
//            //办公区域经纬度 默认用友软件园
//            PreUtils.putString(this, "j_w", "116.240794,40.072816");
//            //距离目标点有效距离默认300米
//            PreUtils.putFloat(this, "distance", 200);
//            PreUtils.putString(this, "address", "用友软件园附近");
//        }
        if (TextUtils.isEmpty(PreUtils.getString(getActivity(), "phone_number", ""))) {
            new MaterialDialog.Builder(getActivity())
                    .title("请输入您的手机号")
                    .inputType(InputType.TYPE_CLASS_PHONE)
                    .cancelable(false)
                    .inputRange(11, 11)
                    .input("手机号", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            PreUtils.putString(getActivity(), "phone_number", input.toString());
                            if (TextUtils.isEmpty(PreUtils.getString(getActivity(), "j_w", ""))) {
                                new MaterialDialog.Builder(getActivity())
                                        .title("提示")
                                        .cancelable(false)
                                        .icon(new IconicsDrawable(getActivity())
                                                .color(ThemeUtils.getThemeColor(getActivity(), R.attr.colorPrimary))
                                                .icon(MaterialDesignIconic.Icon.gmi_alert_circle)
                                                .sizeDp(20))
                                        .content("您还没有设置考勤参数，请设置！")
                                        .positiveText("好的")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                                EventBus.getDefault().post(new SwitchFragmentEvent(MainActivity.SETTING_CLOCK_DATA_FRAGMENT));
                                            }
                                        })
                                        .show();
                            }
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

    }


    private void initData() {
        getLoaderManager().initLoader(0, null, this);
        String j_w = PreUtils.getString(getActivity(), "j_w", "");
        if (!TextUtils.isEmpty(j_w)) {
            String[] jw = j_w.split(",");
            double t_latitude, t_longitude;
            t_longitude = Double.valueOf(jw[0]);
            t_latitude = Double.valueOf(jw[1]);
            LatLng target = new LatLng(t_latitude, t_longitude);
            MyMapUtils.maker(mBaiduMap, target, new OnGetGeoCoderResultListener() {
                @Override
                public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                }

                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

                }
            });
        }

    }

    private void initWifi() {
        wifiOpenHelper = new WifiOpenHelper(getActivity());
    }


    @Override
    public void onStart() {
        super.onStart();
        mLocationClient.start();
    }

    private void initMap() {
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // 地图初始化
        mMapView = (MapView) rootView.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //删除logo
        mMapView.removeViewAt(1);

//        MyMapUtils.addArea(getActivity(), mBaiduMap);
        // 定位初始化
        mLocationClient = new LocationClient(getActivity());
        mLocationClient.registerLocationListener(myListener);


        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            //此方法就是点击地图监听
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String address = PreUtils.getString(getActivity(), "address", "无数据");
                MyMapUtils.showLocation(getActivity(), mBaiduMap, marker, address, null);
                return false;
            }
        });
        initLocation();
    }


    //LocationClientOption用来设置定位SDK的定位方式
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );
        ////可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        int span = 1000;
        ///可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(span);
        ////可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setLocationNotify(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setEnableSimulateGps(true);
        mLocationClient.setLocOption(option);
        //启动位置信息
        mLocationClient.start();
    }


    @Subscribe
    public void onEvent(SkinChangeEvent event) {
        Logger.v("收到主题切换了");
        clockAdapter.notifyDataSetChanged();
        setViewColor();
    }

    private void setViewColor() {
        mMapView.getMap().clear();
//        MyMapUtils.addArea(getActivity(), mBaiduMap);
        tvClock.refreshDrawableState();
        btnParent.setBackgroundColor(ThemeUtils.getThemeColor(getActivity(), R.attr.colorPrimary));
        listParent.setBackgroundColor(ThemeUtils.getThemeColor(getActivity(), R.attr.colorPrimary));
        GradientDrawable background = (GradientDrawable) mapParent.getBackground();
        background.setStroke(DimenUtils.dp2px(getActivity(), 3), ThemeUtils.getThemeColor(getActivity(), R.attr.colorPrimary));

    }

    @OnLongClick({R.id.tv_clock})
    public boolean OnLongClick(View view) {
        switch (view.getId()) {
            case R.id.tv_clock:
                if (checkCanClock()) {
                    //打卡
                    clock();
                }
                break;
        }
        return true;
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyMapUtils.printReceiveLocation(location);
//            isInRadius = MyMapUtils.isPolygonContainPoint(location.getLatitude(), location.getLongitude());
            isInRadius = MyMapUtils.isNear(location.getLatitude(), location.getLongitude());
            if (isInRadius) {
                mStatus.setText("您在办公区域附近");
                mStatus.setBackgroundColor(getResources().getColor(R.color.colorGreenPrimary));
                mStatus.setTextColor(Color.WHITE);
            } else {
                mStatus.setText("您不在办公区域附近");
                mStatus.setBackgroundColor(getResources().getColor(R.color.colorRedPrimary));
                mStatus.setTextColor(Color.WHITE);
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getLoaderManager().restartLoader(0, null, ClockFragment.this);
            //监听到签到数据变化
        }
    };


    @Override
    public void onPause() {
        Logger.v("MapFragment onPause");
        mMapView.onPause();
        super.onPause();

    }

    @Override
    public void onResume() {
        Logger.v("MapFragment onResume");
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Logger.v("MapFragment onDestroyView");
        super.onDestroyView();
        // 退出时销毁定位
        mLocationClient.stop();

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        wifiOpenHelper = null;
        getActivity().getContentResolver().unregisterContentObserver(mContentObserver);
        EventBus.getDefault().post(new CanSlideEvent(false));
        EventBus.getDefault().unregister(this);
    }


    /***
     * 判断是否可以打卡
     *
     * @return
     */
    private boolean checkCanClock() {
        String useWifiSSID = PreUtils.getString(getActivity(), "attend_wifi_ssid", "");
        boolean connectedWifi = wifiOpenHelper.checkIsConnected(useWifiSSID);
        if (TextUtils.isEmpty(useWifiSSID)) {
            connectedWifi = false;
        }
        if (!connectedWifi) {
            MyViewUtils.showCenterToast(getActivity(), "未连接指定wifi");
            return false;
        } else if (!isInRadius) {
            MyViewUtils.showCenterToast(getActivity(), "未在办公区域");
            return false;
        } else {
            return true;
        }
    }


    /***
     * 打卡
     */
    private void clock() {
        ContentResolver resolver = getActivity().getContentResolver();

        ContentValues cValues = new ContentValues();
        String phoneNumber = PreUtils.getString(getActivity(), "phone_number", "");
        cValues.put(ClockBean.PHONE_NUMBER, phoneNumber);
        long dateTime = new Date().getTime();
        cValues.put(ClockBean.CLOCK_TIME, dateTime);
        cValues.put(ClockBean.GROUP_BY, Utils.getDate(dateTime));
        resolver.insert(ClockBean.ITEMS_URI, cValues);
        MyViewUtils.showCenterToast(getActivity(), "打卡成功");
    }


    @Override
    public Loader<ArrayList<ClockBean>> onCreateLoader(int id, Bundle args) {
        Logger.v("onCreateLoader");
        DataAsyncTaskLoader dataAsyncTaskLoader = new DataAsyncTaskLoader(getActivity());
        return dataAsyncTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ClockBean>> loader, ArrayList<ClockBean> data) {
        Logger.v("onLoadFinished");
        clocks = data;
        if (data.size() <= 0) {
            mList.setVisibility(View.GONE);
        } else {
            mList.setVisibility(View.VISIBLE);
        }
        clockAdapter.setList(data);
        clockAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ClockBean>> loader) {
        loader = null;
        clockAdapter.notifyDataSetChanged();
    }

    private static class DataAsyncTaskLoader extends AsyncTaskLoader<ArrayList<ClockBean>> {

        private Context context;

        public DataAsyncTaskLoader(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public ArrayList<ClockBean> loadInBackground() {
            Logger.v("DataAsyncTaskLoader--》loadInBackground");
            String order = ClockBean.CLOCK_TIME + " DESC limit 0,2";
            Cursor c = context.getContentResolver().query(ClockBean.ITEMS_URI, ClockBean.PROJECTION, null, null, order);
//            Cursor c = context.getContentResolver().query(ClockBean.ITEMS_URI, new String[]{"MAX(" + ClockBean.CLOCK_TIME + ") as maxtime"}, " 1=1" + " ) " + " group by (" + ClockBean.CLOCK_TIME, null, null);
//            Cursor c = context.getContentResolver().query(ClockBean.ITEMS_URI, new String[]{"MAX("+ClockBean.CLOCK_TIME+") as max","MIN(" + ClockBean.CLOCK_TIME + ") as min","(MAX(" + ClockBean.CLOCK_TIME + ")-MIN(" + ClockBean.CLOCK_TIME + ")) as maxtime"}, null, null, null);
            ArrayList<ClockBean> clockBeans = ClockBean.getBeans(c);
            return clockBeans;
        }

        @Override
        protected void onStartLoading() {
            Logger.v("DataAsyncTaskLoader--》onStartLoading");
            forceLoad();    //强制加载
        }

        @Override
        protected void onStopLoading() {
            Logger.v("DataAsyncTaskLoader--》onStopLoading");
            super.onStopLoading();
        }
    }

}
