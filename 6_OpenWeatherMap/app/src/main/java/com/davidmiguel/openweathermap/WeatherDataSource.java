package com.davidmiguel.openweathermap;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class WeatherDataSource {

    private GetWeatherCallback callback;

    /**
     * Get current weather data.
     *
     * @param latitude the latitude of the location.
     * @param longitude the longitude of the location.
     * @return current weather data.
     */
    void getCurrentWeather(double latitude, double longitude, GetWeatherCallback getWeatherCallback) {
        this.callback = getWeatherCallback;
        new GetWeatherTask().execute(NetworkUtils.getCurrentWeatherUrl(latitude, longitude));
    }

    private class GetWeatherTask extends AsyncTask<URL, Void, MeteoRecord> {

        @Override
        protected MeteoRecord doInBackground(URL... urls) {
            URL weatherRequestUrl = urls[0];
            try {
                String json = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                return OpenWeatherMapUtils.parseCurrentWeatherJson(json);
            } catch (IOException | JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(MeteoRecord meteoRecord) {
            if(meteoRecord == null) {
                callback.onDataNotAvailable();
            }
            callback.onWeatherLoaded(meteoRecord);
        }
    }

    interface GetWeatherCallback {
        void onWeatherLoaded(MeteoRecord meteoRecord);

        void onDataNotAvailable();
    }
}
