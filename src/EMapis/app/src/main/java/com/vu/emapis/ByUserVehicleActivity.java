package com.vu.emapis;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vu.emapis.objects.byUserVehicleObject;
import com.vu.emapis.request.StatsManage;
import com.vu.emapis.request.VehicleManage;

import org.json.JSONArray;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

public class ByUserVehicleActivity extends AppCompatActivity {

    private String alias;
    private int VehicleID;

    private TextView totalTrips;
    private TextView totalDistance;
    private TextView avgCons;
    private TextView declaredCons;
    private Button backButton;

    VehicleManage vehicleManage;
    StatsManage statsManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_user_vehicle);

        // Declared widgets
        totalTrips = findViewById(R.id.totalTrips);
        totalDistance = findViewById(R.id.totalDistance);
        avgCons = findViewById(R.id.avgConsumption);
        declaredCons = findViewById(R.id.declaredConsumption);
        backButton = findViewById(R.id.backButton);

        // Declare vars
        vehicleManage = new VehicleManage(ByUserVehicleActivity.this);
        statsManage = new StatsManage(ByUserVehicleActivity.this);

        vehicleManage.getUserVehicles(Integer.parseInt(LoginActivity.userId), new VolleyCallBackInterface() {
            @Override
            public void onSuccess(String result) {
                if(vehicleManage.userVehicleList.length == 0) {

                    Toast.makeText(ByUserVehicleActivity.this, "Create a vehicle before starting a trip", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ByUserVehicleActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(ByUserVehicleActivity.this, "Data retrieved!", Toast.LENGTH_SHORT).show();


                    Set<String> vehicleAliasSet = new HashSet<>();
                    String[] vehicleAlias;


                    for(int i=0; i< vehicleManage.userVehicleList.length; i++) {
                        vehicleAliasSet.add(vehicleManage.userVehicleList[i].getVehicle_alias());
                    }
                    vehicleAlias = vehicleAliasSet.toArray(new String[0]);

                    spinnerInit(vehicleAlias);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ByUserVehicleActivity.this, "Something went wrong while loading.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

                for(int i = 0; i<vehicleManage.userVehicleList.length; i++) {
                    if(alias.equals(vehicleManage.userVehicleList[i].getVehicle_alias())) {
                        VehicleID = vehicleManage.userVehicleList[i].getUser_vehicle_id();
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

    private void showStats(int vehicleID) {
        Log.d(String.valueOf(vehicleID), "veh_id");

        statsManage.getUserVehicleStats(vehicleID, new VolleyCallBackInterfaceJSON() {
            @Override
            public void onSuccess(JSONArray response) {
                for(byUserVehicleObject obj : statsManage.byUserVehicleStats) {

                    if (obj.getNo_of_trips() == 0) {                  // if value 'null' = no info yet
                        totalTrips.setText("No trips recorded!");
                        totalDistance.setText("");
                        avgCons.setText("");
                    } else {
                        totalTrips.setText("Total trips: " + obj.getNo_of_trips()  + " trips");
                        totalDistance.setText("Total distance: " + BigDecimal.valueOf(obj.getTotal_distance()/1000).setScale(2, RoundingMode.HALF_UP).doubleValue() + " km");
                        avgCons.setText("Average consumption: " + BigDecimal.valueOf(obj.getAverage_consumption()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " kWh/km");
                    }
                    declaredCons.setText("Declared consumption for this model: " + BigDecimal.valueOf(obj.getDeclared_consumption()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " kWh/km");
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