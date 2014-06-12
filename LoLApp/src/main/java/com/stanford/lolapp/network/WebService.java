package com.stanford.lolapp.network;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WebService {

    private static final String TAG                             = "WebService";

    //Riot API key
    public static final String RIOT_API_KEY                     = "b2bf733d-dbb0-47c1-a0c4-f71791727277";
    public static final String PARAM_RIOT_API_KEY               = "api_key";

    //Headers for Parse
    public static final String APPLICATION_ID                   = "FDdFS2e3smhbncfsUHIkdr0FP0GRyZVPer4EChyH";
    public static final String REST_API_KEY                     = "K4NrDUXTOQWkzyinicLhFK9sSHXRA2w76YrvBwEf";


    // host names
    public static final String PRODUCTION_RIOT_HOSTNAME         = "prod.api.pvp.net";
    public static final String PRODUCTION_PARSE_HOSTNAME        = "api.parse.com";
    public static final String PRODUCTION_RIOT_API_PREFIX       = "/api/lol";
    public static final String PRODUCTION_PARSE_API_VER         = "/1";
    public static final String PRODUCTION_CHAMPION_API_VER      = "/v1.2";
    public static final String PRODUCTION_GAMES_API_VER         = "/v1.3";
    public static final String PRODUCTION_LEAGUE_API_VER        = "/v1.3";
    public static final String PRODUCTION_STATIC_API_VER        = "/v1.2";
    public static final String PRODUCTION_STATS_API_VER         = "/v1.3";
    public static final String PRODUCTION_SUMMONER_API_VER      = "/v1.4";
    public static final String PRODUCTION_TEAM_API_VER          = "/v2.2";
    public static final String PRODUCTION_STATIC                = "/static-data";

    //API Paths, these are the endpoints unless there is an ID, then the ID is the endpoint
    public static final String PATH_GET_CHAMP                   = "/champion/";
    public static final String PATH_GET_ALL_CHAMPS              = "/champion";
    public static final String PATH_GET_CHAMP_ID                = "/champion/";
    public static final String PATH_GET_ALL_CHAMP_IDS           = "/champion";
    public static final String PATH_GET_ITEM                    = "/item/";
    public static final String PATH_GET_ALL_ITEMS               = "/item";
    public static final String PATH_LOGIN                       = "/login";
    public static final String PATH_CREATE_USER                 = "/users";


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
    public static final int SERVICE_GET_ALL_ITEM_DATA           = 4;
    public static final int SERVICE_GET_ITEM_DATA               = 5;
    public static final int SERVICE_SIGN_UP                     = -1;
    public static final int SERVICE_LOG_OUT                     = -2;
    public static final int SERVICE_LOGIN                       = -3;

    //Required headers for parse
    public static final String HEADER_REQUIRED_APP_KEY          = "X-Parse-Application-Id";
    public static final String HEADER_REQUIRED_API_KEY          = "X-Parse-REST-API-Key";
    public static final String HEADER_REQUIRED_FORMAT           = "Content-Type";


    // Required parameter that we set on almost every service call
    public static final String PARAM_REQUIRED_AUTH_TOKEN        = "authToken";
    public static final String PARAM_REQUIRED_LOCATION          = "location";
    public static final String PARAM_REQUIRED_LOCALE            = "locale";
    public static final String PARAM_REQUIRED_API_KEY           = "REST-API-Key";

    // Optional parameters available for pretty much every service call
    public static final String PARAM_OPTIONAL_FORMAT            = "format";
    public static final String FORMAT_JSON                      = "application/json";
    public static final String PARAM_OPTIONAL_CALLBACK          = "callback";
    public static final String PARAM_OPTIONAL_RETURNHTML        = "returnHTML";

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

    public static void makeRequest(
            final RequestQueue queue,
            final WebserviceRequest serviceRequest,
            final Bundle requestParams,
            final JSONBody body,
            final Response.Listener<JSONObject> successListener,
            final Response.ErrorListener errorListener) {

        //Get the proper headers
        final Map<String,String> headers = generateHeaders(serviceRequest);

        //Make sure all required headers are there
        validateHeaders(serviceRequest,errorListener,headers);

        //Params, api key needs to be first I think
        final Bundle params = generateParams(requestParams);

        // validate params
        validateParams(serviceRequest,errorListener,params);

        //Generates the URI from the location, request, params etc
        Uri uri = generateURI(serviceRequest,params);

        Log.d(TAG, String.format("Making request: %s", uri.toString()));

        JsonObjectRequest request = new JsonObjectRequest(
                serviceRequest.getRequestMethod(),
                uri.toString(),
                null,
                successListener, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    errorListener.onErrorResponse(error);
                    return;
                }

                Integer statusCode = error.networkResponse.statusCode;
                if (statusCode >= 400 && statusCode < 500) {
                    errorListener.onErrorResponse(serviceRequest.getClientErrorObject(error.networkResponse));
                } else {
                    // we do not have special types for any other status codes yet
                    errorListener.onErrorResponse(error);
                }
            }
        }){
            /**
             * Add headers here
             * @return
             * @throws AuthFailureError
             */
            @Override public Map<String,String> getHeaders() throws AuthFailureError{
                return headers != null ? headers : super.getHeaders();
            }

            /**
             * Add body to request here
             * @return
             */
            @Override public byte[] getBody() {
                if(body != null){
                    if(!serviceRequest.isBodyValid(body)) {
                        errorListener.onErrorResponse(new VolleyError("Unable to send request.  Missing required body param."));
                        return super.getBody();
                    }

                    return JSONBody.toByteArray(body.toString());
                }
                return super.getBody();
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

        queue.start();
    }

    /**
     * Validates the headers, if there is an issue it will call the error response
     * @param serviceRequest
     * @param errorListener
     * @param headers
     */
    private static void validateHeaders(WebserviceRequest serviceRequest,Response.ErrorListener errorListener, Map<String,String> headers){
        // validate headers
        if (serviceRequest.isAuthHeaderRequired() && !serviceRequest.areHeadersValid(headers)) {
            Log.e(TAG, "makeRequest missing required headers");
            errorListener.onErrorResponse(new VolleyError("Unable to send request.  Missing required header."));
        }
    }

    /**
     * Puts the required headers for the app into the map
     * @param serviceRequest
     * @return
     */
    private static Map<String,String> generateHeaders(WebserviceRequest serviceRequest){
        Map<String, String> headers = new HashMap<String, String>();
        if(serviceRequest.isAuthHeaderRequired()) {
            Log.d(TAG,"Auth Header Required");
            headers.put(HEADER_REQUIRED_API_KEY, REST_API_KEY);
            headers.put(HEADER_REQUIRED_APP_KEY, APPLICATION_ID);
        }
        return headers;
    }

    /**
     * Puts params into the bundle
     * @param requestParams
     * @return
     */
    private static Bundle generateParams(Bundle requestParams){
        Bundle params = new Bundle();
        if (requestParams != null) {
            params.putAll(requestParams);
        }
        return params;
    }

    /**
     * validates the params. Squaks to the listener if its done goofed.
     * @param serviceRequest
     * @param errorListener
     * @param params
     */
    private static void validateParams(WebserviceRequest serviceRequest, Response.ErrorListener errorListener, Bundle params){
        if (!serviceRequest.areParamsValid(params)) {
            Log.e(TAG, "makeRequest missing required param");
            errorListener.onErrorResponse(new VolleyError("Unable to send request.  Missing required parameter."));
        }
    }

    /**
     * returns the location
     * @param requestParams
     * @return
     */
    private static String generateLocation(Bundle requestParams){
        String location = "/na"; //TODO: grab this default from a sharepref
        if (requestParams != null && requestParams.containsKey(PARAM_REQUIRED_LOCATION)) {
            location = requestParams.getString(PARAM_REQUIRED_LOCATION);
            requestParams.remove(PARAM_REQUIRED_LOCATION); //Don't want this to be involved with args
        }
        return location;
    }

    /**
     * Returns the URI based on params and request
     * @param serviceRequest
     * @return
     */
    private static Uri generateURI(WebserviceRequest serviceRequest,Bundle params){

        String API_VERSION = "";
        Uri uri = null;

        String hostName = serviceRequest.getHostName();
        String uriString = serviceRequest.getServiceUri();

        //Check the location in the bundle.  If no location, then NA is default.
        String mLocation = generateLocation(params);

        //Set the API version and URI based on request
        switch(serviceRequest.getServiceName()){
            case SERVICE_GET_CHAMPION_DATA:
                API_VERSION = PRODUCTION_STATIC_API_VER;
                params.putString(PARAM_RIOT_API_KEY, RIOT_API_KEY);
                uri = Uri.parse(String.format("%s://%s%s%s%s%s%s",
                        serviceRequest.getRequestTypeSecure() ? "https" : "http",
                        hostName,
                        PRODUCTION_RIOT_API_PREFIX,
                        PRODUCTION_STATIC,
                        mLocation,
                        API_VERSION,
                        uriString));
                uri = appendQueryBundle(uri, params);
                break;
            case SERVICE_GET_ALL_CHAMPION_DATA:
                API_VERSION = PRODUCTION_STATIC_API_VER;
                params.putString(PARAM_RIOT_API_KEY, RIOT_API_KEY);
                uri = Uri.parse(String.format("%s://%s%s%s%s%s%s",
                        serviceRequest.getRequestTypeSecure() ? "https" : "http",
                        hostName,
                        PRODUCTION_RIOT_API_PREFIX,
                        PRODUCTION_STATIC,
                        mLocation,
                        API_VERSION,
                        uriString));
                uri = appendQueryBundle(uri, params);
                break;
            case SERVICE_GET_CHAMPION_ID:
                params.putString(PARAM_RIOT_API_KEY, RIOT_API_KEY);
                API_VERSION = PRODUCTION_CHAMPION_API_VER;
                uri = Uri.parse(String.format("%s://%s%s%s%s%s",
                        serviceRequest.getRequestTypeSecure() ? "https" : "http",
                        hostName,
                        PRODUCTION_RIOT_API_PREFIX,
                        mLocation,
                        API_VERSION,
                        uriString));
                uri = appendQueryBundle(uri, params);
                break;
            case SERVICE_GET_ALL_CHAMPION_IDS:
                API_VERSION = PRODUCTION_CHAMPION_API_VER;
                params.putString(PARAM_RIOT_API_KEY, RIOT_API_KEY);
                uri = Uri.parse(String.format("%s://%s%s%s%s%s",
                        serviceRequest.getRequestTypeSecure() ? "https" : "http",
                        hostName,
                        PRODUCTION_RIOT_API_PREFIX,
                        mLocation,
                        API_VERSION,
                        uriString));
                uri = appendQueryBundle(uri, params);
                break;
            case SERVICE_GET_ITEM_DATA:
                API_VERSION = PRODUCTION_STATIC_API_VER;
                params.putString(PARAM_RIOT_API_KEY, RIOT_API_KEY);
                uri = Uri.parse(String.format("%s://%s%s%s%s%s%s",
                        serviceRequest.getRequestTypeSecure() ? "https" : "http",
                        hostName,
                        PRODUCTION_RIOT_API_PREFIX,
                        PRODUCTION_STATIC,
                        mLocation,
                        API_VERSION,
                        uriString));
                uri = appendQueryBundle(uri, params);
                break;
            case SERVICE_GET_ALL_ITEM_DATA:
                API_VERSION = PRODUCTION_STATIC_API_VER;
                params.putString(PARAM_RIOT_API_KEY, RIOT_API_KEY);
                uri = Uri.parse(String.format("%s://%s%s%s%s%s%s",
                        serviceRequest.getRequestTypeSecure() ? "https" : "http",
                        hostName,
                        PRODUCTION_RIOT_API_PREFIX,
                        PRODUCTION_STATIC,
                        mLocation,
                        API_VERSION,
                        uriString));
                uri = appendQueryBundle(uri, params);
                break;
            case SERVICE_LOG_OUT:
                break;
            case SERVICE_LOGIN:
                uri = Uri.parse(String.format("%s://%s%s%s",
                        serviceRequest.getRequestTypeSecure() ? "https" : "http",
                        PRODUCTION_PARSE_HOSTNAME,
                        PRODUCTION_PARSE_API_VER,
                        uriString));
                uri = appendQueryBundle(uri, params);
                break;
            case SERVICE_SIGN_UP:
                uri = Uri.parse(String.format("%s://%s%s%s",
                        serviceRequest.getRequestTypeSecure() ? "https" : "http",
                        PRODUCTION_PARSE_HOSTNAME,
                        PRODUCTION_PARSE_API_VER,
                        uriString));
                uri = appendQueryBundle(uri, params);
                break;
            default:
                break;
        }
        return uri;
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
}
