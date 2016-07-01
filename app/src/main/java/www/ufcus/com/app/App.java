package www.ufcus.com.app;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import www.ufcus.com.BuildConfig;
import www.ufcus.com.http.interceptor.LoggingInterceptor;

/**
 * Created by andyliu on 16-6-28.
 */
public class App extends Application {
    private static App app;
    private static OkHttpUtils okHttpUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        initBDMap();
        app = this;
        initLogger();
        initOkHttp();

    }

    private void initLogger() {
        Logger.init("focus")
                .methodOffset(2)
                .methodCount(2)
                .hideThreadInfo()
                .logLevel(BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE);
    }

    private void initBDMap() {
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
    }

    private void initOkHttp() {
        try {
            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] x509Certificates = new X509Certificate[0];
                    return x509Certificates;
                }
            };
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL");

                sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LoggingInterceptor())
                    .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                    .readTimeout(10000L, TimeUnit.MILLISECONDS)
                    .sslSocketFactory(sslContext.getSocketFactory(), xtm)
                    .hostnameVerifier(DO_NOT_VERIFY)
                    .build();
            okHttpUtils = OkHttpUtils.initClient(okHttpClient);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OkHttpUtils getOkHttpUtils() {
        return okHttpUtils;
    }

    public static App getInstance() {
        return app;
    }

}
