package com.bhm.rxhttp.core.callback

import io.reactivex.rxjava3.disposables.Disposable


/** 事件执行的回调
 * Created by bhm on 2022/9/15.
 */
open class DownloadCallBack : CallBackImp<Any>() {
    override fun onStart(disposable: Disposable?) {}
    override fun onProgress(progress: Int, bytesWritten: Long, contentLength: Long) {}
    override fun onSuccess(response: Any) {}
    override fun onFail(e: Throwable?) {}
    override fun onComplete() {}
}