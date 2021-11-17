package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {


    public static String userId;

    public static String getUserId() {
        return userId;
    }

    public static final String EXTRA_MESSAGE = "com.vu.emapis.MESSAGE";
    private String url ="http://193.219.91.103:8666/rpc/get_pw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // this activity is related to UI in a activity_login.xml file
    }

    /** This method is called when the user clicks "LOGIN" button **/
    public void onClick(View view) {

        EditText txtUserName = findViewById(R.id.usernameTextField);
        EditText txtPassword = findViewById(R.id.passwordTextField);

        String username = txtUserName.getText().toString();
        String password = txtPassword.getText().toString();




        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
        } else {
            sendPostRequest(username, password);
        }

        String getUrl = "http://193.219.91.103:8666/users?select=user_id&username=eq." + username;
        sendGetRequest(getUrl);


    }

    private void sendPostRequest(String username, String password) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String hashedPassword = response;
                hashedPassword = hashedPassword.replace("\"", "");

                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
                Log.d("result", result.toString());
                if (result.verified) {
                    Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class); // Start new activity
                    intent.putExtra(EXTRA_MESSAGE, username); // Adds extra data to intent. (nameOfData, data)
                    startActivity(intent); // Starts the new activity
                } else {
                    Toast.makeText(LoginActivity.this, "Password or username incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {

                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("x", username);
                return MyData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoidG9kb191c2VyIn0.kTNyXxM8oq1xhVwNznb08dlSxIjq1F023zeTWyKNcNY");
                return headers;
            }
        };

        requestQueue.add(stringRequest);

    }

    private void sendGetRequest(String url) {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        userId = response.replaceAll("[\"\\[\\].{}:user_id]", "");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoidG9kb191c2VyIn0.kTNyXxM8oq1xhVwNznb08dlSxIjq1F023zeTWyKNcNY");
                return headers;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
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