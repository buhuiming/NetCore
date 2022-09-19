package com.microport.rxhttp.rxjava

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.microport.rxhttp.rxjava.ResponseConverterFactory.Companion.create
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

/**
 * Created by bhm on 2022/9/15.
 */
class RetrofitCreateHelper(private val builder: RxBuilder) {

    fun <T> createApi(clazz: Class<T>, url: String): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(GenerateOkHttpClient().make(builder))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(create(gsonBuilder))
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