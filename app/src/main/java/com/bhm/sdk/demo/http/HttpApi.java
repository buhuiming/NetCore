package com.bhm.sdk.demo.http;

import com.bhm.sdk.demo.entity.DoGetEntity;
import com.bhm.sdk.demo.entity.DoPostEntity;
import com.bhm.sdk.demo.entity.UpLoadEntity;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by bhm on 2018/5/11.
 */

public interface HttpApi {

    @GET("/api/4/news/latest")
    Observable<DoGetEntity> getData(@Header("token") String token,
                                    @Query("type")  String type);

    @FormUrlEncoded
    @POST("apiv2/app/getCOSToken")
    Observable<DoPostEntity> getDataPost(@Field("_api_key") String api_key, @Field("buildType") String buildType);

    /*上传文件*/
    @Multipart
    @POST("apiv1/app/upload")
    Observable<UpLoadEntity> upload(
            @Part("uKey") RequestBody uKey,
            @Part("_api_key") RequestBody api_key,
            @Part MultipartBody.Part file);

    /*上传文件*/
//    @Multipart //不需要这个
    @POST("common/update-avatar")
    Observable<UpLoadEntity> upload(
            @Header("Authorization") String token,
            @Body MultipartBody body);//文件和字段一起

    /*下载*/
    @Streaming
    @GET
    Observable<ResponseBody> downLoad(@Header("RANGE") String range, @Url String url);
}
