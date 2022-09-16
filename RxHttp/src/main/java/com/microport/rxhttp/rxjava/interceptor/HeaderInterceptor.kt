package com.microport.rxhttp.rxjava.interceptor

import com.microport.rxhttp.rxjava.RxBuilder
import okhttp3.Interceptor

/**
 * @author Buhuiming
 * @description: 表头拦截器
 * @date :2022/9/16 16:57
 */
class HeaderInterceptor {
    fun make(builder: RxBuilder): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val requestBuilder: okhttp3.Request.Builder = chain.request()
                .newBuilder()
            if (builder.defaultHeader != null && !builder.defaultHeader.isEmpty()) {
                for (stringStringEntry in builder.defaultHeader.entries) {
                    val key = (stringStringEntry as Map.Entry<*, *>).key.toString()
                    val value = (stringStringEntry as Map.Entry<*, *>).value.toString()
                    requestBuilder.addHeader(key, value)
                }
            }
            chain.proceed(requestBuilder.build())
        }
    }
}