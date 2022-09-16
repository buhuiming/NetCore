package com.bhm.sdk.demo.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Buhuiming
 */
public class DoPostEntity implements Serializable {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private DataEntity data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public DataEntity getData() {
        return data;
    }

    public static final class DataEntity implements Serializable {
        @SerializedName("key")
        private String key;
        @SerializedName("endpoint")
        private String endpoint;
        @SerializedName("params")
        private Object params;

        public String getKey() {
            return key;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public Object getParams() {
            return params;
        }
    }
}
