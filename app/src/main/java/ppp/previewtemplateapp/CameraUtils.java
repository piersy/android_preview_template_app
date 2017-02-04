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

    /**
     * Opens and returns the front facing camera. Returns null if there is no front
     * facing camera or there is a problem opening the camera.
     */
    static Camera openFrontFacingCamera() {
        int cameraCount;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    return Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                    break;
                }
            }
        }
        return null;
    }

    public void orientCamera(Camera camera, WindowManager windowManager, int w, int h) {

        Camera.Parameters parameters = camera.getParameters();

        Display display = windowManager.getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_0) {
            parameters.setPreviewSize(h, w);
            camera.setDisplayOrientation(90);
        }

        if (display.getRotation() == Surface.ROTATION_90) {
            parameters.setPreviewSize(w, h);
        }

        if (display.getRotation() == Surface.ROTATION_180) {
            parameters.setPreviewSize(h, w);
        }

        if (display.getRotation() == Surface.ROTATION_270) {
            parameters.setPreviewSize(w, h);
            camera.setDisplayOrientation(180);
        }

        camera.setParameters(parameters);
    }
}
