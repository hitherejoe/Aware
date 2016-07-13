package com.hitherejoe.aware.ui.fence.time;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.hitherejoe.aware.R;

import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeFenceActivity extends AppCompatActivity {

    private static final String TIME_FENCE_KEY = "TIME_FENCE_KEY";
    private static final int TIME_WITHIN = 0;
    private static final int TIME_NOT_IN = 1;

    @BindView(R.id.layout_root) RelativeLayout mLayoutTimeFence;
    @BindView(R.id.text_headphone_state) TextView mTimeText;

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;
    private TimeFenceReceiver mTimeFenceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headphone_fence);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.text_time_fence);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();

        mTimeFenceReceiver = new TimeFenceReceiver();
        Intent intent = new Intent(TimeFenceReceiver.FENCE_RECEIVER_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupFence();
        registerReceiver(mTimeFenceReceiver, new IntentFilter(TimeFenceReceiver.FENCE_RECEIVER_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterFence();
        unregisterReceiver(mTimeFenceReceiver);
    }

    private void setupFence() {
        AwarenessFence timeFence = TimeFence.inDailyInterval(TimeZone.getDefault(),
                0L, 24L * 60L * 60L * 1000L);
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .addFence(TIME_FENCE_KEY, timeFence, mPendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Snackbar.make(mLayoutTimeFence,
                                    "Fence Registered",
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(mLayoutTimeFence,
                                    "Fence Not Registered",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void unregisterFence() {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(TIME_FENCE_KEY)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Snackbar.make(mLayoutTimeFence,
                        "Fence Removed",
                        Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Snackbar.make(mLayoutTimeFence,
                        "Fence Not Removed",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setTimeState(int timeState) {
        if (timeState == TIME_WITHIN) {
            mTimeText.setText(R.string.text_that_time);
        } else {
            mTimeText.setText(R.string.text_not_that_time);
        }
    }

     class TimeFenceReceiver extends BroadcastReceiver {

        public static final String FENCE_RECEIVER_ACTION =
                "com.hitherejoe.aware.ui.fence.TimeFenceReceiver.FENCE_RECEIVER_ACTION";

        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);

            if (TextUtils.equals(fenceState.getFenceKey(), TIME_FENCE_KEY)) {
                switch(fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        setTimeState(TIME_WITHIN);
                        break;
                    case FenceState.FALSE:
                        setTimeState(TIME_NOT_IN);
                        break;
                    case FenceState.UNKNOWN:
                        Snackbar.make(mLayoutTimeFence,
                                "Oops, your time status is unknown!",
                                Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }

}
