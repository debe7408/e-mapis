package com.vu.tutorialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.vu.tutorialapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // this activity is related to UI in a activity_login.xml file
    }



    /** This method is called when the user clicks "LOGIN" button **/
    public void sendMessage(View view) {

        Intent intent = new Intent(LoginActivity.this, MainscreenActivity.class); // Start new activity
        EditText editUsername = (EditText) findViewById(R.id.usernameTextField); // find text from username field
        String message = editUsername.getText().toString(); // EditText -> String transformation
        intent.putExtra(EXTRA_MESSAGE, message); // Adds extra data to intent. (nameOfData, data)
        startActivity(intent); // Starts the new activity
    }




}