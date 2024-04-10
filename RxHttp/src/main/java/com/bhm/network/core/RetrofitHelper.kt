package com.bhm.network.core

import com.bhm.network.adapter.IntegerDefaultAdapter
import com.bhm.network.adapter.LongDefaultAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

/**
 * Created by bhm on 2023/5/6.
 */
open class RetrofitHelper(private val builder: HttpOptions) {

    fun <T> createRequest(clazz: Class<T>, url: String, client: OkHttpClient): T {
        if (builder.isShowDialog && null != builder.dialog) {
            builder.dialog?.showLoading(builder)
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(NetCoreConverterFactory.create(
                gsonBuilder,
                builder.messageKey,
                builder.codeKey,
                builder.dataKey,
                builder.successCode,
                builder.parseDataKey)
            )
            .build()
        return retrofit.create(clazz)
    }

    fun <T> createRequest(clazz: Class<T>, url: String): T {
        if (builder.isShowDialog && null != builder.dialog) {
            builder.dialog?.showLoading(builder)
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(GenerateOkHttpClient().make(builder))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(NetCoreConverterFactory.create(
                gsonBuilder,
                builder.messageKey,
                builder.codeKey,
                builder.dataKey,
                builder.successCode,
                builder.parseDataKey)
            )
            .build()
        return retrofit.create(clazz)
    }

    private val gsonBuilder: Gson
        get() = GsonBuilder()
            .registerTypeAdapter(Int::class.java, IntegerDefaultAdapter())
            .registerTypeAdapter(Int::class.javaPrimitiveType, IntegerDefaultAdapter())
            .registerTypeAdapter(Long::class.java, LongDefaultAdapter())
            .registerTypeAdapter(Long::class.javaPrimitiveType, LongDefaultAdapter())
            .create()
}