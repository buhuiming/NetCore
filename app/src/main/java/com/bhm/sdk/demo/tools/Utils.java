package com.bhm.sdk.demo.tools;

import android.content.Context;

import java.io.File;

/**
 * Created by bhm on 2018/5/29.
 */

public class Utils {

    public static File getFile(Context context){
        return new File(context.getExternalFilesDir("apk").getPath()
                + File.separator + "demo.apk");
    }
}
