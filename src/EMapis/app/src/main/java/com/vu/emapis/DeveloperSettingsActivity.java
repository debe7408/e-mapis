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
        Button button = findViewById(R.id.volleyCallBackButton);
        TextView textView = findViewById(R.id.userIDText);

        if(LoginActivity.userId != null) {
            textView.setText("User ID: " + LoginActivity.userId);
        } else {
            textView.setText("Something went wrong.");
        }



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeveloperSettingsActivity.this, VolleyCallBackTest.class);
                startActivity(intent);
            }
        });

    }
}