package com.vu.emapis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    public static String userId;
    public static String getUserId() {
        return userId;
    }

    public static final String EXTRA_MESSAGE = "com.vu.emapis.MESSAGE";
    private String url ="http://193.219.91.103:4558/rpc/find_password";

    public ProgressBar progressBar;

    public void onClickQuitApp(View view) {
        onBackPressed();
    }

    // VolleyCallback interface
    public interface VolleyCallbackGet {
        void onSuccess(String result);
        void onError(String error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // this activity is related to UI in a activity_login.xml file

        //Define widgets

        progressBar = findViewById(R.id.loadingBar);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /** This method is called when the user clicks "LOGIN" button **/
    public void onClickLoginButton(View view) {

        EditText txtUserName = findViewById(R.id.usernameTextField);
        EditText txtPassword = findViewById(R.id.passwordTextField);

        String username = txtUserName.getText().toString();
        String password = txtPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            sendPostRequest(username, password, new VolleyCallbackGet() {
                @Override
                public void onSuccess(String response) {

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

                @Override
                public void onError(String error) {

                    Toast.makeText(LoginActivity.this, "Connection issues!", Toast.LENGTH_SHORT).show();

                }
            });
        }

        String getUrl = "http://193.219.91.103:4558/users?select=user_id&username=eq." + username;
        sendGetRequest(getUrl);


    }

    private void sendPostRequest(String username, String password, final VolleyCallbackGet callback) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                callback.onSuccess(response);

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callback.onError(error.toString());

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
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

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
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void onClickRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting")
                .setMessage("Are you sure you want to quit?")
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