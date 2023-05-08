package com.bhm.network.define

import android.text.TextUtils
import android.util.Log
import com.bhm.network.core.HttpOptions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by bhm on 2022/9/15.
 */
object CommonUtil {
    /**
     * 将输入流写入文件
     *
     * @param inputString
     * @param httpOptions
     */
    @Synchronized
    @Throws(IOException::class)
    fun writeFile(inputString: InputStream, httpOptions: HttpOptions) {
        val fos = FileOutputStream(checkFile(httpOptions), true)
        val b = ByteArray(1024)
        var len: Int
        while (inputString.read(b).also { len = it } != -1) {
            fos.write(b, 0, len)
        }
        fos.flush()
        inputString.close()
        fos.close()
    }

    /** 下载前，如果不支持断点下载，或者已经下载过，那会把已下载的文件删除
     * @param httpOptions
     * @param contentLength
     * @throws IOException
     */
    @Synchronized
    @Throws(IOException::class)
    fun deleteFile(httpOptions: HttpOptions, contentLength: Long) {
        val file = checkFile(httpOptions)
        if (!file.exists()) {
            return
        }
        if (!httpOptions.isAppendWrite || file.length() >= contentLength || httpOptions.writtenLength() == 0L) {
            file.delete()
        }
    }

    /** 生成文件
     * @param httpOptions
     * @return
     * @throws IOException
     */
    @Synchronized
    @Throws(IOException::class)
    private fun checkFile(httpOptions: HttpOptions): File {
        if (TextUtils.isEmpty(httpOptions.filePath) || TextUtils.isEmpty(httpOptions.fileName)) {
            throw IOException("filePath or fileName is null!")
        }
        val fileDir = File(httpOptions.filePath!!)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        val file: File = if (httpOptions.filePath!!.endsWith("/")) {
            File(httpOptions.filePath + httpOptions.fileName)
        } else {
            File(httpOptions.filePath + "/" + httpOptions.fileName)
        }
        return file
    }

    @JvmStatic
    fun logger(httpOptions: HttpOptions, tag: String?, msg: String?) {
        if (httpOptions.isLogOutPut) {
            Log.e(tag, msg?: "")
        }
    }
}