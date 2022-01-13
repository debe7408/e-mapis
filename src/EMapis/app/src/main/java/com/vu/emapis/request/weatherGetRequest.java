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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class weatherGetRequest {

    public void getWeatherRequest(Context context, VolleyCallBackInterface callback) {

        String q = "Vilnius"; // City
        String appid = "984a2f5b726e95045b31716e5539ff10"; // API key
        String units = "metric"; // Unit system

        String url = "https://api.openweathermap.org/data/2.5/weather?" + "q=" + q + "&appid=" + appid + "&units=" + units; // Request URL
        Log.d("URL", url);

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // We get response as a JSON Object, but the information about weather state is a JSON Array in the said JSON Object.
                String weatherState = null;
                int finalValue = 0;


                try {
                    // Here we try to extract the weather JSON Array into our variable so we can further work with it.
                    JSONArray array = response.getJSONArray("weather");

                    // here we loop through the array and input all the data into JSON Object so we can extract only the information we need.
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject weather = array.getJSONObject(i);


                        weatherState = ("Weather: " + weather.getString("main"));

                        Log.d("Weather", weather.getString("main"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // We also want to obtain temperature, which is in a JSON Object called main.
                String weatherTemp = null;
                try {
                    JSONObject main = response.getJSONObject("main");

                    double value = Double.parseDouble(main.getString("temp"));

                    if(value >= 1.5 ) {
                        finalValue = (int) Math.ceil(value);
                    } else finalValue = (int) Math.floor(value);

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
