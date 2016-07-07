package www.ufcus.com.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import www.ufcus.com.R;
import www.ufcus.com.activity.MainActivity;
import www.ufcus.com.event.CanSlideEvent;
import www.ufcus.com.event.SkinChangeEvent;
import www.ufcus.com.utils.MyMapUtils;
import www.ufcus.com.utils.PreUtils;
import www.ufcus.com.utils.ThemeUtils;
import www.ufcus.com.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingClockDataFragment extends Fragment {


    protected View rootView;
    // 定位相关
    LocationClient mLocationClient;
    public MyLocationListener myListener = new MyLocationListener();
    private MyLocationConfiguration.LocationMode mCurrentMode;


    @BindView(R.id.bmapView)
    MapView mMapView;
    BaiduMap mBaiduMap;

    @BindView(R.id.et_distance)
    EditText etDistance;
    @BindView(R.id.et_wifi_ssid)
    EditText etWifiSSID;
    @BindView(R.id.et_work_time)
    EditText etWorkTime;
    @BindView(R.id.ll_edit)
    View model_edit;
    @BindView(R.id.ll_map)
    View model_map;
    @BindView(R.id.tv_other_setting)
    View otherSettingBtn;

    @BindView(R.id.tv_setting_detail)
    TextView settingDetails;
    // UI相关
    boolean isFirstLoc = true; // 是否首次定位

    private String address = "";


    protected int getLayoutResource() {
        return R.layout.fragment_setting_clock_data;
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
        initMap();
        initData();
        setViewColor();
        return rootView;
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
                MyMapUtils.maker(mBaiduMap, latLng, new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                    }

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                        //获取点击的坐标地址
                        address = reverseGeoCodeResult.getAddress();
                        Logger.v("您点击的地址为" + reverseGeoCodeResult.getAddress());
                    }
                });

            }
        });

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MyMapUtils.showLocation(getActivity(), mBaiduMap, marker, address, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initData();
                    }
                });
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
        setViewColor();
    }

    private void setViewColor() {
        model_edit.setBackgroundColor(ThemeUtils.getThemeColor(getActivity(), R.attr.colorPrimary));
        otherSettingBtn.setBackgroundColor(ThemeUtils.getThemeColor(getActivity(), R.attr.colorPrimary));
    }

    @OnClick({R.id.tv_ok, R.id.tv_cancel, R.id.tv_other_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_ok:
                if (TextUtils.isEmpty(etWifiSSID.getText().toString())) {
                    Toast.makeText(getActivity(), "wifi名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Utils.checkIsNumber(etDistance.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入正确的距离", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Utils.checkIsNumber(etWorkTime.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入正确的工作时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveData();
                initData();
                break;
            case R.id.tv_cancel:
                changeView(R.id.ll_map);
                break;
            case R.id.tv_other_setting:
                changeView(R.id.ll_edit);
                break;
        }
    }

    public void changeView(int id) {
        switch (id) {
            case R.id.ll_map:
                ((MainActivity) getActivity()).nowSettingView = MainActivity.SETTING_CLOCK_MAP_VIEW;
                model_edit.setVisibility(View.GONE);
                model_map.setVisibility(View.VISIBLE);
                break;

            case R.id.ll_edit:
                ((MainActivity) getActivity()).nowSettingView = MainActivity.SETTING_CLOCK_SETTING_VIEW;
                model_edit.setVisibility(View.VISIBLE);
                model_map.setVisibility(View.GONE);
                break;
        }
    }

    private void saveData() {
        PreUtils.putString(getActivity(), "attend_wifi_ssid", etWifiSSID.getText().toString());
        PreUtils.putFloat(getActivity(), "work_time", Float.valueOf(etWorkTime.getText().toString()));
        PreUtils.putFloat(getActivity(), "distance", Float.valueOf(etDistance.getText().toString()));
        Toast.makeText(getActivity(), "数据设定成功！", Toast.LENGTH_SHORT).show();
    }


    private void initData() {
//        Bundle bundle = getArguments();
//        String showView = bundle.getString("view", MainActivity.SETTING_CLOCK_MAP_VIEW);
//        if (showView.equals(MainActivity.SETTING_CLOCK_SETTING_VIEW)) {
//            changeView(R.id.ll_edit);
//        } else {
//            changeView(R.id.ll_map);
//        }
        String ssid = PreUtils.getString(getActivity(), "attend_wifi_ssid", "");
        String workTime = String.valueOf(PreUtils.getFloat(getActivity(), "work_time", 0));
        String distance = String.valueOf(PreUtils.getFloat(getActivity(), "distance", 0));
        String j_w = PreUtils.getString(getActivity(), "j_w", "");
        String _address = PreUtils.getString(getActivity(), "address", "");
        etWifiSSID.setText(ssid);
        etWorkTime.setText(workTime);
        etDistance.setText(distance);

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
                    //获取点击的坐标地址
                    address = reverseGeoCodeResult.getAddress();
                    Logger.v("您点击的地址为" + reverseGeoCodeResult.getAddress());
                }
            });
        }
        String settingDetail = "WIFI:" + ssid + "\n工作时间:" + workTime + "\n打卡距离:" + distance + "\n地点:" + _address + "\n" + "坐标:" + j_w;
        settingDetails.setText(settingDetail);
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
        EventBus.getDefault().post(new CanSlideEvent(false));
        EventBus.getDefault().unregister(this);
    }


}
