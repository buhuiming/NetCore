package com.bhm.sdk.demo.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.microport.netcore.R

/**
 * Created by bhm on 2022/9/15.
 */
class MainUIAdapter(data: MutableList<String?>?) :
    BaseQuickAdapter<String?, BaseViewHolder>(R.layout.layout_main_ui_item, data) {
    override fun convert(holder: BaseViewHolder, item: String?) {
        val uiItem = holder.getView<TextView>(R.id.tv_main_ui_item)
        uiItem.text = item
    }
}