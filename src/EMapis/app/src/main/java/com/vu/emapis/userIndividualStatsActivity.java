package com.vu.emapis;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
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
import com.google.gson.internal.bind.ObjectTypeAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class userIndividualStatsActivity extends AppCompatActivity {


    private ListView listView;

    private statisticsObject[] stats;
    private ArrayList<String> statsArray = new ArrayList<String>();


    public interface VolleyCallbackGet {
        void onSuccess(JSONArray result) throws JSONException;
        void onError(String error);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_individual_stats);

        // Declare widgets
        listView = findViewById(R.id.ListView);

        // URL for the get request
        String url = "http://193.219.91.103:4558/trips?user_id=eq."+ LoginActivity.userId;


        // Call a new get request
        sendGetRequest(url, new VolleyCallbackGet() {

            // If the get request is successful execute this
            @Override
            public void onSuccess(JSONArray result) throws JSONException {

                // If the user has no trips recorded, the activity will exit
                if(stats == null || stats.length <= 0) {
                    Log.d("Success", "Empty");
                    Toast.makeText(userIndividualStatsActivity.this, "Seems like you don't have any trips yet", Toast.LENGTH_SHORT).show();
                    finish();
                }

                // Otherwise, store data to an ArrayList and display it in the ListView
                else {
                    for(int i=0; i<stats.length; i++) {
                        Log.d("Success", "trip id = "+ stats[i].getTrip_id());

                        statsArray.add("Trip ID = " + stats[i].getTrip_id());
                    }
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(userIndividualStatsActivity.this, android.R.layout.simple_list_item_1, statsArray);
                listView.setAdapter(arrayAdapter);
            }

            // When error accures, do something
            @Override
            public void onError(String error) {
                Log.d("error", error);
                //TODO Implement on error handling
            }
        });

        // Click listener for the ListView
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

               Object o = listView.getItemAtPosition(position);
               Toast.makeText(userIndividualStatsActivity.this, "Hello " + o, Toast.LENGTH_SHORT).show();

           }
       });

    }


    public void sendGetRequest(String url, VolleyCallbackGet callback) {

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        // JSON -> GSON
                        Gson gson = new Gson();
                        stats = gson.fromJson(String.valueOf(response), statisticsObject[].class);

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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

// Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }
}