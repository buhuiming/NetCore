@file:Suppress("unused")

package com.bhm.network.core

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bhm.network.base.HttpLoadingDialog
import com.bhm.network.core.HttpConfig.Companion.cancelable
import com.bhm.network.core.HttpConfig.Companion.httpLoadingDialog
import com.bhm.network.core.HttpConfig.Companion.writtenLength
import com.bhm.network.core.callback.CallBackImp
import com.bhm.network.define.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient

/**
 * Created by bhm on 2023/5/6.
 */
class HttpOptions(private val builder: Builder) {
    var currentRequestDateTamp: Long = 0
    val activity: FragmentActivity
        get() = builder.activity
    var callBack: CallBackImp<*>? = null
    val isShowDialog: Boolean
        get() = builder.isShowDialog
    val isLogOutPut: Boolean
        get() = builder.isLogOutPut
    val dialog: HttpLoadingDialog?
        get() = builder.dialog
    val isDefaultToast: Boolean
        get() = builder.isDefaultToast
    val readTimeOut: Int
        get() = builder.readTimeOut
    val connectTimeOut: Int
        get() = builder.connectTimeOut
    val okHttpClient: OkHttpClient?
        get() = builder.okHttpClient
    val filePath: String?
        get() = builder.filePath
    val fileName: String?
        get() = builder.fileName
    val isCancelable: Boolean
        get() = builder.isCancelable
    val isDialogDismissInterruptRequest: Boolean
        get() = builder.isDialogDismissInterruptRequest
    val isAppendWrite: Boolean
        get() = builder.isAppendWrite
    val jobKey: String
        get() = builder.jobKey

    fun writtenLength(): Long {
        return builder.writtenLength
    }

    val loadingTitle: String?
        get() = builder.loadingTitle
    val defaultHeader: HashMap<String, String>?
        get() = builder.defaultHeader
    val delaysProcessLimitTimeMillis: Long
        get() = builder.delaysProcessLimitTimeMillis
    val specifiedTimeoutMillis: Long
        get() = builder.specifiedTimeoutMillis
    val messageKey: String
        get() = builder.messageKey
    val codeKey: String
        get() = builder.codeKey
    val dataKey: String
        get() = builder.dataKey
    val successCode: Int
        get() = builder.successCode
    val parseDataKey: Boolean
        get() = builder.parseDataKey

    class Builder(val activity: FragmentActivity) {
        internal var isShowDialog = HttpConfig.isShowDialog
        internal var isCancelable = cancelable()
        internal var dialog = httpLoadingDialog
        internal var isDefaultToast = HttpConfig.isDefaultToast
        internal var readTimeOut = HttpConfig.readTimeOut
        internal var connectTimeOut = HttpConfig.connectTimeOut
        internal var okHttpClient = HttpConfig.okHttpClient
        internal var isLogOutPut = HttpConfig.isLogOutPut
        internal var filePath = HttpConfig.filePath
        internal var fileName = HttpConfig.fileName
        internal var writtenLength = writtenLength()
        internal var isAppendWrite = HttpConfig.isAppendWrite
        internal var loadingTitle = HttpConfig.loadingTitle
        internal var isDialogDismissInterruptRequest = HttpConfig.isDialogDismissInterruptRequest
        internal var defaultHeader = HttpConfig.defaultHeader
        internal var delaysProcessLimitTimeMillis = HttpConfig.delaysProcessLimitTimeMillis
        internal var specifiedTimeoutMillis = HttpConfig.specifiedTimeoutMillis
        internal var messageKey = HttpConfig.messageKey
        internal var codeKey = HttpConfig.codeKey
        internal var dataKey = HttpConfig.dataKey
        internal var successCode = HttpConfig.successCode
        internal var parseDataKey = HttpConfig.parseDataKey
        internal var jobKey = System.currentTimeMillis().toString()

        init {
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    if (isDialogDismissInterruptRequest) {
                        JobManager.get().removeJob(jobKey)
                    }
                    if (activity.isTaskRoot) {
                        JobManager.get().clear()
                    }
                    dialog?.close()
                    dialog = null
                    activity.lifecycle.removeObserver(this)
                }
            })
        }

        /**
         * 设置请求loading页面
         */
        fun setLoadingDialog(dialog: HttpLoadingDialog?) = apply {
            this.dialog = dialog
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
            isCancelable = cancelable
            this.isDialogDismissInterruptRequest = dialogDismissInterruptRequest
        }

        /**
         * 是否使用Toast提示，默认为false
         */
        fun setIsDefaultToast(isDefaultToast: Boolean) = apply {
            this.isDefaultToast = isDefaultToast
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

        /** 不推荐使用，使用此方法，将取消默认的设置，包括但不限于日志，缓存，下载，上传，网络，SSL。
         * @param okHttpClient
         */
        @Deprecated("使用此方法，将取消默认的设置，包括但不限于日志，缓存，下载，上传，网络，SSL。",
            ReplaceWith("apply { this.okHttpClient = okHttpClient }")
        )
        fun setOkHttpClient(okHttpClient: OkHttpClient?) = apply {
            this.okHttpClient = okHttpClient
        }

        /**
         * 是否输出日志，默认false不输出
         */
        fun setIsLogOutPut(isLogOutPut: Boolean) = apply {
            this.isLogOutPut = isLogOutPut
        }

        /**
         * 设置请求loading页面提示语
         */
        fun setLoadingTitle(loadingTitle: String?) = apply {
            this.loadingTitle = loadingTitle
        }

        /**
         * 设置请求默认的header
         */
        fun setDefaultHeader(defaultHeader: HashMap<String, String>?) = apply {
            this.defaultHeader = defaultHeader
        }

        /**
         * 设置下载文件的属性
         * @param mFilePath 文件路径，以\结尾
         * @param mFileName 文件名称
         * @param mAppendWrite 是否追加写入 true为追加写入
         * @param mWrittenLength 原文件的长度
         */
        fun setDownLoadFileAtr(
            mFilePath: String?,
            mFileName: String?,
            mAppendWrite: Boolean,
            mWrittenLength: Long
        ) = apply {
            filePath = mFilePath
            fileName = mFileName
            this.isAppendWrite = mAppendWrite
            writtenLength = mWrittenLength
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

        fun build(): HttpOptions {
            return HttpOptions(this)
        }
    }

    companion object {
        @JvmStatic
        fun create(activity: FragmentActivity) = Builder(activity)

        @JvmStatic
        fun getDefaultHttpOptions(activity: FragmentActivity): HttpOptions {
            return create(activity)
                .setLoadingDialog(HttpLoadingDialog())
                .build()
        }
    }
}