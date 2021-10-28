package com.vu.emapis;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
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

    // location last updated time
    private String mLastUpdateTime;


    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    // postURL
    private final String postURL = "http://193.219.91.103:3906/rpc/points_insert";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_trip);

        ButterKnife.bind(this);
        initLib();
        startLocationService();

        simpleChronometer = findViewById(R.id.simpleChronometer);
        simpleChronometer.start();
        simpleChronometer.setBase(SystemClock.elapsedRealtime());
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
                .setMessage("Are you sure you want to exit? Your progress will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
            sendPostRequest(); //TODO UNCOMMENT THIS TO SEND X,Y,Z AUTOMATICALLY
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

    private void sendPostRequest() {
        String trip_id = "69";

        RequestQueue queue = Volley.newRequestQueue(this); // New requestQueue using Volley's default queue.

        JSONObject postData = new JSONObject(); // Creating JSON object with data that will be sent via POST request.
        try {

            postData.put("trip_id", Integer.parseInt(trip_id));
            postData.put("x", mCurrentLocation.getLatitude());
            postData.put("y", mCurrentLocation.getLongitude());
            postData.put("z", mCurrentLocation.getAltitude());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postURL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
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
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoidG9kb191c2VyIn0.qusYUABWSUHBopNCItHAvrW6SHJe1BAoKqXqo-E26Zs");
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
        startActivity(intent);
        finish();
    }
}