package com.hitherejoe.aware.ui.snapshot.beacons;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.BeaconStateResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.BeaconState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.hitherejoe.aware.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BeaconActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;

    @BindView(R.id.layout_beacons) LinearLayout mBeaconsLayout;

    private GoogleApiClient mGoogleApiClient;

    List<BeaconState.TypeFilter> BEACON_TYPE_FILTERS = Arrays.asList(
            BeaconState.TypeFilter.with(
                    "my.beacon.namespace",
                    "my-attachment-type"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.text_beacons);
        }
        setupGoogleApiClient();
        getBeacons();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getBeacons();
                } else {
                    Snackbar.make(mBeaconsLayout,
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

    private void getBeacons() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            Awareness.SnapshotApi.getBeaconState(mGoogleApiClient, BEACON_TYPE_FILTERS)
                    .setResultCallback(new ResultCallback<BeaconStateResult>() {
                        @Override
                        public void onResult(@NonNull BeaconStateResult beaconStateResult) {
                            if (beaconStateResult.getStatus().isSuccess()) {
                                BeaconState beaconState = beaconStateResult.getBeaconState();
                                List<BeaconState.BeaconInfo> beaconInfos = beaconState.getBeaconInfo();
                                for (BeaconState.BeaconInfo beacon : beaconInfos) {
                                    addBeaconText(beacon);
                                }
                            } else {
                                Snackbar.make(mBeaconsLayout,
                                        getString(R.string.error_general),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void addBeaconText(BeaconState.BeaconInfo beaconInfo) {
        TextView beaconText = new TextView(this);
        beaconText.setText(beaconInfo.getNamespace());
        beaconText.setPadding(16, 16, 16, 16);
        mBeaconsLayout.addView(beaconText);
    }

}
