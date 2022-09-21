# NetCore

### 用法

#### 1、Activity继承HttpActivity，Fragment继承HttpFragment，DialogFragment继承HttpDialogFragment

#### 2、Application配置默认的Rx配置项
        HttpConfig.create()
            .setLoadingDialog(MyLoadingDialog())
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
#### 3、发起请求(参考demo MainActivity)

### TO DO

#### 1、优化生命周期管理的方式
#### 2、优化CallBack方式为suspendCoroutine