package ppp.previewtemplateapp;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by piers on 04/02/17.
 */

public class Preview implements SurfaceHolder.Callback {

    private final SurfaceHolder holder;
    private final Context context;

    public Preview(SurfaceView previewSurface, Context context) {
        holder = previewSurface.getHolder();
        this.context = context;
        holder.addCallback(this);
    }

    private Camera camera = null;
    private final String TAG = "Preview";

    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // no-op -- wait until surfaceChanged()
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: ");
        camera = openFrontFacingCameraGingerbread();
        if (camera == null) return;
        Camera.Parameters params = camera.getParameters();
        if (params.getPreviewFormat() != ImageFormat.NV21) return;
        try {
            camera.setPreviewDisplay(holder);
        } catch (Throwable t) {
            Log.e(TAG, "Exception in setPreviewDisplay()", t);
            Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "Camera focus mode: " + params.getFocusMode());

        Camera.Size size = getOptimalPreviewSize(params.getSupportedPreviewSizes(), width, height);
        params.setPreviewSize(size.width, size.height);
        camera.setParameters(params);

        int bufferSize = ImageFormat.getBitsPerPixel(ImageFormat.NV21) * size.width * size.height / 8;
        camera.setPreviewCallbackWithBuffer(previewCallback);
        camera.addCallbackBuffer(new byte[bufferSize]);
        camera.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // no-op
    }

    /**
     * Should be called when app is paused, allows the Preview to stop and release the camera.
     */
    public void onPause(){
        camera.stopPreview();
        camera.release();
    }

    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.i(TAG, "onPreviewFrame: ");
            camera.addCallbackBuffer(data);
//                Rect rect = new Rect(0, 0, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height);
//                YuvImage img = new YuvImage(data, ImageFormat.NV21, rect.width(), rect.height(), null);
//                OutputStream outStream = null;
//                File file = new File("preview_" + imageNum);
//                imageNum++;
//                try {
//                    outStream = new FileOutputStream(file);
//                    img.
//                            img.compressToJpeg(rect, 100, outStream);
//                    outStream.flush();
//                    outStream.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
        }


    };


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
   Log.e(TAG,"SurfaceWidth:"+w);
    Log.e(TAG,"SurfaceHeight:"+h);
    final double ASPECT_TOLERANCE = 0.05;
    double targetRatio = (double) w/h;

    if (sizes==null) return null;

    Camera.Size optimalSize = null;

    double minDiff = Double.MAX_VALUE;

    int targetHeight = h;

    // Find size
    for (Camera.Size size : sizes) {
      Log.e(TAG,"--------------------");
      Log.e(TAG,"Width:"+size.width);
      Log.e(TAG,"Height:"+size.height);
      double ratio = (double) size.width / size.height;
      if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
      if (Math.abs(size.height - targetHeight) < minDiff) {
        optimalSize = size;
        minDiff = Math.abs(size.height - targetHeight);
      }
    }

    if (optimalSize == null) {
      minDiff = Double.MAX_VALUE;
      for (Camera.Size size : sizes) {
        if (Math.abs(size.height - targetHeight) < minDiff) {
          optimalSize = size;
          minDiff = Math.abs(size.height - targetHeight);
        }
      }
    }
    Log.e(TAG,"--------------------");
    Log.e(TAG,"OptimalWidth:"+optimalSize.width);
    Log.e(TAG,"OptimalHeight:"+optimalSize.height);
    return optimalSize;
    }

}
