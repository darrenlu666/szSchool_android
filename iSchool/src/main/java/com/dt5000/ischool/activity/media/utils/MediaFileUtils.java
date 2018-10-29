package com.dt5000.ischool.activity.media.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 多媒体文件创建类
 * create by eachann
 */
public class MediaFileUtils {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_AUDIO = 3;
    public static final int DIRECTORY_DOCUMENTS = 4;

    /**
     * Creates a media file in the {@code Environment.DIRECTORY_PICTURES} directory. The directory
     * is persistent and available to other applications like gallery.
     *
     * @param type Media type. Can be video or image.
     * @return A file object pointing to the newly created file.
     */
    public static File getOutputMediaFile(int type,String userName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        String mediaStorageDir = getFileDirs(type);
        if (mediaStorageDir == null) return null;

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else if (type == MEDIA_TYPE_AUDIO) {
            mediaFile = new File(mediaStorageDir + File.separator +
                    userName + timeStamp + ".aac");
        } else if (type == DIRECTORY_DOCUMENTS) {
            mediaFile = new File(mediaStorageDir + File.separator +
                    "LOG_" + timeStamp + ".txt");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Nullable
    private static String getFileDirs(int type) {
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        File mediaStorageDir = null;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "codyy");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES), "codyy");
        } else if (type == MEDIA_TYPE_AUDIO) {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC), "codyy");
        } else if (type == DIRECTORY_DOCUMENTS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS), "codyy");
            } else {
                File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
                boolean isPresent = true;
                if (!docsFolder.exists()) {
                    isPresent = docsFolder.mkdir();
                }
                if (isPresent) {
                    mediaStorageDir = new File(docsFolder.getAbsolutePath(), "codyy");
                }
            }
        }

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (mediaStorageDir != null && !mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MediaFileUtils", "failed to create directory");
                return null;
            }
        }
        return mediaStorageDir == null ? null : mediaStorageDir.getPath();
    }

    /**
     * 通知媒体库更新文件
     *
     * @param context  上下文
     * @param filePath 文件全路径
     */
    public static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    /**
     * 通知媒体库更新文件夹
     *
     * @param context 上下文
     * @param type    文件夹
     */
    @SuppressWarnings("unused")
    public static void scanDirs(Context context, int type) {
        String filePath = getFileDirs(type);
        if (TextUtils.isEmpty(filePath)) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            scanFile(context, filePath);
        } else {
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
            scanIntent.setData(Uri.fromFile(new File(filePath)));
            context.sendBroadcast(scanIntent);
        }
    }
}
