package com.bhm.sdk.demo.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microport.netcore.R;
import com.microport.rxhttp.rxjava.RxBuilder;
import com.microport.rxhttp.utils.RxLoadingFragment;


public class MyRxLoadingFragment extends RxLoadingFragment {

    private RxBuilder rxBuilder;

    MyRxLoadingFragment(RxBuilder builder){
        super(builder);
        this.rxBuilder = builder;
    }

    @Override
    public Dialog initDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.layout_my_loading, null);// 得到加载view
        @SuppressWarnings("ConstantConditions")
        Dialog dialog = new Dialog(getActivity(), com.microport.rxhttp.R.style.loading_dialog);// 创建自定义样式dialog
        dialog.setCancelable(rxBuilder.isCancelable());// false不可以用“返回键”取消
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(v, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));// 设置布局
        if(!TextUtils.isEmpty(rxBuilder.getLoadingTitle())){
            TextView textView = v.findViewById(R.id.dialog_text_loading);
            textView.setText(rxBuilder.getLoadingTitle());
        }
        return dialog;
    }
}
