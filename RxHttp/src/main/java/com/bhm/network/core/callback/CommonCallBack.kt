package com.bhm.network.core.callback

import io.reactivex.rxjava3.disposables.Disposable


/** 事件执行的回调
 * Created by bhm on 2022/9/15.
 */
open class CommonCallBack<T> : SpecifiedTimeoutCallBack<T>() {

    private var _start: ((disposable: Disposable?) -> Unit)? = null

    private var _success: ((response: T) -> Unit)? = null

    private var _fail: ((e: Throwable?) -> Unit)? = null

    private var _complete: (() -> Unit)? = null

    override var code: Int = 0

    fun start(value: (disposable: Disposable?) -> Unit) {
        _start = value
    }

    fun success(value: (response: T) -> Unit) {
        _success = value
    }

    fun fail(value: (e: Throwable?) -> Unit) {
        _fail = value
    }

    fun complete(value: () -> Unit) {
        _complete = value
    }

    override fun onStart(disposable: Disposable?, specifiedTimeoutMillis: Long) {
        super.onStart(disposable, specifiedTimeoutMillis)
        _start?.invoke(disposable)
    }

    override fun onSuccess(response: T) {
        _success?.invoke(response)
    }

    override fun onFail(e: Throwable?) {
        //可以在此处理异常，比如e is HttpException 401,404等问题
        super.onFail(e)
        _fail?.invoke(e)
    }

    override fun onComplete() {
        super.onComplete()
        _complete?.invoke()
    }
}