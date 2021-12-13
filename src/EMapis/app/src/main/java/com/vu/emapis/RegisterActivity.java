package com.vu.emapis;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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

    private String url ="http://193.219.91.103:4558/rpc/new_user";
    public boolean userNameTaken = false;

    ProgressBar progressBar;


    public interface VolleyCallback{
        void onSuccess(String result);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressBar = findViewById(R.id.loadingBar);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //isOnline();

    }

    public void isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()) {

        }
        else {
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void onClickRegister(View view) throws InterruptedException {

        //isOnline();

        progressBar.setVisibility(View.VISIBLE);

        EditText txtUserName = findViewById(R.id.registerUsernameText);
        EditText txtPassword = findViewById(R.id.registerPasswordText);
        EditText txtEmail = findViewById(R.id.registerEmailText);

        String username = txtUserName.getText().toString();
        String password = txtPassword.getText().toString();
        String email = txtEmail.getText().toString();



        checkIfUserNameNotTaken(username, password, email);

    }

    public void actions(String username, String password, String email, boolean userNameValid) {


        if (username.matches("") || password.matches("") || email.matches("")) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
        } else if (!userNameValid) {
            Toast.makeText(this, "Username must be between 4 and 20 characters in length and cannot contain special characters", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("taken1", String.valueOf(userNameTaken));
            if (userNameTaken) {
                Toast.makeText(this, "Username taken, srry", Toast.LENGTH_SHORT).show();
            }
            else {
                String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());

                sendPostRequest(username, bcryptHashString, email);

                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }

    }

    public void checkIfUserNameNotTaken(String username, String password, String email){


        String getUrl = "http://193.219.91.103:4558/users?select=username&username=eq." + username;
        sendGetRequest(getUrl, username, new VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                if (result.replaceAll("[\"\\[\\].{}: ]", "").equals("username" + username)) {
                    userNameTaken = true;
                } else {
                    userNameTaken = false;
                }

                boolean userNameValid = checkUsername(username);

                actions(username, password, email, userNameValid);
            }

        });
    }


    public boolean checkUsername(String username) {
        boolean passed = true;
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE); //check if username does not contain special characters
        Matcher m = p.matcher(username);
        boolean special = m.find();

        if (special || (username.length() < 4 || username.length() > 20)) {
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
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void sendGetRequest(String url, String username, final VolleyCallback callback) {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
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

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }
}