package com.stanford.lolapp.network;

/**
 * Created by Mark Stanford on 4/25/14.
 */

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.NetworkResponse;

@SuppressWarnings("serial")
public class SsoClientError extends com.android.volley.VolleyError {

    public static final String TAG = "SsoClientError";

    public static final String GENERIC_ERROR_MESSAGE = "Sorry. There has been an error.";

    public SsoClientError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public SsoClientError() {
        super();
    }

    public Integer getStatusCode() {
        return networkResponse.statusCode;
    }

    public String getInputError() {
        try {
            String str = new String(networkResponse.data, "UTF8");
            JSONObject errorJson = new JSONObject(str);

            int statusCode = getStatusCode();
            String errorMessage = errorJson.optString("error");

            // Authorization Error, we'll do generic messaging
            if (statusCode == 400 || statusCode == 401) {
                Log.e(TAG, String.format("Status Code %d: %s", statusCode, errorMessage));
                //TODO - get this out of strings resource
                errorMessage = GENERIC_ERROR_MESSAGE;
            }

            return errorMessage;

        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error: response cannot be decoded");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG, "Error: response contains invalid json");
            e.printStackTrace();
        }

        return "";
    }
}