package www.ufcus.com.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.ufcus.com.R;
import www.ufcus.com.activity.MainActivity;
import www.ufcus.com.event.CanSlideEvent;
import www.ufcus.com.event.SkinChangeEvent;
import www.ufcus.com.fragment.base.BaseFragment;
import www.ufcus.com.utils.MyMapUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {
    protected View rootView;
    // 定位相关
    LocationClient mLocationClient;
    public MyLocationListener myListener = new MyLocationListener();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    @BindView(R.id.bmapView)
    MapView mMapView;
    BaiduMap mBaiduMap;

    // UI相关
//    RadioGroup.OnCheckedChangeListener radioButtonListener;
//    @BindView(R.id.button1)
//    Button requestLocButton;
    @BindView(R.id.map_status)
    TextView mStatus;
    boolean isFirstLoc = true; // 是否首次定位

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
        initMap();
        initView();
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        mLocationClient.start();
    }

    private void initMap() {
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
//        requestLocButton.setText("普通");
//        View.OnClickListener btnClickListener = new View.OnClickListener() {
//            public void onClick(View v) {
//                switch (mCurrentMode) {
//                    case NORMAL:
//                        requestLocButton.setText("跟随");
//                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
//                        mBaiduMap
//                                .setMyLocationConfigeration(new MyLocationConfiguration(
//                                        mCurrentMode, true, mCurrentMarker));
//                        break;
//                    case COMPASS:
//                        requestLocButton.setText("普通");
//                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
//                        mBaiduMap
//                                .setMyLocationConfigeration(new MyLocationConfiguration(
//                                        mCurrentMode, true, mCurrentMarker));
//                        break;
//                    case FOLLOWING:
//                        requestLocButton.setText("罗盘");
//                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
//                        mBaiduMap
//                                .setMyLocationConfigeration(new MyLocationConfiguration(
//                                        mCurrentMode, true, mCurrentMarker));
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
//        requestLocButton.setOnClickListener(btnClickListener);
//
//        RadioGroup group = (RadioGroup) rootView.findViewById(R.id.radioGroup);
//        radioButtonListener = new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.defaulticon) {
        //传入null则，恢复默认图标
//                    mCurrentMarker = null;
//                    mBaiduMap
//                            .setMyLocationConfigeration(new MyLocationConfiguration(
//                                    mCurrentMode, true, null));
//                }
//                if (checkedId == R.id.customicon) {
//                     //修改为自定义marker
//                    mCurrentMarker = BitmapDescriptorFactory
//                            .fromResource(R.drawable.icon_geo);
//                    mBaiduMap
//                            .setMyLocationConfigeration(new MyLocationConfiguration(
//                                    mCurrentMode, true, mCurrentMarker,
//                                    accuracyCircleFillColor, accuracyCircleStrokeColor));
//                }
//            }
//        };
//        group.setOnCheckedChangeListener(radioButtonListener);

        // 地图初始化
        mMapView = (MapView) rootView.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        MyMapUtils.addArea(getActivity(), mBaiduMap);
        // 定位初始化
        mLocationClient = new LocationClient(getActivity());
        mLocationClient.registerLocationListener(myListener);
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
        Logger.v("收到主题切换了；额");
        mMapView.getMap().clear();
        MyMapUtils.addArea(getActivity(), mBaiduMap);

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyMapUtils.printReceiveLocation(location);
            boolean isInRadius = MyMapUtils.isPolygonContainPoint(location.getLatitude(), location.getLongitude());
//            String inResult = (isInRadius ? "您在办公区域内" : "您不在办公区域内");
//            Logger.v("您的位置是否在区域内呢？\n" + inResult);
            if (isInRadius) {
                mStatus.setText("您在办公区域");
                mStatus.setBackgroundColor(getResources().getColor(R.color.colorGreenPrimary));
                mStatus.setTextColor(Color.WHITE);
            } else {
                mStatus.setText("您不在办公区域");
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


    private void initView() {
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
