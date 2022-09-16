package com.microport.rxhttp.rxjava;

import android.annotation.SuppressLint;

import com.microport.rxhttp.rxjava.interceptor.CacheInterceptor;
import com.microport.rxhttp.rxjava.interceptor.DownloadInterceptor;
import com.microport.rxhttp.rxjava.interceptor.HeaderInterceptor;
import com.microport.rxhttp.rxjava.interceptor.LoggingInterceptor;
import com.microport.rxhttp.rxjava.interceptor.UploadInterceptor;

import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * @author Buhuiming
 */
@SuppressLint({"CustomX509TrustManager", "TrustAllX509TrustManager", "BadHostnameVerifier"})
public class GenerateOkHttpClient {

    private int TIMEOUT_READ = 30;//读取超时

    private int TIMEOUT_CONNECTION = 20;//连接超时

    protected GenerateOkHttpClient() {

    }

    protected OkHttpClient make(RxBuilder builder) {
        if(null != builder.getOkHttpClient()){
            return builder.getOkHttpClient();
        }
        if(builder.getReadTimeOut() > 0){
            TIMEOUT_READ = builder.getReadTimeOut();
        }
        if(builder.getConnectTimeOut() > 0){
            TIMEOUT_CONNECTION = builder.getConnectTimeOut();
        }
        return new OkHttpClient.Builder()
                .sslSocketFactory(getUnsafeOkHttpClient(), new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                })//SSL证书
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(new LoggingInterceptor().make(builder))//打印日志
                .addInterceptor(new HeaderInterceptor().make(builder))
                .addInterceptor(new DownloadInterceptor().make(builder))
                .addInterceptor(new UploadInterceptor().make(builder))
                .addNetworkInterceptor(new CacheInterceptor().make(builder))//设置Cache拦截器
                .cache(HttpCache.getCache(builder.getActivity()))
                .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)//time out
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)//失败重连
                .build();
    }

    private SSLSocketFactory getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final X509TrustManager[] trustAllCerts = new X509TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        X509Certificate[] chain,
                        String authType) {
                }

                @Override
                public void checkServerTrusted(
                        X509Certificate[] chain,
                        String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager

            return sslContext
                    .getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
