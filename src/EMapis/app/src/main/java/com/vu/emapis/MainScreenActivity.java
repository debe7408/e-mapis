package com.vu.emapis;

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

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = "Hi, " + intent.getStringExtra(LoginActivity.EXTRA_MESSAGE) + "!" + " user_id: " + LoginActivity.userId;

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.statText);
        textView.setText(message);
    }

    public void showStatistics(View view) {
        Intent intent = new Intent(MainScreenActivity.this, VolleyActivity.class);
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