package com.vu.emapis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vu.emapis.request.MetaDataPostRequest;
import com.vu.emapis.request.TripManage;

import butterknife.BindView;

public class TripEndActivity extends AppCompatActivity {

    // Widgets
    private SeekBar seekBar;
    private TextView textView;

    // Vars
    private int seekBarValue = OngoingTripActivity.seekBarValue;
    private TripManage tripManage;
    private MetaDataPostRequest metaDataPostRequest;
    private String trip_ID;

    @BindView(R.id.btn_showStats)
    Button btnShowStats;

    //TODO
    // Make a pop-up that tells the user that the trip is being built and show the progressBar
    // Give the user the ability to exit that pop-up or wait for the trip to finish building

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_end);

        trip_ID = OngoingTripActivity.trip_ID;

        // Create object to manage trip like end/start
        tripManage = new TripManage(TripEndActivity.this);

        // Create class object to send data to Meta table
        metaDataPostRequest = new MetaDataPostRequest(TripEndActivity.this);

        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);

        seekBarInit();
    }

    public void seekBarInit() {
        textView.setText("Energy levels: " + seekBar.getProgress() + "%");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progressValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText("Energy levels: " + progressValue + "%");
            }
        });
    }

    // Triggered when Finalize Trip button is clicked
    public void finalizeTrip(View view) {

        if (seekBarValue >= seekBar.getProgress()){

            metaDataPostRequest.sendMetaData(trip_ID, "last_input", seekBar.getProgress(), new VolleyCallBackInterface(){

                @Override
                public void onSuccess(String result) {
                    tripManage.buildRoute(trip_ID, new VolleyCallBackInterface() {
                        @Override
                        public void onSuccess(String result) {

                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(TripEndActivity.this, "Processing latest trip. This can take up to 5 min", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Intent intent = new Intent(TripEndActivity.this, MainScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }

                @Override
                public void onError(String error) {

                }
            });
        }
        else {
            Toast.makeText(TripEndActivity.this, "Error! Check if you have correctly inputted your energy level", Toast.LENGTH_LONG).show();
        }
    }
}