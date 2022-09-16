package com.microport.rxhttp.rxjava.interceptor;

import android.util.Log;

import com.microport.rxhttp.rxjava.RxBuilder;
import com.microport.rxhttp.utils.RxUtils;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author Buhuiming
 * @description: http拦截器，打印数据
 * @date :2022/9/16 16:47
 */
public class LoggingInterceptor {

    private final StringBuilder mMessage = new StringBuilder();

    public HttpLoggingInterceptor make(RxBuilder builder) {
        return new HttpLoggingInterceptor(
                message -> {
                    if(!builder.isLogOutPut()){
                        return;
                    }
                    // 请求或者响应开始
                    if (message.startsWith("--> POST") || message.startsWith("--> GET")) {
                        mMessage.delete(0,mMessage.length());
                        mMessage.setLength(0);
                    }

                    if(message.contains("&")){
                        RxUtils.logger(builder,"RetrofitCreateHelper-> ", stringToKeyValue(message));
                    }
                    // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
                    if ((message.startsWith("{") && message.endsWith("}"))
                            || (message.startsWith("[") && message.endsWith("]"))) {
                        Log.e("RetrofitCreateHelper-> ", replaceBlank(message) + "\n");
                    }
                    mMessage.append(message.concat("\n"));
                    // 响应结束，打印整条日志
                    if (message.startsWith("<-- END HTTP")) {
                        Log.e("RetrofitCreateHelper-> ", mMessage.toString());
                    }
                }).setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    /**
     *  去除字符串中的空格、回车、换行符、制表符
     */
    private String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    private String stringToKeyValue(String msg){
        StringBuilder mMessage = new StringBuilder();
        try {
            msg = URLDecoder.decode(msg, "UTF-8");
            mMessage.append("\n");
            if(msg.contains("?") && !msg.endsWith("?")) {
                mMessage.append(msg.substring(0, msg.lastIndexOf("?")));
                msg = msg.substring(msg.lastIndexOf("?") + 1);
                mMessage.append("\n");
            }
            for (String key : msg.split("&")) {
                mMessage.append(key);
                mMessage.append("\n");
            }
            mMessage.append("\n");
        }catch (Exception e){
            mMessage.append("");
        }
        return mMessage.toString().replace("=","  =  ");
    }
}
