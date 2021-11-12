package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class RegisterActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.vu.emapis.MESSAGE";

    private String url ="http://193.219.91.103:8666/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onClick(View view) {
        EditText txtUserName = findViewById(R.id.registerUsernameText);
        EditText txtPassword = findViewById(R.id.registerPasswordText);
        EditText txtEmail = findViewById(R.id.registerEmailText);

        String username = txtUserName.getText().toString();
        String password = txtPassword.getText().toString();
        String email = txtEmail.getText().toString();

        boolean userNameValid = checkUsername(username);

        if (username.matches("") || password.matches("") || email.matches("")) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
        } else if (userNameValid == false) {
            Toast.makeText(this, "Username must be between 4 and 20 characters in length and cannot contain special characters", Toast.LENGTH_SHORT).show();
        } else {

            String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());

            sendPostRequest(username, bcryptHashString, email);

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public boolean checkUsername(String username) {
        boolean passed = true;
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE); //check if username does not contain special characters
        Matcher m = p.matcher(username);
        boolean special = m.find();

        if (special==true || (username.length() < 4 || username.length() > 20)) {
            passed = false;
        }

        return passed;
    }


    private void sendPostRequest(String username, String password, String email) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);
            postData.put("username", username);
            postData.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoidG9kb191c2VyIn0.kTNyXxM8oq1xhVwNznb08dlSxIjq1F023zeTWyKNcNY");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
}