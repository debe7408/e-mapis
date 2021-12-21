package com.vu.emapis;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class TripEndActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView textView;

    public static int seekBarValue = OngoingTripActivity.seekBarValue;

    String postURL = "http://193.219.91.103:8666/rpc/end_trip";

    public static String trip_ID;

    @BindView(R.id.btn_showStats)
    Button btnShowStats;

    private interface VolleyCallbackGet {
        void onSuccess(String result);
        void onError(String error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_end);

        trip_ID = OngoingTripActivity.trip_ID;


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

    public void finalizeTrip(View view) {

        if (seekBarValue >= seekBar.getProgress()){

            sendUserInput("last_input", seekBar.getProgress(), new VolleyCallbackGet(){

                @Override
                public void onSuccess(String result) {

                }

                @Override
                public void onError(String error) {

                }
            });



            Intent intent = new Intent(TripEndActivity.this, MainScreenActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(TripEndActivity.this, "Error! Check if you have correctly inputted your energy level", Toast.LENGTH_LONG).show();
        }
    }


    private void sendUserInput(String inputKey, Integer inputValue, final VolleyCallbackGet callbackPost2) {

        RequestQueue queue = Volley.newRequestQueue(this); // New requestQueue using Volley's default queue.

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://193.219.91.103:4558/rpc/_emapis_update_battery_level", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                callbackPost2.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callbackPost2.onError(error.toString());
            }
        }) {
            protected Map<String, String> getParams() {

                Map<String, String> MyData = new HashMap<String, String>();

                MyData.put("trip_id", trip_ID);
                MyData.put("input_key", inputKey);
                MyData.put("input_value", String.valueOf(inputValue));

                return MyData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

        queue.add(stringRequest);

    }


}