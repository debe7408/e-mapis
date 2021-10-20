package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.vu.emapis.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onClick(View view) {
        EditText txtUserName = findViewById(R.id.registerUsernameText);
        EditText txtPassword = findViewById(R.id.registerPasswordText);
        EditText txtEmail = findViewById(R.id.registerEmailText);

        Intent intent = new Intent(this, MainScreenActivity.class);
        String username = txtUserName.getText().toString();
        intent.putExtra(EXTRA_MESSAGE,username);
        startActivity(intent);
    }
}