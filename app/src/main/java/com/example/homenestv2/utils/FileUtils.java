package com.example.homenestv2.utils;

import android.content.Context;
import android.os.Environment;
import java.io.File;

public class FileUtils {
    private static final String APP_DIRECTORY = "HomeNest";

    public static File getAppDirectory(Context context) {
        File appDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            appDir = new File(context.getExternalFilesDir(null), APP_DIRECTORY);
        } else {
            appDir = new File(context.getFilesDir(), APP_DIRECTORY);
        }

        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        return appDir;
    }

    public static File getRecentsDirectory(Context context) {
        File recentsDir = new File(getAppDirectory(context), "recents");
        if (!recentsDir.exists()) {
            recentsDir.mkdirs();
        }
        return recentsDir;
    }
} 