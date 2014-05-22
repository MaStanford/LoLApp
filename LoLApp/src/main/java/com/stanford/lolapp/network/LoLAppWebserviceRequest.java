package com.stanford.lolapp.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Stanford on 5/21/14.
 */
public class LoLAppWebserviceRequest extends WebserviceRequest {

    public LoLAppWebserviceRequest(int serviceName, String serviceUri) {
        super(serviceName, serviceUri);
        mRequestMethod = Request.Method.GET;
        mHostName = WebService.PRODUCTION_RIOT_HOSTNAME;
    }

    public List<String> createDefaultOptionalParams() {
        List<String> optionalParams = new ArrayList<String>();
        mRequiredHeaders.add(WebService.HEADER_REQUIRED_FORMAT);
        optionalParams.add(WebService.PARAM_OPTIONAL_FORMAT);
        optionalParams.add(WebService.PARAM_OPTIONAL_CALLBACK);
        optionalParams.add(WebService.PARAM_OPTIONAL_RETURNHTML);

        return optionalParams;
    }

    public ClientError getClientErrorObject(NetworkResponse networkResponse) {
        return new ClientError(networkResponse);
    }
}

