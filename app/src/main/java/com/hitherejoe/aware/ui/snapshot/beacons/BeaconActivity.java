package com.hitherejoe.aware.ui.snapshot.beacons;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    @BindView(R.id.layout_beacons) LinearLayout mBeaconsLayout;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Awareness.SnapshotApi.getWeather(mGoogleApiClient)
                            .setResultCallback(new ResultCallback<WeatherResult>() {
                                @Override
                                public void onResult(@NonNull WeatherResult weatherResult) {
                                    if (weatherResult.getStatus().isSuccess()) {
                                        Weather weather = weatherResult.getWeather();
                                        Log.e("WEATHER", weather.getHumidity() + "");
                                    }
                                }
                            });

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    private void setupGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();
        getBeacons();
    }

    private void getBeacons() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Awareness.SnapshotApi.getBeaconState(mGoogleApiClient, BEACON_TYPE_FILTERS)
                .setResultCallback(new ResultCallback<BeaconStateResult>() {
                    @Override
                    public void onResult(@NonNull BeaconStateResult beaconStateResult) {
                        if (beaconStateResult.getStatus().isSuccess()) {
                            BeaconState beaconState = beaconStateResult.getBeaconState();
                            List<BeaconState.BeaconInfo> beaconInfos = beaconState.getBeaconInfo();
                            if (beaconInfos == null) {
                                beaconInfos = new ArrayList<BeaconState.BeaconInfo>();
                            }
                            BeaconState.BeaconInfo beaconInfo = new BeaconState.BeaconInfo() {
                                @Override
                                public String getNamespace() {
                                    return "0009-AABG-LLKM-UU89";
                                }

                                @Override
                                public String getType() {
                                    return "AltBeacon";
                                }

                                @Override
                                public byte[] getContent() {
                                    return new byte[0];
                                }
                            };
                            beaconInfos.add(beaconInfo);
                            for (BeaconState.BeaconInfo beacon : beaconInfos) {
                                addBeaconText(beacon);
                            }
                        } else {
                            Log.d("NO", "NO");

                            List<BeaconState.BeaconInfo> beaconInfos = new ArrayList<BeaconState.BeaconInfo>();

                            BeaconState.BeaconInfo beaconInfo = new BeaconState.BeaconInfo() {
                                @Override
                                public String getNamespace() {
                                    return "0009-AABG-LLKM-UU89";
                                }

                                @Override
                                public String getType() {
                                    return "Alt-Beacon";
                                }

                                @Override
                                public byte[] getContent() {
                                    return new byte[0];
                                }
                            };

                            BeaconState.BeaconInfo beaconInfoo = new BeaconState.BeaconInfo() {
                                @Override
                                public String getNamespace() {
                                    return "9784-BYHG-IKOM-77DV";
                                }

                                @Override
                                public String getType() {
                                    return "Alt-Beacon";
                                }

                                @Override
                                public byte[] getContent() {
                                    return new byte[0];
                                }
                            };

                            beaconInfos.add(beaconInfo);
                            beaconInfos.add(beaconInfoo);
                            for (BeaconState.BeaconInfo beacon : beaconInfos) {
                                addBeaconText(beacon);
                            }
                        }
                    }
                });
    }

    private void addBeaconText(BeaconState.BeaconInfo beaconInfo) {
        TextView beaconText = new TextView(this);
        beaconText.setText(beaconInfo.getNamespace());
        beaconText.setPadding(16, 16, 16, 16);
        mBeaconsLayout.addView(beaconText);
    }

}
