package com.vu.emapis;

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

import com.vu.emapis.objects.generalStatsObject;
import com.vu.emapis.objects.vehicle;
import com.vu.emapis.request.StatsManage;
import com.vu.emapis.request.VehicleManage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

public class GeneralStatsActivity extends AppCompatActivity {

    private String make;
    private String model;
    private int vehicle_id;

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
        setContentView(R.layout.activity_general_stats);

        totalTrips = findViewById(R.id.totalTrips);
        totalDistance = findViewById(R.id.totalDistance);
        avgCons = findViewById(R.id.avgConsumption);
        declaredCons = findViewById(R.id.declaredConsumption);
        backButton = findViewById(R.id.backButton);

        vehicleManage = new VehicleManage(GeneralStatsActivity.this);
        statsManage = new StatsManage(GeneralStatsActivity.this);

        vehicleManage.getAllVehicles(new VolleyCallBackInterface() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(GeneralStatsActivity.this, "Data retrieved", Toast.LENGTH_SHORT).show();

                Set<String> vehicleSetMake = new HashSet<>();

                String[] vehiclesMake;

                if(vehicleManage.vehiclesList == null) {
                    Toast.makeText(GeneralStatsActivity.this, "Something went wrong: No vehicles found.", Toast.LENGTH_SHORT).show();
                }

                for(int i = 0; i< vehicleManage.vehiclesList.length; i++) {
                    vehicleSetMake.add(vehicleManage.vehiclesList[i].getMake());
                }
                vehiclesMake = vehicleSetMake.toArray(new String[0]);

                spinnerInit(vehiclesMake, vehicleManage.vehiclesList);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(GeneralStatsActivity.this, "Something went wrong :( Check your internet connection", Toast.LENGTH_LONG).show();

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

    public void spinnerInit(String[] vehicles, vehicle[] vehiclesList) {

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

                modelSpinnerInit(vehiclesModel, vehicleManage.vehiclesList);

            }
            // Gets called when nothing has been selected (not being used, but has to be implemented)
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    public void modelSpinnerInit(String[] vehiclesModel, vehicle[] vehiclesList) {

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

    private void showStats(int vehicleID) {

        statsManage.getVehicleStats(vehicleID, new VolleyCallBackInterface() {
            @Override
            public void onSuccess(String result) {

                for(generalStatsObject obj : statsManage.generalStats) {

                    if (obj.getTotal_no_of_trips() == 0) {                  // if value 'null' = no info yet
                        Log.d("Data retrieved", "null");
                        totalTrips.setText("No records for this vehicle found!");
                        totalDistance.setText("");
                        avgCons.setText("");
                    } else {
                        totalTrips.setText("Total trips: " + obj.getTotal_no_of_trips()  + " trips");
                        totalDistance.setText("Total distance: " + BigDecimal.valueOf(obj.getTraveled_distance()/1000).setScale(2, RoundingMode.HALF_UP).doubleValue() + " km");
                        avgCons.setText("Average consumption: " + BigDecimal.valueOf(obj.getAverage_consumption()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " kWh/km");
                    }
                    declaredCons.setText("Declared consumption for this model: " + BigDecimal.valueOf(obj.getDeclared_consumption()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " kWh/km");

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