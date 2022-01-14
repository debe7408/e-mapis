package com.vu.emapis.request;

import android.content.Context;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vu.emapis.UserRemoveVehicleActivity;
import com.vu.emapis.VolleyCallBackInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class removeUserVehicleRequest {

    public Context context;

    public removeUserVehicleRequest(Context context) {
        this.context = context;
    }

    public void sendRemoveRequest(int UserVehicleID, VolleyCallBackInterface callback) {

        String removeUrl = "http://193.219.91.103:4558/rpc/remove_user_vehicle";

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
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiZW1hcGlzX2RldmljZSJ9.xDyrK7WodZgZFaa2JjoBVmZG42Wqtx-vGj_ZyYO3vxQ");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
}
