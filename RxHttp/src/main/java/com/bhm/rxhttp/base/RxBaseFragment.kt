package com.bhm.rxhttp.base

import android.content.Context
import com.bhm.rxhttp.core.RxManager
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import com.trello.rxlifecycle4.components.support.RxFragment

/**
 * Created by bhm on 2022/9/15.
 */
class RxBaseFragment : RxFragment() {

    private var activity: RxAppCompatActivity? = null

    private var rxManager = RxManager()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as RxAppCompatActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        rxManager.unSubscribe()
    }
}