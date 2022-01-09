package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Map;

public class SingleTripInfoActivity extends AppCompatActivity {


    private TextView makeAndModelTextView;
    private TextView dateTextView;
    private TextView distanceAndDurationTextView;
    private TextView consumedEnergyTextView;
    private TextView avgConsumptionTextView;
    private TextView titleTextView;
    private String trip_ID;
    private String getURL;
    private tripStatsObject[] stats;

    public interface VolleyCallbackGet {
        void onSuccess();
        void onError(String error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_trip_info);

        //Declaring Widgets
        titleTextView = findViewById(R.id.titleText);
        makeAndModelTextView = findViewById(R.id.makeAndModelTextView);
        dateTextView = findViewById(R.id.dateTextView);
        distanceAndDurationTextView = findViewById(R.id.distanceAndDurationTextView);
        consumedEnergyTextView = findViewById(R.id.consumedEnergyTextView);
        avgConsumptionTextView = findViewById(R.id.avgConsumptionTextView);

        // We try to get trip ID from last activity, if we don't get it, we throw an error.
        if(getIntent().hasExtra("trip_ID")) {

            trip_ID = (getIntent().getStringExtra("trip_ID"));

            titleTextView.setText("Trip " + trip_ID + " statistics");

            // URL for Get Request for statistics for the exact trip
            getURL = "http://193.219.91.103:4558/_emapis_get_data_about_trip?trip_id=eq."+trip_ID;
            sendGetRequest(getURL, new VolleyCallbackGet() {

                // On Success displays the info
                @Override
                public void onSuccess() {

                    Log.d("Data", stats[0].toString());

                    if(!stats[0].isStats_ready()) {
                        titleTextView.setText("Stats are not ready yet, please check back later");
                    } else {

                        if(stats[0].getTrip_distance() <= 0) {
                            titleTextView.setText("Trip distance is null");
                        } else {

                            Double tripDistance = BigDecimal.valueOf(stats[0].getTrip_distance())
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .doubleValue();

                            dateTextView.append(stats[0].getDate());
                            makeAndModelTextView.append(stats[0].getMake().concat(" " + stats[0].getModel()));
                            distanceAndDurationTextView.append(String.valueOf(tripDistance).concat(" "+ stats[0].getTrip_total_time()));
                            consumedEnergyTextView.append(String.valueOf(stats[0].getConsumed_energy()));
                            avgConsumptionTextView.append(String.valueOf(stats[0].getAvg_consumption()));
                        }
                    }
                }

                // On Error finishes the activity.
                @Override
                public void onError(String error) {
                    Toast.makeText(SingleTripInfoActivity.this, "Could not retrieve data for that trip", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });


        } else {
            Toast.makeText(SingleTripInfoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void sendGetRequest(String url, SingleTripInfoActivity.VolleyCallbackGet callback) {

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        // JSON -> GSON
                        Gson gson = new Gson();
                        stats = gson.fromJson(String.valueOf(response), tripStatsObject[].class);

                            callback.onSuccess();

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