package com.microport.rxhttp.rxjava.callback

import io.reactivex.rxjava3.disposables.Disposable


/** 事件执行的回调
 * Created by bhm on 2022/9/15.
 */
open class CallBack<T> : CallBackImp<T>() {
    override fun onStart(disposable: Disposable?) {}
    override fun onSuccess(response: T) {}
    override fun onFail(e: Throwable?) {}
    override fun onComplete() {}
}