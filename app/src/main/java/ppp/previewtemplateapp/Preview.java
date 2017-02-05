package ppp.previewtemplateapp;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import static ppp.previewtemplateapp.CameraUtils.*;

/**
 * Created by piers on 04/02/17.
 */

public class Preview implements SurfaceHolder.Callback {

    private final SurfaceHolder holder;
    private SurfaceView previewSurface;
    private final Context context;
    private WindowManager windowManager;
    private LinearLayout.LayoutParams lp;

    public Preview(SurfaceView previewSurface, Context context, WindowManager windowManager) {
        holder = previewSurface.getHolder();
        this.previewSurface = previewSurface;
        this.context = context;
        this.windowManager = windowManager;
        holder.addCallback(this);
    }

   // private CameraAndInfo cameraAndInfo = null;
    private Camera camera = null;
    private final String TAG = "Preview";


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: ");
        CameraAndInfo cni = CameraUtils.getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        camera = cni.camera;
        if (camera == null) return;
        Log.d(TAG, "camera is not null: ");
        // Camera is always oriented in landscape but this app is fixed in portrait so we
        // rotate through 90 to fix this.
       camera.setDisplayOrientation(90);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: ");
        Camera.Parameters params = camera.getParameters();
        if (params.getPreviewFormat() != ImageFormat.NV21) return;
        try {
            camera.setPreviewDisplay(holder);
        } catch (Throwable t) {
            Log.e(TAG, "Exception in setPreviewDisplay()", t);
            Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        // These preview sizes are with respect to the original camera orientation.
        // Therefor the size.width will be our height and vice versa.
        Camera.Size size = getOptimalPreviewSize(params, width, height);
        if (size == null) return;
        params.setPreviewSize(size.width, size.height);
        camera.setParameters(params);
        Log.e(TAG, "SurfaceWidth:" + width);
        Log.e(TAG, "SurfaceHeight:" + height);
        Log.e(TAG, "PreviewWidth:" + size.width);
        Log.e(TAG, "PreviewHeight:" + size.height);
        // Add padding to fix the aspect ratio of the preview.
        if(height != size.width) {
            Log.e(TAG, "padding");

            //Now padd the surface view so that the preview is in 1:1 ratio
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,height - size.width , 0, 0);
            previewSurface.setLayoutParams(lp);
        }

        int bufferSize = ImageFormat.getBitsPerPixel(ImageFormat.NV21) * size.width * size.height / 8;
        camera.setPreviewCallbackWithBuffer(previewCallback);
        camera.addCallbackBuffer(new byte[bufferSize]);
        startPreview();
    }

    // same for stopping the preview
    public void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
        }
    }

    public void startPreview() {
        Log.d(TAG, "About to start preview: ");
        if (camera != null) {
            Log.d(TAG, "StartPreview: ");
           // previewSurface.setLayoutParams(lp);
            camera.startPreview();
        }
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            stopPreview();
            camera.release();
            camera = null;
        }
    }

    /**
     * Should be called when app is paused, allows the Preview to stop and release the
     */
    public void onPause() {
        Log.d(TAG, "onPause: ");
        stopPreview();
    }

    /**
     * Should be called when app is paused, allows the Preview to stop and release the
     */
    public void onResume() {
        Log.d(TAG, "onResume: ");
        startPreview();
    }




    //This optimal size makes sure we can display a 1:1 ratio of preview
    //pixels to view pixels. It does this by finding a preview with a small
    //dimension equal to the view's small dimension with the preview large dimension
    //smaller than that of the view.
    private Camera.Size getOptimalPreviewSize(Camera.Parameters params, int w, int h) {
    //    Log.e(TAG, "SurfaceWidth:" + w);
    //    Log.e(TAG, "SurfaceHeight:" + h);

        Camera.Size optimalSize = null;

        int vs = Math.min(w, h);
        int vl = Math.max(w, h);
        int cs=0, cl= 0;

      //  Log.e(TAG, "vs:" + vs);
      //  Log.e(TAG, "vl:" + vl);
        // Find size
        for (Camera.Size size : params.getSupportedPreviewSizes()) {
              //  Log.e(TAG, "Width:" + size.width);
              //  Log.e(TAG, "Height:" + size.height);
            cs = Math.min(size.width, size.height);
            cl = Math.max(size.width, size.height);
         //   Log.e(TAG, "cs:" + cs);
          //  Log.e(TAG, "cl:" + cl);

            if (cs == vs && cl <= vl) {
              //  Log.e(TAG, "size:" + size);
               return size;
            }

        }
        return null;
    }

    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        public void onPreviewFrame(byte[] data, Camera camera) {
            //Log.i(TAG, "onPreviewFrame: ");
            //cameraAndInfo.addCallbackBuffer(data);
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

}
