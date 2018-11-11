package com.dt5000.ischool.utils;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

public class VideoUtil {

    public static void downLoadVideo(Context context, String url, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir("/download/", fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    public static File getFile(String fileName){
        File f = new File(Environment.getExternalStorageDirectory() + "/download/"+fileName);
        if(f.exists())
            return f;
        return null;
    }
}
