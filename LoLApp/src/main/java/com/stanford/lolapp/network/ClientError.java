package com.stanford.lolapp.network;

/**
 * Created by Mark Stanford on 4/25/14.
 */

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

public class ClientError extends VolleyError {

    public static final String TAG = "ClientError";

    public ClientError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public ClientError() {
        super();
    }

    public Integer getStatusCode() {
        return networkResponse.statusCode;
    }

    public List<InputError> getInputErrors() {
        List<InputError> errorList = new ArrayList<InputError>();

        try {
            String str = new String(networkResponse.data, "UTF8");

            JSONObject errorJson = new JSONObject(str);
            if (errorJson.has("errors")) {
                JSONArray errors = errorJson.getJSONArray("errors");

                for (Integer i = 0; i < errors.length(); i++) {
                    JSONObject errorData = errors.getJSONObject(i);
                    if (errorData.has("id") && errorData.has("message")) {
                        String id = errorData.getString("id");
                        String message = errorData.getString("message");

                        Log.d(TAG, String.format("id: %s message: %s", id, message));
                        errorList.add(new InputError(id, message));
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error: response cannot be decoded");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG, "Error: response contains invalid json");
            e.printStackTrace();
        }

        return errorList;
    }
}

