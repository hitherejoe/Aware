package com.hitherejoe.aware.ui.fence.beacon;

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
import com.google.android.gms.awareness.fence.BeaconFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.state.BeaconState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.hitherejoe.aware.R;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BeaconFenceActivity extends AppCompatActivity {

    private static final String BEACON_FENCE_KEY = "BEACON_FENCE_KEY";
    private static final int BEACON_ZONE_IN = 0;
    private static final int BEACON_ZONE_OUT = 1;

    @BindView(R.id.layout_root)
    RelativeLayout mLayoutHeadphoneFence;
    @BindView(R.id.text_headphone_state)
    TextView mHeadphoneText;

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;
    private BeaconFenceReceiver mBeaconFenceReceiver;

    List<BeaconState.TypeFilter> BEACON_TYPE_FILTERS = Arrays.asList(
            BeaconState.TypeFilter.with(
                    "my.beacon.namespace",
                    "my-attachment-type"),
            BeaconState.TypeFilter.with(
                    "my.other.namespace",
                    "my-attachment-type"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headphone_fence);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.text_beacon_fence);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();

        mBeaconFenceReceiver = new BeaconFenceReceiver();
        Intent intent = new Intent(BeaconFenceReceiver.FENCE_RECEIVER_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupGoogleApiClient();
        registerReceiver(mBeaconFenceReceiver, new IntentFilter(BeaconFenceReceiver.FENCE_RECEIVER_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterFences();
        unregisterReceiver(mBeaconFenceReceiver);
    }

    private void setupGoogleApiClient() {

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
        AwarenessFence beaconFence = BeaconFence.found(BEACON_TYPE_FILTERS);
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .addFence(BEACON_FENCE_KEY, beaconFence, mPendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Snackbar.make(mLayoutHeadphoneFence,
                                    "Fence Registered",
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(mLayoutHeadphoneFence,
                                    "Fence Not Registered",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void unregisterFences() {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(BEACON_FENCE_KEY)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Snackbar.make(mLayoutHeadphoneFence,
                        "Fence Removed",
                        Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Snackbar.make(mLayoutHeadphoneFence,
                        "Fence Not Removed",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setBeaconState(int beaconState) {
        if (beaconState == BEACON_ZONE_IN) {
            mHeadphoneText.setText(R.string.text_in_beacon_zone);

        } else {
            mHeadphoneText.setText(R.string.text_not_in_beacon_zone);
        }
    }

     class BeaconFenceReceiver extends BroadcastReceiver {

        public static final String FENCE_RECEIVER_ACTION =
                "com.hitherejoe.aware.ui.fence.BeaconFenceReceiver.FENCE_RECEIVER_ACTION";

        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);

            if (TextUtils.equals(fenceState.getFenceKey(), BEACON_FENCE_KEY)) {
                switch(fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        setBeaconState(BEACON_ZONE_IN);
                        break;
                    case FenceState.FALSE:
                        setBeaconState(BEACON_ZONE_OUT);
                        break;
                    case FenceState.UNKNOWN:
                        Snackbar.make(mLayoutHeadphoneFence,
                                "Oops, your headphone status is unknown!",
                                Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }

}
