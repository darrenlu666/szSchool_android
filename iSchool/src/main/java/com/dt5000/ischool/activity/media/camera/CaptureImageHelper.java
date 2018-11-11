/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dt5000.ischool.activity.media.camera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.view.Surface;

import java.util.List;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

public class CaptureImageHelper {
    private final CameraHelperImpl mImpl;

    public CaptureImageHelper(final Context context) {
        if (SDK_INT >= GINGERBREAD) {
            mImpl = new CaptureImageHelperGB();
        } else {
            mImpl = new CaptureHelperBase(context);
        }
    }

    public interface CameraHelperImpl {
        int getNumberOfCameras();

        Camera openCamera(int id);

        Camera openDefaultCamera();

        Camera openCameraFacing(int facing);

        boolean hasCamera(int cameraFacingFront);

        void getCameraInfo(int cameraId, CameraInfo2 cameraInfo);
    }

    public int getNumberOfCameras() {
        return mImpl.getNumberOfCameras();
    }

    public Camera openCamera(final int id) {
        return mImpl.openCamera(id);
    }

    public Camera openDefaultCamera() {
        return mImpl.openDefaultCamera();
    }

    public Camera openFrontCamera() {
        return mImpl.openCameraFacing(CameraInfo.CAMERA_FACING_FRONT);
    }

    public Camera openBackCamera() {
        return mImpl.openCameraFacing(CameraInfo.CAMERA_FACING_BACK);
    }

    public boolean hasFrontCamera() {
        return mImpl.hasCamera(CameraInfo.CAMERA_FACING_FRONT);
    }

    public boolean hasBackCamera() {
        return mImpl.hasCamera(CameraInfo.CAMERA_FACING_BACK);
    }

    public void getCameraInfo(final int cameraId, final CameraInfo2 cameraInfo) {
        mImpl.getCameraInfo(cameraId, cameraInfo);
    }

    public void setCameraDisplayOrientation(final Activity activity, final int cameraId,
                                            final Camera camera) {
        int result = getCameraDisplayOrientation(activity, cameraId);
        camera.setDisplayOrientation(result);
    }

    public int getCameraDisplayOrientation(final Activity activity, final int cameraId) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        CameraInfo2 info = new CameraInfo2();
        getCameraInfo(cameraId, info);
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public static class CameraInfo2 {
        public int facing;
        public int orientation;
    }
    /**
     * Iterate over supported camera preview sizes to see which one best fits the
     * dimensions of the given view while maintaining the aspect ratio. If none can,
     * be lenient with the aspect ratio.
     *
     * @param sizes Supported camera preview sizes.
     * @param w The width of the view.
     * @param h The height of the view.
     * @return Best match camera preview size to fit in the view.
     */
    public static  Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;

        // Start with max value and refine as we iterate over available preview sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;

        // Target view height
        int targetHeight = h;

        // Try to find a preview size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find preview size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * @return the default camera on the device. Return null if there is no camera on the device.
     */
    public static Camera getDefaultCameraInstance() {
        return Camera.open();
    }


    /**
     * @return the default rear/back facing camera on the device. Returns null if camera is not
     * available.
     */
    public static Camera getDefaultBackFacingCameraInstance() {
        return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * @return the default front facing camera on the device. Returns null if camera is not
     * available.
     */
    public static Camera getDefaultFrontFacingCameraInstance() {
        return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }


    /**
     *
     * @param position Physical position of the camera i.e Camera.CameraInfo.CAMERA_FACING_FRONT
     *                 or Camera.CameraInfo.CAMERA_FACING_BACK.
     * @return the default camera on the device. Returns null if camera is not available.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static Camera getDefaultCamera(int position) {
        // Find the total number of cameras available
        int  mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the back-facing ("default") camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == position) {
                return Camera.open(i);

            }
        }

        return null;
    }

}
