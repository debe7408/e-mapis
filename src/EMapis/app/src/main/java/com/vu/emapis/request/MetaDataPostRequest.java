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
import com.vu.emapis.R;
import com.vu.emapis.VolleyCallBackInterface;

import java.util.HashMap;
import java.util.Map;

public class MetaDataPostRequest {

    private Context context;

    public MetaDataPostRequest(Context context) {
        this.context = context;
    }

    /**
     * Sends a new POST request to META table in the E-MAPIS database.
     *  @param tripID Trip ID that will be related to the information
     * @param inputKey InputKey defines the information's key to access it
     * @param inputValue InputValue defines the information's value
     * @param callback Callback interface
     */

    public void sendMetaData(String tripID, String inputKey, int inputValue, VolleyCallBackInterface callback) {

        String url = context.getString(R.string.sendDataToMetaURL);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess("Success: MetaDataPostRequest");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onError("Failed: MetaDataPostRequest");
                error.printStackTrace();

            }
        }){
            protected Map<String, String> getParams() {

                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("trip_id", tripID);
                MyData.put("input_key", inputKey);
                MyData.put("input_value", String.valueOf(inputValue));

                return MyData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", EMAPIS_DATABASE_TOKEN);
                return headers;
            }
        };

        requestQueue.add(stringRequest);

    }

}
