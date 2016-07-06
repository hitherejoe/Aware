package com.hitherejoe.aware.ui.fence.headphone;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.hitherejoe.aware.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeadphoneFenceActivity extends AppCompatActivity {

    private static final String HEADPHONE_FENCE_KEY = "HEADPHONE_FENCE_KEY";

    @BindView(R.id.layout_root) RelativeLayout mLayoutHeadphoneFence;
    @BindView(R.id.image_headphones) ImageView mHeadphoneImage;
    @BindView(R.id.text_headphone_state) TextView mHeadphoneText;

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;
    private HeadphoneFenceReceiver mHeadphoneFenceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headphone_fence);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.text_headphone_fence);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();

        mHeadphoneFenceReceiver = new HeadphoneFenceReceiver();
        Intent intent = new Intent(HeadphoneFenceReceiver.FENCE_RECEIVER_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerFence();
        registerReceiver(mHeadphoneFenceReceiver, new IntentFilter(HeadphoneFenceReceiver.FENCE_RECEIVER_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterFence();
        unregisterReceiver(mHeadphoneFenceReceiver);
    }

    private void registerFence() {
        AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);

        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .addFence(HEADPHONE_FENCE_KEY, headphoneFence, mPendingIntent)
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

    private void unregisterFence() {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(HEADPHONE_FENCE_KEY)
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

    private void setHeadphoneState(int headphoneState) {
        if (headphoneState == HeadphoneState.PLUGGED_IN) {
            mHeadphoneImage.setImageResource(R.drawable.ic_headset_black_128dp);
            mHeadphoneText.setText(R.string.text_headphones_plugged_in);
        } else {
            mHeadphoneImage.setImageResource(R.drawable.ic_volume_up_black_128dp);
            mHeadphoneText.setText(R.string.text_headphones_unplugged);
        }
    }

     class HeadphoneFenceReceiver extends BroadcastReceiver {

        public static final String FENCE_RECEIVER_ACTION =
                "com.hitherejoe.aware.ui.fence.HeadphoneFenceReceiver.FENCE_RECEIVER_ACTION";

        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);

            if (TextUtils.equals(fenceState.getFenceKey(), HEADPHONE_FENCE_KEY)) {
                switch(fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        setHeadphoneState(HeadphoneState.PLUGGED_IN);
                        break;
                    case FenceState.FALSE:
                        setHeadphoneState(HeadphoneState.UNPLUGGED);
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
