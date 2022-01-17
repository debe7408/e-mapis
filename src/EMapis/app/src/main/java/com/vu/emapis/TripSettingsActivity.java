package com.vu.emapis;

import android.content.Intent;
import android.os.Bundle;
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

import com.vu.emapis.objects.userVehicle;
import com.vu.emapis.request.TripManage;
import com.vu.emapis.request.VehicleManage;

import java.util.HashSet;
import java.util.Set;

public class TripSettingsActivity extends AppCompatActivity {

    // Widgets
    private SeekBar seekBar;
    private TextView textView;
    private ProgressBar progressBar;
    private Button startButton;

    private String alias;
    private int VehicleID;
    private int UserID;
    private String trip_ID;
    public static int seekBarValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_settings);

        UserID = Integer.parseInt(LoginActivity.userId);
        // Define widgets
        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);
        startButton = findViewById(R.id.button4);
        progressBar = findViewById(R.id.loadingBar);
        progressBar.setVisibility(View.VISIBLE);

        // Create a new userVehicle object
        VehicleManage vehicleManage = new VehicleManage(TripSettingsActivity.this);

        // Call a method to obtain all the information about user's vehicles
        vehicleManage.getUserVehicles(UserID, new VolleyCallBackInterface() {

            // On Success returns us the list
            @Override
            public void onSuccess(String result) {

                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                // Check if the user has any vehicles
                if(vehicleManage.userVehicleList.length == 0) {

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
                    spinnerInit(vehicleManage.userVehicleList);
                }
            }
            // On Failure, inform the user about it and finish the activity
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

    public void spinnerInit(userVehicle[] userVehicleList) {

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
            Toast.makeText(TripSettingsActivity.this, "Can not start a trip with energy levels at " + seekBar.getProgress() +"%.", Toast.LENGTH_SHORT).show();
        } else {

            seekBarValue = seekBar.getProgress();


            // Create a new tripManage object
            TripManage tripManage = new TripManage(TripSettingsActivity.this);

            // Call start a new trip method that returns us tripID
            tripManage.startNewTrip(UserID, VehicleID, new VolleyCallBackInterface() {

                // On Success returns tripID and continues code
                @Override
                public void onSuccess(String result) {

                    if (progressBar != null && progressBar.isShown()) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    trip_ID = result;

                    Intent intent = new Intent(TripSettingsActivity.this, OngoingTripActivity.class);
                    intent.putExtra("tripID", trip_ID);
                    startActivity(intent);
                    finish();
                }

                // On Error informs the user about the failure
                @Override
                public void onError(String error) {

                    if (progressBar != null && progressBar.isShown()) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    Toast.makeText(TripSettingsActivity.this, "Something went wrong while starting the trip", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}