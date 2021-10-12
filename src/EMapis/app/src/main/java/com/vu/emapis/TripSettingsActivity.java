package com.vu.emapis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class TripSettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView seekBarLabel;
    TextView test;
    String[] vehicles = new String[] {"BMW","Golf","Bolto paspirtukas"}; // String array for testing purposes ( Will be replaced with a database later ).

    //GPS variables
    Button button;
    TextView latitude, longitude;
    FusedLocationProviderClient fusedLocationProviderClient; //google api get location


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_settings);

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


    // Gets called when an item has been selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        String make = (String) parent.getItemAtPosition(pos); // Get the content of selected item in the spinner

        test  = findViewById(R.id.TEST);
        test.setText("DEBUG: You have selected: ".concat(make)); // Display the text in the textview

    }

    // Gets called when nothing has been selected (not being used, but has to be implemented)
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }




}