package com.vu.emapis;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

public class StatisticsActivity extends AppCompatActivity {

    private static String userId = LoginActivity.userId; //TODO IMPLEMENT INTO THE URL
    private statisticsObject[] stats;

    private Button individualStatsButton;
    private Button generalStatsButton;
    private Button byUserVehicleStatsButton;



    public interface VolleyCallbackGet {
        void onSuccess(JSONArray result) throws JSONException;
        void onError(String error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        individualStatsButton = findViewById(R.id.individualStatsButton);
        generalStatsButton = findViewById(R.id.generalStatsButton);
        byUserVehicleStatsButton = findViewById(R.id.generalIndividualStatsButton);

        individualStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this, userIndividualStatsActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(StatisticsActivity.this).toBundle());
            }
        });

        generalStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this, GeneralStatsActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(StatisticsActivity.this).toBundle());
            }
        });

        byUserVehicleStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this, ByUserVehicleActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(StatisticsActivity.this).toBundle());
            }
        });




        String url = "http://193.219.91.103:4558/trips?select=trip_id,average_consumption,trip_distance,stats_ready&user_id=eq.95";
        sendGetRequest(url, new VolleyCallbackGet() {

            @Override
            public void onSuccess(JSONArray result) {

                //Log.d("avg", stats[0].getAverage_consumption());
                ConstraintLayout statsAct = (ConstraintLayout) findViewById(R.id.statsAct);

                for(statisticsObject obj : stats) {

                    if(obj.isStats_ready()) {
                        //display stat box

                        /*TextView dynamicTextView = new TextView(StatisticsActivity.this);
                        dynamicTextView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
                        dynamicTextView.setText("Trip distance "+obj.getTrip_distance());

                        statsAct.addView(dynamicTextView);*/


                    } else {
                        //display 'processing' box

                    }
                }

            }
            @Override
            public void onError(String error) {


            }
        });

    }


    private void sendGetRequest(String url, final VolleyCallbackGet callback) {

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

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