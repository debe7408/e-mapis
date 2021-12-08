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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OngoingTripActivity extends AppCompatActivity {

    Chronometer simpleChronometer;
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

    @BindView(R.id.btn_update_energy_level)
    Button btnUpdateEnergy;

    // location last updated time
    private String mLastUpdateTime;


    // location updates interval - 5 sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    // fastest updates interval - 1 sec
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 3000;

    double[] pointArray = new double[60];
    double[] firstPointArray = new double[9];
    int global_index = 0;
    private boolean passed = false;

    // postURL
    private final String pointBlockUrl = "http://193.219.91.103:4558/rpc/point_insert_array";
    private final String firstPointURL =  "http://193.219.91.103:4558/rpc/first_point_insert";
    private final String insertInputURL = "http://193.219.91.103:4558/rpc/new_inputs";

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

    private SeekBar seekBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_trip);
        seekBar = findViewById(R.id.rechargedEnergyLevels);
        textView = findViewById(R.id.energyLevelText);



        ButterKnife.bind(this);

        seekBarInit();

        simpleChronometer = findViewById(R.id.simpleChronometer);
        simpleChronometer.start();
        simpleChronometer.setBase(SystemClock.elapsedRealtime());

        Intent intent = getIntent();
        trip_ID = intent.getStringExtra(TripSettingsActivity.trip_ID);
        // DEBUG: System.out.println("Testas =" + trip_ID);

        initLib();
        startLocationService();



    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnline();
        if(clicked) {
            resumeLocationUpdates();
            simpleChronometer.setBase(simpleChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
            simpleChronometer.start();
            clicked = false;
        }
    }

    public void isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()) {

        }
        else {
            Toast.makeText(this, "Your trip is not being recorded due to connection issues. Enable internet connection and try again!", Toast.LENGTH_LONG).show();
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

        if ((mCurrentLocation != null) && (!passed)) {

            firstPointArray[global_index] = mCurrentLocation.getLatitude();
            global_index++;
            firstPointArray[global_index] = mCurrentLocation.getLongitude();
            global_index++;
            firstPointArray[global_index] = mCurrentLocation.getAltitude();
            global_index++;

            Log.d("global_index", Integer.toString(global_index));

            if (global_index==9) {
                Log.d("array", Arrays.toString(firstPointArray));

                sendFirstPointPostRequest();
                //sendPostRequest();
                global_index=0;
            }
        } else if (mCurrentLocation != null) {
            pointArray[global_index] = mCurrentLocation.getLatitude();
            global_index++;
            pointArray[global_index] = mCurrentLocation.getLongitude();
            global_index++;
            pointArray[global_index] = mCurrentLocation.getAltitude();
            global_index++;

            Log.d("global_index", Integer.toString(global_index));

            if (global_index==60) {
                Log.d("array", Arrays.toString(pointArray));

                sendPostRequest();
                global_index=0;
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



//    private void sendFirstPointPostRequest(String testas) {
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//
//        JSONArray jsonArray = new JSONArray();
//
//        for(int i=0; i<9; i++) {
//            try {
//                jsonArray.put(pointArray[i]);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        Log.d("Arejus,", jsonArray.toString());
//
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, firstPointURL, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//
//                    Log.d("Response", response);
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                    error.printStackTrace();
//                    isOnline();
//                }
//            }) {
//                protected Map<String, String> getParams() {
//
//                    Map<String, String[]> MyData = new HashMap<String, String[]>();
//
//                    MyData.put("trip_id", trip_ID);
//                    MyData.put("points", jsonArray);
//
//                    return MyData;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> headers = new HashMap<>();
//                    //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                    headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
//                    return headers;
//                }
//            };
//
//            queue.add(stringRequest);
//    }



    private void sendFirstPointPostRequest() {

        RequestQueue queue = Volley.newRequestQueue(this); // New requestQueue using Volley's default queue.

        JSONObject postData = new JSONObject(); // Creating JSON object with data that will be sent via POST request.
        JSONArray points = new JSONArray();
        try {

            postData.put("trip_id", Integer.parseInt(trip_ID));

            for (int i = 0; i < 9; i++) {
                points.put(firstPointArray[i]);
            }

            postData.put("points", points);



        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, firstPointURL, postData, new Response.Listener<JSONObject>() {



            @Override
            public void onResponse(JSONObject response) {

                Log.d("Response", response.toString());

                Log.d("Response", "Sw");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
                isOnline();

                passed = true;

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

        //jsonObjectRequest.getRetryPolicy();

        queue.add(jsonObjectRequest);
    }


    private void sendPostRequest() {

        RequestQueue queue = Volley.newRequestQueue(this); // New requestQueue using Volley's default queue.

        JSONObject postData = new JSONObject(); // Creating JSON object with data that will be sent via POST request.
        JSONArray points = new JSONArray();
        try {

            postData.put("trip_id", Integer.parseInt(trip_ID));

            for (int i = 0; i < 60; i++) {
                points.put(pointArray[i]);
            }

            postData.put("points", points);



        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, pointBlockUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                    error.printStackTrace();
                    isOnline();
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

    public void rechargeOnClick(View view) {
        Intent intent = new Intent(this, RechargingActivity.class);

        stopLocationUpdates();
        lastPause = SystemClock.elapsedRealtime();
        simpleChronometer.stop();
        clicked = true;

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

    public void updateEnergyLevel(View view){

        stopLocationUpdates();
        sendUserInput();
        resumeLocationUpdates();

        //post request

        //point id reikia priskirt (get request?)

        //sendUserInput();


    }

    private void sendUserInput() {

        RequestQueue queue = Volley.newRequestQueue(this); // New requestQueue using Volley's default queue.

        JSONObject postData = new JSONObject(); // Creating JSON object with data that will be sent via POST request.
        try {

            postData.put("user_id", LoginActivity.userId);
            postData.put("trip_id", Integer.parseInt(trip_ID));
            postData.put("input_value", "100");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, insertInputURL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //error.printStackTrace();
                isOnline();
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