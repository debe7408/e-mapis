package com.vu.emapis.request;
import com.vu.emapis.VolleyCallBackInterface;
import com.vu.emapis.objects.userVehicle;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.vu.emapis.LoginActivity;
import com.vu.emapis.TripSettingsActivity;
import com.vu.emapis.objects.userVehicle;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class getUserVehiclesRequest {

    public userVehicle[] userVehicleList;

    public void getUserVehicles(Context context, VolleyCallBackInterface callback) {

        String url = "http://193.219.91.103:4558/user_vehicles?user_id=eq." + LoginActivity.userId;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Gson gson = new Gson();
                userVehicleList = gson.fromJson(String.valueOf(response), userVehicle[].class);
                Log.d("list-trip-settings", String.valueOf(response));

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
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {

            }
        });
    }

}
