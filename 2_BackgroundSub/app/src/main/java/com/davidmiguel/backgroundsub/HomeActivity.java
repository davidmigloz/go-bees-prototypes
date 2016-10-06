package com.davidmiguel.backgroundsub;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private final int REQUEST_OPEN_VIDEO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void loadVideo(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent,"Select Video"), REQUEST_OPEN_VIDEO);
    }

    public void openCamera(View view) {
        Intent i = new Intent(getApplicationContext(), CameraActivity.class);
        startActivity(i);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_OPEN_VIDEO) {
                Uri selectedVideoURI = data.getData();
                if (selectedVideoURI != null) {
                    Intent i = new Intent(getApplicationContext(), VideoActivity.class);
                    i.putExtra(VideoActivity.EXTRA_VIDEO_URI, selectedVideoURI);
                    startActivity(i);
                }
            }
        }
    }
}
