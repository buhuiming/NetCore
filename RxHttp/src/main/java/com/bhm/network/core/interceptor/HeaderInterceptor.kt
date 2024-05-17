package com.bhm.network.core.interceptor

import com.bhm.network.core.HttpOptions
import com.bhm.network.define.CommonUtil
import okhttp3.Interceptor

/**
 * @author Buhuiming
 * @description: 表头拦截器
 * @date :2023/5/6
 */
class HeaderInterceptor {
    fun make(builder: HttpOptions): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val requestBuilder: okhttp3.Request.Builder = chain.request()
                .newBuilder()
            builder.defaultHeader?.let {
                val stringBuilder = StringBuilder()
                stringBuilder.append("Header: ")
                for (stringStringEntry in it.entries) {
                    val key = (stringStringEntry as Map.Entry<*, *>).key.toString()
                    val value =
                        (stringStringEntry as Map.Entry<*, *>).value
                            .toString()
                            .replace("\u2212", "-")// 清洗字符串，去除不合法字符
                            .replace("−", "-")//负号替换为减号
                    requestBuilder.addHeader(key, value)
                    stringBuilder.append(key)
                    stringBuilder.append(" = ")
                    stringBuilder.append(value)
                    stringBuilder.append(", ")
                }
                CommonUtil.logger(builder, "Http Header", stringBuilder.toString())
            }
            chain.proceed(requestBuilder.build())
        }
    }
}