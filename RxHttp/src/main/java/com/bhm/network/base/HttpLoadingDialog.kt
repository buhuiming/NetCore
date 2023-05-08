package com.bhm.network.base

import com.bhm.network.core.HttpOptions
import android.app.Activity

/**
 * Created by bhm on 2022/9/15.
 */
open class HttpLoadingDialog {

    private var httpLoadingFragment: HttpLoadingFragment? = null

    private var showAgain = false

    /**
     * rxManager 用户按返回关闭，请求取消
     * isCancelable true,单击返回键，dialog关闭；false,1s内双击返回键，dialog关闭，否则dialog不关闭
     */
    fun showLoading(builder: HttpOptions) {
        if (!builder.activity.isFinishing && builder.isShowDialog) {
            if (httpLoadingFragment == null) {
                httpLoadingFragment = initDialog(builder)
            }
            val fm = builder.activity.supportFragmentManager
            showAgain =
                if (!httpLoadingFragment!!.isAdded && null == fm.findFragmentByTag("default")) {
                    httpLoadingFragment?.show(fm, "default")
                    false
                } else {
                    httpLoadingFragment?.changDialogContent(builder)
                    true
                }
        }
    }

    open fun initDialog(builder: HttpOptions?): HttpLoadingFragment {
        return HttpLoadingFragment(builder!!)
    }

    fun dismissLoading(activity: Activity?) {
        cancelLoading(activity)
    }

    private fun cancelLoading(activity: Activity?) {
        if (null != httpLoadingFragment && !showAgain && null != activity && null != httpLoadingFragment?.dialog && (activity
                    == httpLoadingFragment?.activity)
        ) {
            httpLoadingFragment?.dismiss()
            httpLoadingFragment = null
        }
        showAgain = false
    }

    companion object {
        private var RxDialog: HttpLoadingDialog? = null
        val defaultDialog: HttpLoadingDialog?
            get() {
                if (null == RxDialog) {
                    RxDialog = HttpLoadingDialog()
                }
                return RxDialog
            }
    }
}