package com.bhm.network.core.interceptor

import android.content.Context
import android.net.ConnectivityManager
import com.bhm.network.core.HttpCache.getUserAgent
import com.bhm.network.core.HttpOptions
import okhttp3.CacheControl
import okhttp3.Interceptor

/**
 * @author Buhuiming
 * @description: 缓存拦截器 设置相关参数
 * @date :2022/9/16 16:55
 */
class CacheInterceptor {

    companion object {
        const val USER_AGENT = "User-Agent"
        const val ACCEPT_ENCODING = "Accept-Encoding"
        const val CACHE_CONTROL = "Cache-Control"
    }
    fun make(builder: HttpOptions): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (isNetworkConnected(builder.activity)) {
                // 有网络时, 缓存5s，根据实际情况设置
                val maxAge = builder.cacheDuration
                request = request.newBuilder()
                    .removeHeader(USER_AGENT)
                    .removeHeader(ACCEPT_ENCODING)
                    .header(ACCEPT_ENCODING, "identity")
                    .header(USER_AGENT, getUserAgent(builder.activity))
                    .build()
                val response = chain.proceed(request)
                builder.callBack?.code = response.code
                response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader(CACHE_CONTROL)
                    .header(CACHE_CONTROL, "public, max-age=$maxAge")
                    .build()
            } else {
                // 无网络时，缓存为3天
                val maxStale = builder.cacheDurationNoNet
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .removeHeader(USER_AGENT)
                    .removeHeader(ACCEPT_ENCODING)
                    .header(ACCEPT_ENCODING, "identity")
                    .header(USER_AGENT, getUserAgent(builder.activity))
                    .build()
                val response = chain.proceed(request)
                builder.callBack?.code = response.code
                response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader(CACHE_CONTROL)
                    .header(CACHE_CONTROL, "public, only-if-cached, max-stale=$maxStale")
                    .build()
            }
        }
    }

    /**
     * 判断是否有网络
     *
     * @return 返回值
     */
    @Suppress("DEPRECATION")
    private fun isNetworkConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager.activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable
            }
        }
        return false
    }
}