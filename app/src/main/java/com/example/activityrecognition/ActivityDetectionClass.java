package com.example.activityrecognition;

import android.app.IntentService;
import com.google.android.gms.location.ActivityRecognitionResult;
import android.content.Intent;
import java.util.ArrayList;
import com.google.android.gms.location.DetectedActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ActivityDetectionClass extends IntentService {

    private static final String TAG = "Activity Detection Class";

    public ActivityDetectionClass() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult mResult = ActivityRecognitionResult.extractResult(intent);
        Intent mIntent = new Intent(GlobalValues.STRING_ACTION);
        ArrayList<DetectedActivity> detectedActivityArrayList = (ArrayList) mResult.getProbableActivities();
        mIntent.putExtra(GlobalValues.STRING_EXTRA, detectedActivityArrayList);
        LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent);
    }
}