package com.microport.rxhttp.rxjava.callback

/**
 * Created by bhm on 2022/9/15.
 */
abstract class RxStreamCallBackImp {
    abstract fun onStart()
    abstract fun onProgress(progress: Int, bytesWritten: Long, contentLength: Long)
    abstract fun onFinish()
    abstract fun onFail(errorInfo: String?)
}