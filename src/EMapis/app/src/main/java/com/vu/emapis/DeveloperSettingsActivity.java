package com.vu.emapis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DeveloperSettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_settings);

        // Declare widgets
        Button volleyButton = findViewById(R.id.volleyCallBackButton);
        Button weatherButton = findViewById(R.id.weatherAPIButton);
        TextView textView = findViewById(R.id.userIDText);


        // Display User ID
        if(LoginActivity.userId != null) {
            textView.setText("User ID: " + LoginActivity.userId);
        } else {
            textView.setText("Something went wrong.");
        }

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeveloperSettingsActivity.this, openWeatherAPITest.class);
                startActivity(intent);
            }
        });



        volleyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeveloperSettingsActivity.this, VolleyCallBackTest.class);
                startActivity(intent);
            }
        });

    }
}