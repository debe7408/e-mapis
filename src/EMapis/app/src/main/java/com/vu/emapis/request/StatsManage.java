package com.vu.emapis.request;

import static com.vu.emapis.Constants.EMAPIS_DATABASE_TOKEN;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.vu.emapis.R;
import com.vu.emapis.VolleyCallBackInterface;
import com.vu.emapis.VolleyCallBackInterfaceJSON;
import com.vu.emapis.objects.byUserVehicleObject;
import com.vu.emapis.objects.generalStatsObject;
import com.vu.emapis.objects.statisticsObject;
import com.vu.emapis.objects.tripStatsObject;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StatsManage {

    private final Context context;
    public generalStatsObject[] generalStats;
    public byUserVehicleObject[] byUserVehicleStats;
    public statisticsObject[] stats;
    public tripStatsObject[] tripStats;


    public StatsManage(Context context) {
        this.context = context;
    }

    /**
     * Returns statistics about the trips done on a specific vehicle globally
     * @param vehicleID Specifies the vehicle used
     * @param callback Interface to handle return status
     */
    public void getVehicleStats(int vehicleID, VolleyCallBackInterface callback) {

        String url = "http://193.219.91.103:4558/_emapis_get_vehicle_info?vehicle_id=eq." + vehicleID;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d("Response", response.toString());

                Gson gson = new Gson();
                generalStats = gson.fromJson(String.valueOf(response), generalStatsObject[].class);

                Log.d("Response2", Arrays.toString(generalStats));

                callback.onSuccess("");


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onError(error.toString());
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", EMAPIS_DATABASE_TOKEN);
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }

    /**
     * Returns statistics about trips done one specific user's vehicle
     * @param userVehicleID Specifies the user's vehicle used
     * @param callback Interface to handle return status
     */
    public void getUserVehicleStats(int userVehicleID, VolleyCallBackInterfaceJSON callback) {

        String url = "http://193.219.91.103:4558/_emapis_get_user_vehicle_info?user_vehicle_id=eq." + userVehicleID;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                byUserVehicleStats = gson.fromJson(String.valueOf(response), byUserVehicleObject[].class);

                callback.onSuccess(response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onError(error.toString());
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", EMAPIS_DATABASE_TOKEN);
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }

    /**
     * Returns a list of users trips
     * @param UserID Specifies a specific user
     * @param callback Interface to handle return status
     */
    public void getIndividualTrips(int UserID, VolleyCallBackInterfaceJSON callback) {

        String url = context.getString(R.string.getIndividualTripsURL)+ UserID;

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        // JSON -> GSON
                        Gson gson = new Gson();
                        stats = gson.fromJson(String.valueOf(response), statisticsObject[].class);

                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onError(error.toString());
                error.printStackTrace();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", EMAPIS_DATABASE_TOKEN);
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }

    /**
     * Returns statistics about a single trip by a user
     * @param trip_ID Specifies the user
     * @param callback Interface to handle errors/success
     */
    public void getIndividualTripStats(String trip_ID, VolleyCallBackInterface callback) {

        String url = "http://193.219.91.103:4558/_emapis_get_data_about_trip?trip_id=eq." + trip_ID;

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        // JSON -> GSON
                        Gson gson = new Gson();
                        tripStats = gson.fromJson(String.valueOf(response), tripStatsObject[].class);

                        callback.onSuccess("");

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onError(error.toString());
                error.printStackTrace();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", EMAPIS_DATABASE_TOKEN);
                return headers;
            }
        };

// Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }
}
