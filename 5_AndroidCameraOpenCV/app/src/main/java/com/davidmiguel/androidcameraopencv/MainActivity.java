package com.davidmiguel.androidcameraopencv;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.davidmiguel.androidcameraopencv.camera.AndroidCamera;
import com.davidmiguel.androidcameraopencv.camera.AndroidCameraImpl;
import com.davidmiguel.androidcameraopencv.camera.CameraFrame;
import com.davidmiguel.androidcameraopencv.camera.AndroidCameraListener;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity implements AndroidCameraListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AndroidCamera hardwareCamera;
    private boolean openCVLoaded = false;
    private ImageView mIV;
    private Bitmap img;


    private BaseLoaderCallback loaderCallback =
            new BaseLoaderCallback(this) {
                @Override
                public void onManagerConnected(final int status) {
                    switch (status) {
                        case LoaderCallbackInterface.SUCCESS:
                            openCVLoaded = true;
                            Log.d(TAG, "OpenCV loaded successfully");
                            hardwareCamera = new AndroidCameraImpl(MainActivity.this,
                                    Camera.CameraInfo.CAMERA_FACING_BACK, 640, 480, 100);
                            if (!hardwareCamera.isConnected()) {
                                hardwareCamera.connect();
                            }
                            break;
                        default:
                            super.onManagerConnected(status);
                            break;
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIV = (ImageView) findViewById(R.id.image_view);
        // Don't switch off screen
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, loaderCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release camera
        if (hardwareCamera != null && hardwareCamera.isConnected()) {
            hardwareCamera.release();
            hardwareCamera = null;
        }
    }

    @Override
    public boolean isOpenCVLoaded() {
        return openCVLoaded;
    }

    @Override
    public void onCameraStarted(int width, int height) {
        img = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    }

    @Override
    public void onPreviewFrame(CameraFrame cameraFrame) {
        Mat frame = cameraFrame.rgba();
        Utils.matToBitmap(frame, img);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIV.setImageBitmap(img);
            }
        });
    }
}
