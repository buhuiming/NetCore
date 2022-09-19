package com.bhm.sdk.demo

import android.app.Application
import com.bhm.sdk.demo.tools.MyLoadingDialog
import com.bhm.rxhttp.core.RxConfig

/**
 * Created by bhm on 2022/9/15.
 */
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        /*配置默认的Rx配置项*/
        RxConfig.newBuilder()
            .setRxLoadingDialog(MyLoadingDialog())
            .setDialogAttribute(
                isShowDialog = true,
                cancelable = false,
                dialogDismissInterruptRequest = false
            )
            .isDefaultToast(true)
            .isLogOutPut(true)
            .setReadTimeOut(30000)
            .setConnectTimeOut(30000)
            .setDelaysProcessLimitTime(0) //请求成功/失败之后，再过0秒后去处理结果
            .setOkHttpClient(null)
            .build()
    }
}