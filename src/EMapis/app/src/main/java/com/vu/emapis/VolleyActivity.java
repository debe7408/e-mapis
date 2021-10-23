package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class VolleyActivity extends AppCompatActivity {

    private TextView get_response_text, post_response_text;
    private String url ="http://193.219.91.103:3906/todos";
    private String url2 = "http://193.219.91.103:3906/rpc/points_insert";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);

        get_response_text = (TextView) findViewById(R.id.get_response_text);
        post_response_text = (TextView) findViewById(R.id.post_response_text);

        Button get_response_button = findViewById(R.id.get_data);
        Button post_response_data = findViewById(R.id.post_data);

        get_response_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendGetRequest();
            }
        });

        post_response_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPostRequest();
            }
        });
    }

    private void sendPostRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject postData = new JSONObject();
        try {
            postData.put("id", ThreadLocalRandom.current().nextInt(0, 100));
            postData.put("done", true);
            postData.put("task", "Send POST REQUEST from android");
            postData.put("due", "2021-10-14T00:00:00+03:00");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoidG9kb191c2VyIn0.qusYUABWSUHBopNCItHAvrW6SHJe1BAoKqXqo-E26Zs");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    private void sendGetRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        get_response_text.setText("Response:\n"+ response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                get_response_text.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}