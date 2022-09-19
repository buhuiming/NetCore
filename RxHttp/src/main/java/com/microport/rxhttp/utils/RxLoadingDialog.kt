package com.microport.rxhttp.utils

import com.microport.rxhttp.rxjava.RxBuilder
import android.app.Activity

/**
 * Created by bhm on 2022/9/15.
 */
open class RxLoadingDialog {

    private var rxLoadingFragment: RxLoadingFragment? = null

    private var showAgain = false

    /**
     * rxManager 用户按返回关闭，请求取消
     * isCancelable true,单击返回键，dialog关闭；false,1s内双击返回键，dialog关闭，否则dialog不关闭
     */
    fun showLoading(builder: RxBuilder) {
        if (!builder.activity.isFinishing && builder.isShowDialog) {
            if (rxLoadingFragment == null) {
                rxLoadingFragment = initDialog(builder)
            }
            val fm = builder.activity.supportFragmentManager
            showAgain =
                if (!rxLoadingFragment!!.isAdded && null == fm.findFragmentByTag("default")) {
                    rxLoadingFragment!!.show(fm, "default")
                    false
                } else {
                    rxLoadingFragment!!.changDialogContent(builder)
                    true
                }
        }
    }

    open fun initDialog(builder: RxBuilder?): RxLoadingFragment {
        return RxLoadingFragment(builder!!)
    }

    fun dismissLoading(activity: Activity?) {
        cancelLoading(activity)
    }

    private fun cancelLoading(activity: Activity?) {
        if (null != rxLoadingFragment && !showAgain) {
            if (null != activity && null != rxLoadingFragment!!.dialog && (activity
                        == rxLoadingFragment!!.activity)
            ) {
                rxLoadingFragment!!.dismiss()
                rxLoadingFragment = null
            }
        }
        showAgain = false
    }

    companion object {
        private var RxDialog: RxLoadingDialog? = null
        val defaultDialog: RxLoadingDialog?
            get() {
                if (null == RxDialog) {
                    RxDialog = RxLoadingDialog()
                }
                return RxDialog
            }
    }
}