package com.vu.emapis;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vu.emapis.request.MetaDataPostRequest;

public class PopUpActivity extends AppCompatActivity {

    // Widgets
    private SeekBar seekBar;
    private TextView textView;
    // Vars
    public static int seekBarValue;
    public String trip_ID;
    MetaDataPostRequest metaDataPostRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        // Define widgets
        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);

        // Define vars
        trip_ID = OngoingTripActivity.trip_ID;
        metaDataPostRequest = new MetaDataPostRequest(PopUpActivity.this);



        seekBarInit();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*0.7));

    }

    // Is called when the user click to update their energy levels
    public void updateEnergyLevelPopUp(View view){
        //Logcat testing
        Log.d(".getProgress", String.valueOf(seekBar.getProgress()));

        seekBarValue = OngoingTripActivity.seekBarValue;
        Log.d("seekbarvalue", String.valueOf(seekBarValue));


        if (seekBarValue >= seekBar.getProgress()){

            metaDataPostRequest.sendMetaData(trip_ID, "before_recharge", seekBar.getProgress(), new VolleyCallBackInterface() {
                @Override
                public void onSuccess(String result) {
                    seekBarValue = seekBar.getProgress();
                    Intent intent = new Intent(PopUpActivity.this, RechargingActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String error) {

                }
            });
        }
        else {
            Toast.makeText(PopUpActivity.this, "Error! Check if you have correctly inputted your energy level", Toast.LENGTH_LONG).show();
        }

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
}