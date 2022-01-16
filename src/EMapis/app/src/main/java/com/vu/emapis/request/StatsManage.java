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
import com.vu.emapis.ByUserVehicleActivity;
import com.vu.emapis.VolleyCallBackInterface;
import com.vu.emapis.VolleyCallBackInterfaceJSON;
import com.vu.emapis.objects.byUserVehicleObject;
import com.vu.emapis.objects.generalStatsObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class StatsManage {

    private final Context context;
    public generalStatsObject[] generalStats;
    public byUserVehicleObject[] byUserVehicleStats;



    public StatsManage(Context context) {
        this.context = context;
    }

    public void getVehicleStats(int vehicleID, VolleyCallBackInterface callback) {

        String url = "http://193.219.91.103:4558/_emapis_get_vehicle_info?vehicle_id=eq." + vehicleID;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                generalStats = gson.fromJson(String.valueOf(response), generalStatsObject[].class);

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
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }

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
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }
}
