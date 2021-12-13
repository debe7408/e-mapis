package com.vu.emapis;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class VolleyCallBackTest extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley_call_back_test);



        Button button = findViewById(R.id.volleyGet);
        TextView textView = findViewById(R.id.textView2);
        progressBar = findViewById(R.id.progressBar2);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                //String url = "https://simple-books-api.glitch.me/books";
                String url = "http://193.219.91.103:4558/vehicles?select=*";

                sendGetRequest(url, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("Code 1",result);

                        textView.setText(result);

                    }

                    @Override
                    public void onError(String error) {
                        Log.d("Code Error", error);

                        textView.setText("Something went wrong :(");
                    }
                });

            }
        });

        textView.setText("Before button was clicked");
    }

    public interface VolleyCallback{
        void onSuccess(String result);

        void onError(String result);
    }

    private void sendGetRequest(String url, final VolleyCallback callback) {


        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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



        request.setRetryPolicy(new DefaultRetryPolicy(
                5000, // Timeout after
                5, // Number of retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // A multiplier which is used to determine exponential time set to socket for every retry attempt.

        ));

        queue.add(request);
        
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}