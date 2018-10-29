package com.dt5000.ischool.activity.media.other;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Build;
import android.util.Log;


import com.dt5000.ischool.activity.media.utils.MediaFileUtils;

import java.io.File;

/**
 * Used to record audio and video
 * added by eachann 2016-02-25
 * <p/>
 * Examples of supported video encoding parameters for the H.264 Baseline Profile codec.
 * ****************************************************************************************
 * *                 * SD (Low quality) * SD (High quality) * HD 720p (N/A on all devices)*
 * ****************************************************************************************
 * *Video resolution *  176 x 144 px    *   480 x 360 px    *    1280 x 720 px            *
 * ****************************************************************************************
 * *Video frame rate *  12 fps          *   30 fps          *    30 fps                   *
 * ****************************************************************************************
 * *Video bitrate    *  56 Kbps         *   500 Kbps        *    2 Mbps                   *
 * ****************************************************************************************
 * *Audio codec      *  AAC-LC          *   AAC-LC          *    AAC-LC                   *
 * ****************************************************************************************
 * *Audio channels   *  1 (mono)        *   2 (stereo)      *    2 (stereo)               *
 * ****************************************************************************************
 * *Audio bitrate    *  24 Kbps         *   128 Kbps        *    192 Kbps                 *
 * ****************************************************************************************
 */
public class DefaultVideoRecorder extends VideoBaseRecorder {
    private final String TAG = DefaultVideoRecorder.class.getSimpleName();
    private MediaRecorder recorder;
    private CameraWrapper mCameraPair;
    private boolean prepared = false;
    private boolean preparing = false;
    private int mMaxDuration = 10;//10s
    private int mMaxFileSize = 500 * 1024 * 1024;//500Mb
    private boolean reachMaxDuration = false;
    private boolean reachMaxFileSize = false;
    private static int sRotation = 90;
    private static Size sVideoSize;

    public DefaultVideoRecorder() {
        super();
    }

    public static void setRecorderRotation(int rotation) {
        sRotation = rotation;
    }

    public static void setRecorderVideoSize(Size size) {
        //TODO This may cause problem on some devices when recording...
        //sVideoSize = size;
    }

    public void setMaxDuration(int seconds) {
        mMaxDuration = seconds;
    }

    public int getMaxFileSize() {
        return mMaxFileSize;
    }

    public void setMaxFileSize(int mMaxFileSize) {
        if (mMaxFileSize < Integer.MAX_VALUE && mMaxFileSize > 0) {
            this.mMaxFileSize = mMaxFileSize;
        }
    }

    public int getMaxDurationInSeconds() {
        return mMaxDuration;
    }

    public boolean reachRecorderMaxDuration() {
        return reachMaxDuration;
    }

    public boolean reachRecorderMaxFileSize() {
        return reachMaxFileSize;
    }

    public void bindCamera(CameraWrapper camera) {
        mCameraPair = camera;
    }

    public boolean isPrepared() {
        return prepared;
    }

    public boolean isPreparing() {
        return preparing;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean prepare() {
        if (isPreparing()) return false;

        preparing = true;
        recorder = null;
        recorder = new MediaRecorder();
        recorder.setOrientationHint(90);
        recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {

            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    reachMaxDuration = true;
                } else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                    reachMaxFileSize = true;
                }
            }
        });

        recorder.setOnErrorListener(new OnErrorListener() {

            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                mRecordingListener.onError("" + extra);
                if (extra == -1007) {
                    prepare();
                    isRecording = false;
                }
            }
        });
        recorder.reset();
        Camera mCamera = mCameraPair.getCamera();
        mCamera.unlock();
        recorder.setCamera(mCamera);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //modify by kmdai
