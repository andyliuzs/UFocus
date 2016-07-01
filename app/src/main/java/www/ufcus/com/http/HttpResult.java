package www.ufcus.com.http;

import android.support.annotation.NonNull;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Response;
import www.ufcus.com.app.App;

/**
 * Created by andyliu on 16-6-29.
 */
public class HttpResult {
    private static final MediaType TYPE_JSON = MediaType.parse("application/json");
    public static final String URL_VIDEOS = "http://gank.io/api/data/休息视频/";
    public static final String URL_GET_ONE_VIDEO = "http://gank.io/api/data/休息视频/1/1";
    public static final String URL_FULIS = "http://gank.io/api/data/福利/";
    public static final String URL_GET_ONE_FULI = "http://gank.io/api/data/福利/1/1";
    public static final String URL_GET_ALL = "http://gank.io/api/data/all/";


    /***
     * 获取添加分页参数的url
     *
     * @param url
     * @param pageSize
     * @param page
     * @return
     */
    public static String getParamsUrl(String url, int pageSize, int page) {

        return url + String.valueOf(pageSize) + "/"
                + String.valueOf(page);
    }


    /**
     * 同步
     *
     * @param url
     * @param tag
     * @param params:請求參數的map集合
     */
    public static Response get(@NonNull String url, Object tag, Map<String, String> headers, Map<String, String> params) throws IOException {
        return OkHttpUtils.get().url(url).tag(tag).headers(headers).params(params).build().execute();
    }

    /**
     * 異步
     *
     * @param url
     * @param tag
     * @param params:請求參數的map集合
     * @param callback
     */
    public static void get(@NonNull String url, Object tag, Map<String, String> headers, Map<String, String> params, @NonNull Callback callback) {
        OkHttpUtils.get().url(url).tag(tag).headers(headers).params(params).build().execute(callback);
    }

    /**
     * 同步post
     *
     * @param url
     * @param tag
     * @param params:請求參數的map集合
     */
    public static Response post(@NonNull String url, Object tag, Map<String, String> headers, Map<String, String> params) throws IOException {
        return OkHttpUtils.post().url(url).tag(tag).headers(headers).params(params).build()
                .execute();
    }

    /**
     * 异步post
     *
     * @param url
     * @param tag
     * @param params:請求參數的map集合
     * @param callback
     */
    public static void post(@NonNull String url, Object tag, Map<String, String> headers, Map<String, String> params, @NonNull Callback callback) {
        OkHttpUtils.post().url(url).tag(tag).headers(headers).params(params).build()
                .execute(callback);
    }

    /**
     * 同步post
     *
     * @param url
     * @param tag
     * @param headers
     * @param params
     */
    public static Response postJson(@NonNull String url, Object tag, Map<String, String> headers, String params) throws IOException {
        return OkHttpUtils.postString().mediaType(TYPE_JSON).url(url).tag(tag).headers(headers).content(params).build().execute();
    }

    /**
     * 異步post
     *
     * @param url
     * @param tag
     * @param headers
     * @param params
     * @param callback
     */
    public static void postJson(@NonNull String url, Object tag, Map<String, String> headers, String params, @NonNull Callback callback) {
        OkHttpUtils.postString().mediaType(TYPE_JSON).url(url).tag(tag).headers(headers).content(params).build().execute(callback);
    }


    public static void cancelTag(Object tag) {
        App.getInstance().getOkHttpUtils().cancelTag(tag);
    }


}
