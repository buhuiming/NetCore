package com.bhm.sdk.demo.tools

import android.Manifest
import android.content.Context
import android.os.Build
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

    /*Android13细化了存储权限*/
    fun getStoragePermission(context: Context): Array<String>{
        return getStoragePermission(context, 1)
    }

    /*存储权限*/
    fun getStoragePermission(context: Context, chooseMode: Int): Array<String>{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val targetSdkVersion = context.applicationInfo.targetSdkVersion
            return when (chooseMode) {
                1 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                            Manifest.permission.READ_MEDIA_IMAGES
                        )
                    } else if (targetSdkVersion >= Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                }
                2 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                            Manifest.permission.READ_MEDIA_VIDEO
                        )
                    } else if (targetSdkVersion >= Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
                    } else {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                }
                3 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                            Manifest.permission.READ_MEDIA_AUDIO
                        )
                    } else if (targetSdkVersion >= Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
                    } else {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                        )
                    } else if (targetSdkVersion >= Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO
                        )
                    } else {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val targetSdkVersion = context.applicationInfo.targetSdkVersion
            return when (chooseMode) {
                1 -> {
                    if (targetSdkVersion >= Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                }
                2 -> {
                    if (targetSdkVersion >= Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
                    } else {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                }
                3 -> {
                    if (targetSdkVersion >= Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
                    } else {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                }
                else -> {
                    if (targetSdkVersion >= Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO
                        )
                    } else {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                }
            }
        }
        return arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}