package com.bhm.sdk.demo.tools;


import com.microport.rxhttp.rxjava.RxBuilder;
import com.microport.rxhttp.utils.RxLoadingDialog;
import com.microport.rxhttp.utils.RxLoadingFragment;

/**
 * Created by bhm on 2018/5/14.
 */

public class MyLoadingDialog extends RxLoadingDialog {

    @Override
    public RxLoadingFragment initDialog(RxBuilder builder) {
        return new MyRxLoadingFragment(builder);
    }
}
