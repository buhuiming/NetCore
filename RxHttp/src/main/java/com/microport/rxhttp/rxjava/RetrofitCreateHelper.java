package com.microport.rxhttp.rxjava;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microport.rxhttp.rxjava.callback.LongDefaultAdapter;

import androidx.annotation.NonNull;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;


/**
 * Created by bhm on 2022/9/15.
 */
public class RetrofitCreateHelper {

    private final RxBuilder builder;

    public RetrofitCreateHelper(@NonNull RxBuilder builder){
        this.builder = builder;
    }

    public <T> T createApi(Class<T> clazz, String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(new GenerateOkHttpClient().make(builder))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(ResponseConverterFactory.create(getGsonBuilder()))
                .build();
        return retrofit.create(clazz);
    }

    private Gson getGsonBuilder(){
        return new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(Integer.class, new IntegerDefaultAdapter())
                .registerTypeAdapter(int.class, new IntegerDefaultAdapter())
                .registerTypeAdapter(Long.class, new LongDefaultAdapter())
                .registerTypeAdapter(long.class, new LongDefaultAdapter())
                .create();
    }

}
