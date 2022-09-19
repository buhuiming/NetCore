package com.bhm.rxhttp.rxjava

import android.annotation.SuppressLint
import android.widget.Toast
import com.google.gson.JsonSyntaxException
import com.bhm.rxhttp.rxjava.RxConfig.Companion.cancelable
import com.bhm.rxhttp.rxjava.RxConfig.Companion.rxLoadingDialog
import com.bhm.rxhttp.rxjava.RxConfig.Companion.writtenLength
import com.bhm.rxhttp.rxjava.RxManager.Companion.rxSchedulerHelper
import com.bhm.rxhttp.rxjava.callback.CallBack
import com.bhm.rxhttp.rxjava.callback.RxStreamCallBackImp
import com.bhm.rxhttp.utils.ResultException
import com.bhm.rxhttp.utils.RxLoadingDialog
import com.bhm.rxhttp.utils.RxUtil.logger
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
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
class RxBuilder(private val builder: Builder) {
    var listener: RxStreamCallBackImp? = null
        private set
    private var currentRequestDateTamp: Long = 0
    val activity: RxAppCompatActivity
        get() = builder.activity
    val isShowDialog: Boolean
        get() = builder.isShowDialog
    val isLogOutPut: Boolean
        get() = builder.isLogOutPut
    val dialog: RxLoadingDialog?
        get() = builder.dialog
    private val isDefaultToast: Boolean
        get() = builder.isDefaultToast
    val rxManager: RxManager?
        get() = builder.rxManager
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
    private val delaysProcessLimitTime: Long
        get() = builder.delaysProcessLimitTime

    fun <T> createApi(cla: Class<T>, host: String): T {
        if (builder.isShowDialog && null != builder.dialog) {
            builder.dialog!!.showLoading(this)
        }
        return RetrofitCreateHelper(this)
            .createApi(cla, host)
    }

    /** 上传请求
     * @param cla
     * @param host 请求地址
     * @param listener
     * @return
     */
    fun <T> createApi(cla: Class<T>, host: String, listener: RxStreamCallBackImp?): T {
        if (null == listener) {
            throw NullPointerException("RxStreamCallBackImp(listener) can not be null!")
        }
        if (builder.isShowDialog && null != builder.dialog) {
            builder.dialog!!.showLoading(this)
        }
        this.listener = listener
        return RetrofitCreateHelper(this)
            .createApi(cla, host)
    }

    fun <T: Any> setCallBack(observable: Observable<T>, callBack: CallBack<T>?): Disposable {
        val disposable = observable.compose(builder.activity.bindToLifecycle()) //管理生命周期
            .compose(rxSchedulerHelper()) //发布事件io线程
            .subscribe(
                getBaseConsumer(callBack),
                getThrowableConsumer(callBack),
                getDefaultAction(callBack),
                disposableContainer
            )
        currentRequestDateTamp = System.currentTimeMillis()
        //做准备工作
        callBack?.onStart(disposable)
        if (builder.rxManager != null) {
            builder.rxManager!!.subscribe(disposable)
        }
        return disposable
    }

