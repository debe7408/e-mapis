package com.vu.emapis;

import org.json.JSONArray;

public interface VolleyCallBackInterfaceJSON {
    void onSuccess(JSONArray response);
    void onError(String error);
}
