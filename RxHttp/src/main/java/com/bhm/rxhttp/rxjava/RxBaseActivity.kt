package com.bhm.rxhttp.rxjava

import com.trello.rxlifecycle4.components.support.RxAppCompatActivity

/**
 * Created by bhm on 2022/9/15.
 */
open class RxBaseActivity : RxAppCompatActivity() {

    var rxManager = RxManager()

    override fun onDestroy() {
        super.onDestroy()
        rxManager.unSubscribe()
    }
}