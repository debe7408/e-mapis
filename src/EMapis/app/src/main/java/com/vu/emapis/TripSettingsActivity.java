package com.vu.emapis;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TripSettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    private String[] vehicles;
    private SeekBar seekBar;
    private TextView textView;
    private final String postURL = "http://193.219.91.103:8666/rpc/new_trip";
    public static String trip_ID;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_settings);

        sendGetRequest();

        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);

        seekBarInit();

        sendPostRequest();

        Runnable r = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                spinnerInit();
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 500);
    }

    public void spinnerInit() {
        Spinner selectVehicle = findViewById(R.id.vehicleMenu); // Here we define that our Spinner object will be reflected by vehicleMenu Spinner in XML file.

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehicles);
        /** Here we create a new ArrayAdapter, which supplies the spinner with the array.
         * 1st argument -> the context in which this will be done.
         * 2nd argument -> the layout resource that defines how the selected choice appears in the spinner.
         * 3rd argument -> the String array we have defined earlier.
         */

        selectVehicle.setAdapter(adapter); // call the adapter to the spinner
        selectVehicle.setOnItemSelectedListener(this);
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

    // Gets called when an item has been selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        String make = (String) parent.getItemAtPosition(pos); // Get the content of selected item in the spinner

    }

    // Gets called when nothing has been selected (not being used, but has to be implemented)
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void sendPostRequest() {

        int userID = Integer.parseInt(LoginActivity.userId);
        int vehicleID = 1;

        System.out.println("Testas:" + userID);


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                trip_ID = response;
               //DEBUG: System.out.println("Testas 1:" + trip_ID);

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
                MyData.put("user_vehicle_id", String.valueOf(vehicleID));
                MyData.put("fuel_at_start", String.valueOf(seekBar.getProgress()));
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

    private void sendGetRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://193.219.91.103:8666/vehicles?select=make";

// Request a string response from the provided URL.

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {



                    vehicles = new String[response.length()];

                    for(int i=0; i< response.length(); i++) {

                        vehicles[i] = response.optString(i).replace("{\"make\":\"", "").replace("\"}", "");
                    }

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

        queue.add(jsonArrayRequest);
    }




}