package com.vu.emapis;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vu.emapis.request.weatherRequest;

public class openWeatherAPITest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_weather_apitest);

        TextView weatherResults = findViewById(R.id.weatherResults);
        Button getWeatherButton = findViewById(R.id.getWeatherButton);

        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                weatherRequest weatherData = new weatherRequest("Vilnius", "metric");

                weatherData.getWeatherData(openWeatherAPITest.this, new VolleyCallBackInterface() {
                    @Override
                    public void onSuccess(String result) {
                        weatherResults.setText(result);
                    }

                    @Override
                    public void onError(String error) {
                        Log.d("error", "kazkas ne to");
                    }
                });

            }
        });
    }
}