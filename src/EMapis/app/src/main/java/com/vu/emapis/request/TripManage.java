package com.vu.emapis.request;

import static com.vu.emapis.Constants.EMAPIS_DATABASE_TOKEN;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vu.emapis.R;
import com.vu.emapis.VolleyCallBackInterface;

import java.util.HashMap;
import java.util.Map;

public class TripManage {

    // Vars
    private final Context context;

    // Constructor
    public TripManage(Context context) {
        this.context = context;
    }

    /**
     * Starts a new trip and returns a unique TripID
     * @param UserID Specifies which user starts the trip
     * @param VehicleID Specifies with what vehicle the user starts the trip
     * @param callback Interface for onSuccess and onError
     */
    public void startNewTrip(int UserID, int VehicleID, VolleyCallBackInterface callback) {

        String url = context.getString(R.string.startNewTripURL);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            // Returns tripID
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {

                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("user_id", String.valueOf(UserID));
                MyData.put("user_vehicle_id", String.valueOf(VehicleID));
                return MyData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", EMAPIS_DATABASE_TOKEN);
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    /**
     * Returns a built route
     * @param trip_ID Trip ID to specify which trip to build
     * @param callback Interface to handle error/success
     */
    public void buildRoute(String trip_ID, VolleyCallBackInterface callback) {

        String url = context.getString(R.string.buildRouteURL);

        RequestQueue queue = Volley.newRequestQueue(context); // New requestQueue using Volley's default queue.

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callback.onError(error.toString());
            }
        }) {
            protected Map<String, String> getParams() {

                Map<String, String> MyData = new HashMap<String, String>();

                MyData.put("trip_id", trip_ID);

                return MyData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", EMAPIS_DATABASE_TOKEN);
                return headers;
            }
        };

        queue.add(stringRequest);

    }

}
