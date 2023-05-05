package com.bhm.rxhttp.core

import android.annotation.SuppressLint
import android.widget.Toast
import com.bhm.rxhttp.base.HttpActivity
import com.bhm.rxhttp.base.HttpLoadingDialog
import com.bhm.rxhttp.core.HttpConfig.Companion.cancelable
import com.bhm.rxhttp.core.HttpConfig.Companion.httpLoadingDialog
import com.bhm.rxhttp.core.HttpConfig.Companion.writtenLength
import com.bhm.rxhttp.core.callback.CallBackImp
import com.bhm.rxhttp.define.*
import com.bhm.rxhttp.define.CommonUtil.logger
import com.google.gson.JsonSyntaxException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.DisposableContainer
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Created by bhm on 2022/9/15.
 */
@Suppress("unused")
class HttpBuilder(private val builder: Builder) {
    private var currentRequestDateTamp: Long = 0
    val activity: HttpActivity
        get() = builder.activity
    var callBack: CallBackImp<*>? = null
        private set
    val isShowDialog: Boolean
        get() = builder.isShowDialog
    val isLogOutPut: Boolean
        get() = builder.isLogOutPut
    val dialog: HttpLoadingDialog?
        get() = builder.dialog
    private val isDefaultToast: Boolean
        get() = builder.isDefaultToast
    val disposeManager: DisposeManager?
        get() = builder.disposeManager
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

    fun writtenLength(): Long {
        return builder.writtenLength
    }

    val loadingTitle: String?
        get() = builder.loadingTitle
    val defaultHeader: HashMap<String, String>?
        get() = builder.defaultHeader
    private val delaysProcessLimitTimeMillis: Long
        get() = builder.delaysProcessLimitTimeMillis
    private val specifiedTimeoutMillis: Long
        get() = builder.specifiedTimeoutMillis
    val messageKey: String
        get() = builder.messageKey
    val codeKey: String
        get() = builder.codeKey
    val dataKey: String
        get() = builder.dataKey
    val successCode: Int
        get() = builder.successCode

    /*
    *  设置请求回调
    */
    fun <T: Any> enqueue(observable: Observable<T>, callBack: CallBackImp<T>?): Disposable {
        this.callBack = callBack
        val disposable = observable
            .compose(builder.activity.bindToLifecycle()) //管理生命周期
            .compose(rxSchedulerHelper()) //发布事件io线程
            .subscribe(
                getBaseConsumer(callBack),
                getThrowableConsumer(callBack),
                getDefaultAction(callBack),
                disposableContainer
            )
        currentRequestDateTamp = System.currentTimeMillis()
        //做准备工作
        callBack?.onStart(disposable, specifiedTimeoutMillis)
        builder.disposeManager?.add(disposable)
        return disposable
    }

    /*
    *  设置上传文件回调
    */
    fun <T: Any> uploadEnqueue(observable: Observable<T>, callBack: CallBackImp<T>?): Disposable {
        return this.enqueue(observable, callBack)
    }

