package com.bhm.rxhttp.core.callback

import io.reactivex.rxjava3.disposables.Disposable


/** 事件执行的回调
 * Created by bhm on 2022/9/15.
 */
interface CallBackImp<T> {
    fun onStart(disposable: Disposable?)
    fun onProgress(progress: Int, bytesWritten: Long, contentLength: Long)
    fun onSuccess(response: T)
    fun onFail(e: Throwable?)
    fun onComplete()
}