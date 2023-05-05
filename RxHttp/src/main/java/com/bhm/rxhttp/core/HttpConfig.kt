package com.bhm.rxhttp.core

import com.bhm.rxhttp.base.HttpLoadingDialog
import com.bhm.rxhttp.define.CODE_KEY
import com.bhm.rxhttp.define.DATA_KEY
import com.bhm.rxhttp.define.MESSAGE_KEY
import com.bhm.rxhttp.define.OK_CODE
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
        internal var delaysProcessLimitTimeMillis: Long = 0
        internal var specifiedTimeoutMillis: Long = 0
        internal var messageKey: String = MESSAGE_KEY
        internal var codeKey: String = CODE_KEY
        internal var dataKey: String = DATA_KEY
        internal var successCode: Int = OK_CODE

        fun setLoadingDialog(setDialog: HttpLoadingDialog?) = apply {
            dialog = setDialog
        }

        /** 不推荐使用，使用此方法，将取消默认的设置，包括但不限于日志，缓存，下载，上传，网络，SSL。
         * @param setOkHttpClient
         * @return
         */
        fun setOkHttpClient(setOkHttpClient: OkHttpClient?) = apply {
            okHttpClient = setOkHttpClient
        }

        fun setConnectTimeOut(setConnectTimeOut: Int) = apply {
            connectTimeOut = setConnectTimeOut
        }

        fun setReadTimeOut(setReadTimeOut: Int) = apply {
            readTimeOut = setReadTimeOut
        }

        fun setDefaultHeader(defaultHeader: HashMap<String, String>?) = apply {
            this.defaultHeader = defaultHeader
        }

        fun setDialogAttribute(
            isShowDialog: Boolean,
            cancelable: Boolean,
            dialogDismissInterruptRequest: Boolean
        ) = apply {
            this.isShowDialog = isShowDialog
            this.cancelable = cancelable
            this.dialogDismissInterruptRequest = dialogDismissInterruptRequest
        }

        fun isDefaultToast(defaultToast: Boolean) = apply {
            isDefaultToast = defaultToast
        }

        fun setLoadingTitle(loadingTitle: String?) = apply {
            this.loadingTitle = loadingTitle
        }

        fun isLogOutPut(logOutPut: Boolean) = apply {
            isLogOutPut = logOutPut
        }

        fun setDownLoadFileAtr(
            mFilePath: String?,
            mFileName: String?,
            mIsAppendWrite: Boolean,
            mWrittenLength: Long
        ) = apply {
            filePath = mFilePath
            fileName = mFileName
            writtenLength = mWrittenLength
            isAppendWrite = mIsAppendWrite
        }

        fun setDelaysProcessLimitTimeMillis(delaysProcessLimitTimeMillis: Long) = apply {
            this.delaysProcessLimitTimeMillis = delaysProcessLimitTimeMillis
        }

        fun setSpecifiedTimeoutMillis(specifiedTimeoutMillis: Long) = apply {
            this.specifiedTimeoutMillis = specifiedTimeoutMillis
        }

        fun setJsonCovertKey(messageKey: String = MESSAGE_KEY,
                             codeKey: String = CODE_KEY,
                             dataKey: String = DATA_KEY,
                             successCode: Int = OK_CODE) = apply {
            this.messageKey = messageKey
            this.codeKey = codeKey
            this.dataKey = dataKey
            this.successCode = successCode
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
        var delaysProcessLimitTimeMillis: Long = 0 //请求有结果之后，延迟处理时间 单位毫秒
            private set
        @JvmStatic
        var specifiedTimeoutMillis: Long = 0 //指定超时，在规定的时间内没有结果(成功/失败)。用在提示用户网络环境不给力的情况
            private set

        @JvmStatic
        var messageKey: String = MESSAGE_KEY
            private set

        @JvmStatic
        var codeKey: String = CODE_KEY
            private set

        @JvmStatic
        var dataKey: String = DATA_KEY
            private set

        @JvmStatic
        var successCode: Int = OK_CODE
            private set

        @JvmStatic
        fun create() = Builder()

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
        delaysProcessLimitTimeMillis = builder.delaysProcessLimitTimeMillis
        specifiedTimeoutMillis = builder.specifiedTimeoutMillis
        messageKey = builder.messageKey
        codeKey = builder.codeKey
        dataKey = builder.dataKey
        successCode = builder.successCode
    }
}