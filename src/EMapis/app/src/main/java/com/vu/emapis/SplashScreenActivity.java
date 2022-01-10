package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences appSettings; // Shared Preferences object for app prefs
    public static SharedPreferences.Editor appSettingsEditor; // Shared Preferences Object editor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                appFirstTimeRun();
            }
        }, 1000);   //5 seconds
    }

    // Methods checks if the app was run in this version and was it the first time the app was launched
    public void appFirstTimeRun() {

        appSettings = getSharedPreferences("appSettings", MODE_PRIVATE);
        appSettingsEditor = appSettings.edit();

        int currentBuildVersion = BuildConfig.VERSION_CODE;
        int lastBuildVersion = appSettings.getInt("app_last_time_version", 0);

        if(currentBuildVersion == lastBuildVersion) {
            //TODO Not first time
            Log.d("FirstTime?", "not");

            Intent intent = new Intent(this, userAgreementDialog.class); // TODO CHANGE THIS TO LoginActivity.class
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle()); // Starts the new activity
            finish();

            //startActivity(new Intent(this, LoginActivity.class));

        } else {

            appSettingsEditor.putInt("app_last_time_version", currentBuildVersion).apply();

            if(lastBuildVersion == 0) {
                // TODO First time
                Intent intent = new Intent(this, userAgreementDialog.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle()); // Starts the new activity
                finish();

                Log.d("FirstTime?", "yes");

            } else {
                // TODO first time in this update
                // What's new in this version Dialog
                Log.d("FirstTime?", "first in this update");
            }

        }
    }
}