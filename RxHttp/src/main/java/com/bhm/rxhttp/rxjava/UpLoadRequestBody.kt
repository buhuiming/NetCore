package com.bhm.rxhttp.rxjava

import android.annotation.SuppressLint
import com.bhm.rxhttp.rxjava.callback.RxUpLoadCallBack
import com.bhm.rxhttp.utils.RxUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

/**
 * Created by bhm on 2022/9/15.
 */
class UpLoadRequestBody(private val mRequestBody: RequestBody, private val rxBuilder: RxBuilder?) :
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
        val bufferedSink: BufferedSink
        val mCountingSink = CountingSink(sink)
        bufferedSink = mCountingSink.buffer()
        mRequestBody.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    internal inner class CountingSink(delegate: Sink?) : ForwardingSink(delegate!!) {
        private var bytesWritten = 0L
        private var contentLength = 0L
        @SuppressLint("CheckResult")
        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            if (rxBuilder?.listener != null &&
                rxBuilder.listener is RxUpLoadCallBack
            ) {
                if (contentLength == 0L) {
                    contentLength = contentLength()
                }
                if (bytesWritten == 0L) {
                    Observable.just(bytesWritten)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            (rxBuilder.listener as RxUpLoadCallBack).onStart()
                            RxUtils.logger(rxBuilder, "upLoad-- > ", "begin upLoad")
                        }
                }
                bytesWritten += byteCount
                val progress = (bytesWritten * 100 / contentLength).toInt()
                Observable.just(bytesWritten)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        (rxBuilder.listener as RxUpLoadCallBack).onProgress(
                            if (progress > 100) 100 else progress,
                            byteCount,
                            contentLength
                        )
                    }
            }
        }
    }
}