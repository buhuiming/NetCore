package com.bhm.rxhttp.core.interceptor

import android.content.Context
import android.net.ConnectivityManager
import com.bhm.rxhttp.core.HttpCache.getUserAgent
import com.bhm.rxhttp.core.RxBuilder
import okhttp3.CacheControl
import okhttp3.Interceptor

/**
 * @author Buhuiming
 * @description: 缓存拦截器 设置相关参数
 * @date :2022/9/16 16:55
 */
class CacheInterceptor {
    fun make(builder: RxBuilder): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (isNetworkConnected(builder.activity)) {
                // 有网络时, 缓存5s，根据实际情况设置
                val maxAge = 5
                request = request.newBuilder()
                    .removeHeader("User-Agent")
                    .removeHeader("Accept-Encoding")
                    .header("Accept-Encoding", "identity")
                    .header("User-Agent", getUserAgent(builder.activity))
                    .build()
                val response = chain.proceed(request)
                response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .build()
            } else {
                // 无网络时，缓存为3天
                val maxStale = 60 * 60 * 24 * 3
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .removeHeader("User-Agent")
                    .removeHeader("Accept-Encoding")
                    .header("Accept-Encoding", "identity")
                    .header("User-Agent", getUserAgent(builder.activity))
                    .build()
                val response = chain.proceed(request)
                response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
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