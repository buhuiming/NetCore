package com.bhm.rxhttp.core

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.ArrayList

/**
 * Created by bhm on 2022/9/15.
 * 用于管理每个请求
 */
class DisposeManager {

    private val mCompositeDisposable = CompositeDisposable() //管理订阅者者

    private var list: MutableList<Disposable>? = ArrayList()

    fun add(d: Disposable) {
        if (null == list) {
            list = ArrayList()
        }
        if (!list!!.contains(d)) {
            list?.add(d)
            mCompositeDisposable.add(d) //注册订阅
        }
    }

    /**
     * 清空监听，再次调用需new CompositeDisposable()
     */
    fun dispose() {
        mCompositeDisposable.dispose() //取消订阅  activity销毁时调用
        list = null
    }

    /**
     * 取消一个请求
     */
    fun removeDispose() { //中断监听 取消请求
        if (null != list && list!!.size > 0) {
            mCompositeDisposable.remove(list!![list!!.size - 1])
            list!!.remove(list!![list!!.size - 1])
        }
    }

    /**
     * 取消一个请求
     */
    fun removeDispose(disposable: Disposable?) { //中断监听 取消请求
        if (null != disposable) {
            mCompositeDisposable.remove(disposable)
            if (list != null) {
                list!!.remove(disposable)
            }
        }
    }

    fun isExitDispose(disposable: Disposable): Boolean {
        return null != list && list!!.contains(disposable)
    }

    companion object {
        /**
         * 统一线程处理
         * 发布事件io线程，接收事件主线程
         */
        @JvmStatic
        fun <T : Any> rxSchedulerHelper(): ObservableTransformer<T, T> { //compose处理线程
            return ObservableTransformer { upstream ->
                upstream.subscribeOn(Schedulers.io()) //读写文件、读写数据库、网络信息交互等
                    .observeOn(AndroidSchedulers.mainThread()) //指定的是它之后的操作所在的线程。
            }
        }
    }
}