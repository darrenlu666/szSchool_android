package com.dt5000.ischool.activity.media.activity;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dt5000.ischool.R;
import com.dt5000.ischool.activity.media.other.CameraWrapper;
import com.dt5000.ischool.activity.media.other.CaptureVideoHelper;
import com.dt5000.ischool.activity.media.other.DefaultVideoRecorder;
import com.dt5000.ischool.activity.media.other.VideoBaseRecorder;
import com.dt5000.ischool.activity.media.utils.DurationUtils;

import java.io.File;
import java.io.IOException;

/**
 * Customize Video Recording
 * Created by eachann on 2016/2/23.
 * Video recording process:
 * if(!isCameraSupported){
 * finish();
 * }else{
 * getCamera();
 * addCameraAttributes();
 * initView();
 * initEvent();
 * if(surfaceTextureAvaliable){
 * initRecorder();
 * startCameraPreview();
 * prepareRecorder();
 * startRecord();
 * stopRecord();
 * getOutputFile();
 * }
 * }
 */
@SuppressWarnings("deprecation")
public class RecordVideoActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static final String TAG = RecordVideoActivity.class.getSimpleName();
    /**
     * preview view
     */
    private TextureView mPreview;
    /**
     * start and stop button
     */
    private Button mCaptureButton;
    /**
     * switch camera front or back
     */
    private ImageView mChangeButton;
    /**
     * finish this activity
     */
    private TextView mTvCancle;
    /**
     * video duration
     */
    private TextView mTvDuration;
    /**
     * video file path
     */
    private String mFilePath;
    /**
     * camera wrapper
     */
    private CameraWrapper mCameraPair;
    /**
     * how long record
     */
    private long seconds = 0;
    /**
     * video recorder
     */
    private DefaultVideoRecorder mRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_video);
        if (!getCameraObject(true, Camera.CameraInfo.CAMERA_FACING_BACK)) {
            Toast.makeText(getBaseContext(),
                    R.string.not_support_camera, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mPreview = (TextureView) findViewById(R.id.surface_view);
        mCaptureButton = (Button) findViewById(R.id.btn_record);
        mChangeButton = (ImageView) findViewById(R.id.iv_camera_switch);
        mChangeButton.setVisibility(Camera.getNumberOfCameras() > 1 ? View.VISIBLE
                : View.GONE);
        mChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
        mTvCancle = (TextView) findViewById(R.id.tv_cancel);
        mTvDuration = (TextView) findViewById(R.id.tv_duration);
        mTvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPreview.setSurfaceTextureListener(this);
        initRecorder();
    }

    /**
     * switch camera
     */
    private void switchCamera() {
        //Close current camera firstly
        Camera mCamera = mCameraPair.getCamera();
        mCamera.lock();
        mCamera.stopPreview();
        mCamera.release();
        if (mRecorder.getOutputFile() != null && mRecorder.getOutputFile().length() == 0) {
            mRecorder.deleteOutputFile();
            Log.i(TAG, "onPause deleteOutputFile");
        }
        int camId = mCameraPair.getCameraInfo().facing;
        int toggleCamId = (camId == Camera.CameraInfo.CAMERA_FACING_BACK ?
                Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK);
        if (!getCameraObject(false, toggleCamId)) {
            return;
        }

        initRecorder();
        startCameraPreview();
    }

    /**
     * @param first    To determine whether you need to check is camera supported.
     * @param cameraId Camera id.
     * @return is camera supported
     */
    private boolean getCameraObject(boolean first, int cameraId) {
        if (first && !CaptureVideoHelper.isCameraSupported(this)) return false;

        mCameraPair = CaptureVideoHelper.getCamera(cameraId);
        if (mCameraPair == null && Camera.getNumberOfCameras() > 1) {
            int toggleCamId = (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ?
                    Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK);
            mCameraPair = CaptureVideoHelper.getCamera(toggleCamId);
        }
        if (mCameraPair != null) {
            CaptureVideoHelper.addCameraAttributes(this, mCameraPair);
            return true;
        }
        return false;
    }

    /**
     * The capture button controls all user interaction. When recording, the button click
     * stops recording, releases {@link android.media.MediaRecorder} and {@link Camera}. When not recording,
     * it prepares the {@link android.media.MediaRecorder} and starts recording.
     *
     * @param v the view generating the event.
     */
    public void onCaptureClick(View v) {
        String tag = (String) v.getTag();
        if (mRecorder.isRecording() && "stop".equals(tag)) {
            mRecorder.stopRecording(false);
        } else if ("start".equals(tag)) {
            mRecorder.startRecording();
            mCaptureButton.setEnabled(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCaptureButton.setEnabled(true);
                }
            }, 1000);
        }
    }

    /**
     * Invoked when a {@link TextureView}'s SurfaceTexture is ready for use.
     *
     * @param surface The surface returned by
     *                {@link TextureView#getSurfaceTexture()}
     * @param width   The width of the surface
     * @param height  The height of the surface
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mPreview.setFocusable(true);
        mPreview.setKeepScreenOn(true);
        startCameraPreview();
    }

    /**
     * Invoked when the {@link SurfaceTexture}'s buffers size changed.
     *
     * @param surface The surface returned by
     *                {@link TextureView#getSurfaceTexture()}
     * @param width   The new width of the surface
     * @param height  The new height of the surface
     */
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    /**
     * Invoked when the specified {@link SurfaceTexture} is about to be destroyed.
     * If returns true, no rendering should happen inside the surface texture after this method
     * is invoked. If returns false, the client needs to call {@link SurfaceTexture#release()}.
     * Most applications should return true.
     *
     * @param surface The surface about to be destroyed
     */
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    /**
     * Invoked when the specified {@link SurfaceTexture} is updated through
     * {@link SurfaceTexture#updateTexImage()}.
     *
     * @param surface The surface just updated
     */
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void startCameraPreview() {
        Camera mCamera = mCameraPair.getCamera();
        if (mCamera != null) {
            mCamera.lock();
            try {
                mCamera.setPreviewTexture(mPreview.getSurfaceTexture());
                mCamera.startPreview();
                //CameraHelper.addCameraAutoFocusFeature(mCamera);
            } catch (IOException e) {
                e.printStackTrace();
            }
            prepareRecorder();
        }
    }

    private void prepareRecorder() {
        mCaptureButton.setClickable(false);
        new Thread() {
            public void run() {
                mRecorder.prepare();
            }
        }.start();
    }

    private void initRecorder() {
        if (mRecorder != null) {
            mRecorder.release();
        }
        mRecorder = new DefaultVideoRecorder();
        mRecorder.setMaxFileSize(getIntent().getIntExtra("EXTRA_SIZE", 500 * 1024 * 1024));//500M default
        mRecorder.bindCamera(mCameraPair);
        mRecorder.setOnRecordingListener(new VideoBaseRecorder.OnRecordingListener() {

            @Override
            public void onPrepared() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mCaptureButton.setVisibility(View.VISIBLE);
                        mCaptureButton.setClickable(true);
                        setUIState(false);
                    }
                });
            }

            @Override
            public void onStart() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        setUIState(true);
                    }
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            int errorCode = Integer.valueOf(error);
                            if (errorCode == -1007) {
                                Toast.makeText(getApplicationContext(),
                                        R.string.stop_recording_video_err, Toast.LENGTH_SHORT).show();
                            }
                            setUIState(false);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                });
            }

            @Override
            public void onFailure() {
                onError(null);
            }

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        setUIState(false);
                        mFilePath = mRecorder.getOutputFile().getAbsolutePath();
                        Log.i(TAG, mRecorder.getOutputFile().getAbsolutePath());
                        sendScanFileAction(mFilePath);
                        showPreviewPage();
                    }
                });
            }

            @Override
            public void onCancel() {
                onFailure();
            }

            @Override
            public void onReachMaxSize() {

            }

            @Override
            public void onReachMaxDuration() {

            }
        });
    }


    private void setUIState(boolean recording) {
        if (!recording) {
            mCaptureButton.setBackgroundResource(R.drawable.ic_exam_media_start);
            mCaptureButton.setTag("start");
            mTvCancle.setVisibility(View.VISIBLE);
            seconds = 0;
            mUIHandler.removeCallbacks(mUIRunnable);
            mTvDuration.setCompoundDrawables(null, null, null, null);
            mTvDuration.setVisibility(View.GONE);
            mChangeButton.setVisibility(View.VISIBLE);
        } else {
            mCaptureButton.setBackgroundResource(R.drawable.ic_exam_media_stop);
            mCaptureButton.setTag("stop");
            mTvCancle.setVisibility(View.GONE);
            seconds = 0;
            mUIHandler.postDelayed(mUIRunnable, 0);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_recording);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mTvDuration.setCompoundDrawables(drawable, null, null, null);
            mTvDuration.setVisibility(View.VISIBLE);
            mChangeButton.setVisibility(View.INVISIBLE);
        }
    }

    private Handler mUIHandler = new Handler();
    private Runnable mUIRunnable = new Runnable() {

        @Override
        public void run() {
            mTvDuration.setText(DurationUtils.formatDuration(seconds++));
           /* if (mRecorder != null && seconds >= mRecorder.getMaxDurationInSeconds() &&
                    mRecorder.reachRecorderMaxDuration()) {*/
            if (mRecorder != null && mRecorder.reachRecorderMaxFileSize()) {
                mRecorder.stopRecording(false);
                /*Log.i(TAG, mRecorder.getOutputFile().getAbsolutePath());
                mFilePath = mRecorder.getOutputFile().getAbsolutePath();
                sendScanFileAction(mFilePath);
                showPreviewPage();*/
            } else mUIHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        int cameraId;
        if (mCameraPair != null) {
            cameraId = mCameraPair.getCameraInfo().facing;
        } else cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

        if (!getCameraObject(false, cameraId)) {
            Toast.makeText(getBaseContext(),
                    R.string.not_support_camera, Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        initRecorder();
        startCameraPreview();
        if (mPreview.isShown()) {
            //After screen OFF >  screen ON
            //startCameraPreview();
        } //else {
        //After press HOME
        // }
    }

    @Override
    protected void onPause() {
        if (mRecorder.isRecording()) {
            mRecorder.stopRecording(true);//取消
        } else {
            if (mRecorder.getOutputFile() != null && mRecorder.getOutputFile().length() == 0) {
                mRecorder.deleteOutputFile();
                Log.i(TAG, "onPause deleteOutputFile");
            }
        }
        if (mRecorder != null) {
            mRecorder.release();
        }
        super.onPause();
    }

    private void sendScanFileAction(String path) {
        if (TextUtils.isEmpty(path))
            return;
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mFilePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraPair != null) {
            mCameraPair.getCamera().lock();
            mCameraPair.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mRecorder.isRecording()) {
            super.onBackPressed();
        }
    }

    private void showPreviewPage() {
        Intent intent = new Intent(RecordVideoActivity.this, VideoPlayerActivity.class);
        intent.putExtra("EXTRA_VIDEO_TYPE", "RECORD");
        intent.putExtra("EXTRA_VIDEO_PATH", mFilePath);
        intent.putExtra("EXTRA_VIDEO_THUMB_PATH", mFilePath);
        intent.putExtra("EXTRA_VIDEO_DURATION", mTvDuration.getText().toString());
        startActivityForResult(intent, 1 << 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 << 2 ) {
            if (resultCode == VideoPlayerActivity.EXTRA_RESULT_RETRY) {
                //initRecorder();
                //startCameraPreview();
                //switchCamera();
            } else if (resultCode == RESULT_OK && data != null) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
