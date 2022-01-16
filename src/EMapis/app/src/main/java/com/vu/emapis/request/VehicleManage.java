package com.vu.emapis.request;

import static com.vu.emapis.Constants.EMAPIS_DATABASE_TOKEN;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.vu.emapis.R;
import com.vu.emapis.VolleyCallBackInterface;
import com.vu.emapis.objects.userVehicle;
import com.vu.emapis.objects.vehicle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                headers.put("Authorization", EMAPIS_DATABASE_TOKEN);
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
                headers.put("Authorization", EMAPIS_DATABASE_TOKEN);
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }

    public void sendRemoveRequest(int UserVehicleID, VolleyCallBackInterface callback) {

        String removeUrl = context.getString(R.string.removeUserVehiclesURL);

        RequestQueue queue = Volley.newRequestQueue(context); // New requestQueue using Volley's default queue.

        JSONObject postData = new JSONObject(); // Creating JSON object with data that will be sent via POST request.

        try {

            postData.put("uvid", UserVehicleID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, removeUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onSuccess(response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onSuccess(""); // Volley returns error due to blank space conversion to JSON. we ignore this
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

        queue.add(jsonObjectRequest);
    }

}

