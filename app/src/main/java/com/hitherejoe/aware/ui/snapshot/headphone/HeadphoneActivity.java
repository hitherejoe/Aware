package com.hitherejoe.aware.ui.snapshot.headphone;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.hitherejoe.aware.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeadphoneActivity extends AppCompatActivity {

    @BindView(R.id.layout_headphones) RelativeLayout mHeadphonesLayout;
    @BindView(R.id.image_headphones) ImageView mHeadphoneImage;
    @BindView(R.id.text_headphone_state) TextView mHeadphoneText;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headphone);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.text_headphone_state);
        }
        setupGoogleApiClient();
    }

    private void setupGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();
        getHeadphoneState();
    }

    private void getHeadphoneState() {
        Awareness.SnapshotApi.getHeadphoneState(mGoogleApiClient)
                .setResultCallback(new ResultCallback<HeadphoneStateResult>() {
                    @Override
                    public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
                        if (headphoneStateResult.getStatus().isSuccess()) {
                            HeadphoneState headphoneState =
                                    headphoneStateResult.getHeadphoneState();
                            int state = headphoneState.getState();
                            setHeadphoneState(state);
                        } else {
                            Snackbar.make(mHeadphonesLayout,
                                    getString(R.string.error_general),
                                    Snackbar.LENGTH_LONG).show();
                        }
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

}