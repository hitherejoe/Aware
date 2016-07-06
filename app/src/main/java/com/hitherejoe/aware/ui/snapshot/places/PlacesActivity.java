package com.hitherejoe.aware.ui.snapshot.places;

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
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.hitherejoe.aware.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesActivity extends AppCompatActivity {

    @BindView(R.id.layout_places) LinearLayout mLayoutPlaces;
    @BindView(R.id.progress) ProgressBar mProgress;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.text_places);
        }
        setupGoogleApiClient();
        getPlaces();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPlaces();
                } else {
                    mProgress.setVisibility(View.GONE);
                    Snackbar.make(mLayoutPlaces,
                            getString(R.string.error_loading_places),
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

    private void getPlaces() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            Awareness.SnapshotApi.getPlaces(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<PlacesResult>() {
                        @Override
                        public void onResult(@NonNull PlacesResult placesResult) {
                            if (placesResult.getStatus().isSuccess()) {
                                mProgress.setVisibility(View.GONE);
                                List<PlaceLikelihood> placeLikelihood =
                                        placesResult.getPlaceLikelihoods();
                                if (placeLikelihood != null && !placeLikelihood.isEmpty()) {
                                    for (PlaceLikelihood likelihood : placeLikelihood) {
                                        addPlace(likelihood.getPlace().getName().toString(),
                                                likelihood.getLikelihood());
                                    }
                                } else {
                                    mProgress.setVisibility(View.GONE);
                                    Snackbar.make(mLayoutPlaces,
                                            getString(R.string.error_no_places),
                                            Snackbar.LENGTH_LONG).show();
                                }
                            } else {
                                mProgress.setVisibility(View.GONE);
                                Snackbar.make(mLayoutPlaces,
                                        getString(R.string.error_loading_places),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void addPlace(String placeName, float likelihood) {
        TextView mPlaceText = new TextView(this);
        mPlaceText.setText(getString(R.string.text_place, placeName, likelihood));
        mPlaceText.setPadding(32, 32, 32, 32);
        mLayoutPlaces.addView(mPlaceText);
    }

}
