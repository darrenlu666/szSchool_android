package com.dt5000.ischool.activity.media.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.media.utils.MediaFileUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Custom Video Players
 * Created by eachann on 2016/2/22.
 */
public class VideoPlayerActivity extends AppCompatActivity {
    protected static final int EXTRA_RESULT_RETRY = 0X001;
    private SurfaceView mSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;
    private MediaPlayer mMediaPlayer = null;
    /**
     * play button
     */
    private ImageView mPlay;
    /**
     * title bar;
     * cancle button;
     * upload button;
     */
    private TextView mToolbar, mCancle, mUpload;
    private SimpleDraweeView mSimpleDraweeView;
    private LinearLayout mBottomLinearLayout;
    /**
     * video path
     */
    private String mFilePath;
    /**
     * video current position
     */
    private int mPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt("position", mPosition);
            mFilePath = savedInstanceState.getString("path", mFilePath);
        }
        setContentView(R.layout.activity_task_video_player);
        initView();
        initController();
    }

    /**
     * init controller
     */
    private void initController() {
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSimpleDraweeView.setVisibility(View.GONE);
                if (mMediaPlayer.isPlaying()) {
                    mPosition = mMediaPlayer.getCurrentPosition();
                    mMediaPlayer.pause();
                    mPlay.setImageDrawable(ContextCompat.getDrawable(VideoPlayerActivity.this, R.drawable.ic_exam_video_play));
                } else {

                    if (mPosition > 0) {
                        mMediaPlayer.seekTo(mPosition);
                    } else {
                        mMediaPlayer.start();
                    }
                    mPlay.setImageDrawable(ContextCompat.getDrawable(VideoPlayerActivity.this, R.drawable.ic_exam_video_stop));
                }
            }
        });
    }

    /**
     * format time
     *
     * @param time video duration
     * @return HH:mm:ss
     */
    private String formatTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(new Date(time));
    }

    /**
     * init mediaPlayer
     */
    private void initMediaPlayer() {
        mFilePath = getIntent().getStringExtra("EXTRA_VIDEO_PATH");
        if (mMediaPlayer == null) {
            try {
//                mMediaPlayer = MediaPlayer.create(this, Uri.parse(mFilePath));
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(this, Uri.parse(mFilePath));
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mp.start();
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mp == null) {
                    mBottomLinearLayout.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "视频破损", Toast.LENGTH_SHORT).show();
                    return;
                }
                mp.setLooping(false);
                // on prepared ,set display with holer
                mp.setDisplay(mSurfaceHolder);
                //get total video duration from mediaPlayer,set to title bar
                mToolbar.setText(formatTime(mMediaPlayer.getDuration()));
                // init mTimer
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(WHAT);
                    }
                }, 0, 1000);
            }
        });

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.reset();

                return false;
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mToolbar.setText(getIntent().getStringExtra("EXTRA_VIDEO_DURATION"));
                mPosition = 0;
                mPlay.setImageDrawable(ContextCompat.getDrawable(VideoPlayerActivity.this, R.drawable.ic_exam_video_play));
            }
        });
    }

    private Timer mTimer = null;
    private final static int WHAT = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case WHAT:
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        int currentPlayer = 0;
                        try {
                            currentPlayer = mMediaPlayer.getCurrentPosition();
                            if (currentPlayer > 0) {
                                mMediaPlayer.getCurrentPosition();
                                mToolbar.setText(formatTime(currentPlayer));

                            } else {
                                mToolbar.setText(getString(R.string.video_duration_default_format));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    break;

                default:
                    break;
            }
        }

    };

    /**
     * init view
     */
    private void initView() {
        mPlay = (ImageView) findViewById(R.id.tv_play);
        mToolbar = (TextView) findViewById(R.id.tool_bar);
        mCancle = (TextView) findViewById(R.id.tv_cancel);
        mBottomLinearLayout = (LinearLayout) findViewById(R.id.ll_task_video_player);
        mSimpleDraweeView = (SimpleDraweeView) findViewById(R.id.iv_place_holder);
        mSimpleDraweeView.setImageURI(Uri.parse("file://" + getIntent().getStringExtra("EXTRA_VIDEO_THUMB_PATH")));
        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCancle.getText().toString().equals(getString(R.string.video_retry))) {
                    File file = new File(mFilePath);
                    file.delete();
                    MediaFileUtils.scanFile(VideoPlayerActivity.this, mFilePath);
                }
                setResult(EXTRA_RESULT_RETRY);
                finish();
            }
        });
        mUpload = (TextView) findViewById(R.id.tv_upload);
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("EXTRA_DATA", mFilePath);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        if (mSurfaceView != null)
            mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initMediaPlayer();

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {

            }
        });
        if (getIntent().getStringExtra("EXTRA_VIDEO_TYPE") != null) {
            mCancle.setText(getString(R.string.video_retry));
            mUpload.setText(getString(R.string.video_confirm));
        }
    }

    @Override
    protected void onPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mPosition = mMediaPlayer.getCurrentPosition();
            mMediaPlayer.pause();
            mPlay.setImageDrawable(ContextCompat.getDrawable(VideoPlayerActivity.this, R.drawable.ic_exam_video_play));
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mTimer != null)
            mTimer.cancel();
        mHandler.removeMessages(WHAT);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("position", mPosition);
        outState.putString("path", mFilePath);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null)
            mTimer.cancel();

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }
}