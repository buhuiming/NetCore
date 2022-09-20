package com.bhm.rxhttp.core.interceptor

import com.bhm.rxhttp.core.HttpBuilder
import okhttp3.Interceptor

/**
 * @author Buhuiming
 * @description: 表头拦截器
 * @date :2022/9/16 16:57
 */
class HeaderInterceptor {
    fun make(builder: HttpBuilder): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val requestBuilder: okhttp3.Request.Builder = chain.request()
                .newBuilder()
            builder.defaultHeader?.let {
                for (stringStringEntry in it.entries) {
                    val key = (stringStringEntry as Map.Entry<*, *>).key.toString()
                    val value = (stringStringEntry as Map.Entry<*, *>).value.toString()
                    requestBuilder.addHeader(key, value)
                }
            }
            chain.proceed(requestBuilder.build())
        }
    }
}