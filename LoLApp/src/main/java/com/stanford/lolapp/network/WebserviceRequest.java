package com.stanford.lolapp.network;

import android.os.Bundle;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mark Stanford on 5/21/14.
 */
public class WebserviceRequest {
    protected boolean mRequestTypeSecure = true;
    protected int mServiceName;
    protected String mServiceUri;
    protected int mRequestMethod;
    protected String mHostName;

    protected List<String> mRequiredParams = new ArrayList<String>();
    protected List<String> mOptionalParams = new ArrayList<String>();
    protected List<String> mRequiredHeaders = new ArrayList<String>();
    protected List<String> mRequiredBody = new ArrayList<String>();
    protected List<String> mOptionalBody = new ArrayList<String>();

    public WebserviceRequest(int serviceName, String serviceUri) {
        mServiceName = serviceName;
        mServiceUri = serviceUri;
    }

    public WebserviceRequest setRequestMethod(int requestMethod){
        this.mRequestMethod = requestMethod;
        return this;
    }

    public int getRequestMethod() {
        return mRequestMethod;
    }

    public int getServiceName() {
        return mServiceName;
    }

    public String getServiceUri() {
        return mServiceUri;
    }

    public String getHostName() {
        return mHostName;
    }

    public boolean getRequestTypeSecure() {
        return mRequestTypeSecure;
    }

    public void setRequestTypeSecure(boolean requestTypeSecure) {
        mRequestTypeSecure = requestTypeSecure;
    }

    public List<String> getRequiredParams() {
        return mRequiredParams;
    }

    public List<String> getOptionalParams() {
        return mOptionalParams;
    }

    public List<String> getRequiredHeaders() {
        return mRequiredHeaders;
    }

    public Boolean isAuthTokenRequired() {
        return mRequiredParams.contains(WebService.PARAM_REQUIRED_AUTH_TOKEN);
    }

    public Boolean isAuthHeaderRequired() {
        return mRequiredHeaders.contains(WebService.HEADER_REQUIRED_API_KEY)
                || mRequiredHeaders.contains(WebService.HEADER_REQUIRED_APP_KEY);
    }

    /**
     * Checks to see if all params are included in the bundle
     * @param params
     * @return
     */
    public Boolean areParamsValid(Bundle params) {
        // make certain all required params are present
        for (String param : mRequiredParams) {
            if (!params.containsKey(param)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks to see if all headers are included in the bundle
     * @param headers
     * @return
     */
    public Boolean areHeadersValid(Map<String,String> headers) {
        // make certain all required params are present
        for (String param : mRequiredHeaders) {
            if (!headers.containsKey(param)) {
                return false;
            }
        }
        return true;
    }

    public boolean isBodyValid(JSONBody body){
        for (String param : mRequiredBody) {
            if (!body.getMap().containsKey(param)) {
                return false;
            }
        }
        return true;
    }

    public VolleyError getClientErrorObject(NetworkResponse networkResponse) {
        return new VolleyError(networkResponse);
    }
}

