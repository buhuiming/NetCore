package com.bhm.network.core.interceptor

import com.bhm.network.body.DownloadResponseBody
import com.bhm.network.core.HttpOptions
import okhttp3.Interceptor

/**
 * @author Buhuiming
 * @description: 下载进度拦截器
 * @date :2022/9/16 17:00
 */
class DownloadInterceptor {
    fun make(builder: HttpOptions?): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val response = chain.proceed(chain.request())
            response.newBuilder().body(
                DownloadResponseBody(response.body!!, builder)
            ).build()
        }
    }
}