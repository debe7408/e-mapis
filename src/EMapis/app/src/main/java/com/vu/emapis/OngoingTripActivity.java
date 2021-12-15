package com.vu.emapis;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OngoingTripActivity extends AppCompatActivity {


    boolean clicked = false;
    private long lastPause;

    @BindView(R.id.tripStatus)
    TextView tripStatus;

    @BindView(R.id.btn_pause_trip)
    Button btnPauseTrip;

    @BindView(R.id.btn_stop_trip)
    Button btnStopTrip;

    @BindView(R.id.btn_recharge)
    Button btnRecharge;

    // location last updated time
    private String mLastUpdateTime;


    // location updates interval - 5 sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    // fastest updates interval - 4 sec
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 4000;

    double[] pointArray = new double[63 ];
    int global_index = 0;
    private boolean passed = true;  // TODO Might delete this since we only use 1 function to send data

    public static int seekBarValue = TripSettingsActivity.seekBarValue;

    private final String insertInputURL = "http://193.219.91.103:4558/rpc/new_inputs"; //TODO FIX URL AFTER EVALUATING WHAT WE WANT TO DO
    private final String sendingDataURL = "http://193.219.91.103:4558/rpc/_emapis_gps_trace_insert";


    // location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    private View view;

    public static String trip_ID;
    public BigDecimal finalSend = BigDecimal.valueOf(0);

    private SeekBar seekBar;
    private TextView textView;
    private Chronometer simpleChronometer;
    private TextView tripLenghtTextView;




    // VolleyCallback interface
    public interface VolleyCallbackGet {
        void onSuccess(String result);
        void onError(String error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        //TODO:
        // Implement Async
        // Implement an interface that reacts if the user has allowed the app to access permissions


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_trip);

        // Define widgets

        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);
        tripLenghtTextView = findViewById(R.id.textView4);

        ButterKnife.bind(this);

        seekBarInit();

        simpleChronometer = findViewById(R.id.simpleChronometer);
        simpleChronometer.start();
        simpleChronometer.setBase(SystemClock.elapsedRealtime());

        Intent intent = getIntent();
        trip_ID = intent.getStringExtra(TripSettingsActivity.trip_ID);
        Log.d("Testas =", trip_ID);

        initLib();
        startLocationService();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(clicked) {
            resumeLocationUpdates();
            simpleChronometer.setBase(simpleChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
            simpleChronometer.start();
            clicked = false;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting")
                .setMessage("Are you sure you want to go back? The trip will be ended.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopLocationUpdates();
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void initLib() {  //initialize all the location related clients
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this); //this API makes it easy for an app to ensure that the device's system settings are properly configured for the app's location needs.

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocation();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @SuppressLint("SetTextI18n")
    private void updateLocation() {


        if (mCurrentLocation != null) {

            pointArray[global_index] = mCurrentLocation.getLatitude();
            global_index++;
            pointArray[global_index] = mCurrentLocation.getLongitude();
            global_index++;
            pointArray[global_index] = mCurrentLocation.getAltitude();
            global_index++;

            Log.d("global_index", Integer.toString(global_index));

            if (global_index == 60) {

                String dataPost = (Arrays.toString(pointArray).replace("[", "{")).replace("]", "}");
                Log.d("Array2: ", dataPost);

                sendArrayPointPost(sendingDataURL, dataPost, new VolleyCallbackGet() {
                    @Override
                    public void onSuccess(String result) {
                        global_index = 0;
                        Log.d("Final", result);
                        global_index = 0;
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(OngoingTripActivity.this, "Uh oh, something went wrong.", Toast.LENGTH_LONG).show();
                        global_index = 0;
                    }
                });

            }
        }
    }


    public void startLocationService() {

        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();

                        // TODO before the user accepts permissions, do not count time etc.
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when permission denied
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) { //fix
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openSettings() { // Opens settings to add permissions.
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocation();
                    }
                });
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
    }

    protected void resumeLocationUpdates() {
        startLocationUpdates();
    }

    private void sendArrayPointPost(String url, String dataPost, final VolleyCallbackGet callbackPost2) {

        RequestQueue queue = Volley.newRequestQueue(this); // New requestQueue using Volley's default queue.

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                String distance = response.replace("\"", "");
                BigDecimal bigDecimal = new BigDecimal(Double.valueOf(distance));
                BigDecimal divisor = new BigDecimal(1000);

                bigDecimal = bigDecimal.divide(divisor);
                bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);

                finalSend = finalSend.add(bigDecimal);

                distance = finalSend.toString();

                tripLenghtTextView.setText(distance.concat(" km"));

                Arrays.fill(pointArray, 0.0);
                callbackPost2.onSuccess(distance);


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
                MyData.put("points", dataPost);

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

    public void rechargeOnClick(View view) {

        stopLocationUpdates();

        // Send data
        String dataPost = (Arrays.toString(pointArray).replace("[", "{")).replace("]", "}");
        Log.d("Recharge send data: ", dataPost);

        sendArrayPointPost(sendingDataURL, dataPost, new VolleyCallbackGet() {
            @Override
            public void onSuccess(String result) {
                global_index=0;
                Log.d("Final", result);
                global_index=0;
            }

            @Override
            public void onError(String error) {
                Toast.makeText(OngoingTripActivity.this, "Uh oh, something went wrong.", Toast.LENGTH_LONG).show();
                global_index=0;
            }
        });

        lastPause = SystemClock.elapsedRealtime();
        simpleChronometer.stop();
        clicked = true;

        Intent intent = new Intent(this, PopUpActivity.class);
        startActivity(intent);
    }

    public void pauseTripOnClick(View view) {

        if(!clicked) {
            stopLocationUpdates();
            lastPause = SystemClock.elapsedRealtime();
            simpleChronometer.stop();
            tripStatus.setText("Trip is paused");
            btnPauseTrip.setText("Resume the trip");
            clicked = true;

            String dataPost = (Arrays.toString(pointArray).replace("[", "{")).replace("]", "}");
            Log.d("Pause trip send data: ", dataPost);

            // Send data
            sendArrayPointPost(sendingDataURL, dataPost, new VolleyCallbackGet() {
                @Override
                public void onSuccess(String result) {
                    global_index=0;
                    Log.d("Final", result);
                    global_index=0;
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(OngoingTripActivity.this, "Uh oh, something went wrong.", Toast.LENGTH_LONG).show();
                    global_index=0;
                }
            });

        } else {
            resumeLocationUpdates();
            simpleChronometer.setBase(simpleChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
            simpleChronometer.start();
            tripStatus.setText("Trip is in progress");
            btnPauseTrip.setText("Pause the trip");
            clicked = false;
        }
    }

    public void endTripOnClick(View view) {
        stopLocationUpdates();

        String dataPost = (Arrays.toString(pointArray).replace("[", "{")).replace("]", "}");
        Log.d("End trip send data: ", dataPost);
        // Send data
        sendArrayPointPost(sendingDataURL, dataPost, new VolleyCallbackGet() {
            @Override
            public void onSuccess(String result) {
                global_index=0;
                Log.d("Final", result);
                global_index=0;
            }

            @Override
            public void onError(String error) {
                Toast.makeText(OngoingTripActivity.this, "Uh oh, something went wrong.", Toast.LENGTH_LONG).show();
                global_index=0;
            }
        });

        Intent intent = new Intent(this, TripEndActivity.class);
        intent.putExtra(trip_ID, trip_ID);
        startActivity(intent);
        finish();
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

    public void updateEnergyLevelButton(View view){
        Log.d("seekBarValue", String.valueOf(seekBarValue));
        Log.d(".getProgress", String.valueOf(seekBar.getProgress()));

        if (seekBarValue >= seekBar.getProgress()){
            stopLocationUpdates();

            String dataPost = (Arrays.toString(pointArray).replace("[", "{")).replace("]", "}");
            Log.d("charge send data: ", dataPost);

            // Send data
            sendArrayPointPost(sendingDataURL, dataPost, new VolleyCallbackGet() {
                @Override
                public void onSuccess(String result) {
                    global_index=0;
                    Log.d("Final", result);
                    seekBarValue = seekBar.getProgress();
                    // sendUserInput(); TODO ---------------- CREATE CALLBACK ----------- LEAVE POST COMMENTED FOR NOW
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(OngoingTripActivity.this, "Uh oh, something went wrong.", Toast.LENGTH_LONG).show();
                    global_index=0;
                }
            });


            global_index=0;
        }
        else {
            Toast.makeText(OngoingTripActivity.this, "Error! Check if you have correctly inputted your energy level", Toast.LENGTH_LONG).show();
        }
    }

    public void sendUserInput() {

        RequestQueue queue = Volley.newRequestQueue(this); // New requestQueue using Volley's default queue.

        JSONObject postData = new JSONObject(); // Creating JSON object with data that will be sent via POST request.
        try {
            // TODO THINK OF WHAT WE WANT TO POST
            postData.put("user_id", LoginActivity.userId);
            postData.put("trip_id", Integer.parseInt(trip_ID));
            postData.put("input_value", String.valueOf(seekBar.getProgress()));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, insertInputURL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                resumeLocationUpdates();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
}