package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = "Hello, " + intent.getStringExtra(LoginActivity.EXTRA_MESSAGE) + "!";

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.greetingMessage);
        textView.setText(message);
    }

    public void showStatistics(View view) {

    }
}