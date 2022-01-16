package com.vu.emapis;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vu.emapis.request.StatsManage;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class userIndividualTripStatsActivity extends AppCompatActivity {


    private TextView makeAndModelTextView;
    private TextView dateTextView;
    private TextView distanceTextView;
    private TextView durationTextView;
    private TextView consumedEnergyTextView;
    private TextView avgConsumptionTextView;
    private TextView titleTextView;
    private Button backButton;
    private String trip_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_trip_info);

        //Declaring Widgets
        backButton = findViewById(R.id.backButton);
        titleTextView = findViewById(R.id.titleText);
        makeAndModelTextView = findViewById(R.id.makeAndModelTextView);
        dateTextView = findViewById(R.id.dateTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        durationTextView = findViewById(R.id.durationTextView);
        consumedEnergyTextView = findViewById(R.id.consumedEnergyTextView);
        avgConsumptionTextView = findViewById(R.id.avgConsumptionTextView);

        // We try to get trip ID from last activity, if we don't get it, we throw an error.
        if(getIntent().hasExtra("trip_ID")) {

            trip_ID = (getIntent().getStringExtra("trip_ID"));
            trip_ID = trip_ID.substring(0, trip_ID.length() - 13);

            titleTextView.setText("Trip " + trip_ID + " statistics");



            StatsManage statsManage = new StatsManage(userIndividualTripStatsActivity.this);
            statsManage.getIndividualTripStats(trip_ID, new VolleyCallBackInterface() {
                @Override
                public void onSuccess(String result) {
                    Log.d("Data", statsManage.tripStats[0].toString());

                    if(!statsManage.tripStats[0].isStats_ready()) {
                        titleTextView.setText("Processing statistics...");
                    } else {

                        if(statsManage.tripStats[0].getTrip_distance() <= 0) {
                            titleTextView.setText("Trip distance is null");
                        } else {

                            Double tripDistance = BigDecimal.valueOf(statsManage.tripStats[0].getTrip_distance()/1000)
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .doubleValue();
                            Double energy_cons = BigDecimal.valueOf(statsManage.tripStats[0].getConsumed_energy())
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .doubleValue();
                            Double avg_cons = BigDecimal.valueOf(statsManage.tripStats[0].getAvg_consumption())
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .doubleValue();
                            String time = String.valueOf(statsManage.tripStats[0].getTrip_total_time());

                            dateTextView.append(statsManage.tripStats[0].getDate());
                            makeAndModelTextView.append(statsManage.tripStats[0].getMake().concat(" " + statsManage.tripStats[0].getModel()));
                            distanceTextView.append(tripDistance + " km");
                            durationTextView.append(time.substring(0, time.length() - 6));
                            consumedEnergyTextView.append(energy_cons/1000 + " kWh");
                            avgConsumptionTextView.append(avg_cons/1000 + " kWh/km");
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(userIndividualTripStatsActivity.this, "Could not retrieve data for that trip", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        } else {
            Toast.makeText(userIndividualTripStatsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}