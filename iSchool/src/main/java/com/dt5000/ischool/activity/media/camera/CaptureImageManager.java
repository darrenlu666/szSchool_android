package com.dt5000.ischool.activity.media.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.Stack;

/**
 * 相机管理类
 */
public class CaptureImageManager {

    private static CaptureImageManager mInstance;
    private Stack<Activity> cameras = new Stack<Activity>();

    public static CaptureImageManager getInst() {
        if (mInstance == null) {
            synchronized (CaptureImageManager.class) {
                if (mInstance == null)
                    mInstance = new CaptureImageManager();
            }
        }
        return mInstance;
    }

    //打开照相界面
    public void openCamera(Context context) {
        Intent intent = new Intent(context, CaptureImageActivity.class);
        context.startActivity(intent);
    }

    public void close() {
        for (Activity act : cameras) {
            try {
                act.finish();
            } catch (Exception e) {

            }
        }
        cameras.clear();
    }

    public void addActivity(Activity act) {
        cameras.add(act);
    }

    public void removeActivity(Activity act) {
        cameras.remove(act);
    }


}
