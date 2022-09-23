package com.bhm.rxhttp.define

import android.text.TextUtils
import android.util.Log
import com.bhm.rxhttp.core.HttpBuilder
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
     * @param httpBuilder
     */
    @Synchronized
    @Throws(IOException::class)
    fun writeFile(inputString: InputStream, httpBuilder: HttpBuilder) {
        val fos = FileOutputStream(checkFile(httpBuilder), true)
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
     * @param httpBuilder
     * @param contentLength
     * @throws IOException
     */
    @Synchronized
    @Throws(IOException::class)
    fun deleteFile(httpBuilder: HttpBuilder, contentLength: Long) {
        val file = checkFile(httpBuilder)
        if (file.exists()) {
            if (!httpBuilder.isAppendWrite || file.length() >= contentLength || httpBuilder.writtenLength() == 0L) {
                file.delete()
            }
        }
    }

    /** 生成文件
     * @param httpBuilder
     * @return
     * @throws IOException
     */
    @Synchronized
    @Throws(IOException::class)
    private fun checkFile(httpBuilder: HttpBuilder): File {
        if (TextUtils.isEmpty(httpBuilder.filePath) || TextUtils.isEmpty(httpBuilder.fileName)) {
            throw IOException("filePath or fileName is null!")
        }
        val fileDir = File(httpBuilder.filePath!!)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        val file: File = if (httpBuilder.filePath!!.endsWith("/")) {
            File(httpBuilder.filePath + httpBuilder.fileName)
        } else {
            File(httpBuilder.filePath + "/" + httpBuilder.fileName)
        }
        return file
    }

    @JvmStatic
    fun logger(httpBuilder: HttpBuilder, tag: String?, msg: String?) {
        if (httpBuilder.isLogOutPut) {
            Log.e(tag, msg?: "")
        }
    }
}