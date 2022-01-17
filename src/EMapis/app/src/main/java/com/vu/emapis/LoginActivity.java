package com.vu.emapis;

import static com.vu.emapis.Constants.EMAPIS_DATABASE_TOKEN;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
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

    private CheckBox saveLoginInformation; // Remember Me? Checkbox
    private ProgressBar progressBar; // Progress animation when contacting a database
    private EditText txtUserName; // Username text field
    private EditText txtPassword; // Password text field
    private static SharedPreferences loginPref; // Shared Preferences object for login prefs
    public static SharedPreferences.Editor loginPrefEditor; // Shared Preferences Object editor
    private Boolean saveLogin; // simple boolean to check if login information is saved


    public static final String EXTRA_MESSAGE = "com.vu.emapis.MESSAGE";
    private final String url = "http://193.219.91.103:4558/rpc/find_password";

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

        txtUserName = findViewById(R.id.usernameTextField);
        txtPassword = findViewById(R.id.passwordTextField);
        saveLoginInformation = findViewById(R.id.rememberMeCheckbox);
        progressBar = findViewById(R.id.loadingBar);
        loginPref = getSharedPreferences("loginPref", MODE_PRIVATE);
        loginPrefEditor = loginPref.edit();

        saveLogin = loginPref.getBoolean("saveLogin", false);

        // Check on launch if the login information is saved
        if(saveLogin) {
            // If true, sets username and password as username and password
            txtUserName.setText(loginPref.getString("username", ""));
            txtPassword.setText(loginPref.getString("password", ""));
            saveLoginInformation.setChecked(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickQuitApp(View view) {
        onBackPressed();
    }

    /** This method is called when the user clicks "LOGIN" button **/
    public void onClickLoginButton(View view) {

        // Close virtual keyboard after the button is clicked for better User Experience
        try  {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {}


        // Extract string and username from the TextFields
        String username = txtUserName.getText().toString();
        String password = txtPassword.getText().toString();

        // If Remember Me Checkbox is checked, save info to SharedPreferences, else, clear it
        if (saveLoginInformation.isChecked()) {
            loginPrefEditor.putBoolean("saveLogin", true);
            loginPrefEditor.putString("username", username);
            loginPrefEditor.putString("password", password);
        } else {
            loginPrefEditor.clear();
        }
        loginPrefEditor.commit();


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
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle()); // Starts the new activity


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

        String getUrl = getString(R.string.infoAboutUsersURL) + username;
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
                headers.put("Authorization", EMAPIS_DATABASE_TOKEN);
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
                headers.put("Authorization", EMAPIS_DATABASE_TOKEN);
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