package com.davidmiguel.backgroundsub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void openCamera(View view) {
        Intent i = new Intent(getApplicationContext(), CameraActivity.class);
        startActivity(i);
    }
}
