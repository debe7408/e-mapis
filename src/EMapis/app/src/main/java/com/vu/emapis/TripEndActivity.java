package com.vu.emapis;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class TripEndActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView textView;

    @BindView(R.id.btn_showStats)
    Button btnShowStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_end);

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
                textView.setText("Energy levels: "+progressValue + "%");
            }
        });
    }

    public void showStatistics(View view) {
        Intent intent = new Intent(TripEndActivity.this, VolleyActivity.class);
        startActivity(intent);
    }

}