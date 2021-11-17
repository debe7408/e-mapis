package com.vu.emapis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

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

    private VehicleObject[] vehiclesList;
    private String[] temp = {"Hi", "Bye"};
    private SeekBar seekBar;
    private TextView textView;
    private final String postURL = "http://193.219.91.103:8666/rpc/new_trip";
    public static String trip_ID;


    private String make;
    private String model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_settings);
        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);



        sendGetRequest();

        seekBarInit();

        sendPostRequest();

        Runnable r = new Runnable() {
            @Override
            public void run() {

               // Set<String> vehicleSetModel = new HashSet<>();
                Set<String> vehicleSetMake = new HashSet<>();
                //String[] vehiclesModel;
                String[] vehiclesMake;


                for(int i=0; i< vehiclesList.length; i++) {
                    vehicleSetMake.add(vehiclesList[i].getMake());
                   // vehicleSetModel.add(vehiclesList[i].getModel());
                }
                vehiclesMake = vehicleSetMake.toArray(new String[0]);
                //vehiclesModel = vehicleSetModel.toArray(new String[0]);

                spinnerInit(vehiclesMake);
                //modelSpinnerInit(vehiclesModel);
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 500);
    }

    public void modelSpinnerInit(String[] vehiclesModel) {

        Spinner selectModel = findViewById(R.id.vehicleMenu2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehiclesModel);
        selectModel.setAdapter(adapter);
        selectModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                model = adapterView.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void spinnerInit(String[] vehicles) {

        Spinner selectMake = findViewById(R.id.vehicleMenu); // Here we define that our Spinner object will be reflected by vehicleMenu Spinner in XML file.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehicles);
        selectMake.setAdapter(adapter); // call the adapter to the spinner
        selectMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Gets called when an item has been selected
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                make = parent.getItemAtPosition(pos).toString();

                Set<String> vehicleSetModel = new HashSet<>();
                String[] vehiclesModel;

                for(int i = 0; i<vehiclesList.length; i++) {

                    if(vehiclesList[i].getMake().equals(make)) {
                        vehicleSetModel.add(vehiclesList[i].getModel());
                    }
                }
                vehiclesModel = vehicleSetModel.toArray(new String[0]);

                modelSpinnerInit(vehiclesModel);

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

        String url = "http://193.219.91.103:8666/vehicles?select=*";

// Request a string response from the provided URL.

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                vehiclesList = gson.fromJson(String.valueOf(response), VehicleObject[].class);

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