    @SuppressLint("CheckResult")
    private fun <T: Any> getBaseConsumer(callBack: CallBack<T>?): Consumer<T> {
        return Consumer { t ->
            if (System.currentTimeMillis() - currentRequestDateTamp <= delaysProcessLimitTime) {
                Observable.timer(delaysProcessLimitTime, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { doBaseConsumer(callBack, t) }
            } else {
                doBaseConsumer(callBack, t)
            }
        }
    }

    private fun <T> doBaseConsumer(callBack: CallBack<T>?, t: T) {
        callBack?.onSuccess(t)
        if (isShowDialog && null != dialog) {
            dialog!!.dismissLoading(activity)
        }
    }

    @SuppressLint("CheckResult")
    private fun <T> getThrowableConsumer(callBack: CallBack<T>?): Consumer<Throwable> {
        return Consumer { e ->
            logger(this@RxBuilder, "ThrowableConsumer-> ", e.message) //抛异常
            if (System.currentTimeMillis() - currentRequestDateTamp <= delaysProcessLimitTime) {
                Observable.timer(delaysProcessLimitTime, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { doThrowableConsumer(callBack, e) }
            } else {
                doThrowableConsumer(callBack, e)
            }
        }
    }

    private fun <T> doThrowableConsumer(callBack: CallBack<T>?, e: Throwable) {
        callBack?.onFail(e)
        if (isShowDialog && null != dialog) {
            dialog!!.dismissLoading(activity)
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
    private fun <T> getDefaultAction(callBack: CallBack<T>?): Action {
        return Action { callBack?.onComplete() }
    }

    /** 做准备工作
     * @return
     */
    private val disposableContainer: DisposableContainer
        get() = CompositeDisposable()

    fun beginDownLoad(observable: Observable<ResponseBody>): Disposable {
        return observable.subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .map { responseBody -> responseBody.byteStream() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ }) { throwable ->
                if (null != listener) {
                    listener!!.onFail(throwable.message)
                    if (null != builder.dialog && builder.isShowDialog) {
                        builder.dialog!!.dismissLoading(builder.activity)
                    }
                }
                if (builder.rxManager != null) {
                    builder.rxManager!!.removeObserver()
                }
            }
    }

    class Builder(val activity: RxAppCompatActivity) {
        internal var rxManager: RxManager? = null
        internal var isShowDialog = RxConfig.isShowDialog
        internal var isCancelable = cancelable()
        internal var dialog = rxLoadingDialog
        internal var isDefaultToast = RxConfig.isDefaultToast
        internal var readTimeOut = RxConfig.readTimeOut
        internal var connectTimeOut = RxConfig.connectTimeOut
        internal var okHttpClient = RxConfig.okHttpClient
        internal var isLogOutPut = RxConfig.isLogOutPut
        internal var filePath = RxConfig.filePath
        internal var fileName = RxConfig.fileName
        internal var writtenLength = writtenLength()
        internal var isAppendWrite = RxConfig.isAppendWrite
        internal var loadingTitle = RxConfig.loadingTitle
        internal var isDialogDismissInterruptRequest = RxConfig.isDialogDismissInterruptRequest
        internal var defaultHeader = RxConfig.defaultHeader
        internal var delaysProcessLimitTime = RxConfig.delaysProcessLimitTime

        fun setLoadingDialog(dialog: RxLoadingDialog?): Builder {
            this.dialog = dialog
            return this
        }

        fun setDialogAttribute(
            isShowDialog: Boolean,
            cancelable: Boolean,
            dialogDismissInterruptRequest: Boolean
        ): Builder {
            this.isShowDialog = isShowDialog
            isCancelable = cancelable
            this.isDialogDismissInterruptRequest = dialogDismissInterruptRequest
            return this
        }

        fun setIsDefaultToast(isDefaultToast: Boolean, rxManager: RxManager?): Builder {
            this.isDefaultToast = isDefaultToast
            this.rxManager = rxManager
            return this
        }

        fun setRxManager(rxManager: RxManager?): Builder {
            this.rxManager = rxManager
            return this
        }

        fun setHttpTimeOut(readTimeOut: Int, connectTimeOut: Int): Builder {
            this.readTimeOut = readTimeOut
            this.connectTimeOut = connectTimeOut
            return this
        }

        /** 不推荐使用，使用此方法，将取消默认的设置，包括但不限于日志，缓存，下载，上传，网络，SSL。
         * @param okHttpClient
         * @return
         */
        @Deprecated("")
        fun setOkHttpClient(okHttpClient: OkHttpClient?): Builder {
            this.okHttpClient = okHttpClient
            return this
        }

        fun setIsLogOutPut(isLogOutPut: Boolean): Builder {
            this.isLogOutPut = isLogOutPut
            return this
        }

        fun setLoadingTitle(loadingTitle: String?): Builder {
            this.loadingTitle = loadingTitle
            return this
        }

        fun setDefaultHeader(defaultHeader: HashMap<String, String>?): Builder {
            this.defaultHeader = defaultHeader
            return this
        }

        fun setDownLoadFileAtr(
            mFilePath: String?,
            mFileName: String?,
            mAppendWrite: Boolean,
            mWrittenLength: Long
        ): Builder {
            filePath = mFilePath
            fileName = mFileName
            this.isAppendWrite = mAppendWrite
            writtenLength = mWrittenLength
            return this
        }

        fun setDelaysProcessLimitTime(delaysProcessLimitTime1: Long): Builder {
            delaysProcessLimitTime = delaysProcessLimitTime1
            return this
        }

        fun bindRx(): RxBuilder {
            return RxBuilder(this)
        }
    }

    companion object {
        @JvmStatic
        fun newBuilder(activity: RxAppCompatActivity): Builder {
            return Builder(activity)
        }
    }
}