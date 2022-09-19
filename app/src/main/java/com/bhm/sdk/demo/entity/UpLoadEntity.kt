package com.bhm.sdk.demo.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by bhm on 2022/9/15.
 */
@Suppress("unused")
class UpLoadEntity : Serializable {
    @SerializedName("code")
    var code = 0

    @SerializedName("message")
    var msg: String? = null

    @SerializedName("data")
    var data: DataEntity? = null

    class DataEntity : Serializable {
        @SerializedName("appKey")
        val appKey: String? = null

        @SerializedName("userKey")
        val userKey: String? = null

        @SerializedName("appType")
        val appType: String? = null

        @SerializedName("appIsLastest")
        val appIsLastest: String? = null

        @SerializedName("appFileSize")
        val appFileSize: String? = null

        @SerializedName("appName")
        val appName: String? = null

        @SerializedName("appVersion")
        val appVersion: String? = null

        @SerializedName("appVersionNo")
        val appVersionNo: String? = null

        @SerializedName("appBuildVersion")
        val appBuildVersion: String? = null

        @SerializedName("appIdentifier")
        val appIdentifier: String? = null

        @SerializedName("appIcon")
        val appIcon: String? = null

        @SerializedName("appDescription")
        val appDescription: String? = null

        @SerializedName("appUpdateDescription")
        val appUpdateDescription: String? = null

        @SerializedName("appScreenshots")
        val appScreenshots: String? = null

        @SerializedName("appShortcutUrl")
        val appShortcutUrl: String? = null

        @SerializedName("appCreated")
        val appCreated: String? = null

        @SerializedName("appUpdated")
        val appUpdated: String? = null

        @SerializedName("appQRCodeURL")
        val appQRCodeURL: String? = null
    }
}