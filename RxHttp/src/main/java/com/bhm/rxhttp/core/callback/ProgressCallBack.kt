package com.bhm.rxhttp.core.callback

/**
 * @author Buhuiming
 * @description:进度回调
 * @date :2023/2/10 15:15
 */
interface ProgressCallBack<T>: CallBackImp<T>{
    fun onProgress(progress: Int, bytesWritten: Long, contentLength: Long)
}