package com.vu.emapis;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vu.emapis.objects.userVehicle;
import com.vu.emapis.request.VehicleManage;

import java.util.HashSet;
import java.util.Set;

public class UserRemoveVehicleActivity extends AppCompatActivity {

    private String alias;
    private int UserVehicleID;

    private int UserID;
    VehicleManage vehicleManage;

    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_remove_vehicle);

        UserID = Integer.parseInt(LoginActivity.userId);

        // Declare widgets
        Button removeVehicle = findViewById(R.id.vehicleDeleteButton);
        ProgressBar progressBar = findViewById(R.id.loadingBar);

        // Create a new class userVehicle object
        vehicleManage = new VehicleManage(UserRemoveVehicleActivity.this);

        // Call a method to obtain all the information about user's vehicles
        vehicleManage.getUserVehicles(UserID, new VolleyCallBackInterface() {

            // On Success returns us the list
            @Override
            public void onSuccess(String result) {

                Log.d("Success:","Data retrieved!");

                Set<String> vehicleAliasSet = new HashSet<>();
                String[] vehicleAlias;

                for(int i=0; i< vehicleManage.userVehicleList.length; i++) {
                    vehicleAliasSet.add(vehicleManage.userVehicleList[i].getVehicle_alias());
                }
                vehicleAlias = vehicleAliasSet.toArray(new String[0]);

                spinnerInit(vehicleAlias, vehicleManage.userVehicleList);

            }

            // On Failure, inform the user about it and finish the activity
            @Override
            public void onError(String error) {
                Toast.makeText(UserRemoveVehicleActivity.this, "Something went wrong :( Check your internet connection", Toast.LENGTH_LONG).show();
                finish();
            }
        });


        // Remove vehicle button listener
        removeVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Enable progressBar while loading
                progressBar.setVisibility(View.VISIBLE);

                vehicleManage.sendRemoveRequest(UserVehicleID, new VolleyCallBackInterface() {
                    @Override
                    public void onSuccess(String result) {
                        if (progressBar != null && progressBar.isShown()) { progressBar.setVisibility(View.INVISIBLE); }

                        Toast.makeText(UserRemoveVehicleActivity.this, "Vehicle removed!", Toast.LENGTH_SHORT).show();

                        // Reload activity
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(UserRemoveVehicleActivity.this, "Something went wrong:(", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    public void spinnerInit(String[] vehicleAlias, userVehicle[] userVehicleList) {

        Spinner selectAlias = findViewById(R.id.vehicleMenu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehicleAlias);
        selectAlias.setAdapter(adapter);
        selectAlias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                alias = parent.getItemAtPosition(pos).toString();

                for(int i = 0; i<userVehicleList.length; i++) {
                    if(alias.equals(userVehicleList[i].getVehicle_alias())) {
                        UserVehicleID = userVehicleList[i].getUser_vehicle_id();
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
}