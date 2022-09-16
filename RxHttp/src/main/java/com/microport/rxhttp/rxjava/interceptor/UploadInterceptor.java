package com.microport.rxhttp.rxjava.interceptor;

import com.microport.rxhttp.rxjava.RxBuilder;
import com.microport.rxhttp.rxjava.UpLoadRequestBody;

import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * @author Buhuiming
 * @description: 上传进度拦截器
 * @date :2022/9/16 17:00
 */
@SuppressWarnings("ConstantConditions")
public class UploadInterceptor {

    public Interceptor make(RxBuilder builder) {
        return chain -> {
            Request request = chain.request();
            if(null == request.body()){
                return chain.proceed(request);
            }
            Request build = request.newBuilder()
                    .method(request.method(),
                            new UpLoadRequestBody(request.body(), builder))
                    .build();
            return chain.proceed(build);
        };
    }
}
