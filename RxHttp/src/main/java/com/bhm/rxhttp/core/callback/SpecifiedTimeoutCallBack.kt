package com.bhm.rxhttp.core.callback

import android.os.Looper
import com.bhm.rxhttp.base.WeakHandler
import io.reactivex.rxjava3.disposables.Disposable

/**
 * @author Buhuiming
 * @description: 指定超时，在规定的时间内没有结果(成功/失败)，则触发。用在提示用户网络环境不给力的情况
 * @date :2023/2/10 15:38
 */
abstract class SpecifiedTimeoutCallBack<T>: CallBackImp<T> {

    private var mainHandler: WeakHandler? = null

    private var _specifiedTimeout: (() -> Unit)? = null

    init {
        mainHandler = WeakHandler(Looper.getMainLooper()) { msg ->
            when (msg.what) {
                DELAY -> {
                    onSpecifiedTimeout()
                    done()
                }
            }
            false
        }
    }

    override fun onStart(disposable: Disposable?, specifiedTimeoutMillis: Long) {
        mainHandler?.sendEmptyMessageDelayed(DELAY, specifiedTimeoutMillis)
    }

    override fun onComplete() {
        done()
    }

    override fun onFail(e: Throwable?) {
        done()
    }

    @Synchronized
    private fun done() {
        mainHandler?.removeMessages(DELAY)
    }

    fun specifiedTimeout(value: () -> Unit) {
        _specifiedTimeout = value
    }

    override fun onSpecifiedTimeout() {
       _specifiedTimeout?.invoke()
    }

}

const val DELAY = 1