package com.bhm.rxhttp.core.callback

/**
 * Created by bhm on 2022/9/15.
 */
abstract class StreamCallBackImp {
    abstract fun onStart()
    abstract fun onProgress(progress: Int, bytesWritten: Long, contentLength: Long)
    abstract fun onFinish()
    abstract fun onFail(errorInfo: String?)
}