package com.microport.rxhttp.rxjava

import com.google.gson.Gson
import retrofit2.Converter
import retrofit2.Retrofit
import okhttp3.ResponseBody
import okhttp3.RequestBody
import java.lang.NullPointerException
import java.lang.reflect.Type

class ResponseConverterFactory private constructor(gson: Gson?) : Converter.Factory() {
    private val gson: Gson
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        //返回我们自定义的Gson响应体变换器
        return GsonResponseBodyConverter<Any>(gson, type)
    }

    override fun requestBodyConverter(
        type: Type, parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<*, RequestBody> {
        //返回我们自定义的Gson响应体变换器
        return GsonResponseBodyConverter(gson, type)
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun create(gson: Gson? = Gson()): ResponseConverterFactory {
            return ResponseConverterFactory(gson)
        }
    }

    init {
        if (gson == null) throw NullPointerException("gson == null")
        this.gson = gson
    }
}