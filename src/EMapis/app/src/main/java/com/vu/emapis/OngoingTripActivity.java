package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class OngoingTripActivity extends AppCompatActivity {

    Chronometer simpleChronometer;
    boolean clicked = false;
    private long lastPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_trip);

        simpleChronometer = findViewById(R.id.simpleChronometer);
        simpleChronometer.start();
        simpleChronometer.setBase(SystemClock.elapsedRealtime());

    }

    public void pauseTripOnClick(View view) {

        TextView textView = findViewById(R.id.textView2);
        Button button = findViewById(R.id.button2);

        if(!clicked) {
            lastPause = SystemClock.elapsedRealtime();
            simpleChronometer.stop();
            textView.setText("Trip is paused");
            button.setText("Resume the trip");
            clicked = true;
        } else {
            simpleChronometer.setBase(simpleChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
            simpleChronometer.start();
            textView.setText("Trip is in progress");
            button.setText("Pause the trip");
            clicked = false;
        }
    }
}