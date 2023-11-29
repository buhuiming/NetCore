package com.bhm.network.core.callback

import io.reactivex.rxjava3.disposables.Disposable


/** 事件执行的回调
 * Created by bhm on 2022/9/15.
 */
interface CallBackImp<T> {
    var code: Int
    fun onStart(disposable: Disposable?, specifiedTimeoutMillis: Long)
    fun onSuccess(response: T)
    fun onSpecifiedTimeout() //指定超时，在规定的时间内没有结果(成功/失败)，则触发。用在提示用户网络环境不给力的情况
    fun onFail(e: Throwable?)
    fun onComplete()//onSuccess执行后，执行onComplete，与onFail互斥
}