package com.hitherejoe.aware.ui.snapshot.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.hitherejoe.aware.R;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends AppCompatActivity {

    @BindView(R.id.text_error) TextView mErrorText;
    @BindView(R.id.text_probable_activities) TextView mProbableActivitiesText;
    @BindView(R.id.text_most_probable_activity) TextView mMostProbableActivityText;
    @BindView(R.id.text_time) TextView mTimeText;
    @BindView(R.id.text_elapsed_time) TextView mElapsedTimeText;
    @BindView(R.id.text_activity_confidence) TextView mConfidenceText;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activity);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.text_user_activity);
        }
        setupGoogleApiClient();
        getUserActivity();
    }

    private void setupGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void getUserActivity() {
        Awareness.SnapshotApi.getDetectedActivity(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (detectedActivityResult.getStatus().isSuccess()) {
                            ActivityRecognitionResult activityRecognitionResult =
                                    detectedActivityResult.getActivityRecognitionResult();

                            long detectedActivity =
                                    activityRecognitionResult.getTime();
                            String dateString = DateFormat.format("dd/MM/yyyy hh:mm:ss",
                                    new Date(detectedActivity)).toString();
                            mTimeText.setText(getString(R.string.text_activity_time, dateString));

                            long elapsedTime =
                                    activityRecognitionResult.getElapsedRealtimeMillis();
                            String elapsed =
                                    DateFormat.format("hh:mm:ss", new Date(elapsedTime)).toString();
                            mElapsedTimeText.setText(getString(R.string.text_elapsed_time, elapsed));

                            DetectedActivity mostProbableActivity =
                                    activityRecognitionResult.getMostProbableActivity();
                            mMostProbableActivityText.setText(
                                    getString(R.string.text_most_probable_activity,
                                            getActivityString(mostProbableActivity.getType()),
                                            mostProbableActivity.getConfidence()));

                            List<DetectedActivity> probableActivities =
                                    activityRecognitionResult.getProbableActivities();
                            if (probableActivities.size() > 0) {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < probableActivities.size(); i++) {
                                    if (i > 0) {
                                        stringBuilder.append(", ");
                                    }
                                    stringBuilder.append(
                                            getActivityString(probableActivities.get(i).getType()));
                                }
                                mProbableActivitiesText.setText(
                                        getString(R.string.text_probable_activities,
                                                stringBuilder.toString()));
                            }

                            float activityConfidence =
                                    activityRecognitionResult.getActivityConfidence(
                                            DetectedActivity.RUNNING);
                            mConfidenceText.setText(getString(R.string.text_running_confidence,
                                            activityConfidence));
                        }
                    }
                });
    }

    private String getActivityString(int activity) {
        switch (activity) {
            case DetectedActivity.IN_VEHICLE:
                return getString(R.string.activity_in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return getString(R.string.activity_on_bicycle);
            case DetectedActivity.ON_FOOT:
                return getString(R.string.activity_on_foot);
            case DetectedActivity.RUNNING:
                return getString(R.string.activity_running);
            case DetectedActivity.STILL:
                return getString(R.string.activity_still);
            case DetectedActivity.TILTING:
                return getString(R.string.activity_tilting);
            case DetectedActivity.WALKING:
                return getString(R.string.activity_walking);
            default:
                return getString(R.string.activity_unknown);
        }
    }


}
