package com.hitherejoe.aware.ui.fence.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.hitherejoe.aware.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationFenceActivity extends AppCompatActivity {

    private static final String IN_LOCATION_FENCE_KEY = "IN_LOCATION_FENCE_KEY";
    private static final String EXITING_LOCATION_FENCE_KEY = "EXITING_LOCATION_FENCE_KEY";
    private static final String ENTERING_LOCATION_FENCE_KEY = "ENTERING_LOCATION_FENCE_KEY";

    public static final int STATUS_IN = 0;
    public static final int STATUS_OUT = 1;
    public static final int STATUS_ENTERING = 2;
    public static final int STATUS_EXITING = 3;

    @BindView(R.id.layout_location_fence) RelativeLayout mLayoutLocationFence;
    @BindView(R.id.text_headphone_state) TextView mHeadphoneText;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;
    private LocationFenceReceiver mLocationFenceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_fence);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.text_location_fence);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();

        mLocationFenceReceiver = new LocationFenceReceiver();
        Intent intent = new Intent(LocationFenceReceiver.FENCE_RECEIVER_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerFences();
        registerReceiver(mLocationFenceReceiver, new IntentFilter(LocationFenceReceiver.FENCE_RECEIVER_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterFences();
        unregisterReceiver(mLocationFenceReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registerFences();
                } else {
                    Snackbar.make(mLayoutLocationFence,
                            getString(R.string.error_loading_places),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private void registerFences() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            AwarenessFence inLocationFence = LocationFence.in(50.830951, -0.146978, 200, 1);
            AwarenessFence exitingLocationFence = LocationFence.exiting(50.830951, -0.146978, 200);
            AwarenessFence enteringLocationFence = LocationFence.entering(50.830951, -0.146978, 200);

            Awareness.FenceApi.updateFences(
                    mGoogleApiClient,
                    new FenceUpdateRequest.Builder()
                            .addFence(IN_LOCATION_FENCE_KEY, inLocationFence, mPendingIntent)
                            .addFence(EXITING_LOCATION_FENCE_KEY, exitingLocationFence, mPendingIntent)
                            .addFence(ENTERING_LOCATION_FENCE_KEY, enteringLocationFence, mPendingIntent)
                            .build())
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Snackbar.make(mLayoutLocationFence,
                                        "Fence Registered",
                                        Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(mLayoutLocationFence,
                                        "Fence Not Registered",
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void unregisterFences() {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(IN_LOCATION_FENCE_KEY)
                        .removeFence(EXITING_LOCATION_FENCE_KEY)
                        .removeFence(ENTERING_LOCATION_FENCE_KEY)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Snackbar.make(mLayoutLocationFence,
                        "Fence Removed",
                        Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Snackbar.make(mLayoutLocationFence,
                        "Fence Not Removed",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setHeadphoneState(int status) {
        switch (status) {
            case STATUS_IN:
                mHeadphoneText.setText(R.string.text_at_home);
                break;
            case STATUS_OUT:
                mHeadphoneText.setText(R.string.text_not_at_home);
                break;
            case STATUS_ENTERING:
                mHeadphoneText.setText(R.string.text_entering_home);
                break;
            case STATUS_EXITING:
                mHeadphoneText.setText(R.string.text_exiting_home);
                break;
        }
    }

    class LocationFenceReceiver extends BroadcastReceiver {

        public static final String FENCE_RECEIVER_ACTION =
                "com.hitherejoe.aware.ui.fence.LocationFenceReceiver.FENCE_RECEIVER_ACTION";

        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);

            if (TextUtils.equals(fenceState.getFenceKey(), IN_LOCATION_FENCE_KEY)) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        setHeadphoneState(STATUS_IN);
                        break;
                    case FenceState.FALSE:
                        setHeadphoneState(STATUS_OUT);
                        break;
                    case FenceState.UNKNOWN:
                        Snackbar.make(mLayoutLocationFence,
                                "Oops, your headphone status is unknown!",
                                Snackbar.LENGTH_LONG).show();
                        break;
                }
            } else if (TextUtils.equals(fenceState.getFenceKey(), EXITING_LOCATION_FENCE_KEY)) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        setHeadphoneState(STATUS_EXITING);
                        break;
                    case FenceState.FALSE:

                        break;
                    case FenceState.UNKNOWN:
                        Snackbar.make(mLayoutLocationFence,
                                "Oops, your headphone status is unknown!",
                                Snackbar.LENGTH_LONG).show();
                        break;
                }
            } else if (TextUtils.equals(fenceState.getFenceKey(), ENTERING_LOCATION_FENCE_KEY)) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        setHeadphoneState(STATUS_ENTERING);
                        break;
                    case FenceState.FALSE:

                        break;
                    case FenceState.UNKNOWN:
                        Snackbar.make(mLayoutLocationFence,
                                "Oops, your headphone status is unknown!",
                                Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }

}
