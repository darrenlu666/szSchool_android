package com.dt5000.ischool.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.FileUtil;
import com.dt5000.ischool.utils.VideoUtil;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerController;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class PlayVideoActivity3 extends AppCompatActivity implements Handler.Callback {
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 0x001;

    private NiceVideoPlayer mNiceVideoPlayer;

    private String videoUrl;
    private String videoLocalUrl;
    private String videoName;
    private boolean isSelf;
    private Handler mHandler;
    private boolean isCacheFinished;
    private DownLoadTask task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video3);
        initIntentData();
        init();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        videoUrl = intent.getStringExtra("videoUrl");
        videoLocalUrl = intent.getStringExtra("videoLocalUrl");
        videoName = intent.getStringExtra("videoName");
        isSelf = intent.getBooleanExtra("isSelf", false);
    }

    private void init() {
        mNiceVideoPlayer = (NiceVideoPlayer) findViewById(R.id.nice_video_player);
        mNiceVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_NATIVE); // IjkPlayer or MediaPlayer
        mNiceVideoPlayer.continueFromLastPosition(false);
        TxVideoPlayerController controller = new TxVideoPlayerController(this);
        controller.setLenght(98000);

        mNiceVideoPlayer.setController(controller);


        File externalFile = new File(Environment.getExternalStorageDirectory() +
                "/download/", videoName);
        if (externalFile.exists()) {
            findViewById(R.id.iv_downLoad).setVisibility(View.GONE);
        }
        findViewById(R.id.iv_downLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoad();
            }
        });

        final File file = new File(FileUtil.getDiskCacheDir(this) + "/" + videoName);
        if (isSelf && videoLocalUrl != null) {
            mNiceVideoPlayer.setUp(videoLocalUrl, null);
            mNiceVideoPlayer.start();
        } else if (file.exists()) {
            mNiceVideoPlayer.setUp(FileUtil.getDiskCacheDir(this) + "/" + videoName, null);
            mNiceVideoPlayer.start();
        } else {
            mHandler = new Handler(this);
            isUrlSuccess(videoUrl);
        }
        mNiceVideoPlayer.setOnPlayingListener(new NiceVideoPlayer.OnPlayingListener() {
            @Override
            public void onError() {
                File file1 = new File(getCacheDir() + "/" + videoName);
                if (file1.exists()) {
                    file1.delete();
                }
            }
        });

    }

    private void isUrlSuccess(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL myURL = new URL(url);
                    myURL.openStream();
                    Message message = new Message();
                    message.what = 0x002;
                    mHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = 0x001;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 0x001) {
            Toast.makeText(this, "播放地址失效", Toast.LENGTH_SHORT).show();
            finish();
        } else if (msg.what == 0x002) {
            mNiceVideoPlayer.setUp(videoUrl, null);
            mNiceVideoPlayer.start();
            //VideoUtil.downLoadFile(videoUrl, getCacheDir() + "");
            task = new DownLoadTask(videoUrl, FileUtil.getDiskCacheDir(this));
            task.execute();
        }
        return false;
    }

    private void downLoad() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
        } else {
            downLoadVideo();
        }
    }

    private void downLoadVideo() {
        new AlertDialog.Builder(this)
                .setTitle("是否保存到本地？")
                .setPositiveButton("保存",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                VideoUtil.downLoadVideo(PlayVideoActivity3.this, videoUrl, videoName);
                                findViewById(R.id.iv_downLoad).setVisibility(View.GONE);
                            }
                        }).setNegativeButton("取消", null).show();
    }

    class DownLoadTask extends AsyncTask<String, Integer, Boolean> {
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
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            if (progress >= 100) {
                //mNiceVideoPlayer.setUp(getCacheDir() + "/" + videoName, null);
                //mNiceVideoPlayer.start();
                isCacheFinished = true;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(!isCacheFinished){
                String filename = url.substring(url.lastIndexOf("/") + 1);
                File file1 = new File(path + "/" + filename);
                if (file1.exists()) {
                    file1.delete();
                }
            }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_WRITE_ACCESS_PERMISSION) {
            downLoadVideo();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        if(task != null){
            if(!task.isCancelled()){
                task.cancel(true);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }
}
