package com.dt5000.ischool.activity.media.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.teacher.HomeworkAddActivity;
import com.dt5000.ischool.utils.VideoUtil;

import java.io.File;

import static com.dt5000.ischool.net.UrlProtocol.VIDEO_HOST;


/**
 * 全屏视频播放
 * Created by ldh on 2016/3/10.
 */
public class VideoViewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String EXTRA_URL = "URL";
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 0x001;

    private String mPlayUrl;//播放地址
    private VideoView mVideoView;
    private RelativeLayout mTitleBar;//标题栏
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        mPlayUrl = VIDEO_HOST + getIntent().getStringExtra(EXTRA_URL);
        init();
    }

    private void init() {
        mVideoView = (VideoView) findViewById(R.id.vv_video_view);
        ImageView mBackImageView = (ImageView) findViewById(R.id.imgBtn2);
        RelativeLayout mVideoViewRelativeLayout = (RelativeLayout) findViewById(R.id.rl_video_view);
        mTitleBar = (RelativeLayout) findViewById(R.id.relative_header);
        mBackImageView.setOnClickListener(this);

        MediaController mMediaController = new MediaController(this);
        File file = VideoUtil.getFile(getIntent().getStringExtra(EXTRA_URL));
        if(file != null){
            mVideoView.setVideoURI(Uri.fromFile(file));
        }else{
            mVideoView.setVideoURI(Uri.parse(mPlayUrl));
        }
        mVideoView.setMediaController(mMediaController);
        mMediaController.setMediaPlayer(mVideoView);
        findViewById(R.id.imgBtn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("视频加载中...");
        }
        mProgressDialog.show();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
                mProgressDialog.cancel();
            }
        });



        mVideoViewRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleBar.setVisibility(mTitleBar.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }
        });

        findViewById(R.id.iv_downLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(VideoViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(VideoViewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
                }else{
                    downLoadVideo();
                }
            }
        });
    }

    private void downLoadVideo(){
        new AlertDialog.Builder(this)
                .setTitle("是否保存到本地？")
                .setPositiveButton("保存",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                VideoUtil.downLoadVideo(VideoViewActivity.this,mPlayUrl,getIntent().getStringExtra(EXTRA_URL));
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

    public static void startActivity(Context context, String playUrl, String from, String title) {
        Intent intent = new Intent(context, VideoViewActivity.class);
        intent.putExtra(EXTRA_URL, playUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    public void onClick(View v) {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }
}
