package www.ufcus.com.http;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import me.xiaopan.android.net.NetworkUtils;
import okhttp3.Call;
import www.ufcus.com.app.App;
import www.ufcus.com.db.DBProvider;
import www.ufcus.com.http.callback.CallBack;

/**
 * Created by andyliu on 16-6-29.
 */
public class RequestManager {
    private static final String ERROR = "error";
    private static final String RESULTS = "results";
    private static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 获取Video信息
     *
     * @param getOne
     * @param tag
     * @param isCache
     * @param callBack
     */
    public static void getVideo(boolean getOne, Object tag, final boolean isCache, final CallBack callBack) {
        if (getOne) {
            get(tag, HttpResult.URL_GET_ONE_VIDEO, isCache, callBack);
        } else {
            get(tag, HttpResult.URL_VIDEOS, isCache, callBack);
        }
    }

    private static void get(Object tag, final String url, final boolean isCache, final CallBack callBack) {
        //读取缓存数据
        final DBProvider dbProvider = new DBProvider();
        String data = dbProvider.getData(url);
        if (!"".equals(data)) {
            //解析json数据并返回成功回调
            callBack.onSuccess(new Gson().fromJson(data, callBack.type));
        }

        //判断网络是否已连接，连接则往下走，未连接则返回失败回调，并终止请求
        if (!NetworkUtils.isConnectedByState(App.getInstance())) {
            callBack.onFailure("network not contented!!");
            return;

        }

        HttpResult.get(url, tag, new HashMap<String, String>(), new HashMap<String, String>(), new StringCallback() {

            @Override
            public void onError(Call call, final Exception e, int id) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(e.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onResponse(final String response, int id) {
                //获取String类型响应，注意是string(),不是toString()
                //在控制台格式化打印json数据
                Logger.json(response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleResponse(response, callBack, dbProvider, url, isCache);
                    }
                });

            }
        });
    }


    public static void getFuLi(boolean getOne, Object tag, final boolean isCache, final CallBack callBack) {
        if (getOne) {
            get(tag, HttpResult.URL_GET_ONE_FULI, isCache, callBack);
        } else {
            get(tag, HttpResult.URL_FULIS, isCache, callBack);
        }
    }


    /**
     * 获取全部菜单下的数据
     *
     * @param tag
     * @param isCache
     * @param callBack
     */
    public static void getAll(Object tag, int pageSize, int page, final boolean isCache, final CallBack callBack) {
        get(tag, HttpResult.getParamsUrl(HttpResult.URL_GET_ALL, pageSize, page), isCache, callBack);
    }


    /**
     * 处理请求结果
     *
     * @param json
     * @param callBack
     * @param dbProvider
     * @param url
     */
    private static void handleResponse(String json, CallBack callBack, DBProvider
            dbProvider, String url, boolean isCache) {
        try {
            //转化为json对象
            JSONObject jsonObject = new JSONObject(json);
            //判断error字段是否存在，不存在返回失败信息并结束请求
            if (jsonObject.isNull(ERROR)) {
                callBack.onFailure("error key not exists!!");
                return;
            }
            //判断后台返回结果，true表示失败，false表示成功，失败则返回错误回调并结束请求
            if (jsonObject.getBoolean(ERROR)) {
                callBack.onFailure("request failure!!");
                return;
            }
            //判断results字段是否存在，不存在返回时报回调并结束请求
            if (jsonObject.isNull(RESULTS)) {
                callBack.onFailure("results key not exists!!");
                return;
            }
            //获取results的值
            String results = jsonObject.getString(RESULTS);
            if (isCache) {
                //插入缓存数据库
                dbProvider.insertData(url, results);
            }

            //返回成功回调
            callBack.onSuccess(new Gson().fromJson(results, callBack.type));
        } catch (JSONException e) {
            callBack.onFailure(e.getLocalizedMessage());
        }
    }


    /**
     * 根据tag取消请求
     *
     * @param tag 标签
     */
    public static void cancelRequest(Object tag) {
        HttpResult.cancelTag(tag);
    }
}

