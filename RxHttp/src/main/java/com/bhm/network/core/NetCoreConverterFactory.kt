package com.bhm.network.core

import com.bhm.network.define.CODE_KEY
import com.bhm.network.define.DATA_KEY
import com.bhm.network.define.MESSAGE_KEY
import com.bhm.network.define.OK_CODE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
/**
 * @description 返回参数解析
 * @author Buhuiming
 * @date 2023/11/22/ 17:41
 */
internal class NetCoreConverterFactory private constructor(
    gson: Gson?,
    private val messageKey: String = MESSAGE_KEY,
    private val codeKey: String = CODE_KEY,
    private val dataKey: String = DATA_KEY,
    private val successCode: Int = OK_CODE
) : Converter.Factory() {
    private val gson: Gson
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        //返回我们自定义的Gson响应体变换器
        return GsonResponseBodyConverter<Any>(gson, type, messageKey, codeKey, dataKey, successCode)
    }

    override fun requestBodyConverter(
        type: Type, parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonRequestBodyConverter(gson, adapter)
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun create(gson: Gson? = Gson(),
                   messageKey: String,
                   codeKey: String,
                   dataKey: String,
                   successCode: Int = OK_CODE): NetCoreConverterFactory {
            return NetCoreConverterFactory(gson, messageKey, codeKey, dataKey, successCode)
        }
    }

    init {
        if (gson == null) throw NullPointerException("gson == null")
        this.gson = gson
    }
}