package ppp.previewtemplateapp;

import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by piers on 04/02/17.
 */

class CameraUtils {
    private static final String TAG = "CameraUtils";

    static class CameraAndInfo {
        Camera camera;
        Camera.CameraInfo info;
    }
    /**
     * Opens and returns the camera and info for the given facing. Returns null if there is no front
     * facing camera or there is a problem opening the camera.
     */
    static CameraAndInfo getCamera(int facing) {
        int cameraCount;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == facing) {
                try {
                    CameraAndInfo cni = new CameraAndInfo();
                    cni.camera = Camera.open(camIdx);
                    cni.info = cameraInfo;
                    return cni;
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                    break;
                }
            }
        }
        Log.e(TAG, "Returning null camera: ");
        return null;
    }

    /**
     * Ensures that the camera is correctly aligned with the surface that it is displaying on.
     */
    static void setCameraDisplayOrientation(CameraAndInfo cni, WindowManager windowManager){

        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0;
                Log.e(TAG, "Surface rotation is 0");
                break;
            case Surface.ROTATION_90: degrees = 90;
                Log.e(TAG, "Surface rotation is 90");
                break;
            case Surface.ROTATION_180: degrees = 180;
                Log.e(TAG, "Surface rotation is 180");
                break;
            case Surface.ROTATION_270: degrees = 270;
                Log.e(TAG, "Surface rotation is 270");
                break;
        }

        int result;
        Log.e(TAG, "Camera orientation is "+cni.info.orientation);
        if (cni.info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cni.info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (cni.info.orientation - degrees + 360) % 360;
        }
        Log.e(TAG, "Setting camera display orientation "+result);
        cni.camera.setDisplayOrientation(result);
    }
}
