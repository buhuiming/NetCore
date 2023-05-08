package com.bhm.sdk.demo.tools

import android.annotation.SuppressLint
import android.app.Dialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.bhm.netcore.R
import com.bhm.network.core.HttpOptions
import com.bhm.network.base.HttpLoadingFragment

class MyHttpLoadingFragment internal constructor(private val httpOptions: HttpOptions) :
    HttpLoadingFragment(
        httpOptions
    ) {
    override fun initDialog(): Dialog {
        val inflater = LayoutInflater.from(activity)
        @SuppressLint("InflateParams") val v =
            inflater.inflate(R.layout.layout_my_loading, null) // 得到加载view
        val dialog = Dialog(requireActivity(), com.bhm.network.R.style.loading_dialog) // 创建自定义样式dialog
        dialog.setCancelable(httpOptions.isCancelable) // false不可以用“返回键”取消
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(
            v, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        ) // 设置布局
        if (!TextUtils.isEmpty(httpOptions.loadingTitle)) {
            val textView = v.findViewById<TextView>(R.id.dialog_text_loading)
            textView.text = httpOptions.loadingTitle
        }
        return dialog
    }
}