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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.vu.emapis.objects.statisticsObject;
import com.vu.emapis.request.individualTripStatsRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class userIndividualStatsActivity extends AppCompatActivity {

    private ListView listView;

    private ArrayList<String> statsArray = new ArrayList<String>();

    private int UserID;


    public interface VolleyCallbackGet {
        void onSuccess(JSONArray result) throws JSONException;
        void onError(String error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_individual_stats);

        UserID = Integer.parseInt(LoginActivity.userId);

        // Declare widgets
        listView = findViewById(R.id.ListView);


        individualTripStatsRequest individualTripStats = new individualTripStatsRequest(userIndividualStatsActivity.this);
        //send request to retrieve stats for individual trips
        individualTripStats.getIndividualTripStats(UserID, new VolleyCallbackGet() {
            @Override
            public void onSuccess(JSONArray result) throws JSONException {
                // If the user has no trips recorded, the activity will exit
                if(individualTripStats.stats == null || individualTripStats.stats.length <= 0) {
                    Log.d("Success", "Empty"); // Logcat test

                    Toast.makeText(userIndividualStatsActivity.this, "No trips recorded!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                // Otherwise, store data to an ArrayList and display it in the ListView
                else {
                    for(int i=0; i<individualTripStats.stats.length; i++) {
                        Log.d("Success", "trip id = "+ individualTripStats.stats[i].getTrip_id());
                        String date = individualTripStats.stats[i].getTrip_start_time();
                        if (date != null) {
                            date = date.substring(0, 10);
                        } else {
                            date = "data not found";
                        }

                        statsArray.add("Trip ID: " + individualTripStats.stats[i].getTrip_id() + " (" + date + ")");
                    }
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(userIndividualStatsActivity.this, R.layout.black_text_listview, statsArray);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onError(String error) {
                Log.d("error", error);
                Toast.makeText(userIndividualStatsActivity.this, "Something went wrong retrieving data", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        // Click listener for the ListView item
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

               if (listView.getItemAtPosition(position).toString().contains("found")) {
                   Toast.makeText(userIndividualStatsActivity.this, "Something went wrong, trip data not found", Toast.LENGTH_SHORT).show();
               } else {
                   // Open a new activity for that trip
                   Intent intent = new Intent(userIndividualStatsActivity.this, SingleTripInfoActivity.class);

                   intent.putExtra("trip_ID", listView.getItemAtPosition(position).toString().replace("Trip ID: ", ""));
                   startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(userIndividualStatsActivity.this).toBundle());
               }
           }
       });

       listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

               Object o = listView.getItemAtPosition(position);
               //TODO Implement functionality when user long presses the item
               Toast.makeText(userIndividualStatsActivity.this, "Testas " + o, Toast.LENGTH_SHORT).show();

               return false;
           }
       });

    }
}