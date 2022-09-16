package com.microport.rxhttp.rxjava.interceptor

import com.microport.rxhttp.rxjava.DownLoadResponseBody
import com.microport.rxhttp.rxjava.RxBuilder
import okhttp3.Interceptor

/**
 * @author Buhuiming
 * @description: 下载进度拦截器
 * @date :2022/9/16 17:00
 */
class DownloadInterceptor {
    fun make(builder: RxBuilder?): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val response = chain.proceed(chain.request())
            response.newBuilder().body(
                DownLoadResponseBody(response.body!!, builder)
            ).build()
        }
    }
}