    /*
    *  设置文件下载回调
    */
    fun <T: Any> downloadEnqueue(observable: Observable<ResponseBody>, callBack: CallBackImp<T>?): Disposable {
        this.callBack = callBack
        val disposable = observable
            .compose(builder.activity.bindToLifecycle()) //管理生命周期
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .map { responseBody -> responseBody.byteStream() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}) { throwable ->
                callBack?.onFail(throwable)
                if (null != builder.dialog && builder.isShowDialog) {
                    builder.dialog?.dismissLoading(builder.activity)
                }
                builder.disposeManager?.removeDispose()
            }
        callBack?.onStart(disposable, specifiedTimeoutMillis)
        builder.disposeManager?.add(disposable)
        return disposable
    }

    @SuppressLint("CheckResult")
    private fun <T: Any> getBaseConsumer(callBack: CallBackImp<T>?): Consumer<T> {
        return Consumer { t ->
            if (System.currentTimeMillis() - currentRequestDateTamp <= delaysProcessLimitTimeMillis) {
                Observable.timer(delaysProcessLimitTimeMillis, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { doBaseConsumer(callBack, t) }
            } else {
                doBaseConsumer(callBack, t)
            }
        }
    }

    private fun <T: Any> doBaseConsumer(callBack: CallBackImp<T>?, t: T) {
        callBack?.onSuccess(t)
        if (isShowDialog && null != dialog) {
            dialog?.dismissLoading(activity)
        }
    }

    @SuppressLint("CheckResult")
    private fun <T: Any> getThrowableConsumer(callBack: CallBackImp<T>?): Consumer<Throwable> {
        return Consumer { e ->
            logger(this@HttpBuilder, "ThrowableConsumer-> ", e.message) //抛异常
            if (System.currentTimeMillis() - currentRequestDateTamp <= delaysProcessLimitTimeMillis) {
                Observable.timer(delaysProcessLimitTimeMillis, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { doThrowableConsumer(callBack, e) }
            } else {
                doThrowableConsumer(callBack, e)
            }
        }
    }

    private fun <T: Any> doThrowableConsumer(callBack: CallBackImp<T>?, e: Throwable) {
        callBack?.onFail(e)
        if (isShowDialog && null != dialog) {
            dialog?.dismissLoading(activity)
        }
        if (isDefaultToast) {
            if (e is HttpException) {
                if (e.code() == 404) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                } else if (e.code() == 504) {
                    Toast.makeText(activity, "请检查网络连接！", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "请检查网络连接！", Toast.LENGTH_SHORT).show()
                }
            } else if (e is IndexOutOfBoundsException
                || e is NullPointerException
                || e is JsonSyntaxException
                || e is IllegalStateException
                || e is ResultException
            ) {
                Toast.makeText(activity, "数据异常，解析失败！", Toast.LENGTH_SHORT).show()
            } else if (e is TimeoutException) {
                Toast.makeText(activity, "连接超时，请重试！", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "请求失败，请稍后再试！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** 最终结果的处理
     * @return 和getThrowableConsumer互斥
     */
    private fun <T: Any> getDefaultAction(callBack: CallBackImp<T>?): Action {
        return Action { callBack?.onComplete() }
    }

    /** 做准备工作
     * @return
     */
    private val disposableContainer: DisposableContainer
        get() = CompositeDisposable()

    class Builder(val activity: HttpActivity) {
        internal var disposeManager: DisposeManager? = activity.disposeManager
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

        fun setLoadingDialog(dialog: HttpLoadingDialog?) = apply {
            this.dialog = dialog
        }

        fun setDialogAttribute(
            isShowDialog: Boolean,
            cancelable: Boolean,
            dialogDismissInterruptRequest: Boolean
        ) = apply {
            this.isShowDialog = isShowDialog
            isCancelable = cancelable
            this.isDialogDismissInterruptRequest = dialogDismissInterruptRequest
        }

        fun setIsDefaultToast(isDefaultToast: Boolean) = apply {
            this.isDefaultToast = isDefaultToast
        }

        fun setHttpTimeOut(readTimeOut: Int, connectTimeOut: Int) = apply {
            this.readTimeOut = readTimeOut
            this.connectTimeOut = connectTimeOut
        }

        /** 不推荐使用，使用此方法，将取消默认的设置，包括但不限于日志，缓存，下载，上传，网络，SSL。
         * @param okHttpClient
         * @return
         */
        @Deprecated("")
        fun setOkHttpClient(okHttpClient: OkHttpClient?) = apply {
            this.okHttpClient = okHttpClient
        }

        fun setIsLogOutPut(isLogOutPut: Boolean) = apply {
            this.isLogOutPut = isLogOutPut
        }

        fun setLoadingTitle(loadingTitle: String?) = apply {
            this.loadingTitle = loadingTitle
        }

        fun setDefaultHeader(defaultHeader: HashMap<String, String>?) = apply {
            this.defaultHeader = defaultHeader
        }

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

        fun build(): HttpBuilder {
            return HttpBuilder(this)
        }
    }

    companion object {
        @JvmStatic
        fun create(activity: HttpActivity) = Builder(activity)

        @JvmStatic
        fun getDefaultBuilder(activity: HttpActivity): HttpBuilder {
            return create(activity)
                .setLoadingDialog(HttpLoadingDialog.defaultDialog)
                .build()
        }

        /**
         * 统一线程处理
         * 发布事件io线程，接收事件主线程
         */
        @JvmStatic
        private fun <T : Any> rxSchedulerHelper(): ObservableTransformer<T, T> { //compose处理线程
            return ObservableTransformer { upstream ->
                upstream.subscribeOn(Schedulers.io()) //读写文件、读写数据库、网络信息交互等
                    .observeOn(AndroidSchedulers.mainThread()) //指定的是它之后的操作所在的线程。
            }
        }
    }
}