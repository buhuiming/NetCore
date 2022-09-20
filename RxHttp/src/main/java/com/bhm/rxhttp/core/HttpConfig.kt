package com.bhm.rxhttp.core

import com.bhm.rxhttp.base.HttpLoadingDialog
import okhttp3.OkHttpClient

/**
 * Created by bhm on 2022/9/15.
 */
@Suppress("unused")
class HttpConfig(builder: Builder) {

    class Builder {
        internal var dialog: HttpLoadingDialog? = null
        internal var isShowDialog = false
        internal var cancelable = false
        internal var dialogDismissInterruptRequest = false
        internal var isDefaultToast = false
        internal var readTimeOut = 0
        internal var connectTimeOut = 0
        internal var okHttpClient: OkHttpClient? = null
        internal var isLogOutPut = false
        internal var filePath: String? = null
        internal var fileName: String? = null
        internal var writtenLength: Long = 0
        internal var isAppendWrite = false
        internal var loadingTitle: String? = null
        internal var defaultHeader: HashMap<String, String>? = null
        internal var delaysProcessLimitTime: Long = 0

        fun setLoadingDialog(setDialog: HttpLoadingDialog?): Builder {
            dialog = setDialog
            return this
        }

        /** 不推荐使用，使用此方法，将取消默认的设置，包括但不限于日志，缓存，下载，上传，网络，SSL。
         * @param setOkHttpClient
         * @return
         */
        fun setOkHttpClient(setOkHttpClient: OkHttpClient?): Builder {
            okHttpClient = setOkHttpClient
            return this
        }

        fun setConnectTimeOut(setConnectTimeOut: Int): Builder {
            connectTimeOut = setConnectTimeOut
            return this
        }

        fun setReadTimeOut(setReadTimeOut: Int): Builder {
            readTimeOut = setReadTimeOut
            return this
        }

        fun setDefaultHeader(defaultHeader: HashMap<String, String>?): Builder {
            this.defaultHeader = defaultHeader
            return this
        }

        fun setDialogAttribute(
            isShowDialog: Boolean,
            cancelable: Boolean,
            dialogDismissInterruptRequest: Boolean
        ): Builder {
            this.isShowDialog = isShowDialog
            this.cancelable = cancelable
            this.dialogDismissInterruptRequest = dialogDismissInterruptRequest
            return this
        }

        fun isDefaultToast(defaultToast: Boolean): Builder {
            isDefaultToast = defaultToast
            return this
        }

        fun setLoadingTitle(loadingTitle: String?): Builder {
            this.loadingTitle = loadingTitle
            return this
        }

        fun isLogOutPut(logOutPut: Boolean): Builder {
            isLogOutPut = logOutPut
            return this
        }

        fun setDownLoadFileAtr(
            mFilePath: String?,
            mFileName: String?,
            mIsAppendWrite: Boolean,
            mWrittenLength: Long
        ): Builder {
            filePath = mFilePath
            fileName = mFileName
            writtenLength = mWrittenLength
            isAppendWrite = mIsAppendWrite
            return this
        }

        fun setDelaysProcessLimitTime(delaysProcessLimitTime1: Long): Builder {
            delaysProcessLimitTime = delaysProcessLimitTime1
            return this
        }

        fun build(): HttpConfig {
            return HttpConfig(this)
        }
    }

    companion object {
        @JvmStatic
        var httpLoadingDialog: HttpLoadingDialog? = null
            private set
        @JvmStatic
        var isShowDialog: Boolean = false
            private set
        @JvmStatic
        private var cancelable: Boolean = false
        @JvmStatic
        var isDefaultToast: Boolean = false
            private set
        @JvmStatic
        var readTimeOut: Int = 0
            private set
        @JvmStatic
        var connectTimeOut: Int = 0
            private set
        @JvmStatic
        var okHttpClient: OkHttpClient? = null
            private set
        @JvmStatic
        var isLogOutPut = false
            private set
        @JvmStatic
        var filePath: String? = ""
            private set
        @JvmStatic
        var fileName: String? = ""
            private set
        @JvmStatic
        private var writtenLength: Long = 0
        @JvmStatic
        var isAppendWrite: Boolean = false
            private set
        @JvmStatic
        var loadingTitle: String? = "正在请求..."
            private set
        @JvmStatic
        var isDialogDismissInterruptRequest = true
            private set
        @JvmStatic
        var defaultHeader: HashMap<String, String>? = HashMap()
            private set
        @JvmStatic
        var delaysProcessLimitTime: Long = 0 //请求有结果之后，延迟处理时间 单位毫秒
            private set

        @JvmStatic
        fun create(): Builder {
            return Builder()
        }

        @JvmStatic
        fun cancelable(): Boolean {
            return cancelable
        }

        @JvmStatic
        fun writtenLength(): Long {
            return writtenLength
        }
    }

    init {
        httpLoadingDialog = builder.dialog
        isShowDialog = builder.isShowDialog
        cancelable = builder.cancelable
        isDefaultToast = builder.isDefaultToast
        readTimeOut = builder.readTimeOut
        connectTimeOut = builder.connectTimeOut
        okHttpClient = builder.okHttpClient
        isLogOutPut = builder.isLogOutPut
        filePath = builder.filePath
        fileName = builder.fileName
        writtenLength = builder.writtenLength
        isAppendWrite = builder.isAppendWrite
        loadingTitle = builder.loadingTitle
        isDialogDismissInterruptRequest = builder.dialogDismissInterruptRequest
        defaultHeader = builder.defaultHeader
        delaysProcessLimitTime = builder.delaysProcessLimitTime
    }
}