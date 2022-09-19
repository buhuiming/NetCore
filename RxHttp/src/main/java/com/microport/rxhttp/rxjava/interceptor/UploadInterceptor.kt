package com.microport.rxhttp.rxjava.interceptor

import com.microport.rxhttp.rxjava.RxBuilder
import com.microport.rxhttp.rxjava.UpLoadRequestBody
import okhttp3.Interceptor

/**
 * @author Buhuiming
 * @description: 上传进度拦截器
 * @date :2022/9/16 17:00
 */
class UploadInterceptor {
    fun make(builder: RxBuilder?): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            if (null == request.body) {
                return@Interceptor chain.proceed(request)
            }
            val build = request.newBuilder()
                .method(
                    request.method,
                    UpLoadRequestBody(request.body!!, builder)
                )
                .build()
            chain.proceed(build)
        }
    }
}