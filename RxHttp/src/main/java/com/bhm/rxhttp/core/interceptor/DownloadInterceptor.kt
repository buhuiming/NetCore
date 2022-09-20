package com.bhm.rxhttp.core.interceptor

import com.bhm.rxhttp.body.DownLoadResponseBody
import com.bhm.rxhttp.core.HttpBuilder
import okhttp3.Interceptor

/**
 * @author Buhuiming
 * @description: 下载进度拦截器
 * @date :2022/9/16 17:00
 */
class DownloadInterceptor {
    fun make(builder: HttpBuilder?): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val response = chain.proceed(chain.request())
            response.newBuilder().body(
                DownLoadResponseBody(response.body!!, builder)
            ).build()
        }
    }
}