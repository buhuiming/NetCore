package com.bhm.sdk.demo.tools

import android.os.Bundle
import com.bhm.network.core.HttpOptions
import com.bhm.network.base.HttpLoadingDialog
import com.bhm.network.base.HttpLoadingFragment

/**
 * Created by bhm on 2022/9/15.
 */
class MyHttpLoadingDialog : HttpLoadingDialog() {
    override fun initDialog(builder: HttpOptions?): HttpLoadingFragment {
        return MyHttpLoadingFragment().apply {
            val bundle = Bundle()
            bundle.putSerializable("httpOptions", builder)
            arguments = bundle
        }
    }
}