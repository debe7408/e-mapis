package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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

    private final String removeUrl = "http://193.219.91.103:4558/rpc/remove_user_vehicle";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_remove_vehicle);
        Button removeVehicle = findViewById(R.id.vehicleDeleteButton);

        removeVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRemoveRequest();
                sendGetRequest();
                runnable();
            }
        });


        sendGetRequest();
        runnable();
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

    public void sendGetRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://193.219.91.103:4558/user_vehicles?user_id=eq." + LoginActivity.userId;

// Request a string response from the provided URL.

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                userVehicleList = gson.fromJson(String.valueOf(response), userVehicleObject[].class);
                Log.d("list", String.valueOf(userVehicleList));

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

        queue.add(jsonArrayRequest);
    }


    private void sendRemoveRequest() {

        RequestQueue queue = Volley.newRequestQueue(this); // New requestQueue using Volley's default queue.

        JSONObject postData = new JSONObject(); // Creating JSON object with data that will be sent via POST request.

        try {

            postData.put("uvid", UserVehicleID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, removeUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //error.printStackTrace();

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
    }

    private void runnable() {
        Runnable r = new Runnable() {
            @Override
            public void run() {

                /*if(userVehicleList == null) {
                    tripSettingsActivity.isOnline();
                }*/

                // Set<String> vehicleSetModel = new HashSet<>();
                Set<String> vehicleAliasSet = new HashSet<>();
                String[] vehicleAlias;


                for(int i=0; i< userVehicleList.length; i++) {
                    vehicleAliasSet.add(userVehicleList[i].getVehicle_alias());
                    // vehicleSetModel.add(vehiclesList[i].getModel());
                }
                vehicleAlias = vehicleAliasSet.toArray(new String[0]);
                //vehiclesModel = vehicleSetModel.toArray(new String[0]);

                spinnerInit(vehicleAlias);

                //modelSpinnerInit(vehiclesModel);
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 500);
    }


}