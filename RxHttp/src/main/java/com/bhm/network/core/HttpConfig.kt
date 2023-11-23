package com.bhm.network.core

import com.bhm.network.base.HttpLoadingDialog
import com.bhm.network.define.CODE_KEY
import com.bhm.network.define.DATA_KEY
import com.bhm.network.define.MESSAGE_KEY
import com.bhm.network.define.OK_CODE
import okhttp3.OkHttpClient

/**
 * Created by bhm on 2023/5/6.
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
        internal var parseDataKey: Boolean = false

        /**
         * 设置请求loading页面
         */
        fun setLoadingDialog(setDialog: HttpLoadingDialog?) = apply {
            dialog = setDialog
        }

        /** 不推荐使用，使用此方法，将取消默认的设置，包括但不限于日志，缓存，下载，上传，网络，SSL。
         * @param setOkHttpClient
         */
        @Deprecated("使用此方法，将取消默认的设置，包括但不限于日志，缓存，下载，上传，网络，SSL。",
            ReplaceWith("apply { this.okHttpClient = okHttpClient }")
        )
        fun setOkHttpClient(setOkHttpClient: OkHttpClient?) = apply {
            okHttpClient = setOkHttpClient
        }

        /**
         * 设置请求超时 单位秒
         * @param readTimeOut 读取超时
         * @param connectTimeOut 连接超时
         */
        fun setHttpTimeOut(readTimeOut: Int, connectTimeOut: Int) = apply {
            this.readTimeOut = readTimeOut
            this.connectTimeOut = connectTimeOut
        }

        /**
         * 设置请求默认的header
         */
        fun setDefaultHeader(defaultHeader: HashMap<String, String>?) = apply {
            this.defaultHeader = defaultHeader
        }

        /**
         * 设置请求loading页面的属性
         * @param isShowDialog 是否显示loading页面 true：显示，false：不显示。默认为false
         * @param cancelable 按返回键是否关闭loading页面，true：关闭，false：不关闭(拦截)。默认为false
         * @param dialogDismissInterruptRequest loading页面关闭后，是否终止请求 true：终止请求，false：不终止请求。默认为true
         */
        fun setDialogAttribute(
            isShowDialog: Boolean,
            cancelable: Boolean,
            dialogDismissInterruptRequest: Boolean
        ) = apply {
            this.isShowDialog = isShowDialog
            this.cancelable = cancelable
            this.dialogDismissInterruptRequest = dialogDismissInterruptRequest
        }

        /**
         * 是否使用Toast提示，默认为false
         */
        fun isDefaultToast(defaultToast: Boolean) = apply {
            isDefaultToast = defaultToast
        }

        /**
         * 设置请求loading页面提示语
         */
        fun setLoadingTitle(loadingTitle: String?) = apply {
            this.loadingTitle = loadingTitle
        }

        /**
         * 是否输出日志，默认false不输出
         */
        fun isLogOutPut(logOutPut: Boolean) = apply {
            isLogOutPut = logOutPut
        }

        /**
         * 设置下载文件的属性
         * @param mFilePath 文件路径，以\结尾
         * @param mFileName 文件名称
         * @param mIsAppendWrite 是否追加写入 true为追加写入
         * @param mWrittenLength 原文件的长度
         */
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

        /**
         * 设置请求成功/失败之后，再过[delaysProcessLimitTimeMillis]秒后去处理结果
         */
        fun setDelaysProcessLimitTimeMillis(delaysProcessLimitTimeMillis: Long) = apply {
            this.delaysProcessLimitTimeMillis = delaysProcessLimitTimeMillis
        }

        /**
         * 指定超时，在规定的时间内[specifiedTimeoutMillis]没有返回结果(成功/失败)，比如提示用户网络环境不给力的情况
         */
        fun setSpecifiedTimeoutMillis(specifiedTimeoutMillis: Long) = apply {
            this.specifiedTimeoutMillis = specifiedTimeoutMillis
        }

        /**
         * 设置请求json的解析结构体
         * @param messageKey 提示语字段名
         * @param codeKey 返回码字段名
         * @param dataKey 数据字段名
         * @param successCode 成功时的状态码值，默认[OK_CODE]
         * @param parseDataKey 解析整个Json还是解析dataKey部分，默认false
         */
        fun setJsonCovertKey(messageKey: String = MESSAGE_KEY,
                             codeKey: String = CODE_KEY,
                             dataKey: String = DATA_KEY,
                             successCode: Int = OK_CODE,
                             parseDataKey: Boolean = false) = apply {
            this.messageKey = messageKey
            this.codeKey = codeKey
            this.dataKey = dataKey
            this.successCode = successCode
            this.parseDataKey = parseDataKey
        }

        fun build(): HttpConfig {
            return HttpConfig(this)
        }
    }

    companion object {
        @JvmStatic
        var httpLoadingDialog: HttpLoadingDialog? = null
            internal set
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
        var parseDataKey: Boolean = false
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
        parseDataKey = builder.parseDataKey
    }
}