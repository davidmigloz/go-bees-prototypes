package com.davidmiguel.backgroundsub;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.videoio.VideoCapture;


public class VideoActivity extends AppCompatActivity {

    private static final String TAG = "VideoActivity";
    public final static String EXTRA_VIDEO_URI =
            "com.davidmiguel.backgroundsub.extra.EXTRA_VIDEO_URI";

    private Uri videoURI;
    // OpenCV loader callback
    private BaseLoaderCallback mLoaderCallback =
            new BaseLoaderCallback(this) {
                @Override
                public void onManagerConnected(final int status) {
                    switch (status) {
                        case LoaderCallbackInterface.SUCCESS:
                            Log.d(TAG, "OpenCV loaded successfully");
                            playVideo();
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
        final Intent i = getIntent();
        videoURI = i.getParcelableExtra(EXTRA_VIDEO_URI);
        setContentView(R.layout.activity_video);
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    private void playVideo() {
        if(videoURI != null) {
            VideoCapture capture = new VideoCapture(videoURI.getPath());
            if (capture.isOpened()) {
                Log.d(TAG, "playVideo: OPEN");
            } else {
                Log.e(TAG, "playVideo: ERROR");
            }
        }
    }
}
