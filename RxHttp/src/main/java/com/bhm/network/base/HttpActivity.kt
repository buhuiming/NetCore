package com.bhm.network.base

import com.bhm.network.core.DisposeManager
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity

/**
 * Created by bhm on 2022/9/15.
 */
open class HttpActivity : RxAppCompatActivity() {

    var disposeManager = DisposeManager()

    override fun onDestroy() {
        super.onDestroy()
        disposeManager.dispose()
    }
}