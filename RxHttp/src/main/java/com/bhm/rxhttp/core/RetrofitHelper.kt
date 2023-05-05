package com.bhm.rxhttp.core

import com.bhm.rxhttp.adapter.IntegerDefaultAdapter
import com.bhm.rxhttp.adapter.LongDefaultAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.bhm.rxhttp.core.ResponseConverterFactory.Companion.create
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

/**
 * Created by bhm on 2022/9/15.
 */
class RetrofitHelper(private val builder: HttpBuilder) {

    fun <T> createRequest(clazz: Class<T>, url: String): T {
        if (builder.isShowDialog && null != builder.dialog) {
            builder.dialog?.showLoading(builder)
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(GenerateOkHttpClient().make(builder))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(create(gsonBuilder, builder.messageKey, builder.codeKey, builder.dataKey, builder.successCode))
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