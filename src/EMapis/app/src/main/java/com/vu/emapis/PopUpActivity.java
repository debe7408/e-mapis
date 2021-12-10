package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;

public class PopUpActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView textView;

    public static int seekBarValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);

        seekBarInit();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*0.7));

    }

    public void updateEnergyLevelPopUp(View view){
        Log.d(".getProgress", String.valueOf(seekBar.getProgress()));
        Log.d("seekbarvalue", String.valueOf(seekBarValue));

        seekBarValue = OngoingTripActivity.seekBarValue;
        Log.d("seekbarvalue", String.valueOf(seekBarValue));

        if (seekBarValue >= seekBar.getProgress()){
            // sendUserInput(); TODO UNCOMMENT WHEN DB FUNCTIONING
            seekBarValue = seekBar.getProgress();
            Intent intent = new Intent(this, RechargingActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(PopUpActivity.this, "Error! Check if you have correctly inputted your energy level", Toast.LENGTH_LONG).show();
        }

        /*OngoingTripActivity ongoingTripActivity = new OngoingTripActivity(); TODO UNCOMMENT WHEN DB FUNCTIONING
        ongoingTripActivity.sendUserInput();*/

        //post request

        //point id reikia priskirt (get request?)

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