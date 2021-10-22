package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.vu.emapis.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // this activity is related to UI in a activity_login.xml file
    }

    /** This method is called when the user clicks "LOGIN" button **/
    public void onClick(View view) {

        Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class); // Start new activity
        EditText editUsername = (EditText) findViewById(R.id.usernameTextField); // find text from username field
        String username = editUsername.getText().toString(); // EditText -> String transformation
        intent.putExtra(EXTRA_MESSAGE, username); // Adds extra data to intent. (nameOfData, data)
        startActivity(intent); // Starts the new activity
    }

    public void onClickRegister(View view) {

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}