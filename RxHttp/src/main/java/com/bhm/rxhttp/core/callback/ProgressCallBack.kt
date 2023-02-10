package com.bhm.rxhttp.core.callback

/**
 * @author Buhuiming
 * @description:进度回调
 * @date :2023/2/10 15:15
 */
abstract class ProgressCallBack<T>: SpecifiedTimeoutCallBack<T>() {
    abstract fun onProgress(progress: Int, bytesWritten: Long, contentLength: Long)
}