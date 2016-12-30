package com.davidmiguel.openweathermap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements WeatherDataSource.GetWeatherCallback {

    private Button button;
    private TextView tv;

    private WeatherDataSource weatherDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherDataSource = new WeatherDataSource();

        button = (Button) findViewById(R.id.button);
        tv = (TextView) findViewById(R.id.tv);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAndShowWeather();
            }
        });
    }

    private void getAndShowWeather() {
        tv.setText("Loading...");
        weatherDataSource.getCurrentWeather(42.3567673, -3.6704805, this);
    }

    @Override
    public void onWeatherLoaded(MeteoRecord meteoRecord) {
        tv.setText(meteoRecord.toString());
    }

    @Override
    public void onDataNotAvailable() {
        tv.setText("Error!");
    }
}
