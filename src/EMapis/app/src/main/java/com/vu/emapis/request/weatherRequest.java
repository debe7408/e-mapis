package com.vu.emapis.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vu.emapis.VolleyCallBackInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class weatherRequest {

    private String city;
    private final String appID = "984a2f5b726e95045b31716e5539ff10"; //TODO change this bearer to safe location
    private String units;
    private Context context;

    public weatherRequest(Context context, String city, String units) {

        this.city = city;
        this.units = units;
        this.context = context;

    }
    /**
     * Returns temperature in celsius
     * @param context The context that the method is being executed in
     * @param callback Interface for onSuccess and onError handling
     */
    public void getWeatherData(VolleyCallBackInterface callback) {

        // Request URL with changeable city and units in the initialization
        String url = "https://api.openweathermap.org/data/2.5/weather?" + "q=" + this.city + "&appid=" + this.appID + "&units=" + this.units;

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // We get response as a JSON Object, but the information about weather state is a JSON Array in the said JSON Object.
                int finalValue = 0;

                // We also want to obtain temperature, which is in a JSON Object called main.
                String weatherTemp = null;
                try {
                    JSONObject main = response.getJSONObject("main");

                    double value = Double.parseDouble(main.getString("temp"));

                    finalValue = (int) (value+0.5);

                    weatherTemp = String.valueOf(finalValue);

                    Log.d("Main", main.getString("temp"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String result = weatherTemp;

                callback.onSuccess(result);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(jsonObjectRequest);
    }
}
