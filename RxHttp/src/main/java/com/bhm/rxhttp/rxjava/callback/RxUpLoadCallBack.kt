package com.bhm.rxhttp.rxjava.callback

/**
 * Created by bhm on 2022/9/15.
 */
open class RxUpLoadCallBack : RxStreamCallBackImp() {
    override fun onStart() {}
    override fun onProgress(progress: Int, bytesWritten: Long, contentLength: Long) {}
    override fun onFinish() {}
    override fun onFail(errorInfo: String?) {}
}