package ppp.previewtemplateapp;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class PreviewActivity extends AppCompatActivity {
    private final String TAG = "PreviewActivity";

    private Preview preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Get main view
        setContentView(R.layout.activity_preview);

        SurfaceView view = (SurfaceView) findViewById(R.id.activity_preview);
        // Create the preview, it will take care of managing the preview display
        preview = new Preview(view, this);
    }

    @Override
    protected void onPause() {
        preview.onPause();
        super.onPause();

    }

}
