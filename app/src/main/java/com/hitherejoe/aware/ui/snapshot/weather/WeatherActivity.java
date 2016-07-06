package com.hitherejoe.aware.ui.snapshot.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.hitherejoe.aware.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherActivity extends AppCompatActivity {

    @BindView(R.id.text_temperature) TextView mTemperatureText;
    @BindView(R.id.text_feels_like_temperature) TextView mFeelsLikeTemperatureText;
    @BindView(R.id.text_dewpoint) TextView mDewpointText;
    @BindView(R.id.text_humidity) TextView mHumidityText;
    @BindView(R.id.text_conditions) TextView mConditionsText;
    @BindView(R.id.layout_weather) LinearLayout mWeatherLayout;
    @BindView(R.id.progress) ProgressBar mProgress;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.text_weather);
        }
        setupGoogleApiClient();
        getWeather();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getWeather();
                } else {
                    mProgress.setVisibility(View.GONE);
                    mWeatherLayout.setVisibility(View.GONE);
                    Snackbar.make(mWeatherLayout,
                            getString(R.string.error_weather),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private void setupGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void getWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            Awareness.SnapshotApi.getWeather(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<WeatherResult>() {
                        @Override
                        public void onResult(@NonNull WeatherResult weatherResult) {
                            if (weatherResult.getStatus().isSuccess()) {
                                Weather weather = weatherResult.getWeather();

                                int[] conditions = weather.getConditions();
                                StringBuilder stringBuilder = new StringBuilder();
                                if (conditions.length > 0) {
                                    for (int i = 0; i < conditions.length; i++) {
                                        if (i > 0) {
                                            stringBuilder.append(", ");
                                        }
                                        stringBuilder.append(retrieveConditionString(conditions[i]));

                                    }
                                }
                                mConditionsText.setText(getString(R.string.text_conditions,
                                        stringBuilder.toString()));

                                float humidity = weather.getHumidity();
                                mHumidityText.setText(
                                        getString(R.string.text_humidity, humidity));

                                float temperature = weather.getTemperature(Weather.CELSIUS);
                                mTemperatureText.setText(
                                        getString(R.string.text_temperature, temperature));

                                float dewPoint = weather.getDewPoint(Weather.CELSIUS);
                                mDewpointText.setText(
                                        getString(R.string.text_dew_point, dewPoint));

                                float feelsLikeTemperature = weather.getFeelsLikeTemperature(Weather.CELSIUS);
                                mFeelsLikeTemperatureText.setText(
                                        getString(R.string.text_feels_like_temperature, feelsLikeTemperature));
                                mProgress.setVisibility(View.GONE);
                                mWeatherLayout.setVisibility(View.VISIBLE);
                            } else {
                                mProgress.setVisibility(View.GONE);
                                mWeatherLayout.setVisibility(View.GONE);
                                Snackbar.make(mWeatherLayout,
                                        getString(R.string.error_weather),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private String retrieveConditionString(int condition) {
        switch (condition) {
            case Weather.CONDITION_CLEAR:
                return getString(R.string.condition_clear);
            case Weather.CONDITION_CLOUDY:
                return getString(R.string.condition_cloudy);
            case Weather.CONDITION_FOGGY:
                return getString(R.string.condition_foggy);
            case Weather.CONDITION_HAZY:
                return getString(R.string.condition_hazy);
            case Weather.CONDITION_ICY:
                return getString(R.string.condition_icy);
            case Weather.CONDITION_RAINY:
                return getString(R.string.condition_rainy);
            case Weather.CONDITION_SNOWY:
                return getString(R.string.condition_snowy);
            case Weather.CONDITION_STORMY:
                return getString(R.string.condition_stormy);
            case Weather.CONDITION_WINDY:
                return getString(R.string.condition_windy);
            default:
                return getString(R.string.condition_unknown);
        }
    }
}
