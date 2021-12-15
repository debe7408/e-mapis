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

    public static String trip_ID;


    private interface VolleyCallbackGet {
        void onSuccess(String result);
        void onError(String error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);

        trip_ID = OngoingTripActivity.trip_ID;

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

            Log.d("seekbarvalue", String.valueOf(seekBarValue));
            Log.d("trip_id", trip_ID);

            sendUserInput("before_recharge", seekBar.getProgress(), new VolleyCallbackGet(){

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