package com.vu.emapis;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        // Delcare widgets
        TextView textView = findViewById(R.id.statText);

        // Check if we have any extras from the last activity.
        // Expected: user's name. If there's nothing, return a default value
        Intent intent = getIntent();
        if(intent.hasExtra(LoginActivity.EXTRA_MESSAGE)) {
            String message = "Hey there, " + intent.getStringExtra(LoginActivity.EXTRA_MESSAGE) + "!";
            textView.setText(message);
        } else {
            textView.setText("Hey there!");
        }
    }

    public void showStatistics(View view) {
        Intent intent = new Intent(MainScreenActivity.this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void tripSettings(View view) {
        Intent intent = new Intent(MainScreenActivity.this, TripSettingsActivity.class);
        startActivity(intent);
    }

    public void LocationActivity(View view) {
        Intent intent = new Intent(MainScreenActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void DeveloperSettingsAcitivity(View view) {
        Intent intent = new Intent(MainScreenActivity.this, DeveloperSettingsActivity.class);
        startActivity(intent);
    }
}