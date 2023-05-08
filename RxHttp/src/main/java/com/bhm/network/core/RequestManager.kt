package com.bhm.network.core

import com.bhm.network.core.callback.CommonCallBack
import com.bhm.network.core.callback.DownloadCallBack
import com.bhm.network.core.callback.UploadCallBack
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
            val api = RetrofitHelper(httpOptions).createRequest(aClass, baseUrl)
            val call = CommonCallBack<E>()
            call.apply(callBack)
            return httpOptions.enqueue(httpCall(api), call)
        }

        /**
         * 执行上传请求
         */
        fun <T : Any> uploadExecute(aClass: Class<T>, httpCall: (T) -> Observable<E>, callBack: UploadCallBack<E>.() -> Unit): Disposable {
            val api = RetrofitHelper(httpOptions).createRequest(aClass, baseUrl)
            val call = UploadCallBack<E>()
            call.apply(callBack)
            return httpOptions.uploadEnqueue(httpCall(api), call)
        }

        /**
         * 执行下载请求
         */
        fun <T : Any> downloadExecute(aClass: Class<T>, httpCall: (T) -> Observable<ResponseBody>, callBack: DownloadCallBack.() -> Unit): Disposable {
            val api = RetrofitHelper(httpOptions).createRequest(aClass, baseUrl)
            val call = DownloadCallBack()
            call.apply(callBack)
            return httpOptions.downloadEnqueue(httpCall(api), call)
        }
    }
}