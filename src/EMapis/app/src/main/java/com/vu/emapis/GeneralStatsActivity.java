package com.vu.emapis;
import com.vu.emapis.objects.generalStatsObject;
import com.vu.emapis.objects.vehicle;

import androidx.appcompat.app.AppCompatActivity;

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

public class GeneralStatsActivity extends AppCompatActivity {

    private vehicle[] vehiclesList;
    private generalStatsObject[] generalStats;


    private String make;
    private String model;
    private int vehicle_id;

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
        setContentView(R.layout.activity_general_stats);

        totalTrips = findViewById(R.id.totalTrips);
        totalDistance = findViewById(R.id.totalDistance);
        avgCons = findViewById(R.id.avgConsumption);
        declaredCons = findViewById(R.id.declaredConsumption);


        String url = "http://193.219.91.103:4558/vehicles?";
        getVehicles(url, new UserVehicleActivity.VolleyCallbackGet() {

            @Override
            public void onSuccess(String result) {

                Toast.makeText(GeneralStatsActivity.this, "Data retrieved", Toast.LENGTH_SHORT).show();


                Set<String> vehicleSetMake = new HashSet<>();

                String[] vehiclesMake;

                if(vehiclesList == null) {
                    Toast.makeText(GeneralStatsActivity.this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
                }


                for(int i=0; i< vehiclesList.length; i++) {
                    vehicleSetMake.add(vehiclesList[i].getMake());
                }
                vehiclesMake = vehicleSetMake.toArray(new String[0]);

                spinnerInit(vehiclesMake);

            }

            @Override
            public void onError(String error) {

                Toast.makeText(GeneralStatsActivity.this, "Something went wrong :( Check your internet connection", Toast.LENGTH_LONG).show();

                finish();

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

    public void modelSpinnerInit(String[] vehiclesModel) {

        Spinner selectModel = findViewById(R.id.vehicleMenu2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehiclesModel);
        selectModel.setAdapter(adapter);
        selectModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                model = adapterView.getItemAtPosition(pos).toString();

                for(int i = 0; i<vehiclesList.length; i++) {
                    if(vehiclesList[i].getModel().equals(model)) {
                        vehicle_id = vehiclesList[i].getVehicle_id();
                    }
                }

                showStats(vehicle_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getVehicles(String url, final UserVehicleActivity.VolleyCallbackGet callback) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                vehiclesList = gson.fromJson(String.valueOf(response), vehicle[].class);

                callback.onSuccess(response.toString());

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

    private void getVehicle(String url, final ByUserVehicleActivity.VolleyCallbackGet callback) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                generalStats = gson.fromJson(String.valueOf(response), generalStatsObject[].class);

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
        String url = "http://193.219.91.103:4558/_emapis_get_vehicle_info?vehicle_id=eq." + id;
        getVehicle(url, new ByUserVehicleActivity.VolleyCallbackGet() {

            @Override
            public void onSuccess(JSONArray result) {

                for(generalStatsObject obj : generalStats) {

                    if (obj.getTotal_no_of_trips() == 0) {                  // if value 'null' = no info yet
                        totalTrips.setText("No records for this vehicle found!");
                        totalDistance.setText("");
                        avgCons.setText("");
                        declaredCons.setText("Declared consumption for this model: " + BigDecimal.valueOf(obj.getDeclared_consumption()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " kWh/km");
                    } else {
                        totalTrips.setText("Total trips: " + obj.getTotal_no_of_trips()  + " trips");
                        totalDistance.setText("Total distance: " + BigDecimal.valueOf(obj.getTotal_distance()/1000).setScale(2, RoundingMode.HALF_UP).doubleValue() + " km");
                        avgCons.setText("Average consumption: " + BigDecimal.valueOf(obj.getReal_consumption()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " kWh/km");
                        declaredCons.setText("Declared consumption for this model: " + BigDecimal.valueOf(obj.getDeclared_consumption()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " kWh/km");
                    }

                }


            }

            @Override
            public void onError(String error) {

                Toast.makeText(GeneralStatsActivity.this, "Something went wrong :( Check your internet connection", Toast.LENGTH_LONG).show();

                finish();

            }
        });
    }

}