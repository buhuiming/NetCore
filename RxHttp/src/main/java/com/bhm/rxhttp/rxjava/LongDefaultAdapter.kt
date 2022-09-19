package com.bhm.rxhttp.rxjava

import com.google.gson.*
import java.lang.reflect.Type

/**
 * 针对长整型的解析，先写一个解析适配器，实现JsonSerializer, JsonDeserializer
 * 重写解析方法，先尝试用String类型解析，如果等于空字符串”“或者null，则返回0值
 * 否则再尝试用长整型解析，并且catch数字格式异常转成Json解析异常抛出
 * Created by bhm on 2022/9/15.
 */
class LongDefaultAdapter : JsonSerializer<Long?>, JsonDeserializer<Long> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement, typeOfT: Type,
        context: JsonDeserializationContext
    ): Long {
        try {
            if (json.asString == null || json.asString == "") {
                return 0L
            }
        } catch (ignore: Exception) {
            return 0L
        }
        return try {
            json.asLong
        } catch (e: NumberFormatException) {
            0L
        }
    }

    override fun serialize(
        src: Long?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src)
    }
}