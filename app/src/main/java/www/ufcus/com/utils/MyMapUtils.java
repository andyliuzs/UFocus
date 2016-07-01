package www.ufcus.com.utils;

import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.model.inner.Point;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.java.util.GeometryUtils;

/**
 * Created by andyliu on 16-7-1.
 */
public class MyMapUtils {

    /***
     * 打印获取到的定位信息
     * <p/>
     * 61 ： GPS定位结果，GPS定位成功。
     * 62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位。
     * 63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。
     * 65 ： 定位缓存的结果。
     * 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。
     * 67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。
     * 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
     * 161： 网络定位结果，网络定位定位成功。
     * 162： 请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件。
     * 167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
     * 502： key参数错误，请按照说明文档重新申请KEY。
     * 505： key不存在或者非法，请按照说明文档重新申请KEY。
     * 601： key服务被开发者自己禁用，请按照说明文档重新申请KEY。
     * 602： key mcode不匹配，您的ak配置过程中安全码设置有问题，请确保：sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请KEY。
     * 501～700：key验证失败，请按照说明文档重新申请KEY。
     *
     * @param location
     */

    public static void printReceiveLocation(BDLocation location) {
        //            //Receive Location
        JSONObject json = new JSONObject();

        try {
            json.put("time : ", location.getTime());
            json.put("error code", location.getLocType());
            json.put("latitude（纬度）", location.getLatitude());
            json.put("longitude（经度）", location.getLongitude());
            json.put("radius", location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                json.put("speed", location.getSpeed());// 单位：公里每小时
                json.put("satellite", location.getSatelliteNumber());
                json.put("height", location.getAltitude());// 单位：米
                json.put("direction", location.getDirection());// 单位度
                json.put("addr", location.getAddrStr());
                json.put("describe", "gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                json.put("addr", location.getAddrStr());
                //运营商信息
                json.put("operationers", location.getOperators());
                json.put("describe", "网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                json.put("describe", "离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                json.put("describe", "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                json.put("describe", "网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                json.put("describe", "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            // 位置语义化信息
            json.put("locationdescribe", location.getLocationDescribe());
            // POI数据
            List<Poi> list = location.getPoiList();
            if (list != null) {
                json.put("poilist_size", list.size());
                for (Poi p : list) {
                    json.put("poi", p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Logger.json(json.toString());
    }

    /***
     * 计算点是否在规则矩形
     *
     * @param latitude
     * @param longitude
     * @return boolean
     * <p/>
     * <p/>
     * 用友软件园某公司
     * //维度 经度
     * 西北40.072635,116.240727
     * 东北40.072769,116.241347
     * 西南40.072386,116.240803
     * 东南40.072493,116.241423
     */

    public static boolean isInRadius(double latitude, double longitude) {
        //西南XY，东北XY
        double swX = 116.240803, swy = 40.072386, neX = 116.241347, neY = 40.072769;

        if (longitude >= swX && longitude <= neX && latitude >= swy && latitude <= neY) {
            return true;
        } else {
            return false;
        }


    }


    /*****
     * 计算点是否在复杂多边形内
     *
     * @param pointX
     * @param pointY
     * @return
     */
    public static boolean isPolygonContainPoint(double pointX, double pointY) {
        double[][] vertexPoints = new double[][]{{40.072635, 116.240727}, {40.072769, 116.241347}, {40.072386, 116.240803}, {40.072493, 116.241423}};
        int nCross = 0;

        for (int i = 0; i < vertexPoints.length; ++i) {
            double[] p1 = vertexPoints[i];
            double[] p2 = vertexPoints[(i + 1) % vertexPoints.length];
            if (p1[1] != p2[1] && pointY >= Math.min(p1[1], p2[1]) && pointY < Math.max(p1[1], p2[1])) {
                double x = (double) (pointY - p1[1]) * (double) (p2[0] - p1[0]) / (double) (p2[1] - p1[1]) + (double) p1[0];
                if (x > (double) pointX) {
                    ++nCross;
                }
            }
        }

        return nCross % 2 == 1;
    }


    /***
     * 添加多边形区域
     *
     * @param mBaiduMap <p/>
     *                  <p/>
     *                  西北116.240727,40.072635
     *                  东北116.241347,40.072769
     *                  西南116.240803,40.072386
     *                  东南116.241423,40.072493
     */
    public static void addArea(BaiduMap mBaiduMap, int fllColor) {
        // 添加多边形  y x
        LatLng pt1 = new LatLng(40.072635, 116.240727);
        LatLng pt2 = new LatLng(40.072769, 116.241347);
        LatLng pt3 = new LatLng(40.072493, 116.241423);
        LatLng pt4 = new LatLng(40.072386, 116.240803);
        LatLng pt5 = new LatLng(40.072386, 116.240803);
        List<LatLng> pts = new ArrayList<LatLng>();
        pts.add(pt1);
        pts.add(pt2);
        pts.add(pt3);
        pts.add(pt4);
        pts.add(pt5);
        OverlayOptions ooPolygon = new PolygonOptions().points(pts)
                .stroke(new Stroke(1, fllColor)).fillColor(fllColor);
        mBaiduMap.addOverlay(ooPolygon);

    }

}
