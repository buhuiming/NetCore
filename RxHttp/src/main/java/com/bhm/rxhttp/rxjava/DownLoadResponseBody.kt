package com.bhm.rxhttp.rxjava

import okhttp3.ResponseBody
import android.annotation.SuppressLint
import com.bhm.rxhttp.rxjava.callback.RxDownLoadCallBack
import com.bhm.rxhttp.utils.RxUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import okhttp3.MediaType
import okio.*
import java.io.IOException

/** 下载请求体
 * Created by bhm on 2022/9/15.
 */
class DownLoadResponseBody(
    private val responseBody: ResponseBody,
    private val rxBuilder: RxBuilder?
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
            var totalBytesRead = rxBuilder?.writtenLength() ?: 0L
            val totalBytes = if (rxBuilder == null) responseBody.contentLength() else rxBuilder.writtenLength() + responseBody.contentLength()

            @SuppressLint("CheckResult")
            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                if (rxBuilder?.listener != null &&
                    rxBuilder.listener is RxDownLoadCallBack) {
                    if (totalBytesRead == 0L && bytesRead != -1L) {
                        RxUtils.deleteFile(rxBuilder, totalBytes)
                        Observable.just(bytesRead)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                (rxBuilder.listener as RxDownLoadCallBack).onStart()
                                RxUtils.logger(rxBuilder, "DownLoad-- > ", "begin downLoad")
                            }
                    }
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    if (bytesRead != -1L) {
                        val progress = (totalBytesRead * 100 / totalBytes).toInt()
                        Observable.just(bytesRead)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                (rxBuilder.listener as RxDownLoadCallBack).onProgress(
                                    if (progress > 100) 100 else progress,
                                    bytesRead,
                                    totalBytes
                                )
                            }
                        if (totalBytesRead == totalBytes) {
                            Observable.just(bytesRead)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    (rxBuilder.listener as RxDownLoadCallBack).onProgress(100, bytesRead, totalBytes)
                                    (rxBuilder.listener as RxDownLoadCallBack).onFinish()
                                    RxUtils.logger(rxBuilder, "DownLoad-- > ", "finish downLoad")
                                    if (null != rxBuilder.dialog && rxBuilder.isShowDialog) {
                                        rxBuilder.dialog?.dismissLoading(rxBuilder.activity)
                                    }
                                }
                        }
                    }
                    RxUtils.writeFile(sink.inputStream(), rxBuilder)
                }
                return bytesRead
            }
        }
    }
}