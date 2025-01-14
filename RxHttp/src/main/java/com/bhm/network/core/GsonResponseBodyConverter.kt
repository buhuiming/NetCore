package com.bhm.network.core

import com.bhm.network.define.ResultException
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.Type

class GsonResponseBodyConverter<T> internal constructor(
    private val gson: Gson,
    private val type: Type,
    private val messageKey: String,
    private val codeKey: String,
    private val dataKey: String,
    private val successCode: Int,
    private val parseDataKey: Boolean,
) : Converter<ResponseBody, T> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val response = value.string()
        //先将返回的json数据解析到Response中，如果code==successCode，则解析到我们的实体基类中，否则抛异常
        val jsonObject = JSONObject(response)
        val message = jsonObject.optString(messageKey)
        val code = jsonObject.optInt(codeKey)
        val data = jsonObject.optJSONObject(dataKey)
        val dataArr = jsonObject.optJSONArray(dataKey)
        return when {
            code == successCode || code == 0 -> {
                //successCode的时候就直接解析
                try {
                    if (parseDataKey && data != null) {
                        gson.fromJson(data.toString(), type)
                    } else {
                        gson.fromJson(response, type)
                    }
                } catch (e: Exception) {
                    when {
                        dataArr != null && "[]" == dataArr.toString() ||
                                dataArr != null && dataArr.length() > 0 -> {
                            //这种情况是一个空数组，但是声明的却不是一个数组
                            try {
                                @Suppress("UNCHECKED_CAST")
                                gson.fromJson<List<*>>(dataArr.toString(), type) as T
                            } catch (e: Exception) {
                                throw ResultException(code, code, message, response)
                            }
                        }
                        else -> {
                            throw ResultException(code, code, message, response)
                        }
                    }
                }
            }
            data == null && dataArr == null -> {
                //这种情况是请求成功，但是json不是合理的
                throw ResultException(successCode, code, message?: "json is illegal", response)
            }
            else -> {
                //抛一个自定义ResultException 传入失败时候的状态码，和信息
                throw ResultException(code, code, message, response)
            }
        }
    }
}