package com.bhm.rxhttp.adapter

import com.google.gson.JsonSerializer
import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import com.google.gson.JsonElement
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonPrimitive
import java.lang.Exception
import java.lang.NumberFormatException
import java.lang.reflect.Type

/**
 * 针对整型的解析，先写一个解析适配器，实现JsonSerializer, JsonDeserializer
 * 重写解析方法，先尝试用String类型解析，如果等于空字符串”“或者null，则返回0值
 * 否则再尝试用整型解析，并且catch数字格式异常转成Json解析异常抛出
 * Created by bhm on 2022/9/15.
 */
class IntegerDefaultAdapter : JsonSerializer<Int?>, JsonDeserializer<Int> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement, typeOfT: Type,
        context: JsonDeserializationContext
    ): Int {
        try {
            if (json.asString == null || json.asString == "") {
                return 0
            }
        } catch (ignore: Exception) {
            return 0
        }
        return try {
            json.asInt
        } catch (e: NumberFormatException) {
            0
        }
    }

    override fun serialize(
        src: Int?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src)
    }
}