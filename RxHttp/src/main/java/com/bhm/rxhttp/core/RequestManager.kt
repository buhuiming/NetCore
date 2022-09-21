package com.bhm.rxhttp.core

import com.bhm.rxhttp.base.HttpActivity
import com.bhm.rxhttp.core.callback.CallBackImp
import com.bhm.rxhttp.core.callback.DownloadCallBack
import com.bhm.rxhttp.core.callback.HttpCall
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.ResponseBody

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
        fun builder(): RequestManager {
            synchronized(RequestManager::class.java) {
                if (instance == null) {
                    instance = RequestManager()
                }
            }
            return instance
        }
    }

    fun <E : Any> callManager(): Manager<E> {
        return Manager()
    }

    class Manager<E : Any> {

        private var httpBuilder: HttpBuilder? = null

        private var baseUrl: String? = null

        private var observable: Observable<E>? = null

        private var downloadObservable: Observable<ResponseBody>? = null

        fun setHttpBuilder(httpBuilder: HttpBuilder?): Manager<E> {
            this.httpBuilder = httpBuilder
            return this
        }

        fun setBaseUrl(url: String?): Manager<E> {
            baseUrl = url
            return this
        }

        fun <T> httpCall(aClass: Class<T>, call: HttpCall<E, T>): Manager<E> {
            val api = RetrofitHelper(httpBuilder!!).createRequest(aClass, baseUrl!!)
            observable = call.callHttp(api)
            return this
        }

        fun <T> uploadCall(aClass: Class<T>, call: HttpCall<E, T>): Manager<E> {
            return this.httpCall(aClass, call)
        }

        fun <T> downloadCall(aClass: Class<T>, call: HttpCall<ResponseBody, T>): Manager<E> {
            val api = RetrofitHelper(httpBuilder!!).createRequest(aClass, baseUrl!!)
            downloadObservable = call.callHttp(api)
            return this
        }

        fun execute(callBack: CallBackImp<E>?): Disposable? {
            return httpBuilder?.enqueue(observable!!, callBack)
        }

        fun uploadExecute(callBack: CallBackImp<E>?): Disposable? {
            return this.execute(callBack)
        }

        fun downloadExecute(callBack: DownloadCallBack): Disposable? {
            return httpBuilder?.downloadEnqueue(downloadObservable!!, callBack)
        }
    }
}