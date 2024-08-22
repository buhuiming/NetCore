package com.bhm.network.base

import android.app.Activity
import android.os.Bundle
import com.bhm.network.core.HttpOptions

/**
 * Created by bhm on 2022/9/15.
 */
open class HttpLoadingDialog {

    private var httpLoadingFragment: HttpLoadingFragment? = null

    private var showAgain = false

    companion object {
        const val KEY_DIALOG_CANCELABLE = "key_dialog_cancelable"

        const val KEY_DIALOG_DIALOG_DISMISS_INTERRUPT_REQUEST = "key_dialog_dialogDismissInterruptRequest"

        const val KEY_DIALOG_LOADING_TITLE = "key_dialog_loading_title"
    }

    /**
     * rxManager 用户按返回关闭，请求取消
     * isCancelable true,单击返回键，dialog关闭；false,1s内双击返回键，dialog关闭，否则dialog不关闭
     */
    fun showLoading(builder: HttpOptions) {
        if (!builder.activity.isFinishing && builder.isShowDialog) {
            if (httpLoadingFragment == null) {
                httpLoadingFragment = initDialog(builder)
                httpLoadingFragment?.setCancelDialogEvent{
                    cancelLoading(builder.activity)
                }
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

    private fun initDialog(builder: HttpOptions): HttpLoadingFragment {
        val bundle = Bundle()
        bundle.putBoolean(KEY_DIALOG_CANCELABLE, builder.isCancelable)
        bundle.putBoolean(KEY_DIALOG_DIALOG_DISMISS_INTERRUPT_REQUEST, builder.isDialogDismissInterruptRequest)
        bundle.putString(KEY_DIALOG_LOADING_TITLE, builder.loadingTitle)
        return getLoadingFragment(bundle).apply {
            setDisposeManager(builder.disposeManager)
        }
    }

    open fun getLoadingFragment(bundle: Bundle): HttpLoadingFragment {
        return HttpLoadingFragment().apply {
            arguments = bundle
        }
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
}