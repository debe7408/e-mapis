package com.vu.emapis;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vu.emapis.request.MetaDataPostRequest;

public class RechargingActivity extends AppCompatActivity {

    // Widgets
    private SeekBar seekBar;
    private TextView textView;

    // Vars
    public static int seekBarValue = PopUpActivity.seekBarValue;
    private String trip_ID;
    private MetaDataPostRequest metaDataPostRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharging);

        // Define vars
        trip_ID = OngoingTripActivity.trip_ID;
        metaDataPostRequest = new MetaDataPostRequest(RechargingActivity.this);

        // Define widgets
        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);

        seekBarInit();
    }

    public void seekBarInit() {
        textView.setText("Energy levels: "+seekBar.getProgress() + "%");

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
                textView.setText("Energy levels: "+progressValue + "%");
            }
        });
    }

    // Is called when the user resumes the trip by clicking Resume Trip button
    public void resumeTripOnClick(View view) {

        Log.d("seekbarvalue", String.valueOf(seekBarValue));

        if (seekBarValue <= seekBar.getProgress()){

            metaDataPostRequest.sendMetaData(trip_ID, "after_recharge", seekBar.getProgress(), new VolleyCallBackInterface() {
                @Override
                public void onSuccess(String result) {

                }

                @Override
                public void onError(String error) {

                }
            });

            OngoingTripActivity.seekBarValue = seekBar.getProgress();
            this.finish();
        }
        else {
            Toast.makeText(RechargingActivity.this, "Error! Check if you have correctly inputted your energy level", Toast.LENGTH_LONG).show();
        }
    }
}