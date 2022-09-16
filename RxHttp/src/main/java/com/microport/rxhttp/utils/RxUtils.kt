package com.microport.rxhttp.utils

import android.text.TextUtils
import android.util.Log
import com.microport.rxhttp.rxjava.RxBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by bhm on 2022/9/15.
 */
object RxUtils {
    /**
     * 将输入流写入文件
     *
     * @param inputString
     * @param rxBuilder
     */
    @Synchronized
    @Throws(IOException::class)
    fun writeFile(inputString: InputStream, rxBuilder: RxBuilder) {
        val fos = FileOutputStream(checkFile(rxBuilder), true)
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
     * @param rxBuilder
     * @param contentLength
     * @throws IOException
     */
    @Synchronized
    @Throws(IOException::class)
    fun deleteFile(rxBuilder: RxBuilder, contentLength: Long) {
        val file = checkFile(rxBuilder)
        if (file.exists()) {
            if (!rxBuilder.isAppendWrite || file.length() >= contentLength) {
                file.delete()
            }
        }
    }

    /** 生成文件
     * @param rxBuilder
     * @return
     * @throws IOException
     */
    @Synchronized
    @Throws(IOException::class)
    private fun checkFile(rxBuilder: RxBuilder): File {
        if (TextUtils.isEmpty(rxBuilder.filePath) || TextUtils.isEmpty(rxBuilder.fileName)) {
            throw IOException("filePath or fileName is null!")
        }
        val fileDir = File(rxBuilder.filePath)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        val file: File = if (rxBuilder.filePath.endsWith("/")) {
            File(rxBuilder.filePath + rxBuilder.fileName)
        } else {
            File(rxBuilder.filePath + "/" + rxBuilder.fileName)
        }
        return file
    }

    @JvmStatic
    fun logger(rxBuilder: RxBuilder, tag: String?, msg: String?) {
        if (rxBuilder.isLogOutPut) {
            Log.e(tag, msg?: "")
        }
    }
}