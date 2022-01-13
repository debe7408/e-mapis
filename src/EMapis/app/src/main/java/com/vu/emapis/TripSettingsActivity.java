package com.vu.emapis;
import com.vu.emapis.objects.userVehicle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.vu.emapis.request.weatherGetRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TripSettingsActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView textView;
    public ProgressBar progressBar;
    public Button startButton;


    private userVehicle[] userVehicleList;
    private String alias;
    private int VehicleID;
    public static String trip_ID;
    public static int seekBarValue;
    public static int firstInput;

    // VolleyCallback interface
    public interface VolleyCallbackGet {
        void onSuccess(String result);
        void onError(String error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_settings);

        // Define widgets
        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);
        progressBar = findViewById(R.id.loadingBar);
        startButton = findViewById(R.id.button4);

        progressBar.setVisibility(View.VISIBLE);

        String getUserVehiclesURL = "http://193.219.91.103:4558/user_vehicles?user_id=eq." + LoginActivity.userId;
        getUserVehicles(getUserVehiclesURL, new VolleyCallbackGet() {

            @Override
            public void onSuccess(String result) {

                // Check if the user has any vehicles
                if(userVehicleList.length == 0) {

                    // If the user does not have any vehicles, send him to create one
                    Toast.makeText(TripSettingsActivity.this, "Create a vehicle before starting a trip", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TripSettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    // Initialize button to start the trip
                    startButton.setVisibility(View.VISIBLE);

                    // Initialize seekBar for energy input
                    seekBarInit();

                    // Initialize spinner for vehicle selection
                    spinnerInit();
                }
            }

            @Override
            public void onError(String error) {

                Toast.makeText(TripSettingsActivity.this, "Something went wrong while loading.", Toast.LENGTH_SHORT).show();
                finish();

            }
        });


        // Start Trip Button onClickListener
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTheTrip();
            }
        });

    }

    public void spinnerInit() {

        Set<String> vehicleAliasSet = new HashSet<>();
        String[] vehicleAlias;

        for(int i=0; i< userVehicleList.length; i++) {
            vehicleAliasSet.add(userVehicleList[i].getVehicle_alias());
        }
        vehicleAlias = vehicleAliasSet.toArray(new String[0]);

        Spinner selectAlias = findViewById(R.id.vehicleMenu); // Here we define that our Spinner object will be reflected by vehicleMenu Spinner in XML file.
        selectAlias.setVisibility(View.VISIBLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehicleAlias);
        selectAlias.setAdapter(adapter); // call the adapter to the spinner
        selectAlias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Gets called when an item has been selected
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                alias = parent.getItemAtPosition(pos).toString();

                for(int i = 0; i<userVehicleList.length; i++) {
                    if(alias.equals(userVehicleList[i].getVehicle_alias())) {
                        VehicleID = userVehicleList[i].getUser_vehicle_id();
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
        seekBar.setVisibility(View.VISIBLE);

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
    public void startTheTrip() {

        if (seekBar.getProgress() == 0) {
            Toast.makeText(TripSettingsActivity.this, "Can not start a trip with energy levels at " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
        } else {

            String startNewTripURL = "http://193.219.91.103:4558/rpc/_emapis_new_trip";
            startNewTripPostRequest(startNewTripURL, new VolleyCallbackGet() {
                @Override
                public void onSuccess(String tripoIDas) {

                    // If a new TripID has been created in the database, start new activity
                    trip_ID = tripoIDas;
                    firstInput = seekBar.getProgress();


                    // Retrieve weather info
                    weatherGetRequest weatherData = new weatherGetRequest();
                    weatherData.getWeatherRequest(TripSettingsActivity.this, new VolleyCallBackInterface() {
                        @Override
                        public void onSuccess(String temperature) {

                            // Send weather data to meta table
                            String sendWeatherDataURL = "http://193.219.91.103:4558/rpc/_emapis_update_battery_level";
                            sendWeatherData(tripoIDas, temperature, sendWeatherDataURL, new VolleyCallbackGet() {
                                @Override
                                public void onSuccess(String result) {
                                    Toast.makeText(TripSettingsActivity.this, "Successfully sent weather information", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(TripSettingsActivity.this, "Failed to send weather information", Toast.LENGTH_SHORT).show();

                                }
                            });

                            Intent intent = new Intent(TripSettingsActivity.this, OngoingTripActivity.class);
                            intent.putExtra(trip_ID, trip_ID);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String error) {

                            Toast.makeText(TripSettingsActivity.this, "Something went wrong while starting the trip", Toast.LENGTH_SHORT).show();

                        }
                    });


                }

                @Override
                public void onError(String error) {
                    Log.e("Error", "Failed to retrieve weather data in TripSettingsActivity");

                }
            });
        }

    }

    private void sendWeatherData(String tripID, String temperature, String url, VolleyCallbackGet callback) {


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess("Success");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onError("Error");
                error.printStackTrace();

            }
        }){
            protected Map<String, String> getParams() {

                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("trip_id", tripID);
                MyData.put("input_key", "temperature_input");
                MyData.put("input_value", temperature);

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

    private void startNewTripPostRequest(String postURL, VolleyCallbackGet callbackPost) {

        int userID = Integer.parseInt(LoginActivity.userId);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                callbackPost.onSuccess(response);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackPost.onError(error.toString());
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {

                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("user_id", String.valueOf(userID));
                MyData.put("user_vehicle_id", String.valueOf(VehicleID));
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

    private void getUserVehicles(String url, VolleyCallbackGet callbackGet) {

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                userVehicleList = gson.fromJson(String.valueOf(response), userVehicle[].class);
                Log.d("list-trip-settings", String.valueOf(response));

                callbackGet.onSuccess("");

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callbackGet.onError(error.toString());
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
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //TODO Implement progressBars
}