package com.bhm.network.core.interceptor

import com.bhm.network.core.HttpOptions
import com.bhm.network.body.UploadRequestBody
import okhttp3.Interceptor

/**
 * @author Buhuiming
 * @description: 上传进度拦截器
 * @date :2022/9/16 17:00
 */
class UploadInterceptor {
    fun make(builder: HttpOptions?): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            if (null == request.body) {
                return@Interceptor chain.proceed(request)
            }
            val build = request.newBuilder()
                .method(
                    request.method,
                    UploadRequestBody(request.body!!, builder)
                )
                .build()
            chain.proceed(build)
        }
    }
}