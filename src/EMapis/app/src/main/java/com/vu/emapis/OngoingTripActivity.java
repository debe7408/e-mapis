package com.vu.emapis;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.WallpaperColors;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
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
import com.vu.emapis.request.MetaDataPostRequest;
import com.vu.emapis.request.SendArrayPointPost;
import com.vu.emapis.request.weatherRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OngoingTripActivity extends AppCompatActivity {


    private long lastPause;



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

    public static boolean firstMetaSent = false;
    public static boolean temperatureSent = false;

    private final String insertMetaData = "http://193.219.91.103:4558/rpc/_emapis_update_battery_level"; //This URL is used for sending data to meta table ( inaccurate URL name )

    // location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;


    // Widgets
    private SeekBar seekBar;
    private TextView energyLevelTextView;
    private Chronometer simpleChronometer;
    public TextView tripLengthTextView;
    public TextView tripStatusTextView;
    public Button btnPauseTrip;
    public Button btnStopTrip;
    public Button btnRecharge;

    // Vars

    public BigDecimal finalSend = BigDecimal.valueOf(0);
    public static String trip_ID; // TODO REMOVE STATIC
    private SendArrayPointPost sendArrayPointPost;
    private MetaDataPostRequest metaDataPostRequest;
    private String sendPointArrayURL;
    private boolean clicked = false;
    private String temperature;


    // VolleyCallback interface
    public interface VolleyCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    weatherRequest weatherRequest = new weatherRequest(OngoingTripActivity.this, "Vilnius", "metric");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_trip);

        // Retrieve trip_ID from extras
        Intent intent = getIntent();
        trip_ID = intent.getStringExtra("tripID");
        Log.d("Testas =", trip_ID);

        // Define widgets
        tripStatusTextView = findViewById(R.id.tripStatus);
        btnPauseTrip = findViewById(R.id.btn_pause_trip);
        btnStopTrip = findViewById(R.id.btn_stop_trip);
        btnRecharge = findViewById(R.id.btn_recharge);
        seekBar = findViewById(R.id.rechargedEnergyLevels);
        energyLevelTextView = findViewById(R.id.energyLevelText);
        tripLengthTextView = findViewById(R.id.textView4);

        // Defines vars
        sendPointArrayURL = getString(R.string.postLocationPointsURL);
        int seekBarValue = TripSettingsActivity.seekBarValue;

        //Create SendArrayPointPost object to send location data
        sendArrayPointPost = new SendArrayPointPost(OngoingTripActivity.this);

        // Create MetaDataPostRequest object to send data to meta table like energy etc.
        metaDataPostRequest = new MetaDataPostRequest(OngoingTripActivity.this);

        seekBarInit();

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

    private void initLib() {
        //initialize all the location related clients

        // Initialize FusedLocationClient API for location data tracking
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //this API makes it easy for an app to ensure that the device's system settings are properly configured for the app's location needs.
        mSettingsClient = LocationServices.getSettingsClient(this);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // Retrieve last location
                mCurrentLocation = locationResult.getLastLocation();

                // Retrieve last update time
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                // Update the location
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

    private void updateLocation() {

        // If the current location is not null
        if (mCurrentLocation != null) {

            // Retrieve all coordinates separately
            pointArray[global_index] = mCurrentLocation.getLatitude();
            global_index++;
            pointArray[global_index] = mCurrentLocation.getLongitude();
            global_index++;
            pointArray[global_index] = mCurrentLocation.getAltitude();
            global_index++;

            // Logcat testing
            Log.d("global_index", Integer.toString(global_index));

            // When the global index reaches 60, manipulate and send data
            if (global_index == 60) {

                //
                String dataPost = (Arrays.toString(pointArray).replace("[", "{")).replace("]", "}");
                // Logcat testing
                Log.d("Array2: ", dataPost);


                sendArrayPointPost.sendArrayPointPostRequest(trip_ID, sendPointArrayURL, dataPost, new VolleyCallBackInterface() {
                    @Override
                    public void onSuccess(String response) {

                        // Do length transformations
                        updateLength(response);

                        // Set the distance TextView to the current distance and reset the array
                        Arrays.fill(pointArray, 0.0);
                        global_index = 0;

                        // Logcat testing
                        Log.d("Final Distance:", response);

                        checkFirstInput();
                        sendTemperature();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(OngoingTripActivity.this, "Something went wrong. Data was not sent", Toast.LENGTH_LONG).show();
                        global_index = 0;
                    }
                });
            }
        }
    }

    public void updateLength(String response) {
        // Response transformation
        // ---------------------------------------------------------------------------------
        // Remove " from the response
        String distance = response.replace("\"", "");

        // Convert the response String to BigDecimal
        BigDecimal bigDecimal = BigDecimal.valueOf(Double.parseDouble(distance));

        // Define the divisor for rounding up the answer
        BigDecimal divisor = new BigDecimal(1000);

        // Round the response
        bigDecimal = bigDecimal.divide(divisor).setScale(2, RoundingMode.HALF_UP);

        // Declare the final answer after all the transformation as finalSend

        finalSend = finalSend.add(bigDecimal);

        // ---------------------------------------------------------------------------------

        // Convert the final distance from BigDecimal to String
        distance = finalSend.toString();

        tripLengthTextView.setText(distance.concat(" km"));

    }

    // TODO Fix permissions. A user can easily overcome it and break the app
    public void startLocationService() {

        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        mRequestingLocationUpdates = true;
                        startLocationUpdates();

                        simpleChronometer = findViewById(R.id.simpleChronometer);
                        simpleChronometer.start();
                        simpleChronometer.setBase(SystemClock.elapsedRealtime());
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            Toast.makeText(OngoingTripActivity.this, "Please enable location permission", Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(getApplicationContext(), "Location updates enabled", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocation();
                    }
                });
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        Toast.makeText(getApplicationContext(), "Location updates disabled", Toast.LENGTH_SHORT).show();
    }

    public void resumeLocationUpdates() {
        startLocationUpdates();
    }

    // Triggers when the recharge button is clicked
    public void rechargeOnClick(View view) {

        stopLocationUpdates();

        // Send data
        String dataPost = (Arrays.toString(pointArray).replace("[", "{")).replace("]", "}");
        Log.d("Recharge send data: ", dataPost);

        // Send ArrayPoint data to the database
        sendArrayPointPost.sendArrayPointPostRequest(trip_ID, sendPointArrayURL, dataPost, new VolleyCallBackInterface() {
            @Override
            public void onSuccess(String result) {

                // Do length transformations
                updateLength(result);

                // Set the distance TextView to the current distance and reset the array
                Arrays.fill(pointArray, 0.0);
                global_index = 0;

                // Logcat testing
                Log.d("Final Distance:", result);

                checkFirstInput();
                sendTemperature();

            }

            @Override
            public void onError(String error) {
                Toast.makeText(OngoingTripActivity.this, "Something went wrong. Could not send data", Toast.LENGTH_LONG).show();
                global_index=0;
            }
        });

        // Pause the chronometer
        lastPause = SystemClock.elapsedRealtime();
        simpleChronometer.stop();
        clicked = true;

        // Show a popup to update energy levels
        Intent intent = new Intent(this, PopUpActivity.class);
        startActivity(intent);
    }

    public void pauseTripOnClick(View view) {

        if(!clicked) {
            stopLocationUpdates();
            lastPause = SystemClock.elapsedRealtime();
            simpleChronometer.stop();
            tripStatusTextView.setText("Trip is paused");
            btnPauseTrip.setText("Resume the trip");
            clicked = true;

            String dataPost = (Arrays.toString(pointArray).replace("[", "{")).replace("]", "}");
            Log.d("Pause trip send data: ", dataPost);

            // Send ArrayPoint data to the database
            sendArrayPointPost.sendArrayPointPostRequest(trip_ID, sendPointArrayURL, dataPost, new VolleyCallBackInterface() {
                @Override
                public void onSuccess(String result) {
                    // Do length transformations
                    updateLength(result);

                    // Set the distance TextView to the current distance and reset the array
                    Arrays.fill(pointArray, 0.0);
                    global_index = 0;

                    // Logcat testing
                    Log.d("Final Distance:", result);

                    checkFirstInput();
                    sendTemperature();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(OngoingTripActivity.this, "Something went wrong. Could not send data", Toast.LENGTH_LONG).show();
                    global_index=0;
                }
            });


        } else {
            resumeLocationUpdates();
            simpleChronometer.setBase(simpleChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
            simpleChronometer.start();
            tripStatusTextView.setText("Trip is in progress");
            btnPauseTrip.setText("Pause the trip");
            clicked = false;
        }
    }

    public void endTripOnClick(View view) {
        // Stop updating location
        stopLocationUpdates();

        // Store remaining unsent data and send it one last time
        String dataPost = (Arrays.toString(pointArray).replace("[", "{")).replace("]", "}");

        sendArrayPointPost.sendArrayPointPostRequest(trip_ID, sendPointArrayURL, dataPost, new VolleyCallBackInterface() {
            @Override
            public void onSuccess(String result) {

                // Do length transformations
                updateLength(result);

                // Set the distance TextView to the current distance and reset the array
                Arrays.fill(pointArray, 0.0);
                global_index = 0;

                // Logcat testing
                Log.d("Final Distance:", result);

                checkFirstInput();
                sendTemperature();

                // send inaccurate distance to the meta table for reference
                metaDataPostRequest.sendMetaData(trip_ID, "distance_from_app", (finalSend.multiply(BigDecimal.valueOf(1000))).intValue(), new VolleyCallBackInterface() {

                    @Override
                    public void onSuccess(String result) {
                        Log.d("finalSend", finalSend.multiply(BigDecimal.valueOf(1000)).toString()); // Trip length in meters

                        // Call TripEndActivity
                        Intent intent = new Intent(OngoingTripActivity.this, TripEndActivity.class);
                        intent.putExtra("trip_ID", trip_ID);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(OngoingTripActivity.this, "Error: Could not send distance information", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR", error);

                        // Call TripEndActivity
                        Intent intent = new Intent(OngoingTripActivity.this, TripEndActivity.class);
                        intent.putExtra("trip_ID", trip_ID);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(OngoingTripActivity.this, "Error: Could not end the trip", Toast.LENGTH_LONG).show();
                global_index=0;
            }
        });
    }

    public void seekBarInit() {
        energyLevelTextView.setText("Energy levels: "+seekBar.getProgress() + "%");

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
                energyLevelTextView.setText("Energy levels: "+progressValue + "%");
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

            sendArrayPointPost.sendArrayPointPostRequest(trip_ID, sendPointArrayURL, dataPost, new VolleyCallBackInterface() {
                @Override
                public void onSuccess(String result) {

                    // Do length transformations
                    updateLength(result);

                    // Set the distance TextView to the current distance and reset the array
                    Arrays.fill(pointArray, 0.0);
                    global_index = 0;

                    // Logcat testing
                    Log.d("Final Distance:", result);

                    checkFirstInput();
                    sendTemperature();

                    seekBarValue = seekBar.getProgress();

                    metaDataPostRequest.sendMetaData(trip_ID, "user_update", seekBarValue, new VolleyCallBackInterface() {
                        @Override
                        public void onSuccess(String result) {

                            // Resume chronometer
                            if(clicked) {
                                resumeLocationUpdates();
                                tripStatusTextView.setText("Trip is in progress!");
                                simpleChronometer.setBase(simpleChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
                                simpleChronometer.start();
                                clicked = false;
                            } else resumeLocationUpdates();

                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(OngoingTripActivity.this, "Error: Could not manually update energy levels", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onError(String error) {
                    Toast.makeText(OngoingTripActivity.this, "Error: Could not manually update energy levels", Toast.LENGTH_SHORT).show();
                    global_index=0;
                }
            });
        }
        else {
            Toast.makeText(OngoingTripActivity.this, "Error! Check if you have correctly inputted your energy level", Toast.LENGTH_LONG).show();
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

    public void checkFirstInput(){
        if (!firstMetaSent) {
            metaDataPostRequest.sendMetaData(trip_ID, "first_input", seekBarValue, new VolleyCallBackInterface() {
                @Override
                public void onSuccess(String result) {
                    firstMetaSent = true;
                    Log.d("first_point", "SENT");
                    // TODO implement methods
                }

                @Override
                public void onError(String error) {
                    // TODO implement methods
                    Log.d("first_point", "NOT SENT");
                }
            });
        }
    }

    // Create a Weather class object to obtain weather information

    public void sendTemperature() {
        if (!temperatureSent) {
            weatherRequest.getWeatherData(new VolleyCallBackInterface() {
                @Override
                public void onSuccess(String result) {
                    temperature = result;
                    Log.d("Temp", temperature);

                    metaDataPostRequest.sendMetaData(trip_ID, "temperature_input", Integer.parseInt(temperature), new VolleyCallBackInterface() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d("Temp", "Success");
                            temperatureSent = true;
                            System.out.println(Integer.parseInt(temperature));
                        }

                        @Override
                        public void onError(String error) {
                            Log.d("Temp", "Failure");
                            System.out.println(Integer.parseInt(temperature));

                        }
                    });

                }

                @Override
                public void onError(String error) {
                    Log.d("Temp", "Mega failure");
                }
            });
        }
    }
}