package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserVehicleActivity extends AppCompatActivity {

    private VehicleObject[] vehiclesList;
    private String make;
    private String model;
    private String vehicle_id;
    private String userVehicleId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vehicle);
        Button saveVehicleButton = findViewById(R.id.saveVehicleButton);
        TextView enterVehicleAlias = findViewById(R.id.enterVehicleAlias);

        saveVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(enterVehicleAlias.getText().toString().isEmpty()) {
                    Toast.makeText(UserVehicleActivity.this, "Please your enter vehicle's alias", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserVehicleActivity.this, "Success! You can now start a trip", Toast.LENGTH_SHORT).show();
                    String VehicleAlias = enterVehicleAlias.getText().toString();
                    sendPostRequest(VehicleAlias);
                    finish();
                }
            }
        });


        sendGetRequest();

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


    private void sendPostRequest(String VehicleAlias) {

        String url = "http://193.219.91.103:8666/rpc/new_user_vehicle";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                userVehicleId = response.toString();
                System.out.println("Testas" + userVehicleId);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {

                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("user_id", LoginActivity.userId);
                MyData.put("vehicle_alias", VehicleAlias);
                MyData.put("vehicle_id", vehicle_id);
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

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}