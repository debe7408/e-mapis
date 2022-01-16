package com.vu.emapis;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.vu.emapis.objects.statisticsObject;

import org.json.JSONArray;
import org.json.JSONException;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Button individualStatsButton = findViewById(R.id.individualStatsButton);
        Button generalStatsButton = findViewById(R.id.generalStatsButton);
        Button byUserVehicleStatsButton = findViewById(R.id.generalIndividualStatsButton);

        individualStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this, userIndividualTripsActivity.class);
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