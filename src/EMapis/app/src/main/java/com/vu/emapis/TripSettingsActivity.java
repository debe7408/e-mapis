package com.vu.emapis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TripSettingsActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView textView;
    private final String postURL = "http://193.219.91.103:4558/rpc/new_trip";
    public static String trip_ID;


    private userVehicleObject[] userVehicleList;
    private String alias;
    private int VehicleID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_settings);
        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);



        sendGetRequest();

        seekBarInit();

        Runnable r = new Runnable() {
            @Override
            public void run() {

                if(userVehicleList == null) {
                    isOnline();
                }

                if(userVehicleList.length == 0) {

                    Toast.makeText(TripSettingsActivity.this, "Create a vehicle before starting a trip!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TripSettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }

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


    public void spinnerInit(String[] vehicleAlias) {

        Spinner selectAlias = findViewById(R.id.vehicleMenu); // Here we define that our Spinner object will be reflected by vehicleMenu Spinner in XML file.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehicleAlias);
        selectAlias.setAdapter(adapter); // call the adapter to the spinner
        selectAlias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Gets called when an item has been selected
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                alias = parent.getItemAtPosition(pos).toString();

                for(int i = 0; i<userVehicleList.length; i++) {
                    if(alias.equals(userVehicleList[i].getVehicle_alias())) {
                        VehicleID = userVehicleList[i].getVehicle_id();
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

    public void seekBarInit() {
        textView.setText("Energy levels: "+seekBar.getProgress() + "%");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progressValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText("Energy levels: "+progressValue + "%");
            }
        });
    }

    // Gets called when the button "Start the trip" is pressed
    public void startTheTrip(View view) {

        isOnline();

        sendPostRequest();

        Runnable r = new Runnable() {
            @Override
            public void run(){
                Intent intent = new Intent(TripSettingsActivity.this, OngoingTripActivity.class);
                intent.putExtra(trip_ID, trip_ID);
                startActivity(intent);
                finish();
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 500); // <-- the "1000" is the delay time in miliseconds.

    }



    private void sendPostRequest() {

        int userID = Integer.parseInt(LoginActivity.userId);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                trip_ID = response;
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {

                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("user_id", String.valueOf(userID));
                MyData.put("user_vehicle_id", String.valueOf(VehicleID));
                MyData.put("fuel_at_start", String.valueOf(seekBar.getProgress()));
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
                Log.d("list-trip-settings", String.valueOf(response));

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
}