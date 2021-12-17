package com.vu.emapis;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class openWeatherAPITest extends AppCompatActivity {


    public interface VolleyCallback {
        void onSuccess(String result);
        void onError();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_weather_apitest);

        TextView weatherResults = findViewById(R.id.weatherResults);
        Button getWeatherButton = findViewById(R.id.getWeatherButton);


        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getWeatherRequest(new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        weatherResults.setText(result);

                    }

                    @Override
                    public void onError() {

                    }
                });

            }
        });
    }

    public void getWeatherRequest(VolleyCallback callback) {

        String q = "Kaunas"; // City
        String appid = "984a2f5b726e95045b31716e5539ff10"; // API key
        String units = "metric"; // Unit system

        String url = "https://api.openweathermap.org/data/2.5/weather?" + "q=" + q + "&appid=" + appid + "&units=" + units; // Request URL
        Log.d("URL", url);

        RequestQueue queue = Volley.newRequestQueue(openWeatherAPITest.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // We get response as a JSON Object, but the information about weather state is a JSON Array in the said JSON Object.
                String weatherState = null;
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

                    weatherTemp = (". Temperature: " + main.getString("temp") + " C");


                    Log.d("Main", main.getString("temp"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String result = weatherState + weatherTemp;


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