package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class SingleTripInfoActivity extends AppCompatActivity {


    private TextView textView;
    private TextView titleText;
    private String trip_ID;
    private String getURL;
    private statisticsObject[] stats;

    public interface VolleyCallbackGet {
        void onSuccess(JSONArray result) throws JSONException;
        void onError(String error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_trip_info);

        //Declaring Widgets
        textView = findViewById(R.id.TripInfoText);
        titleText = findViewById(R.id.titleText);

        // We try to get trip ID from last activity, if we don't get it, we throw an error.
        if(getIntent().hasExtra("trip_ID")) {

            trip_ID = (getIntent().getStringExtra("trip_ID"));

            titleText.setText("Trip " + trip_ID + " statistics");

            // URL for Get Request for statistics for the exact trip
            getURL = "http://193.219.91.103:4558/trips?trip_id=eq."+trip_ID;
            sendGetRequest(getURL, new VolleyCallbackGet() {

                // On Success displays the info
                @Override
                public void onSuccess(JSONArray result) throws JSONException {

                    if(!stats[0].isStats_ready()) {
                        textView.setText("Stats are not ready yet, please check back later");
                    } else {
                        textView.setText(stats[0].toString());

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
            Toast.makeText(SingleTripInfoActivity.this, "Somewhing went wrong", Toast.LENGTH_SHORT).show();
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