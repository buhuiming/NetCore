package com.bhm.network.body

import android.annotation.SuppressLint
import com.bhm.network.core.HttpOptions
import com.bhm.network.core.callback.UploadCallBack
import com.bhm.network.define.CommonUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

/**
 * Created by bhm on 2022/9/15.
 */
class UploadRequestBody(private val mRequestBody: RequestBody, private val httpOptions: HttpOptions?) :
    RequestBody() {
    override fun contentType(): MediaType? {
        return mRequestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return try {
            mRequestBody.contentLength()
        } catch (e: IOException) {
            e.printStackTrace()
            -1
        }
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (sink is Buffer
            || sink.toString().contains(
                "com.android.tools.profiler.support.network.HttpTracker\$OutputStreamTracker")) {
            mRequestBody.writeTo(sink);
        } else {
            val bufferedSink: BufferedSink
            val mCountingSink = CountingSink(sink)
            bufferedSink = mCountingSink.buffer()
            mRequestBody.writeTo(bufferedSink)
            bufferedSink.flush()
            bufferedSink.close()
        }
    }

    internal inner class CountingSink(delegate: Sink?) : ForwardingSink(delegate!!) {
        private var bytesWritten = 0L
        private var contentLength = 0L
        @SuppressLint("CheckResult")
        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            httpOptions?.callBack?.let { callBack ->
                if (callBack is UploadCallBack<*>) {
                    if (contentLength == 0L) {
                        contentLength = contentLength()
                    }
                    if (bytesWritten == 0L) {
                        Observable.just(bytesWritten)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                CommonUtil.logger(httpOptions, "upLoad-- > ", "begin upLoad")
                            }
                    }
                    bytesWritten += byteCount
                    val progress = (bytesWritten * 100 / contentLength).toInt()
                    Observable.just(bytesWritten)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            callBack.onProgress(
                                if (progress > 100) 100 else progress,
                                byteCount,
                                contentLength
                            )
                        }
                }
            }
        }
    }
}