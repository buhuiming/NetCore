package com.bhm.rxhttp.rxjava

import com.google.gson.Gson
import com.bhm.rxhttp.utils.OK_CODE
import com.bhm.rxhttp.utils.ResultException
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.Type

class GsonResponseBodyConverter<T> internal constructor(
    private val gson: Gson,
    private val type: Type
) : Converter<ResponseBody, T> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val response = value.string()
        //先将返回的json数据解析到Response中，如果code==OK_CODE，则解析到我们的实体基类中，否则抛异常
        val httpResult = gson.fromJson(response, DataResponse::class.java)
        return if (httpResult == null) {
            //这种情况是请求成功，但是json不是合理的
            throw ResultException(OK_CODE, "json is illegal", response)
        } else if (httpResult.code == OK_CODE || httpResult.ret == OK_CODE) {
            //OK_CODE的时候就直接解析
            try {
                gson.fromJson(response, type)
            } catch (e: Exception) {
                if (httpResult.data.toString() == "[]") {
                    //这种情况是一个空数组，但是声明的却不是一个数组
                    httpResult.data = null
                    gson.fromJson<T>(gson.toJson(httpResult), type)
                } else {
                    throw ResultException(httpResult.code, httpResult.msg, response)
                }
            }
        } else if (httpResult.code == 0 && httpResult.ret == 0) {
            try {
                //这个情况就是没有code、ret的一个json，直接给它按预定的实体解析，抛出后再抛一个自定义ResultException
                gson.fromJson<T>(response, type)
            } catch (e: Exception) {
                //抛一个自定义ResultException 传入失败时候的状态码，和信息
                throw ResultException(httpResult.code, httpResult.msg, response)
            }
        } else {
            //抛一个自定义ResultException 传入失败时候的状态码，和信息
            throw ResultException(httpResult.code, httpResult.msg, response)
        }
    }
}