package ppp.previewtemplateapp;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

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

    private Camera camera = null;
    private final String TAG = "Preview";


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: ");
        camera = CameraUtils.openFrontFacingCamera();
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
        Camera.Size size = getOptimalPreviewSize2(params, width, height);
        if (size == null) return;
        params.setPreviewSize(size.width, size.height);
        camera.setParameters(params);

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
        if (camera != null) {
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
     * Should be called when app is paused, allows the Preview to stop and release the camera.
     */
    public void onPause() {
        Log.d(TAG, "onPause: ");
        stopPreview();
    }

    /**
     * Should be called when app is paused, allows the Preview to stop and release the camera.
     */
    public void onResume() {
        Log.d(TAG, "onResume: ");
        startPreview();
    }




    //This optimal size makes sure we can display a 1:1 ratio of preview
    //pixels to view pixels. It does this by finding a preview with a small
    //dimension equal to the view's small dimension with the preview large dimension
    //smaller than that of the view.
    private Camera.Size getOptimalPreviewSize2(Camera.Parameters params, int w, int h) {
        Log.e(TAG, "SurfaceWidth:" + w);
        Log.e(TAG, "SurfaceHeight:" + h);

        Camera.Size optimalSize = null;

        int vs = Math.min(w, h);
        int vl = Math.max(w, h);
        int cs=0, cl= 0;

        // Find size
        for (Camera.Size size : params.getSupportedPreviewSizes()) {
                Log.e(TAG, "Width:" + size.width);
                Log.e(TAG, "Height:" + size.height);
            cs = Math.min(size.width, size.height);
            cl = Math.max(size.width, size.height);
            if (cs == vs && cl <= vl) {

                optimalSize = size;
                break;
            }

        }
        if (optimalSize == null) return null;
        // If the view is already the right size then don't set the layout params
        // otherwise we get stuck in a loop since setting the layout params causes the
        // surface to be changed.
        if(vs != cs || vl !=cl) {
            //Now padd the surface view so that the preview is in 1:1 ratio
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(vl - cl, 0, 0, 0);
            previewSurface.setLayoutParams(lp);
        }
        return optimalSize;
    }

    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        public void onPreviewFrame(byte[] data, Camera camera) {
            //Log.i(TAG, "onPreviewFrame: ");
            //camera.addCallbackBuffer(data);
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
