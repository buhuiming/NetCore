package com.bhm.rxhttp.core

import com.bhm.rxhttp.core.callback.CommonCallBack
import com.bhm.rxhttp.core.callback.DownloadCallBack
import com.bhm.rxhttp.core.callback.UploadCallBack
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

        fun <T : Any> httpCall(aClass: Class<T>, httpCall:(T) -> Observable<E>): Manager<E> {
            val api = RetrofitHelper(httpBuilder!!).createRequest(aClass, baseUrl!!)
            observable = httpCall(api)
            return this
        }

        fun <T : Any> uploadCall(aClass: Class<T>, httpCall:(T) -> Observable<E>): Manager<E> {
            return this.httpCall(aClass, httpCall)
        }

        fun <T : Any> downloadCall(aClass: Class<T>, httpCall:(T) -> Observable<ResponseBody>): Manager<E> {
            val api = RetrofitHelper(httpBuilder!!).createRequest(aClass, baseUrl!!)
            downloadObservable = httpCall(api)
            return this
        }

        fun execute(callBack: CommonCallBack<E>.() -> Unit): Disposable? {
            val call = CommonCallBack<E>()
            call.apply(callBack)
            return httpBuilder?.enqueue(observable!!, call)
        }

        fun uploadExecute(callBack: UploadCallBack<E>.() -> Unit): Disposable? {
            val call = UploadCallBack<E>()
            call.apply(callBack)
            return httpBuilder?.enqueue(observable!!, call)
        }

        fun downloadExecute(callBack: DownloadCallBack.() -> Unit): Disposable? {
            val call = DownloadCallBack()
            call.apply(callBack)
            return httpBuilder?.downloadEnqueue(downloadObservable!!, call)
        }
    }
}