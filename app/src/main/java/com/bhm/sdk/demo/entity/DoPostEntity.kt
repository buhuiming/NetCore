package com.bhm.sdk.demo.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author Buhuiming
 */
@Suppress("unused")
class DoPostEntity : Serializable {
    @SerializedName("code")
    val code = 0

    @SerializedName("message")
    val message: String? = null

    @SerializedName("data")
    val data: DataEntity? = null

    class DataEntity : Serializable {
        @SerializedName("key")
        val key: String? = null

        @SerializedName("endpoint")
        val endpoint: String? = null

        @SerializedName("params")
        val params: Any? = null
    }
}