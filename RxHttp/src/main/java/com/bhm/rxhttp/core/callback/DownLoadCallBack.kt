package com.bhm.rxhttp.core.callback

/**
 * Created by bhm on 2022/9/15.
 */
open class DownLoadCallBack : StreamCallBackImp() {
    override fun onStart() {}
    override fun onProgress(progress: Int, bytesWritten: Long, contentLength: Long) {}
    override fun onFinish() {}
    override fun onFail(errorInfo: String?) {}
}