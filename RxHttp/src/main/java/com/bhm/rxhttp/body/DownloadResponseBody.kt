package com.bhm.rxhttp.body

import okhttp3.ResponseBody
import android.annotation.SuppressLint
import com.bhm.rxhttp.core.HttpBuilder
import com.bhm.rxhttp.core.callback.DownloadCallBack
import com.bhm.rxhttp.define.CommonUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import okhttp3.MediaType
import okio.*
import java.io.IOException

/** 下载请求体
 * Created by bhm on 2022/9/15.
 */
class DownloadResponseBody(
    private val responseBody: ResponseBody,
    private val httpBuilder: HttpBuilder?
) : ResponseBody() {
    // BufferedSource 是okio库中的输入流，这里就当作inputStream来使用。
    private var bufferedSource: BufferedSource? = null
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody.source()).buffer()
        }
        return bufferedSource as BufferedSource
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = httpBuilder?.writtenLength() ?: 0L
            val totalBytes = if (httpBuilder == null) responseBody.contentLength() else httpBuilder.writtenLength() + responseBody.contentLength()

            @SuppressLint("CheckResult")
            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                httpBuilder?.callBack?.let { callBack ->
                    if (callBack is DownloadCallBack) {
                        if (totalBytesRead == 0L && bytesRead != -1L) {
                            CommonUtil.deleteFile(httpBuilder, totalBytes)
                            Observable.just(bytesRead)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    CommonUtil.logger(httpBuilder, "DownLoad-- > ", "begin downLoad")
                                }
                        }
                        totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                        if (bytesRead != -1L) {
                            val progress = (totalBytesRead * 100 / totalBytes).toInt()
                            Observable.just(bytesRead)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    callBack.onProgress(
                                        if (progress > 100) 100 else progress,
                                        bytesRead,
                                        totalBytes
                                    )
                                }
                            if (totalBytesRead == totalBytes) {
                                Observable.just(bytesRead)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        callBack.onProgress(100, bytesRead, totalBytes)
                                        callBack.onComplete()
                                        CommonUtil.logger(httpBuilder, "DownLoad-- > ", "finish downLoad")
                                        if (null != httpBuilder.dialog && httpBuilder.isShowDialog) {
                                            httpBuilder.dialog?.dismissLoading(httpBuilder.activity)
                                        }
                                    }
                            }
                        }
                        CommonUtil.writeFile(sink.inputStream(), httpBuilder)
                    }
                }
                return bytesRead
            }
        }
    }
}