package com.bhm.sdk.demo.tools

import com.bhm.rxhttp.core.HttpBuilder
import com.bhm.rxhttp.base.HttpLoadingDialog
import com.bhm.rxhttp.base.HttpLoadingFragment

/**
 * Created by bhm on 2022/9/15.
 */
class MyHttpLoadingDialog : HttpLoadingDialog() {
    override fun initDialog(builder: HttpBuilder?): HttpLoadingFragment {
        return MyHttpLoadingFragment(builder!!)
    }
}