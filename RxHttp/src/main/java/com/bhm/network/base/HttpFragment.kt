package com.bhm.network.base

import android.content.Context
import com.bhm.network.core.DisposeManager
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import com.trello.rxlifecycle4.components.support.RxFragment

/**
 * Created by bhm on 2022/9/15.
 */
open class HttpFragment : RxFragment() {

    private var activity: RxAppCompatActivity? = null

    private var disposeManager = DisposeManager()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as RxAppCompatActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeManager.dispose()
    }
}