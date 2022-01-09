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

    }

}