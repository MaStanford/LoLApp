package com.stanford.lolapp.network;

/**
 * Created by Mark Stanford on 4/25/14.
 */
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public abstract class GsonRequestObject<R, T> extends Request<T> {

    protected Gson gson;
    private Listener<T> listener;
    private Class<R> inputType;
    private Class<T> outputType;
    private R object;

    private static final String PROTOCOL_CHARSET = "utf-8";

    public GsonRequestObject(int method, String url, R postObject, Class<R> inputType, Class<T> outputType, Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.inputType = inputType;
        this.outputType = outputType;
        this.object = postObject;
        gson = new Gson();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = null;
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public byte[] getBody() {
        String jsonString = gson.toJson(object, inputType);
        try {
            return jsonString.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException e) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonString, PROTOCOL_CHARSET);
            return null;
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(gson.fromJson(json, outputType), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));

        }
    }
}
