package com.bhm.sdk.demo.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.microport.netcore.R;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by bhm on 2018/5/7.
 */

public class MainUIAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public MainUIAdapter(@Nullable List<String> data) {
        super(R.layout.layout_main_ui_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        TextView tv_main_ui_item = helper.getView(R.id.tv_main_ui_item);
        tv_main_ui_item.setText(item);
    }
}
