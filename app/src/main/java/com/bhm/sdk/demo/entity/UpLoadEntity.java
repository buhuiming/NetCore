package com.bhm.sdk.demo.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by bhm on 2018/5/29.
 */

public class UpLoadEntity implements Serializable {

    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private DataEntity data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return message;
    }

    public void setMsg(String msg) {
        this.message = msg;
    }

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public class DataEntity implements Serializable{
        @SerializedName("appKey")
        private String appKey;

        @SerializedName("userKey")
        private String userKey;

        @SerializedName("appType")
        private String appType;

        @SerializedName("appIsLastest")
        private String appIsLastest;

        @SerializedName("appFileSize")
        private String appFileSize;

        @SerializedName("appName")
        private String appName;

        @SerializedName("appVersion")
        private String appVersion;

        @SerializedName("appVersionNo")
        private String appVersionNo;

        @SerializedName("appBuildVersion")
        private String appBuildVersion;

        @SerializedName("appIdentifier")
        private String appIdentifier;

        @SerializedName("appIcon")
        private String appIcon;

        @SerializedName("appDescription")
        private String appDescription;

        @SerializedName("appUpdateDescription")
        private String appUpdateDescription;

        @SerializedName("appScreenshots")
        private String appScreenshots;

        @SerializedName("appShortcutUrl")
        private String appShortcutUrl;

        @SerializedName("appCreated")
        private String appCreated;

        @SerializedName("appUpdated")
        private String appUpdated;

        @SerializedName("appQRCodeURL")
        private String appQRCodeURL;

        public String getAppKey() {
            return appKey;
        }

        public String getUserKey() {
            return userKey;
        }

        public String getAppType() {
            return appType;
        }

        public String getAppIsLastest() {
            return appIsLastest;
        }

        public String getAppFileSize() {
            return appFileSize;
        }

        public String getAppName() {
            return appName;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public String getAppVersionNo() {
            return appVersionNo;
        }

        public String getAppBuildVersion() {
            return appBuildVersion;
        }

        public String getAppIdentifier() {
            return appIdentifier;
        }

        public String getAppIcon() {
            return appIcon;
        }

        public String getAppDescription() {
            return appDescription;
        }

        public String getAppUpdateDescription() {
            return appUpdateDescription;
        }

        public String getAppScreenshots() {
            return appScreenshots;
        }

        public String getAppShortcutUrl() {
            return appShortcutUrl;
        }

        public String getAppCreated() {
            return appCreated;
        }

        public String getAppUpdated() {
            return appUpdated;
        }

        public String getAppQRCodeURL() {
            return appQRCodeURL;
        }
    }
}
