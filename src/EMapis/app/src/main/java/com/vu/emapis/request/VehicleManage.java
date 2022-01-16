package com.vu.emapis.request;

import android.content.Context;

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
import com.vu.emapis.objects.userVehicle;
import com.vu.emapis.objects.vehicle;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class VehicleManage {

    // Vars
    public Context context;
    public vehicle[] vehiclesList;
    public userVehicle[] userVehicleList;

    // Constructor
    public VehicleManage(Context context) {
        this.context = context;
    }


    /**
     * Returns a list of all vehicles in the E-MAPIS database
     * @param callback Interface callback to handle return status
     */
    public void getAllVehicles(VolleyCallBackInterface callback) {

        String url = context.getString(R.string.getAllVehiclesURL);

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                vehiclesList = gson.fromJson(String.valueOf(response), vehicle[].class);

                callback.onSuccess(response.toString());

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
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }

    /**
     * Returns vehicle list owned by a specific user
     * @param UserID Specifies the user's ID whose vehicles we want to return
     * @param callback Interface to handle return status
     */
    public void getUserVehicles(int UserID, VolleyCallBackInterface callback) {

        String url = context.getString(R.string.getUserVehicesURL) + UserID;

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                userVehicleList = gson.fromJson(String.valueOf(response), userVehicle[].class);

                callback.onSuccess("");

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callback.onError(error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }
}

