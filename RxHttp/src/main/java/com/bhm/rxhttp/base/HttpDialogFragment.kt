package com.bhm.rxhttp.base

import android.content.Context
import com.bhm.rxhttp.core.DisposeManager
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import com.trello.rxlifecycle4.components.support.RxDialogFragment

/**
 * Created by bhm on 2022/9/15.
 */
class HttpDialogFragment : RxDialogFragment() {

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