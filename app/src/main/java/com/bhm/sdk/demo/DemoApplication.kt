package com.bhm.sdk.demo

import android.app.Application
import com.bhm.sdk.demo.tools.MyHttpLoadingDialog
import com.bhm.network.core.HttpConfig

/**
 * Created by bhm on 2022/9/15.
 */
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        /*配置默认的Rx配置项*/
        HttpConfig.create()
            .setLoadingDialog(MyHttpLoadingDialog())
            .setDialogAttribute(
                isShowDialog = true,
                cancelable = false,
                dialogDismissInterruptRequest = false
            )
            .isDefaultToast(true)
            .isLogOutPut(true)
            .setHttpTimeOut(30, 30)
            .setDelaysProcessLimitTimeMillis(0) //请求成功/失败之后，再过0秒后去处理结果
            .setSpecifiedTimeoutMillis(5000) //指定超时，在规定的时间内没有结果(成功/失败)。用在提示用户网络环境不给力的情况
            .setOkHttpClient(null)
            .build()
    }
}