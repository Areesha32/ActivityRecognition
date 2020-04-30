package com.example.activityrecognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
        BroadcastReceiver broadcastReceiver;

        private TextView txtActivity, txtConfidence;
        private ImageView imgActivity;
        private Button btnStartTracking, btnStopTracking;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


            if (Build.VERSION.SDK_INT >= 21) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(this.getResources().getColor(R.color.colorCornflowerBlue));
            }

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCornflowerBlue)));

            txtActivity = findViewById(R.id.txt_activity);
            txtConfidence = findViewById(R.id.txt_confidence);
            imgActivity = findViewById(R.id.img_activity);
            btnStartTracking = findViewById(R.id.btn_start_tracking);
            btnStopTracking = findViewById(R.id.btn_stop_tracking);

            btnStartTracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startTracking();
                }
            });
            btnStartTracking.setOnTouchListener(new View.OnTouchListener() {

                @SuppressLint("ClickableViewAccessibility")
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            v.getBackground().setColorFilter(0x4a4fff1b,PorterDuff.Mode.SRC_ATOP);
                            v.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            v.getBackground().clearColorFilter();
                            v.invalidate();
                            break;
                        }
                    }
                    return false;
                }
            });
            btnStopTracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopTracking();
                }
            });
            btnStopTracking.setOnTouchListener(new View.OnTouchListener() {

                @SuppressLint("ClickableViewAccessibility")
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            v.getBackground().setColorFilter(0x4a4fff1b,PorterDuff.Mode.SRC_ATOP);
                            v.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            v.getBackground().clearColorFilter();
                            v.invalidate();
                            break;
                        }
                    }
                    return false;
                }
            });
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                        int type = intent.getIntExtra("type", -1);
                        int confidence = intent.getIntExtra("confidence", 0);
                        handleUserActivity(type, confidence);
                    }
                }
            };

            startTracking();
        }

        private void handleUserActivity(int type, int confidence) {
           String label = getString(R.string.activity_unknown);

           int icon = R.drawable.ic_still;

            switch (type) {
                case DetectedActivity.IN_VEHICLE: {
                    label = getString(R.string.activity_in_vehicle);
                    icon = R.drawable.ic_driving;
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    label = getString(R.string.activity_on_bicycle);
                    icon = R.drawable.ic_on_bicycle;
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    label = getString(R.string.activity_on_foot);
                    icon = R.drawable.ic_walking;
                    break;
                }
                case DetectedActivity.RUNNING: {
                    label = getString(R.string.activity_running);
                    icon = R.drawable.ic_running;
                    break;
                }
                case DetectedActivity.STILL: {
                    label = getString(R.string.activity_still);
                    break;
                }
                case DetectedActivity.TILTING: {
                    label = getString(R.string.activity_tilting);
                    icon = R.drawable.ic_tilting;
                    break;
                }
                case DetectedActivity.WALKING: {
                    label = getString(R.string.activity_walking);
                    icon = R.drawable.ic_walking;
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    label = getString(R.string.activity_unknown);
                    break;
                }
            }

            Log.e(TAG, "User activity: " + label + ", Confidence: " + confidence);

            if (confidence > Constants.CONFIDENCE) {
                txtActivity.setText(label);
                txtConfidence.setText("Confidence: " + confidence);
                imgActivity.setImageResource(icon);
            }
        }

        @Override
        protected void onResume() {
            super.onResume();

            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                    new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
        }

        @Override
        protected void onPause() {
            super.onPause();

            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }

        private void startTracking() {
            Intent intent1 = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
            startService(intent1);
        }

        private void stopTracking() {
            Intent intent = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
            stopService(intent);
        }


}