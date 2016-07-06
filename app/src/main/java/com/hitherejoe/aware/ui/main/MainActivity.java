package com.hitherejoe.aware.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hitherejoe.aware.R;
import com.hitherejoe.aware.ui.snapshot.beacons.BeaconActivity;
import com.hitherejoe.aware.ui.fence.beacon.BeaconFenceActivity;
import com.hitherejoe.aware.ui.fence.user.DetectedActivityFenceActivity;
import com.hitherejoe.aware.ui.fence.headphone.HeadphoneFenceActivity;
import com.hitherejoe.aware.ui.fence.location.LocationFenceActivity;
import com.hitherejoe.aware.ui.fence.time.TimeFenceActivity;
import com.hitherejoe.aware.ui.snapshot.headphone.HeadphoneActivity;
import com.hitherejoe.aware.ui.snapshot.location.LocationActivity;
import com.hitherejoe.aware.ui.snapshot.places.PlacesActivity;
import com.hitherejoe.aware.ui.snapshot.user.UserActivity;
import com.hitherejoe.aware.ui.snapshot.weather.WeatherActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.text_headphone_state)
    public void onHeadphoneStateTextClick() {
        Intent intent = new Intent(this, HeadphoneActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_weather)
    public void onWeatherTextClick() {
        Intent intent = new Intent(this, WeatherActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_places)
    public void onPlacesTextClick() {
        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_user_activity)
    public void onUserActivityTextClick() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_location)
    public void onLocationTextClick() {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_beacons)
    public void onBeaconTextClick() {
        Intent intent = new Intent(this, BeaconActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_headphone_fence)
    public void onHeadphoneFenceTextClick() {
        Intent intent = new Intent(this, HeadphoneFenceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_detected_activity_fence)
    public void onDetectedActivityFenceTextClick() {
        Intent intent = new Intent(this, DetectedActivityFenceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_location_fence)
    public void onLocationFenceTextClick() {
        Intent intent = new Intent(this, LocationFenceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_time_fence)
    public void onTimeFenceTextClick() {
        Intent intent = new Intent(this, TimeFenceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_beacon_fence)
    public void onBeaconFenceTextClick() {
        Intent intent = new Intent(this, BeaconFenceActivity.class);
        startActivity(intent);
    }
}
