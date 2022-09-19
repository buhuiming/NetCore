package com.bhm.sdk.demo.tools

import com.bhm.rxhttp.rxjava.RxBuilder
import com.bhm.rxhttp.utils.RxLoadingDialog
import com.bhm.rxhttp.utils.RxLoadingFragment

/**
 * Created by bhm on 2022/9/15.
 */
class MyLoadingDialog : RxLoadingDialog() {
    override fun initDialog(builder: RxBuilder?): RxLoadingFragment {
        return MyRxLoadingFragment(builder!!)
    }
}