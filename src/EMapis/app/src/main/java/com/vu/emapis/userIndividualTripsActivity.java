package com.vu.emapis;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vu.emapis.request.StatsManage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class userIndividualTripsActivity extends AppCompatActivity {

    // Widgets
    private ListView listView;

    // Vars
    private ArrayList<String> statsArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_individual_stats);

        int userID = Integer.parseInt(LoginActivity.userId);

        // Declare widgets
        listView = findViewById(R.id.ListView);

        StatsManage statsManage = new StatsManage(userIndividualTripsActivity.this);
        //send request to retrieve stats for individual trips
        statsManage.getIndividualTrips(userID, new VolleyCallBackInterfaceJSON() {
            @Override
            public void onSuccess(JSONArray result) {
                // If the user has no trips recorded, the activity will exit
                if(statsManage.stats == null || statsManage.stats.length <= 0) {
                    Log.d("Success", "Empty"); // Logcat test

                    Toast.makeText(userIndividualTripsActivity.this, "No trips recorded!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                // Otherwise, store data to an ArrayList and display it in the ListView
                else {
                    for(int i=0; i<statsManage.stats.length; i++) {
                        Log.d("Success", "trip id = "+ statsManage.stats[i].getTrip_id());
                        String date = statsManage.stats[i].getTrip_start_time();
                        if (date != null) {
                            date = date.substring(0, 10);
                        } else {
                            date = "data not found";
                        }

                        statsArray.add("Trip ID: " + statsManage.stats[i].getTrip_id() + " (" + date + ")");
                    }
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(userIndividualTripsActivity.this, R.layout.black_text_listview, statsArray);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onError(String error) {
                Log.d("error", error);
                Toast.makeText(userIndividualTripsActivity.this, "Something went wrong retrieving data", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Click listener for the ListView item
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

               if (listView.getItemAtPosition(position).toString().contains("found")) {
                   Toast.makeText(userIndividualTripsActivity.this, "Something went wrong, trip data not found", Toast.LENGTH_SHORT).show();
               } else {
                   // Open a new activity for that trip
                   Intent intent = new Intent(userIndividualTripsActivity.this, userIndividualTripStatsActivity.class);

                   intent.putExtra("trip_ID", listView.getItemAtPosition(position).toString().replace("Trip ID: ", ""));
                   startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(userIndividualTripsActivity.this).toBundle());
               }
           }
       });

       listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

               Object o = listView.getItemAtPosition(position);
               //TODO Implement functionality when user long presses the item
               Toast.makeText(userIndividualTripsActivity.this, "Testas " + o, Toast.LENGTH_SHORT).show();

               return false;
           }
       });
    }
}