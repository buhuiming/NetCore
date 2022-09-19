package com.bhm.sdk.demo.tools

import android.annotation.SuppressLint
import android.app.Dialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.microport.netcore.R
import com.microport.rxhttp.rxjava.RxBuilder
import com.microport.rxhttp.utils.RxLoadingFragment

class MyRxLoadingFragment internal constructor(private val rxBuilder: RxBuilder) :
    RxLoadingFragment(
        rxBuilder
    ) {
    override fun initDialog(): Dialog {
        val inflater = LayoutInflater.from(activity)
        @SuppressLint("InflateParams") val v =
            inflater.inflate(R.layout.layout_my_loading, null) // 得到加载view
        val dialog = Dialog(requireActivity(), com.microport.rxhttp.R.style.loading_dialog) // 创建自定义样式dialog
        dialog.setCancelable(rxBuilder.isCancelable) // false不可以用“返回键”取消
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(
            v, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        ) // 设置布局
        if (!TextUtils.isEmpty(rxBuilder.loadingTitle)) {
            val textView = v.findViewById<TextView>(R.id.dialog_text_loading)
            textView.text = rxBuilder.loadingTitle
        }
        return dialog
    }
}