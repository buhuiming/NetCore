package com.bhm.rxhttp.rxjava.callback

import io.reactivex.rxjava3.disposables.Disposable


/** 事件执行的回调
 * Created by bhm on 2022/9/15.
 */
abstract class CallBackImp<T> {
    abstract fun onStart(disposable: Disposable?)
    abstract fun onSuccess(response: T)
    abstract fun onFail(e: Throwable?)
    abstract fun onComplete()
}