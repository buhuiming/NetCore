package com.bhm.sdk.demo.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bhm.netcore.R
import com.bhm.network.base.HttpActivity
import com.bhm.network.base.HttpLoadingDialog
import com.bhm.network.core.HttpOptions
import com.bhm.network.core.RequestManager
import com.bhm.sdk.demo.adapter.MainUIAdapter
import com.bhm.sdk.demo.entity.DoGetEntity
import com.bhm.sdk.demo.entity.DoPostEntity
import com.bhm.sdk.demo.entity.UpLoadEntity
import com.bhm.sdk.demo.http.HttpApi
import com.bhm.sdk.demo.tools.MyHttpLoadingDialog
import com.bhm.sdk.demo.tools.Utils.getFile
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File

@Suppress("PrivatePropertyName")
@SuppressLint("CheckResult")
open class MainActivity : HttpActivity() {
    private var main_recycle_view: RecyclerView? = null
    private var adapter: MainUIAdapter? = null
    private var progressBarHorizontal: ProgressBar? = null
    private var rxPermissions: RxPermissions? = null
    private var down_Disposable: Disposable? = null
    private var up_Disposable: Disposable? = null
    private var downLoadLength: Long = 0 //已下载的长度

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rxPermissions = RxPermissions(this) //权限申请
        initView()
        initListener()
    }

    private fun initView() {
        main_recycle_view = findViewById<View>(R.id.main_recycle_view) as RecyclerView
        progressBarHorizontal = findViewById<View>(R.id.progressBarHorizontal) as ProgressBar
        val ms = LinearLayoutManager(this)
        ms.orientation = LinearLayoutManager.VERTICAL
        main_recycle_view?.layoutManager = ms
        main_recycle_view?.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        main_recycle_view?.setHasFixedSize(false)
        adapter = MainUIAdapter(items)
        main_recycle_view?.adapter = adapter
    }

    private val items: MutableList<String?>
        get() {
            val list: MutableList<String?> = ArrayList()
            list.add("RxJava2+Retrofit2,Get请求")
            list.add("RxJava2+Retrofit2,post请求")
            list.add("RxJava2+Retrofit2,文件上传（带进度）")
            list.add("取消上传")
            list.add("RxJava2+Retrofit2,文件下载（带进度）")
            list.add("暂停/取消下载")
            list.add("继续下载")
            list.add("")
            return list
        }

    private fun initListener() {
        adapter?.setOnItemClickListener { _, _, position -> openUI(position) }
    }

    private fun openUI(position: Int) {
        when (position) {
            0 -> doGet()
            1 -> doPost()
            2 -> upLoad()
            3 -> disposeManager.removeDispose(up_Disposable)
            4 -> {
                downLoadLength = 0
                progressBarHorizontal?.progress = 0
                downLoad()
            }
            5 -> {
                if (!disposeManager.isExitDispose(down_Disposable!!)) {
                    return
                }
                disposeManager.removeDispose(down_Disposable)
            }
            6 -> downLoad()
            else -> {}
        }
    }

    private fun upLoad() {
        rxPermissions?.request(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )?.subscribe { aBoolean ->
            if (!aBoolean) {
                Toast.makeText(
                    this@MainActivity, "无法获取权限，请在设置中授权",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                upLoadFile() //上传文件
            }
        }
    }

    private fun downLoad() {
        rxPermissions?.request(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )?.subscribe { aBoolean ->
            if (!aBoolean) {
                Toast.makeText(
                    this@MainActivity, "无法获取权限，请在设置中授权",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                downLoadFile() //下载文件
            }
        }
    }

    private fun doGet() {
        /*单独使用配置*/
        /*httpOptions builder = httpOptions.create(this)
                .setLoadingDialog(HttpLoadingDialog())
//                .setLoadingDialog(new MyLoadingDialog())
                .setDialogAttribute(true, false, false)
                .setHttpTimeOut(20000, 20000)
                .setIsLogOutPut(true)//默认是false
                .setIsDefaultToast(true, getRxManager())
                .build();*/
        RequestManager.get()
            .buildRequest<DoGetEntity>()
            .setHttpOptions(HttpOptions.getDefaultHttpOptions(this))//默认使用Application的配置
            .setBaseUrl("http://news-at.zhihu.com")
            .execute(
                HttpApi::class.java,
                {
                    it.getData("Bearer aedfc1246d0b4c3f046be2d50b34d6ff", "1")
                },
                {
                    //可以继承CommonCallBack，重写方法，比如在onFail中处理401，404等
                    success { response ->
                        Log.i(javaClass.name, response.date!!)
                        Toast.makeText(this@MainActivity, response.date, Toast.LENGTH_SHORT).show()
                    }
                    fail { e ->
                        Toast.makeText(this@MainActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            )
    }

    private fun doPost() {
        val httpOptions = HttpOptions.create(this)
            .setLoadingDialog(MyHttpLoadingDialog())
            .setDialogAttribute(
                isShowDialog = true,
                cancelable = false,
                dialogDismissInterruptRequest = false
            )
            .setIsLogOutPut(true)
            .setSpecifiedTimeoutMillis(1000)
            .setIsDefaultToast(false)
            .build()
        RequestManager.get()
            .buildRequest<DoPostEntity>()
            .setHttpOptions(httpOptions)
            .setBaseUrl("https://www.pgyer.com/")
            .execute(
                HttpApi::class.java,
                {
                    it.getDataPost("963ca3d091ba71bdd8596994ad7549b5", "android")
                },
                {
                    success { response ->
                        Log.i(javaClass.name, response.toString())
                        Toast.makeText(this@MainActivity, response.data?.key, Toast.LENGTH_SHORT).show()
                    }
                    fail { e ->
                        AlertDialog.Builder(this@MainActivity)
                            .setMessage(e?.message)
                            .setNegativeButton("确定") { dialog, _ -> dialog.dismiss() }.show()
                    }
                    specifiedTimeout {
                        Log.i(javaClass.name, "请求超过1s还没有完成")
                    }
                }
            )
    }

    private fun upLoadFile() {
        val file = getFile(this)
        val requestBody: RequestBody = file.asRequestBody("*/*; charset=UTF-8".toMediaTypeOrNull())
        val part: MultipartBody.Part = createFormData("file", file.name, requestBody) //key(file)与服务器一致
        val builder = HttpOptions.create(this)
            .setLoadingDialog(HttpLoadingDialog())
            .setDialogAttribute(
                isShowDialog = false,
                cancelable = false,
                dialogDismissInterruptRequest = false
            )
            .setIsLogOutPut(true) //默认是false
            .setIsDefaultToast(true)
            .build()
        RequestManager.get()
            .buildRequest<UpLoadEntity>()
            .setHttpOptions(builder)
            .setBaseUrl("https://upload.pgyer.com/")
            .uploadExecute(
                HttpApi::class.java,
                {
                    it.upload(
                        "8fa554a43b63bad477fd55e72839528e".toRequestBody("text/plain".toMediaTypeOrNull()),
                        "963ca3d091ba71bdd8596994ad7549b5".toRequestBody("text/plain".toMediaTypeOrNull()),
                        part)
                },
                {
                    start { disposable ->
                        up_Disposable = disposable
                        progressBarHorizontal?.progress = 0
                    }
                    progress { progress, bytesWritten, contentLength ->
                        progressBarHorizontal?.progress = progress
                        Log.e(
                            "upLoad---- > ", "progress : " + progress + "，bytesWritten : "
                                    + bytesWritten + "，contentLength : " + contentLength
                        )
                    }
                    success { response ->
                        Log.i(javaClass.name, response.data?.appCreated?: "")
                        Toast.makeText(this@MainActivity, response.data?.appCreated, Toast.LENGTH_SHORT).show()
                    }
                    fail { e ->
                        Toast.makeText(this@MainActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                    complete {
                        Log.i(javaClass.name, "onFinishUpload")
                        Toast.makeText(this@MainActivity, "onFinishUpload", Toast.LENGTH_SHORT).show()
                    }
                }
            )
    }

    /**
     * setDialogAttribute参数：1.filePath：文件下载路径， 2.fileName：文件名
     * 3.mAppendWrite：是否支持暂停下载。true,支持，同时需要记录writtenLength
     * false，每次都重新开始下载，并且会删除原文件。（注：文件下载完后，再下载都会删除原文件重新下载，与此参数无关）
     * 4.writtenLength：当mAppendWrite=true,需要记录已下载的部分，当mAppendWrite=false,writtenLength需
     * 赋值0，否则，新文件会从writtenLength开始下载导致文件不完整。
     *
     * 注：调用的函数downLoad,第一个参数为@Header("RANGE") String range，传递参数格式为："bytes=" + writtenLength + "-"
     * rxDownLoadListener不能为空
     */
    private fun downLoadFile() {
        val filePath = getExternalFilesDir("apk")?.path + File.separator
        val fileName = "demo.apk"
        val builder = HttpOptions.create(this)
            .setLoadingDialog(HttpLoadingDialog())
            .setDialogAttribute(
                isShowDialog = false,
                cancelable = false,
                dialogDismissInterruptRequest = false
            )
            .setDownLoadFileAtr(filePath, fileName, true, downLoadLength)
            .setIsLogOutPut(true)
            .setSpecifiedTimeoutMillis(2000)
            .setIsDefaultToast(true)
            .build()
        RequestManager.get()
            .buildRequest<ResponseBody>()
            .setHttpOptions(builder)
            .setBaseUrl("http://s.downpp.com/")
            .downloadExecute(
                HttpApi::class.java,
                {
                    it.downLoad("bytes=$downLoadLength-", "http://s.downpp.com/apk9/shwnl4.0.0_2265.com.apk")
                },
                {
                    start { disposable ->
                        down_Disposable = disposable
                    }
                    progress { progress, bytesWritten, contentLength ->
                        progressBarHorizontal?.progress = progress
                        downLoadLength += bytesWritten
                        Log.e(
                            "upLoad---- > ", "progress : " + progress + "，bytesWritten : "
                                    + bytesWritten + "，contentLength : " + contentLength
                        )
                    }
                    fail { e ->
                        Toast.makeText(this@MainActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
                    complete {
                        Log.i(javaClass.name, "onFinishDownload")
                        Toast.makeText(this@MainActivity, "onFinishDownload", Toast.LENGTH_SHORT).show()
                    }
                    specifiedTimeout {
                        Log.i(javaClass.name, "请求超过2s还没有完成")
                    }
                }
            )
    }
}