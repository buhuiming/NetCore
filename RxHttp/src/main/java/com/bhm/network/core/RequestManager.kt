package com.bhm.network.core

import android.annotation.SuppressLint
import android.widget.Toast
import com.bhm.network.core.callback.CallBackImp
import com.bhm.network.core.callback.CommonCallBack
import com.bhm.network.core.callback.DownloadCallBack
import com.bhm.network.core.callback.UploadCallBack
import com.bhm.network.define.CommonUtil
import com.bhm.network.define.ResultException
import com.google.gson.JsonSyntaxException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.DisposableContainer
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author Buhuiming
 * @description:
 * @date :2022/9/21 10:39
 */
@Suppress("SENSELESS_COMPARISON")
class RequestManager private constructor() {

    companion object {

        private var instance: RequestManager = RequestManager()

        @JvmStatic
        fun get(): RequestManager {
            synchronized(RequestManager::class.java) {
                if (instance == null) {
                    instance = RequestManager()
                }
            }
            return instance
        }
    }

    fun <E : Any> buildRequest(): Manager<E> {
        return Manager()
    }

    class Manager<E : Any> {

        private lateinit var httpOptions: HttpOptions

        private lateinit var baseUrl: String

        /**
         * 设置请求属性
         */
        fun setHttpOptions(httpOptions: HttpOptions) = apply {
            this.httpOptions = httpOptions
        }

        /**
         * 设置URL域名
         */
        fun setBaseUrl(url: String) = apply {
            baseUrl = url
        }

        /**
         * 执行请求
         */
        fun <T : Any> execute(aClass: Class<T>, httpCall: (T) -> Observable<E>, callBack: CommonCallBack<E>.() -> Unit): Disposable {
            checkOptions()
            val api = RetrofitHelper(httpOptions).createRequest(aClass, baseUrl)
            val call = CommonCallBack<E>()
            call.apply(callBack)
            return enqueue(httpCall(api), call)
        }

        /**
         * 执行上传请求
         */
        fun <T : Any> uploadExecute(aClass: Class<T>, httpCall: (T) -> Observable<E>, callBack: UploadCallBack<E>.() -> Unit): Disposable {
            checkOptions()
            val api = RetrofitHelper(httpOptions).createRequest(aClass, baseUrl)
            val call = UploadCallBack<E>()
            call.apply(callBack)
            return uploadEnqueue(httpCall(api), call)
        }

        /**
         * 执行下载请求
         */
        fun <T : Any> downloadExecute(aClass: Class<T>, httpCall: (T) -> Observable<ResponseBody>, callBack: DownloadCallBack.() -> Unit): Disposable {
            checkOptions()
            val api = RetrofitHelper(httpOptions).createRequest(aClass, baseUrl)
            val call = DownloadCallBack()
            call.apply(callBack)
            return downloadEnqueue(httpCall(api), call)
        }

        private fun checkOptions() {
            requireNotNull(httpOptions) { "Please initialize HttpOptions" }
        }

        /**
         * 设置请求回调
         */
        private fun <T: Any> enqueue(observable: Observable<T>, callBack: CallBackImp<T>?): Disposable {
            httpOptions.callBack = callBack
            val disposable = observable
                .compose(httpOptions.activity.bindToLifecycle()) //管理生命周期
                .compose(HttpOptions.rxSchedulerHelper()) //发布事件io线程
                .subscribe(
                    getBaseConsumer(callBack),
                    getThrowableConsumer(callBack),
                    getDefaultAction(callBack),
                    disposableContainer
                )
            httpOptions.currentRequestDateTamp = System.currentTimeMillis()
            //做准备工作
            callBack?.onStart(disposable, httpOptions.specifiedTimeoutMillis)
            httpOptions.disposeManager?.add(disposable)
            return disposable
        }

        /*
        *  设置上传文件回调
        */
        private fun <T: Any> uploadEnqueue(observable: Observable<T>, callBack: CallBackImp<T>?): Disposable {
            return this.enqueue(observable, callBack)
        }

        /*
        *  设置文件下载回调
        */
        private fun <T: Any> downloadEnqueue(observable: Observable<ResponseBody>, callBack: CallBackImp<T>?): Disposable {
            httpOptions.callBack = callBack
            val disposable = observable
                .compose(httpOptions.activity.bindToLifecycle()) //管理生命周期
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map { responseBody -> responseBody.byteStream() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}) { throwable ->
                    callBack?.onFail(throwable)
                    if (null != httpOptions.dialog && httpOptions.isShowDialog) {
                        httpOptions.dialog?.dismissLoading(httpOptions.activity)
                    }
                    httpOptions.disposeManager?.removeDispose()
                }
            callBack?.onStart(disposable, httpOptions.specifiedTimeoutMillis)
            httpOptions.disposeManager?.add(disposable)
            return disposable
        }

        @SuppressLint("CheckResult")
        private fun <T: Any> getBaseConsumer(callBack: CallBackImp<T>?): Consumer<T> {
            return Consumer { t ->
                val requestSpentTime = System.currentTimeMillis() - httpOptions.currentRequestDateTamp
                if (requestSpentTime < httpOptions.delaysProcessLimitTimeMillis) {
                    Observable.timer(httpOptions.delaysProcessLimitTimeMillis - requestSpentTime, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { doBaseConsumer(callBack, t) }
                } else {
                    doBaseConsumer(callBack, t)
                }
            }
        }

        private fun <T: Any> doBaseConsumer(callBack: CallBackImp<T>?, t: T) {
            callBack?.onSuccess(t)
            if (httpOptions.isShowDialog && null != httpOptions.dialog) {
                httpOptions.dialog?.dismissLoading(httpOptions.activity)
            }
        }

        @SuppressLint("CheckResult")
        private fun <T: Any> getThrowableConsumer(callBack: CallBackImp<T>?): Consumer<Throwable> {
            return Consumer { e ->
                CommonUtil.logger(httpOptions, "ThrowableConsumer-> ", e.message) //抛异常
                val requestSpentTime = System.currentTimeMillis() - httpOptions.currentRequestDateTamp
                if (requestSpentTime < httpOptions.delaysProcessLimitTimeMillis) {
                    Observable.timer(httpOptions.delaysProcessLimitTimeMillis - requestSpentTime, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { doThrowableConsumer(callBack, e) }
                } else {
                    doThrowableConsumer(callBack, e)
                }
            }
        }

        private fun <T: Any> doThrowableConsumer(callBack: CallBackImp<T>?, e: Throwable) {
            callBack?.onFail(e)
            if (httpOptions.isShowDialog && null != httpOptions.dialog) {
                httpOptions.dialog?.dismissLoading(httpOptions.activity)
            }
            if (httpOptions.isDefaultToast) {
                when (e) {
                    is HttpException -> {
                        when {
                            e.code() == 404 -> {
                                Toast.makeText(httpOptions.activity, e.message, Toast.LENGTH_SHORT).show()
                            }
                            e.code() == 504 -> {
                                Toast.makeText(httpOptions.activity, "请检查网络连接！", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(httpOptions.activity, "请检查网络连接！", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    is IndexOutOfBoundsException, is NullPointerException, is JsonSyntaxException, is IllegalStateException, is ResultException -> {
                        Toast.makeText(httpOptions.activity, "数据异常，解析失败！", Toast.LENGTH_SHORT).show()
                    }

                    is TimeoutException -> {
                        Toast.makeText(httpOptions.activity, "连接超时，请重试！", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        Toast.makeText(httpOptions.activity, "请求失败，请稍后再试！", Toast.LENGTH_SHORT).show()
                    }
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
    }
}