//        recorder.setOrientationHint(sRotation);
        int camId = mCameraPair.getCameraInfo().facing;
        CamcorderProfile profile = null;

        //if(isGingerbreadPlus()){
        //String name = Build.MODEL;
        //if(name.equals("HTC_7060")){
        //if(CamcorderProfile.hasProfile(camId, CamcorderProfile.QUALITY_LOW)){
        //profile = CamcorderProfile.get(camId, CamcorderProfile.QUALITY_LOW);
        //}
        //}else if(CamcorderProfile.hasProfile(camId, CamcorderProfile.QUALITY_1080P)){
        //profile = CamcorderProfile.get(camId, CamcorderProfile.QUALITY_1080P);
        //}else if(CamcorderProfile.hasProfile(camId, CamcorderProfile.QUALITY_720P)){
        //profile = CamcorderProfile.get(camId, CamcorderProfile.QUALITY_720P);
        //}else if(CamcorderProfile.hasProfile(camId, CamcorderProfile.QUALITY_480P)){
        //profile = CamcorderProfile.get(camId, CamcorderProfile.QUALITY_480P);
        //}
        //}
        if (camId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            if (CamcorderProfile.hasProfile(camId, CamcorderProfile.QUALITY_720P)) {
                profile = CamcorderProfile.get(camId, CamcorderProfile.QUALITY_720P);//如果是后置摄像头，采用720P进行录制，视频大小限制500M
            }
        } else {
            if (CamcorderProfile.hasProfile(camId, CamcorderProfile.QUALITY_HIGH)) {//判断前置摄像头是否支持高清画质
                profile = CamcorderProfile.get(camId, CamcorderProfile.QUALITY_HIGH);//如果是前置摄像头，采用高清进行录制，视频大小限制500M
            } else if (CamcorderProfile.hasProfile(camId, CamcorderProfile.QUALITY_LOW)) {//若前置不支持高清，则判断是否支持低清画质
                profile = CamcorderProfile.get(camId, CamcorderProfile.QUALITY_LOW);//如果是前置摄像头支持低清，则进行录制，视频大小限制500M
            }
        }
        if (profile != null) {
            Log.i(TAG, "profile exists");
            profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
            profile.videoCodec = MediaRecorder.VideoEncoder.H264;
            //profile.videoBitRate = 500000;
            //profile.videoFrameRate = 30000;
            if (sVideoSize != null) {
                profile.videoFrameWidth = sVideoSize.width;
                profile.videoFrameHeight = sVideoSize.height;
            }
            //profile.audioBitRate = 128000;
            //profile.audioChannels = 2;
            //profile.audioCodec = MediaRecorder.AudioEncoder.AMR_NB;
            //profile.audioSampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
            recorder.setProfile(profile);

            Log.i(TAG, "RECORDER profile: quality=" + profile.quality + ", vfw = " + profile.videoFrameWidth + ", vfh = " + profile.videoFrameHeight);
            Log.i(TAG, "RECORDER profile audio: brate=" + profile.audioBitRate + ", chanel=" + profile.audioChannels
                    + ", codec=" + profile.audioCodec + ", srate= " + profile.audioSampleRate);
        } else {
            //http://developer.android.com/guide/appendix/media-formats.html
            Log.i(TAG, "profile not exists");
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            recorder.setVideoFrameRate(30);//30 fps
            recorder.setVideoEncodingBitRate(500000);//500 Kbps
            if (sVideoSize != null) {
                recorder.setVideoSize(sVideoSize.width, sVideoSize.height);
            }

            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioSamplingRate(
                    AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC));
            recorder.setAudioEncodingBitRate(128000);//128 Kbps
            recorder.setAudioChannels(2);//2 (stereo)
        }

        try {
            File f = MediaFileUtils.getOutputMediaFile(MediaFileUtils.MEDIA_TYPE_VIDEO,"");//获取文件输出路径
            recorder.setOutputFile(f.getAbsolutePath());
            //recorder.setMaxDuration(mMaxDuration * 1000);
            recorder.setMaxFileSize(getMaxFileSize());
            super.setOutputFile(f);
            recorder.prepare();
            prepared = true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        preparing = false;

        super.mRecordingListener.onPrepared();
        return true;
    }

    @Override
    public void startRecording() {
        if (!isPrepared()) return;

        if (isRecording) {
            return;
        }

        isRecording = true;

        new Thread(new Runnable() {

            @Override
            public void run() {
                record();
            }
        }).start();
    }

    private void record() {
        try {
            recorder.start();
            super.mRecordingListener.onStart();
        } catch (Exception e) {
            e.printStackTrace();
            super.mRecordingListener.onError(e.getMessage());

            stopRecording(true);
        }
    }

    @Override
    public void stopRecording(boolean isCancel) {
        if (recorder != null && isRecording) {
            synchronized (recorder) {
                try {
                    recorder.stop();
                    isRecording = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        if (!isCancel) mRecordingListener.onSuccess();
        else {
            deleteOutputFile();
            Log.i(TAG, "deleteOutputFile");
        }
    }

    @Override
    public void release() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

}
