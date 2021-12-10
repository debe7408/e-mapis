package com.vu.emapis;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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
    private String trip_ID;

    public static int seekBarValue = OngoingTripActivity.seekBarValue;

    String postURL = "http://193.219.91.103:8666/rpc/end_trip";

    @BindView(R.id.btn_showStats)
    Button btnShowStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_end);

        Intent intent = getIntent();
        trip_ID = intent.getStringExtra(OngoingTripActivity.trip_ID);

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

    public void showStatistics(View view) {

        if (seekBarValue >= seekBar.getProgress()){
            // sendUserInput(); TODO UNCOMMENT WHEN DB FUNCTIONING
            Intent intent = new Intent(TripEndActivity.this, VolleyActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(TripEndActivity.this, "Error! Check if you have correctly inputted your energy level", Toast.LENGTH_LONG).show();
        }
        //sendPostRequest(); TODO NOT NEEDED?
    }


    /*private void sendPostRequest() {


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {

                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("trip_id", trip_ID);
                MyData.put("fuel_at_end", String.valueOf(seekBar.getProgress()));
                return MyData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoidG9kb191c2VyIn0.kTNyXxM8oq1xhVwNznb08dlSxIjq1F023zeTWyKNcNY");
                return headers;
            }
        };

        requestQueue.add(stringRequest);

    }*/
}