package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ByUserVehicleActivity extends AppCompatActivity {

    private userVehicleObject[] userVehicleList;
    private byUserVehicleObject[] byUserVehicleStats;

    private String alias;
    private int VehicleID;

    private TextView totalTrips;
    private TextView totalDistance;
    private TextView avgCons;
    private TextView declaredCons;

    public interface VolleyCallbackGet {
        void onSuccess(JSONArray result) throws JSONException;
        void onError(String error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_user_vehicle);

        totalTrips = findViewById(R.id.totalTrips);
        totalDistance = findViewById(R.id.totalDistance);
        avgCons = findViewById(R.id.avgConsumption);
        declaredCons = findViewById(R.id.declaredConsumption);

        String getUrl = "http://193.219.91.103:4558/user_vehicles?user_id=eq." + LoginActivity.userId;
        getUserVehicles(getUrl, new TripSettingsActivity.VolleyCallbackGet() {

            @Override
            public void onSuccess(String result) {

                if(userVehicleList.length == 0) {

                    Toast.makeText(ByUserVehicleActivity.this, "Create a vehicle before starting a trip", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ByUserVehicleActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(ByUserVehicleActivity.this, "Data retrieved!", Toast.LENGTH_SHORT).show();


                    Set<String> vehicleAliasSet = new HashSet<>();
                    String[] vehicleAlias;


                    for(int i=0; i< userVehicleList.length; i++) {
                        vehicleAliasSet.add(userVehicleList[i].getVehicle_alias());
                    }
                    vehicleAlias = vehicleAliasSet.toArray(new String[0]);

                    spinnerInit(vehicleAlias);
                }
            }

            @Override
            public void onError(String error) {

                Toast.makeText(ByUserVehicleActivity.this, "Something went wrong while loading :(", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

    }





    private void getUserVehicles(String url, TripSettingsActivity.VolleyCallbackGet callbackGet) {

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                userVehicleList = gson.fromJson(String.valueOf(response), userVehicleObject[].class);
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
    }

    public void spinnerInit(String[] vehicleAlias) {

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

                showStats(VehicleID);


                //TODO LAUNCH REFRESH STATS - refreshstats()

            }

            // Gets called when nothing has been selected (not being used, but has to be implemented)
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    private void getVehicle(String url, final ByUserVehicleActivity.VolleyCallbackGet callback) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                byUserVehicleStats = gson.fromJson(String.valueOf(response), byUserVehicleObject[].class);

                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onError(error.toString());
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

    private void showStats(int id) {
        Log.d(String.valueOf(id), "veh_id");
        String url = "http://193.219.91.103:4558/_emapis_get_user_vehicle_info?user_vehicle_id=eq." + id;
        getVehicle(url, new ByUserVehicleActivity.VolleyCallbackGet() {

            @Override
            public void onSuccess(JSONArray result) {

                for(byUserVehicleObject obj : byUserVehicleStats) {

                    if (obj.getNo_of_trips() == 0) {                  // if value 'null' = no info yet
                        totalTrips.setText("No trips recorded!");
                        totalDistance.setText("");
                        avgCons.setText("");
                        declaredCons.setText("Declared consumption for this model: " + BigDecimal.valueOf(obj.getDeclared_consumption()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " kWh/km");
                    } else {
                        totalTrips.setText("Total trips: " + obj.getNo_of_trips()  + " trips");
                        totalDistance.setText("Total distance: " + BigDecimal.valueOf(obj.getTotal_distance()/1000).setScale(2, RoundingMode.HALF_UP).doubleValue() + " km");
                        avgCons.setText("Average consumption: " + BigDecimal.valueOf(obj.getAverage_consumption()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " kWh/km");
                        declaredCons.setText("Declared consumption for this model: " + BigDecimal.valueOf(obj.getDeclared_consumption()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " kWh/km");
                    }

                }


            }

            @Override
            public void onError(String error) {

                Toast.makeText(ByUserVehicleActivity.this, "Something went wrong :( Check your internet connection", Toast.LENGTH_LONG).show();

                finish();

            }
        });
    }
}