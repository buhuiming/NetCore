package com.bhm.sdk.demo.http

import com.bhm.sdk.demo.entity.DoGetEntity
import com.bhm.sdk.demo.entity.DoPostEntity
import com.bhm.sdk.demo.entity.UpLoadEntity
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by bhm on 2022/9/15.
 */
interface HttpApi {
    @GET("/api/4/news/latest")
    fun getData(
        @Header("token") token: String?,
        @Query("type") type: String?
    ): Observable<DoGetEntity>

    @FormUrlEncoded
    @POST("apiv2/app/getCOSToken")
    fun getDataPost(
        @Field("_api_key") apiKey: String?,
        @Field("buildType") buildType: String?
    ): Observable<DoPostEntity>

    /*上传文件*/
    @Multipart
    @POST("apiv1/app/upload")
    fun upload(
        @Part("uKey") uKey: RequestBody?,
        @Part("_api_key") apiKey: RequestBody?,
        @Part file: MultipartBody.Part
    ): Observable<UpLoadEntity>

    /*下载*/
    @Streaming
    @GET
    fun downLoad(@Header("RANGE") range: String?, @Url url: String?): Observable<ResponseBody>
}