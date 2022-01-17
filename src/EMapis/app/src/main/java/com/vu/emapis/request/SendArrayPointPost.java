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
import com.vu.emapis.VolleyCallBackInterface;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SendArrayPointPost {

    private final Context context;
    private BigDecimal finalSend;


    public SendArrayPointPost(Context context) {
        this.context = context;
    }


    /**
     *
     * @param tripID
     * @param url
     * @param dataPost
     * @param callback
     */

    public void sendArrayPointPostRequest(String tripID, String url, String dataPost, VolleyCallBackInterface callback) {

        RequestQueue queue = Volley.newRequestQueue(this.context); // New requestQueue using Volley's default queue.
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

                MyData.put("trip_id", tripID);
                MyData.put("points", dataPost);

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
