package com.hitherejoe.aware.ui.snapshot.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.hitherejoe.aware.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationActivity extends AppCompatActivity {

    @BindView(R.id.layout_location) LinearLayout mLayoutLocation;
    @BindView(R.id.progress) ProgressBar mProgress;
    @BindView(R.id.text_location) TextView mLocationText;
    @BindView(R.id.text_accuracy) TextView mAccuracyText;
    @BindView(R.id.text_altitude) TextView mAltitudeText;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.text_location);
        }
        setupGoogleApiClient();
        getLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();

                } else {
                    mProgress.setVisibility(View.GONE);
                    Snackbar.make(mLayoutLocation,
                            getString(R.string.error_general),
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

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            mProgress.setVisibility(View.GONE);
                            if (locationResult.getStatus().isSuccess()) {
                                Location location = locationResult.getLocation();
                                String locationText = getString(R.string.text_coordinates,
                                        location.getLatitude() + ", " + location.getLongitude());
                                mLocationText.setText(locationText);
                                mAccuracyText.setText(
                                        getString(R.string.text_accuracy, location.getAccuracy()));
                                mAltitudeText.setText(
                                        getString(R.string.text_altitude, location.getAltitude()));
                            } else {
                                Snackbar.make(mLayoutLocation,
                                        getString(R.string.error_general),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

}
