package com.bhm.sdk.demo.tools

import android.content.Context
import java.io.File

/**
 * Created by bhm on 2022/9/15.
 */
object Utils {
    @JvmStatic
    fun getFile(context: Context): File {
        return File(
            context.getExternalFilesDir("apk")?.path
                    + File.separator + "demo.apk"
        )
    }
}