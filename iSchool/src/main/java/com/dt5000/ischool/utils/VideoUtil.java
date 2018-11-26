package com.dt5000.ischool.utils;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class VideoUtil {

    /**
     * 保存到外部存储设备
     *
     * @param context
     * @param url
     * @param fileName
     */
    public static void downLoadVideo(Context context, String url, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir("/download/", fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }


    public static void downLoadFile(String url, String path) {
        DownLoadTask task = new DownLoadTask(url, path);
        task.execute();
    }

    static class DownLoadTask extends AsyncTask<String, String, Boolean> {
        private String url;
        private String path;

        public DownLoadTask(String url, String path) {
            this.url = url;
            this.path = path;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                final long startTime = System.currentTimeMillis();
                Log.i("DOWNLOAD", "startTime=" + startTime);
                //下载函数
                String filename = url.substring(url.lastIndexOf("/") + 1);
                //获取文件名
                URL myURL = new URL(url);
                URLConnection conn = myURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                int fileSize = conn.getContentLength();//根据响应获取文件大小
                if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
                if (is == null) throw new RuntimeException("stream is null");
                File file1 = new File(path);
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                //把数据存入路径+文件名
                FileOutputStream fos = new FileOutputStream(path + "/" + filename);
                byte buf[] = new byte[1024];
                int downLoadFileSize = 0;
                do {
                    //循环读取
                    int numread = is.read(buf);
                    if (numread == -1) {
                        break;
                    }
                    fos.write(buf, 0, numread);
                    downLoadFileSize += numread;
                    //更新进度条
                } while (true);

                Log.i("DOWNLOAD", "download success");
                Log.i("DOWNLOAD", "totalTime=" + (System.currentTimeMillis() - startTime));

                is.close();
                return true;
            } catch (Exception ex) {
                Log.e("DOWNLOAD", "error: " + ex.getMessage(), ex);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if (!s) {
                String filename = url.substring(url.lastIndexOf("/") + 1);
                File file1 = new File(path + "/" + filename);
                if (file1.exists()) {
                    file1.delete();
                }
            }
        }
    }

    public static boolean isVideo(String url) {
        String lowCaseUrl = url.toLowerCase();
        if (lowCaseUrl.contains(".mp4") ||
                url.contains(".flv") ||
                url.contains(".avi") ||
                url.contains(".rmvb") ||
                url.contains(".mov") ||
                url.contains(".wmv")) return true;
        return false;
    }

}
