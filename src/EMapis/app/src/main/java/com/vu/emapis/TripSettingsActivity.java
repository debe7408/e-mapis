package com.vu.emapis;

import android.content.Intent;
import android.os.Bundle;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TripSettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    private String[] vehicles = new String[] {"BMW","Golf","Bolto paspirtukas"}; // String array for testing purposes ( Will be replaced with a database later ).
    private SeekBar seekBar;
    private TextView textView;
    private final String postURL = "http://193.219.91.103:8666/rpc/new_trip";
    private String trip_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_settings);

        Spinner selectVehicle = findViewById(R.id.vehicleMenu); // Here we define that our Spinner object will be reflected by vehicleMenu Spinner in XML file.
        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);

        seekBarInit();


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

    public void startTheTrip(View view) {
        sendPostRequest();
        Intent intent = new Intent(TripSettingsActivity.this, OngoingTripActivity.class);
        intent.putExtra("tripID", trip_ID);
        startActivity(intent);
        finish();
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

        int userID = 1;
        int vehicleID = 1;

        RequestQueue queue = Volley.newRequestQueue(this); // New requestQueue using Volley's default queue.

        JSONObject postData = new JSONObject(); // Creating JSON object with data that will be sent via POST request.
        try {

            postData.put("user_id", userID);
            postData.put("user_vehicle_id", vehicleID);
            postData.put("fuel_at_start", seekBar.getProgress());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postURL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                System.out.println("NO RESPONSE :(");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoidG9kb191c2VyIn0.kTNyXxM8oq1xhVwNznb08dlSxIjq1F023zeTWyKNcNY");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }




}