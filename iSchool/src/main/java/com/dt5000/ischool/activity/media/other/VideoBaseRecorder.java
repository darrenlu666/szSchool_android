package com.dt5000.ischool.activity.media.other;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class VideoBaseRecorder {
    protected File mOutputFile;
    protected boolean isRecording = false;
    protected OnRecordingListener mRecordingListener;

    public interface OnRecordingListener {
        void onPrepared();

        void onStart();

        void onError(String error);

        void onFailure();

        void onSuccess();

        void onCancel();

        void onReachMaxSize();

        void onReachMaxDuration();
    }

    public VideoBaseRecorder() {
        mRecordingListener = new OnRecordingListener() {

            @Override
            public void onPrepared() {
            }

            @Override
            public void onSuccess() {
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFailure() {
            }

            @Override
            public void onError(String error) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onReachMaxSize() {

            }

            @Override
            public void onReachMaxDuration() {

            }
        };
    }


    protected void setOutputFile(File outputFile) {
        this.mOutputFile = outputFile;
    }

    public void setOnRecordingListener(OnRecordingListener listener) {
        mRecordingListener = listener;
    }

    public File getOutputFile() {
        return mOutputFile;
    }

    public boolean deleteOutputFile() {
        if (mOutputFile != null && mOutputFile.exists()) {
            return mOutputFile.delete();
        }
        return false;
    }

    public boolean isRecording() {
        return isRecording;
    }


    public abstract boolean prepare();

    public abstract void startRecording();

    public abstract void stopRecording(boolean isCancel);

    public abstract void release();

}
