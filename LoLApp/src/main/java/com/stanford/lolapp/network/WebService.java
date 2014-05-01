package com.stanford.lolapp.network;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class WebService /* extends IntentService */ {

    private static final String TAG                             = "WebService";

    public static final String API_KEY                          = "b2bf733d-dbb0-47c1-a0c4-f71791727277";
    private static final String PARAM_API_KEY                   = "api_key";

    // host names
    public static final String PRODUCTION_HOSTNAME              = "prod.api.pvp.net";
    public static final String PRODUCTION_API_PREFIX            = "/api/lol";
    public static final String PRODUCTION_CHAMPION_API_VER      = "/v1.2";
    public static final String PRODUCTION_GAMES_API_VER         = "/v1.3";
    public static final String PRODUCTION_LEAGUE_API_VER        = "/v1.3";
    public static final String PRODUCTION_STATIC_API_VER        = "/v1.2";
    public static final String PRODUCTION_STATS_API_VER         = "/v1.3";
    public static final String PRODUCTION_SUMMONER_API_VER      = "/v1.4";
    public static final String PRODUCTION_TEAM_API_VER          = "/v2.2";
    public static final String PRODUCTION_STATIC                = "/static-data";

    // Image paths
    public static final String PROFILE_ICONS                    = "http://ddragon.leagueoflegends.com/cdn/4.4.3/img/profileicon/";
    public static final String CHAMPIONS_ICONS                  = "http://ddragon.leagueoflegends.com/cdn/4.4.3/img/champion/";
    public static final String PASSIVES_ICONS                   = "http://ddragon.leagueoflegends.com/cdn/4.4.3/img/passive/";
    public static final String CHAMPION_ABILITY_ICONS           = "http://ddragon.leagueoflegends.com/cdn/4.4.3/img/spell/";
    public static final String SUMMONER_SPELL_ICONS             = "http://ddragon.leagueoflegends.com/cdn/4.4.3/img/spell/";
    public static final String ITEM_ICONS                       = "http://ddragon.leagueoflegends.com/cdn/4.4.3/img/item/";
    public static final String MASTERY_ICONS                    = "http://ddragon.leagueoflegends.com/cdn/4.4.3/img/mastery/";
    public static final String RUNE_ICONS                       = "http://ddragon.leagueoflegends.com/cdn/4.4.3/img/rune/";
    public static final String SPRITE_ICONS                     = "http://ddragon.leagueoflegends.com/cdn/4.4.3/img/sprite/";

    //Service names
    public static final int SERVICE_GET_CHAMPION_ID             = 0;
    public static final int SERVICE_GET_ALL_CHAMPION_IDS        = 1;
    public static final int SERVICE_GET_CHAMPION_DATA           = 2;
    public static final int SERVICE_GET_ALL_CHAMPION_DATA       = 3;
    public static final int SERVICE_LOGIN                       = 4;
    public static final int SERVICE_SIGN_UP                     = 5;
    public static final int SERVICE_LOG_OUT                     = 6;

    // Required parameter that we set on almost every service call
    public static final String PARAM_REQUIRED_AUTH_TOKEN        = "authToken";
    public static final String PARAM_REQUIRED_LOCATION          = "location";
    public static final String PARAM_REQUIRED_LOCALE            = "locale";

    // Optional parameters available for pretty much every service call
    private static final String PARAM_OPTIONAL_FORMAT           = "format";
    private static final String FORMAT_JSON                     = "json";
    private static final String PARAM_OPTIONAL_CALLBACK         = "callback";
    private static final String PARAM_OPTIONAL_RETURNHTML       = "returnHTML";

    private static final Integer SOCKET_TIMEOUT_MS              = 15000;

    //Enum for location
    public enum location {
        br("/br"),
        eune("/eune"),
        euw("/euw"),
        kr("/kr"),
        lan("/lan"),
        las("/las"),
        na("/na"),
        oce("/oce"),
        ru("/ru"),
        tr("/tr");
        private String mLocation = "";

        location(String location) {
            this.mLocation = location;
        }

        public String getLocation() {
            return this.mLocation;
        }

    }

    //Enum for locale
    public enum locale {
        en_US("en_US"),
        es_ES("es_ES");
        private String mLocale = "";

        locale(String locale) {
            this.mLocale = locale;
        }

        public String getLocale() {
            return this.mLocale;
        }
    }

    //Enum for ChampData parameter
    public enum ChampData {
        all("all"),
        allytips("allytips"),
        altimages("altimages"),
        blurb("blurb"),
        enemytips("enemytips"),
        image("image"),
        info("info"),
        lore("lore"),
        partype("partype"),
        passive("passive"),
        recommended("recommended"),
        skins("skins"),
        spells("spells"),
        stats("stats"),
        tags("tags");
        private String mData = "";

        ChampData(String data) {
            this.mData = data;
        }

        public String getData() {
            return this.mData;
        }
    }


    /**
     * Root class for any webservice requests
     */
    public static class WebserviceRequest {
        protected boolean mRequestTypeSecure = true;
        protected int mServiceName;
        protected String mServiceUri;
        protected int mRequestMethod;
        protected String mHostName;

        protected List<String> mRequiredParams = new ArrayList<String>();
        protected List<String> mOptionalParams = createDefaultOptionalParams();

        public WebserviceRequest(int serviceName, String serviceUri) {
            mServiceName = serviceName;
            mServiceUri = serviceUri;
        }

        public static List<String> createDefaultOptionalParams() {
            List<String> optionalParams = new ArrayList<String>();

            return optionalParams;
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

        public Boolean isAuthTokenRequired() {
            return mRequiredParams.contains(PARAM_REQUIRED_AUTH_TOKEN);
        }

        public Boolean areParamsValid(Bundle params) {
            // make certain all required params are present
            for (String param : mRequiredParams) {
                if (!params.containsKey(param)) {
                    return false;
                }
            }

            // TODO confirm that all params are in required or optional

            return true;
        }

        public VolleyError getClientErrorObject(NetworkResponse networkResponse) {
            return new VolleyError(networkResponse);
        }
    }

    public static class LoLAppWebserviceRequest extends WebserviceRequest {

        public LoLAppWebserviceRequest(int serviceName, String serviceUri) {
            super(serviceName, serviceUri);
            mRequestMethod = Request.Method.GET;
            mHostName = PRODUCTION_HOSTNAME;
        }

        public static List<String> createDefaultOptionalParams() {
            List<String> optionalParams = new ArrayList<String>();
            optionalParams.add(PARAM_OPTIONAL_FORMAT);
            optionalParams.add(PARAM_OPTIONAL_CALLBACK);
            optionalParams.add(PARAM_OPTIONAL_RETURNHTML);

            return optionalParams;
        }

        public ClientError getClientErrorObject(NetworkResponse networkResponse) {
            return new ClientError(networkResponse);
        }
    }

    /**
     * If I ever decide to have users and need to log in
     */
    public static class AuthLogin extends LoLAppWebserviceRequest {
        public static final String PARAM_REQUIRED_APP_KEY = "appKey";
        public static final String PARAM_REQUIRED_USERNAME = "username";
        public static final String PARAM_REQUIRED_PASSWORD = "password";
        public static final String PARAM_REQUIRED_TIMESTAMP = "timestamp";
        public static final String PARAM_REQUIRED_SIGNATURE = "signature";

        public static final String APP_KEY = "internalemailuse";
        public static final String APP_SECRET = "internalemailuse";

        public AuthLogin() {
            super(SERVICE_LOGIN, "/auth/login");

            mRequiredParams.add(PARAM_REQUIRED_APP_KEY);
            mRequiredParams.add(PARAM_REQUIRED_USERNAME);
            mRequiredParams.add(PARAM_REQUIRED_PASSWORD);
            mRequiredParams.add(PARAM_REQUIRED_TIMESTAMP);
            mRequiredParams.add(PARAM_REQUIRED_SIGNATURE);
        }
    }

    /**
     * If I ever decide to make users
     */
    public static class CreateUser extends LoLAppWebserviceRequest {
        public static final String PARAM_REQUIRED_APP_KEY = "appKey";
        public static final String PARAM_REQUIRED_USERNAME = "username";
        public static final String PARAM_REQUIRED_PASSWORD = "password";
        public static final String PARAM_REQUIRED_TIMESTAMP = "timestamp";
        public static final String PARAM_REQUIRED_SIGNATURE = "signature";
        public static final String PARAM_OPTIONAL_TIMEZONE = "timezone";

        public CreateUser() {
            super(SERVICE_SIGN_UP, "/auth/createuser");

            mRequiredParams.add(PARAM_REQUIRED_APP_KEY);
            mRequiredParams.add(PARAM_REQUIRED_USERNAME);
            mRequiredParams.add(PARAM_REQUIRED_PASSWORD);
            mRequiredParams.add(PARAM_REQUIRED_TIMESTAMP);
            mRequiredParams.add(PARAM_REQUIRED_SIGNATURE);
        }
    }

    /**
     * Get a list of Champion IDs
     */
    public static class GetAllChampionIds extends LoLAppWebserviceRequest {

        public GetAllChampionIds() {
            super(SERVICE_GET_ALL_CHAMPION_IDS, "/champion");
        }
    }

    /**
     * Get a single champion ID
     */
    public static class GetChampionId extends LoLAppWebserviceRequest {


        public GetChampionId(String championID) {
            super(SERVICE_GET_CHAMPION_ID, "/champion/" + championID);

        }
    }

    /**
     * Get a champion
     */
    public static class GetStaticChampion extends LoLAppWebserviceRequest {

        public static final String PARAM_DATA = "champData";


        public GetStaticChampion(int championID) {
            super(SERVICE_GET_CHAMPION_DATA, "/champion/" + championID);
            mRequiredParams.add(PARAM_REQUIRED_LOCATION);
            mRequiredParams.add(PARAM_REQUIRED_LOCALE);
            mOptionalParams.add(PARAM_DATA);
        }
    }

    /**
     * Get a list of all the champions
     */
    public static class GetStaticChampions extends LoLAppWebserviceRequest {

        public static final String PARAM_DATA = "champData";


        public GetStaticChampions() {
            super(SERVICE_GET_ALL_CHAMPION_DATA, "/champion");
            mRequiredParams.add(PARAM_REQUIRED_LOCATION);
            mRequiredParams.add(PARAM_REQUIRED_LOCALE);
            mOptionalParams.add(PARAM_DATA);
        }
    }

    public static void makeStaticRequest(
            final Context context,
            final RequestQueue queue,
            final WebserviceRequest serviceRequest,
            final Bundle requestParams,
            final Response.Listener<JSONObject> successListener,
            final Response.ErrorListener errorListener) {

        //Check the location in the bundle.  If no location, then NA is default.
        String mLocation = "/na";
        if (requestParams != null && requestParams.containsKey(PARAM_REQUIRED_LOCATION))
            mLocation = requestParams.getString(PARAM_REQUIRED_LOCATION);

        //Params, api key needs to be first I think
        Bundle params = new Bundle();
        params.putString(PARAM_API_KEY, API_KEY);
        if (requestParams != null) {
            params.putAll(requestParams);
        }

        // validate params
        if (!serviceRequest.areParamsValid(params)) {
            Log.e(TAG, "makeRequest missing required param");
            errorListener.onErrorResponse(new VolleyError("Unable to send request.  Missing required parameter."));
            return;
        }

        String hostName = serviceRequest.getHostName();
        String uriString = serviceRequest.getServiceUri();

        //Build the URI from all the parts.
        Uri uri = Uri.parse(String.format("%s://%s%s%s%s%s%s",
                        serviceRequest.getRequestTypeSecure() ? "https" : "http",
                        hostName,
                        PRODUCTION_API_PREFIX,
                        PRODUCTION_STATIC,
                        mLocation,
                        PRODUCTION_STATIC_API_VER,
                        uriString)
        );

        //Append the URI params such as IDs, summoners etc
        uri = appendQueryBundle(uri, params);

        Log.d(TAG, String.format("Making request: %s", uri.toString()));
        Log.d(TAG, "Params: " + params.toString());

        JsonObjectRequest request = new JsonObjectRequest(
                serviceRequest.getRequestMethod(),
                uri.toString(),
                null, // JSONObject (post values). null for now
                successListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // If we don't have a network response just pass the error along
                if (error.networkResponse == null) {
                    errorListener.onErrorResponse(error);
                    return;
                }

                // create xxxClientError for status code 4xx
                Integer statusCode = error.networkResponse.statusCode;
                if (statusCode >= 400 && statusCode < 500) {
                    errorListener.onErrorResponse(serviceRequest.getClientErrorObject(error.networkResponse));
                } else {
                    // we do not have special types for any other status codes yet
                    errorListener.onErrorResponse(error);
                }
            }
        }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

        queue.start();
    }

    public static void makeRequest(
            final Context context,
            final RequestQueue queue,
            final WebserviceRequest serviceRequest,
            final Bundle requestParams,
            final Response.Listener<JSONObject> successListener,
            final Response.ErrorListener errorListener) {

        String API_VERSION = "";

        //Check the location in the bundle.  If no location, then NA is default.
        String mLocation = "/na";
        if (requestParams != null && requestParams.containsKey(PARAM_REQUIRED_LOCATION))
            mLocation = requestParams.getString(PARAM_REQUIRED_LOCATION);

        //Params, api key needs to be first I think
        Bundle params = new Bundle();
        params.putString(PARAM_API_KEY, API_KEY);
        if (requestParams != null) {
            params.putAll(requestParams);
        }

        // validate params
        if (!serviceRequest.areParamsValid(params)) {
            Log.e(TAG, "makeRequest missing required param");
            errorListener.onErrorResponse(new VolleyError("Unable to send request.  Missing required parameter."));
            return;
        }

        //Check to see what request it is
        switch(serviceRequest.getServiceName()){
            case SERVICE_GET_ALL_CHAMPION_DATA:
                break;
            case SERVICE_GET_ALL_CHAMPION_IDS:
                API_VERSION = PRODUCTION_CHAMPION_API_VER;
                break;
            case SERVICE_GET_CHAMPION_DATA:
                break;
            case SERVICE_GET_CHAMPION_ID:
                API_VERSION = PRODUCTION_CHAMPION_API_VER;
                break;
            case SERVICE_LOG_OUT:
                break;
            case SERVICE_LOGIN:
                break;
            case SERVICE_SIGN_UP:
                break;
            default:
                break;
        }

        String hostName = serviceRequest.getHostName();
        String uriString = serviceRequest.getServiceUri();

        Uri uri = Uri.parse(String.format("%s://%s%s%s%s%s",
                        serviceRequest.getRequestTypeSecure() ? "https" : "http",
                        hostName,
                        PRODUCTION_API_PREFIX,
                        mLocation,
                        API_VERSION,
                        uriString)
        );

        uri = appendQueryBundle(uri, params);

        Log.d(TAG, String.format("Making request: %s", uri.toString()));

        JsonObjectRequest request = new JsonObjectRequest(
                serviceRequest.getRequestMethod(),
                uri.toString(),
                null, // JSONObject (post values). null for now
                successListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // If we don't have a network response just pass the error along
                if (error.networkResponse == null) {
                    errorListener.onErrorResponse(error);
                    return;
                }

                // create xxxClientError for status code 4xx
                Integer statusCode = error.networkResponse.statusCode;
                if (statusCode >= 400 && statusCode < 500) {
                    errorListener.onErrorResponse(serviceRequest.getClientErrorObject(error.networkResponse));
                } else {
                    // we do not have special types for any other status codes yet
                    errorListener.onErrorResponse(error);
                }
            }
        }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

        queue.start();
    }

    private static Uri appendQueryBundle(Uri uri, Bundle params) {

        Uri.Builder uriBuilder = uri.buildUpon();

        // Loop through our params and append them to the Uri.
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value != null)
                uriBuilder.appendQueryParameter(key, value.toString());
        }

        uri = uriBuilder.build();
        return uri;
    }

    private static void attachUriWithQuery(HttpRequestBase request, Uri uri, Bundle params) {

        try {
            if (params != null) {
                Uri.Builder uriBuilder = uri.buildUpon();

                // Loop through our params and append them to the Uri.
                for (String key : params.keySet()) {
                    Object value = params.get(key);
                    if (value != null)
                        uriBuilder.appendQueryParameter(key, value.toString());
                }

                uri = uriBuilder.build();
            }
            Log.d(TAG, uri.toString());
            request.setURI(new URI(uri.toString()));
        } catch (URISyntaxException e) {
            Log.e(TAG, "URI syntax was incorrect: " + uri.toString(), e);
        }
    }
}
