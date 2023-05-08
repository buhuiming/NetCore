# NetCore

### 用法

        allprojects {
            repositories {
                ...
                maven { url 'https://jitpack.io' }
            }
        }

        dependencies {
            implementation 'com.github.buhuiming:NetCore:1.1.0'
        }

#### 1、Activity继承HttpActivity，Fragment继承HttpFragment，DialogFragment继承HttpDialogFragment (实现内存管理)

#### 2、Application配置默认的全局配置项
        HttpConfig.create()
            .setLoadingDialog(MyLoadingDialog())
            .setDialogAttribute(
                isShowDialog = true,
                cancelable = false,
                dialogDismissInterruptRequest = false
            )
            .isDefaultToast(true)
            .isLogOutPut(true)
            .setHttpTimeOut(30, 30)
            .setDelaysProcessLimitTimeMillis(0) //请求成功/失败之后，再过0秒后去处理结果
            .setJsonCovertKey()//设置json解析的Key
            .setOkHttpClient(null)
            .build()
#### 3、发起请求(参考demo MainActivity)

        RequestManager.get()
            .buildRequest<DoGetEntity>()
            .setHttpOptions(HttpBuilder.getDefaultHttpOptions(this))//默认使用Application的配置
            .setBaseUrl("http://news-at.zhihu.com")
            .execute(
                HttpApi::class.java,
                {
                    it.getData("Bearer aedfc1246d0b4c3f046be2d50b34d6ff", "1")
                },
                {
                    //可以继承CallBackImp，重写方法，比如在onFail中处理401，404等
                    success { response ->
                        Log.i("MainActivity--> ", response.date!!)
                        Toast.makeText(this@MainActivity, response.date, Toast.LENGTH_SHORT).show()
                    }
                    fail { e ->
                        Toast.makeText(this@MainActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            )