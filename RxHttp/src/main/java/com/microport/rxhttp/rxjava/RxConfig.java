package com.microport.rxhttp.rxjava;


import com.microport.rxhttp.utils.RxLoadingDialog;

import java.util.HashMap;

import okhttp3.OkHttpClient;

/**
 * Created by bhm on 2022/9/15.
 */

public class RxConfig {

    private static RxLoadingDialog dialog;
    private static boolean isShowDialog;
    private static boolean cancelable;
    private static boolean isDefaultToast;
    private static int readTimeOut;
    private static int connectTimeOut;
    private static OkHttpClient okHttpClient;
    private static boolean isLogOutPut = false;
    private static String filePath;
    private static String fileName;
    private static long writtenLength;
    private static boolean isAppendWrite;
    private static String loadingTitle = "正在请求...";
    private static boolean dialogDismissInterruptRequest = true;
    private static HashMap<String, String> defaultHeader = new HashMap<>();
    private static long delaysProcessLimitTime;//请求有结果之后，延迟处理时间 单位毫秒

    public RxConfig(Builder builder){
        dialog = builder.dialog;
        isShowDialog = builder.isShowDialog;
        cancelable = builder.cancelable;
        isDefaultToast = builder.isDefaultToast;
        readTimeOut = builder.readTimeOut;
        connectTimeOut = builder.connectTimeOut;
        okHttpClient = builder.okHttpClient;
        isLogOutPut = builder.isLogOutPut;
        filePath = builder.filePath;
        fileName = builder.fileName;
        writtenLength = builder.writtenLength;
        isAppendWrite = builder.isAppendWrite;
        loadingTitle = builder.loadingTitle;
        dialogDismissInterruptRequest = builder.dialogDismissInterruptRequest;
        defaultHeader = builder.defaultHeader;
        delaysProcessLimitTime = builder.delaysProcessLimitTime;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private RxLoadingDialog dialog;
        private boolean isShowDialog;
        private boolean cancelable;
        private boolean dialogDismissInterruptRequest;
        private boolean isDefaultToast;
        private int readTimeOut;
        private int connectTimeOut;
        private OkHttpClient okHttpClient;
        private boolean isLogOutPut = false;
        private String filePath;
        private String fileName;
        private long writtenLength;
        private boolean isAppendWrite;
        private String loadingTitle;
        private HashMap<String, String> defaultHeader;
        private long delaysProcessLimitTime;

        public Builder setRxLoadingDialog(RxLoadingDialog setDialog){
            dialog = setDialog;
            return this;
        }

        /** 不推荐使用，使用此方法，将取消默认的设置，包括但不限于日志，缓存，下载，上传，网络，SSL。
         * @param setOkHttpClient
         * @return
         */
        @Deprecated
        public Builder setOkHttpClient(OkHttpClient setOkHttpClient){
            okHttpClient = setOkHttpClient;
            return this;
        }

        public Builder setConnectTimeOut(int setConnectTimeOut){
            connectTimeOut = setConnectTimeOut;
            return this;
        }

        public Builder setReadTimeOut(int setReadTimeOut){
            readTimeOut = setReadTimeOut;
            return this;
        }

        public Builder setDefaultHeader(HashMap<String, String> defaultHeader){
            this.defaultHeader = defaultHeader;
            return this;
        }

        public Builder setDialogAttribute(boolean isShowDialog, boolean cancelable, boolean dialogDismissInterruptRequest){
            this.isShowDialog = isShowDialog;
            this.cancelable = cancelable;
            this.dialogDismissInterruptRequest = dialogDismissInterruptRequest;
            return this;
        }

        public Builder isDefaultToast(boolean defaultToast){
            isDefaultToast = defaultToast;
            return this;
        }

        public Builder setLoadingTitle(String loadingTitle){
            this.loadingTitle = loadingTitle;
            return this;
        }

        public Builder isLogOutPut(boolean logOutPut){
            isLogOutPut = logOutPut;
            return this;
        }

        public Builder setDownLoadFileAtr(String mFilePath, String mFileName, boolean mIsAppendWrite, long mWrittenLength){
            filePath = mFilePath;
            fileName = mFileName;
            writtenLength = mWrittenLength;
            isAppendWrite = mIsAppendWrite;
            return this;
        }

        public Builder setDelaysProcessLimitTime(long delaysProcessLimitTime1){
            delaysProcessLimitTime = delaysProcessLimitTime1;
            return this;
        }

        public RxConfig build(){
            return new RxConfig(this);
        }
    }

    public static RxLoadingDialog getRxLoadingDialog(){
        return dialog;
    }

    public static OkHttpClient getOkHttpClient(){
        return okHttpClient;
    }

    public static int getConnectTimeOut(){
        return connectTimeOut;
    }

    public static int getReadTimeOut(){
        return readTimeOut;
    }

    public static boolean isShowDialog(){
        return isShowDialog;
    }

    public static boolean cancelable(){
        return cancelable;
    }

    public static boolean isDefaultToast(){
        return isDefaultToast;
    }

    public static boolean isLogOutPut(){
        return isLogOutPut;
    }

    public static String getFilePath(){
        return filePath;
    }

    public static String getFileName(){
        return fileName;
    }

    public static long writtenLength(){
        return writtenLength;
    }

    public static boolean isAppendWrite(){
        return isAppendWrite;
    }

    public static String getLoadingTitle(){
        return loadingTitle;
    }

    public static boolean isDialogDismissInterruptRequest(){
        return dialogDismissInterruptRequest;
    }

    public static HashMap<String, String> getDefaultHeader(){
        return defaultHeader;
    }

    public static long getDelaysProcessLimitTime(){
        return delaysProcessLimitTime;
    }
}
