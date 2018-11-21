package com.dt5000.ischool.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.VideoUtil;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

import java.io.File;

public class PlayVideoActivity3 extends AppCompatActivity {
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 0x001;

    private NiceVideoPlayer mNiceVideoPlayer;

    private String videoUrl;
    private String videoLocalUrl;
    private String videoName;
    private boolean isSelf;

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
        isSelf = intent.getBooleanExtra("isSelf",false);
    }

    private void init() {
        mNiceVideoPlayer = (NiceVideoPlayer) findViewById(R.id.nice_video_player);
        mNiceVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_NATIVE); // IjkPlayer or MediaPlayer

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

        final File file = new File(getCacheDir() + "/" + videoName);
        if(isSelf && videoLocalUrl!= null){
            mNiceVideoPlayer.setUp(videoLocalUrl, null);
        }else if(file.exists()){
            mNiceVideoPlayer.setUp( getCacheDir() + "/" + videoName, null);
        }else{
            mNiceVideoPlayer.setUp(videoUrl, null);
            VideoUtil.downLoadFile(videoUrl, getCacheDir()+"");
        }

        TxVideoPlayerController controller = new TxVideoPlayerController(this);
        controller.setLenght(98000);
        mNiceVideoPlayer.setController(controller);
        mNiceVideoPlayer.start();
    }

    private void downLoad(){
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
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    public void onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }
}
