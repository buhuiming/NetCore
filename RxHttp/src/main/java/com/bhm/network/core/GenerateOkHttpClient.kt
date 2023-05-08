package com.bhm.network.core

import android.annotation.SuppressLint
import com.bhm.network.core.HttpCache.getCache
import com.bhm.network.core.interceptor.*
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * @author Buhuiming
 */
@SuppressLint("CustomX509TrustManager", "TrustAllX509TrustManager")
class GenerateOkHttpClient {

    private var timeOutRead = 30 //读取超时

    private var timeOutConnection = 20 //连接超时

    fun make(builder: HttpOptions): OkHttpClient {
        if (null != builder.okHttpClient) {
            return builder.okHttpClient!!
        }
        if (builder.readTimeOut > 0) {
            timeOutRead = builder.readTimeOut
        }
        if (builder.connectTimeOut > 0) {
            timeOutConnection = builder.connectTimeOut
        }
        return OkHttpClient.Builder()
            .sslSocketFactory(unsafeOkHttpClient, object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }
            }) //SSL证书
            .hostnameVerifier { _: String?, _: SSLSession? -> true }
            .addInterceptor(LoggingInterceptor().make(builder)) //打印日志
            .addInterceptor(HeaderInterceptor().make(builder))
            .addInterceptor(DownloadInterceptor().make(builder))
            .addInterceptor(UploadInterceptor().make(builder))
            .addNetworkInterceptor(CacheInterceptor().make(builder)) //设置Cache拦截器
            .cache(getCache(builder.activity))
            .connectTimeout(timeOutConnection.toLong(), TimeUnit.SECONDS) //time out
            .readTimeout(timeOutRead.toLong(), TimeUnit.SECONDS)
            .writeTimeout(timeOutRead.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true) //失败重连
            .build()
    }

    // Create a trust manager that does not validate certificate chains
    private val unsafeOkHttpClient: SSLSocketFactory
        get() = try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<X509TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {}

                override fun checkServerTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {}

                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
}