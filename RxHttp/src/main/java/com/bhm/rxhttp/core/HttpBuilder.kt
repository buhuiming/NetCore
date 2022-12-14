package com.bhm.rxhttp.core

import android.annotation.SuppressLint
import android.widget.Toast
import com.bhm.rxhttp.base.HttpActivity
import com.bhm.rxhttp.base.HttpLoadingDialog
import com.bhm.rxhttp.core.HttpConfig.Companion.cancelable
import com.bhm.rxhttp.core.HttpConfig.Companion.httpLoadingDialog
import com.bhm.rxhttp.core.HttpConfig.Companion.writtenLength
import com.bhm.rxhttp.core.callback.CallBackImp
import com.bhm.rxhttp.define.CommonUtil.logger
import com.bhm.rxhttp.define.ResultException
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
    private val delaysProcessLimitTime: Long
        get() = builder.delaysProcessLimitTime

    /*
    *  ??????????????????
    */
    fun <T: Any> enqueue(observable: Observable<T>, callBack: CallBackImp<T>?): Disposable {
        this.callBack = callBack
        val disposable = observable
            .compose(builder.activity.bindToLifecycle()) //??????????????????
            .compose(rxSchedulerHelper()) //????????????io??????
            .subscribe(
                getBaseConsumer(callBack),
                getThrowableConsumer(callBack),
                getDefaultAction(callBack),
                disposableContainer
            )
        currentRequestDateTamp = System.currentTimeMillis()
        //???????????????
        callBack?.onStart(disposable)
        builder.disposeManager?.add(disposable)
        return disposable
    }

    /*
    *  ????????????????????????
    */
    fun <T: Any> uploadEnqueue(observable: Observable<T>, callBack: CallBackImp<T>?): Disposable {
        return this.enqueue(observable, callBack)
    }

    /*
    *  ????????????????????????
    */
    fun <T: Any> downloadEnqueue(observable: Observable<ResponseBody>, callBack: CallBackImp<T>?): Disposable {
        this.callBack = callBack
        val disposable = observable
            .compose(builder.activity.bindToLifecycle()) //??????????????????
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
        callBack?.onStart(disposable)
        builder.disposeManager?.add(disposable)
        return disposable
    }

    @SuppressLint("CheckResult")
    private fun <T: Any> getBaseConsumer(callBack: CallBackImp<T>?): Consumer<T> {
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

    private fun <T: Any> doBaseConsumer(callBack: CallBackImp<T>?, t: T) {
        callBack?.onSuccess(t)
        if (isShowDialog && null != dialog) {
            dialog?.dismissLoading(activity)
        }
    }

    @SuppressLint("CheckResult")
    private fun <T: Any> getThrowableConsumer(callBack: CallBackImp<T>?): Consumer<Throwable> {
        return Consumer { e ->
            logger(this@HttpBuilder, "ThrowableConsumer-> ", e.message) //?????????
            if (System.currentTimeMillis() - currentRequestDateTamp <= delaysProcessLimitTime) {
                Observable.timer(delaysProcessLimitTime, TimeUnit.MILLISECONDS)
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
                    Toast.makeText(activity, "????????????????????????", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "????????????????????????", Toast.LENGTH_SHORT).show()
                }
            } else if (e is IndexOutOfBoundsException
                || e is NullPointerException
                || e is JsonSyntaxException
                || e is IllegalStateException
                || e is ResultException
            ) {
                Toast.makeText(activity, "??????????????????????????????", Toast.LENGTH_SHORT).show()
            } else if (e is TimeoutException) {
                Toast.makeText(activity, "???????????????????????????", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "?????????????????????????????????", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** ?????????????????????
     * @return ???getThrowableConsumer??????
     */
    private fun <T: Any> getDefaultAction(callBack: CallBackImp<T>?): Action {
        return Action { callBack?.onComplete() }
    }

    /** ???????????????
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
        internal var delaysProcessLimitTime = HttpConfig.delaysProcessLimitTime

        fun setLoadingDialog(dialog: HttpLoadingDialog?): Builder {
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

        fun setIsDefaultToast(isDefaultToast: Boolean): Builder {
            this.isDefaultToast = isDefaultToast
            return this
        }

        fun setHttpTimeOut(readTimeOut: Int, connectTimeOut: Int): Builder {
            this.readTimeOut = readTimeOut
            this.connectTimeOut = connectTimeOut
            return this
        }

        /** ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????SSL???
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

        fun build(): HttpBuilder {
            return HttpBuilder(this)
        }
    }

    companion object {
        @JvmStatic
        fun create(activity: HttpActivity): Builder {
            return Builder(activity)
        }

        @JvmStatic
        fun getDefaultBuilder(activity: HttpActivity): HttpBuilder {
            return create(activity)
                .setLoadingDialog(HttpLoadingDialog.defaultDialog)
                .build()
        }

        /**
         * ??????????????????
         * ????????????io??????????????????????????????
         */
        @JvmStatic
        private fun <T : Any> rxSchedulerHelper(): ObservableTransformer<T, T> { //compose????????????
            return ObservableTransformer { upstream ->
                upstream.subscribeOn(Schedulers.io()) //??????????????????????????????????????????????????????
                    .observeOn(AndroidSchedulers.mainThread()) //????????????????????????????????????????????????
            }
        }
    }
}