package com.vu.emapis;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserRemoveVehicleActivity extends AppCompatActivity {

    private userVehicleObject[] userVehicleList;
    private String alias;
    private int UserVehicleID;

    public ProgressBar progressBar;


    // VolleyCallback interface
    public interface VolleyCallbackGet {
        void onSuccess(String result);
        void onError(String error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_remove_vehicle);

        // Declare widgets
        Button removeVehicle = findViewById(R.id.vehicleDeleteButton);
        ProgressBar progressBar = findViewById(R.id.loadingBar);

        // Get request URL
        String getURL = "http://193.219.91.103:4558/user_vehicles?user_id=eq." + LoginActivity.userId;

        // Send getRequest to retrieve user vehicles
        sendGetRequest(getURL, new VolleyCallbackGet() {

            // On successful data retrieval:
            @Override
            public void onSuccess(String result) {

                Log.d("Success:","Data retrieved!");

                Set<String> vehicleAliasSet = new HashSet<>();
                String[] vehicleAlias;

                for(int i=0; i< userVehicleList.length; i++) {
                    vehicleAliasSet.add(userVehicleList[i].getVehicle_alias());
                }
                vehicleAlias = vehicleAliasSet.toArray(new String[0]);

                spinnerInit(vehicleAlias);

            }

            // On unsuccessful data retrieval:
            @Override
            public void onError(String error) {

                Toast.makeText(UserRemoveVehicleActivity.this, "Something went wrong :( Check your internet connection", Toast.LENGTH_LONG).show();

                finish();

            }
        });


        // Remove vehicle button listener
        removeVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Enable progressBar while loading
                progressBar.setVisibility(View.VISIBLE);

                String removeUrl = "http://193.219.91.103:4558/rpc/remove_user_vehicle";
                sendRemoveRequest(removeUrl, new VolleyCallbackGet() {
                    @Override
                    public void onSuccess(String result) {

                        Toast.makeText(UserRemoveVehicleActivity.this, "Vehicle removed!", Toast.LENGTH_SHORT).show();

                        // Reload activity
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);

                    }

                    @Override
                    public void onError(String error) {

                        Toast.makeText(UserRemoveVehicleActivity.this, "Something went wrong:(", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    public void spinnerInit(String[] vehicleAlias) {

        Spinner selectAlias = findViewById(R.id.vehicleMenu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehicleAlias);
        selectAlias.setAdapter(adapter);
        selectAlias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                alias = parent.getItemAtPosition(pos).toString();

                for(int i = 0; i<userVehicleList.length; i++) {
                    if(alias.equals(userVehicleList[i].getVehicle_alias())) {
                        UserVehicleID = userVehicleList[i].getUser_vehicle_id();
                    }
                }

            }

            // Gets called when nothing has been selected (not being used, but has to be implemented)
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    public void sendGetRequest(String url, final VolleyCallbackGet callback) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                userVehicleList = gson.fromJson(String.valueOf(response), userVehicleObject[].class);
                Log.d("list", String.valueOf(userVehicleList));

                callback.onSuccess(response.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callback.onError(error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }

    private void sendRemoveRequest(String url, final VolleyCallbackGet callback) {

        RequestQueue queue = Volley.newRequestQueue(this); // New requestQueue using Volley's default queue.

        JSONObject postData = new JSONObject(); // Creating JSON object with data that will be sent via POST request.

        try {

            postData.put("uvid", UserVehicleID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onSuccess(response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onSuccess(""); // Volley returns error due to blank space conversion to JSON. we ignore this
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
}