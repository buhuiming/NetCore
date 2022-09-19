package com.bhm.sdk.demo.tools

import com.microport.rxhttp.rxjava.RxBuilder
import com.microport.rxhttp.utils.RxLoadingDialog
import com.microport.rxhttp.utils.RxLoadingFragment

/**
 * Created by bhm on 2022/9/15.
 */
class MyLoadingDialog : RxLoadingDialog() {
    override fun initDialog(builder: RxBuilder?): RxLoadingFragment {
        return MyRxLoadingFragment(builder!!)
    }
}