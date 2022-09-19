package com.bhm.rxhttp.rxjava.interceptor

import android.util.Log
import com.bhm.rxhttp.rxjava.RxBuilder
import com.bhm.rxhttp.utils.RxUtil.logger
import okhttp3.logging.HttpLoggingInterceptor
import java.net.URLDecoder
import java.util.regex.Pattern

/**
 * @author Buhuiming
 * @description: http拦截器，打印数据
 * @date :2022/9/16 16:47
 */
class LoggingInterceptor {

    private val mMessage = StringBuilder()

    fun make(builder: RxBuilder): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message: String ->
            if (builder.isLogOutPut) {
                // 请求或者响应开始
                if (message.startsWith("--> POST") || message.startsWith("--> GET")) {
                    mMessage.delete(0, mMessage.length)
                    mMessage.setLength(0)
                }
                if (message.contains("&")) {
                    logger(builder, "RetrofitCreateHelper-> ", stringToKeyValue(message))
                }
                // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
                if (message.startsWith("{") && message.endsWith("}")
                    || message.startsWith("[") && message.endsWith("]")
                ) {
                    Log.e("RetrofitCreateHelper-> ", replaceBlank(message).trimIndent())
                }
                mMessage.append(message.trimIndent())
                // 响应结束，打印整条日志
                if (message.startsWith("<-- END HTTP")) {
                    Log.e("RetrofitCreateHelper-> ", mMessage.toString())
                }
            }
        }.setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符
     */
    private fun replaceBlank(str: String?): String {
        var dest = ""
        if (str != null) {
            val p = Pattern.compile("\\s*|\t|\r|\n")
            val m = p.matcher(str)
            dest = m.replaceAll("")
        }
        return dest
    }

    private fun stringToKeyValue(message: String): String {
        var msg = message
        val mMessage = StringBuilder()
        try {
            msg = URLDecoder.decode(msg, "UTF-8")
            mMessage.append("\n")
            if (msg.contains("?") && !msg.endsWith("?")) {
                mMessage.append(msg.substring(0, msg.lastIndexOf("?")))
                msg = msg.substring(msg.lastIndexOf("?") + 1)
                mMessage.append("\n")
            }
            for (key in msg.split("&").toTypedArray()) {
                mMessage.append(key)
                mMessage.append("\n")
            }
            mMessage.append("\n")
        } catch (e: Exception) {
            mMessage.append("")
        }
        return mMessage.toString().replace("=", "  =  ")
    }
}