package com.stanford.lolapp.network;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static final String PARAM_OPTIONAL_FORMAT           = "format";
    private static final String FORMAT_JSON                     = "application/json";
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

    //Enum for item data parameter
    public enum ItemData{
        all("all"),
        colloq("colloq"),
        consumeOnFull("consumeOnFull"),
        consumed("consumed"),
        depth("depth"),
        from("from"),
        gold("gold"),
        groups("groups"),
        hideFromAll("hideFromAll"),
        image("image"),
        inStore("inStore"),
        into("into"),
        maps("maps"),
        requiredChampion("requiredChampion"),
        sanitizedDescription("sanitizedDescription"),
        specialRecipe("specialRecipe"),
        stacks("stacks"),
        stats("stats"),
        tags("tags"),
        tree("tree");
        private String param;
        ItemData(String param){
            this.param = param;
        }
        public String getParam(){
            return this.param;
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
            return mRequiredParams.contains(PARAM_REQUIRED_AUTH_TOKEN);
        }

        public Boolean isAuthHeaderRequired() {
            return mRequiredHeaders.contains(HEADER_REQUIRED_API_KEY)
                    || mRequiredHeaders.contains(HEADER_REQUIRED_APP_KEY);
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

    /**
     * Base request with Get method
     */
    public static class LoLAppWebserviceRequest extends WebserviceRequest {

        public LoLAppWebserviceRequest(int serviceName, String serviceUri) {
            super(serviceName, serviceUri);
            mRequestMethod = Request.Method.GET;
            mHostName = PRODUCTION_RIOT_HOSTNAME;
        }

        public List<String> createDefaultOptionalParams() {
            List<String> optionalParams = new ArrayList<String>();
            mRequiredHeaders.add(HEADER_REQUIRED_FORMAT);
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
    public static class Login extends LoLAppWebserviceRequest {
        public static final String PARAM_REQUIRED_USERNAME = "username";
        public static final String PARAM_REQUIRED_PASSWORD = "password";

        public Login() {
            super(SERVICE_LOGIN, PATH_LOGIN);
            mRequiredParams.add(PARAM_REQUIRED_USERNAME);
            mRequiredParams.add(PARAM_REQUIRED_PASSWORD);
            mRequiredHeaders.add(HEADER_REQUIRED_API_KEY);
            mRequiredHeaders.add(HEADER_REQUIRED_APP_KEY);
        }
    }

    /**
     * If I ever decide to make users
     */
    public static class CreateUser extends LoLAppWebserviceRequest {

        public static final String PARAM_REQUIRED_USERNAME  = "username";
        public static final String PARAM_REQUIRED_PASSWORD  = "password";

        public static final String PARAM_OPTIONAL_EMAIL     = "email";

        public CreateUser() {
            super(SERVICE_SIGN_UP, PATH_CREATE_USER);
            mRequiredBody.add(PARAM_REQUIRED_USERNAME);
            mRequiredBody.add(PARAM_REQUIRED_PASSWORD);
            mOptionalBody.add(PARAM_OPTIONAL_EMAIL);
            mRequiredHeaders.add(HEADER_REQUIRED_API_KEY);
            mRequiredHeaders.add(HEADER_REQUIRED_APP_KEY);
        }
    }

    /**
     * Get a list of Champion IDs
     */
    public static class GetAllChampionIds extends LoLAppWebserviceRequest {

        public GetAllChampionIds() {
            super(SERVICE_GET_ALL_CHAMPION_IDS, PATH_GET_ALL_CHAMP_IDS);
        }
    }

    /**
     * Get a single champion ID
     */
    public static class GetChampionId extends LoLAppWebserviceRequest {


        public GetChampionId(String championID) {
            super(SERVICE_GET_CHAMPION_ID, PATH_GET_CHAMP_ID + championID);
        }
    }

    /**
     * Get a champion
     */
    public static class GetChampionData extends LoLAppWebserviceRequest {

        public static final String PARAM_DATA = "champData"; //Use Enum for this


        public GetChampionData(int championID) {
            super(SERVICE_GET_CHAMPION_DATA, PATH_GET_CHAMP + championID);
            mRequiredParams.add(PARAM_REQUIRED_LOCATION);
            mRequiredParams.add(PARAM_REQUIRED_LOCALE);
            mOptionalParams.add(PARAM_DATA);
        }
    }

    /**
     * Get a list of all the champions
     */
    public static class GetAllChampionData extends LoLAppWebserviceRequest {

        public static final String PARAM_DATA = "champData"; //Use Enum for this

        public GetAllChampionData() {
            super(SERVICE_GET_ALL_CHAMPION_DATA, PATH_GET_ALL_CHAMPS);
            mRequiredParams.add(PARAM_REQUIRED_LOCATION);
            mRequiredParams.add(PARAM_REQUIRED_LOCALE);
            mOptionalParams.add(PARAM_DATA);
        }
    }

    public static class GetItem extends LoLAppWebserviceRequest{

        public static final String PARAM_DATA = "itemData"; //Use Enum for this

        public GetItem(int itemID){
            super(SERVICE_GET_ALL_ITEM_DATA, PATH_GET_ITEM + itemID);
            mRequiredParams.add(PARAM_REQUIRED_LOCATION);
            mRequiredParams.add(PARAM_REQUIRED_LOCALE);
            mOptionalParams.add(PARAM_DATA);
        }
    }

    public static class GetAllItems extends LoLAppWebserviceRequest{

        public static final String PARAM_DATA = "itemListData"; //Use Enum for this

        public GetAllItems(){
            super(SERVICE_GET_ITEM_DATA, PATH_GET_ALL_ITEMS);
            mRequiredParams.add(PARAM_REQUIRED_LOCATION);
            mRequiredParams.add(PARAM_REQUIRED_LOCALE);
            mOptionalParams.add(PARAM_DATA);
        }
    }

    public static void makeRequest(
            final Context context,
            final RequestQueue queue,
            final WebserviceRequest serviceRequest,
            final Bundle requestParams,
            final JSONBody body,
            final Response.Listener<JSONObject> successListener,
            final Response.ErrorListener errorListener) {

        String API_VERSION = "";

        //Get the proper headers
        final Map<String,String> headers = new HashMap<String, String>();
        if(serviceRequest.isAuthHeaderRequired()) {
            Log.d(TAG,"Auth Header Required");
            headers.put(HEADER_REQUIRED_API_KEY, REST_API_KEY);
            headers.put(HEADER_REQUIRED_APP_KEY, APPLICATION_ID);
        }


        // validate headers
        if (serviceRequest.isAuthHeaderRequired() && !serviceRequest.areHeadersValid(headers)) {
            Log.e(TAG, "makeRequest missing required headers");
            errorListener.onErrorResponse(new VolleyError("Unable to send request.  Missing required header."));
            return;
        }


        //Params, api key needs to be first I think
        final Bundle params = new Bundle();
        if (requestParams != null) {
            params.putAll(requestParams);
        }

        // validate params
        if (!serviceRequest.areParamsValid(params)) {
            Log.e(TAG, "makeRequest missing required param");
            errorListener.onErrorResponse(new VolleyError("Unable to send request.  Missing required parameter."));
            return;
        }

        //Check the location in the bundle.  If no location, then NA is default.
        String mLocation = "/na";
        if (requestParams != null && requestParams.containsKey(PARAM_REQUIRED_LOCATION)) {
            mLocation = requestParams.getString(PARAM_REQUIRED_LOCATION);
            params.remove(PARAM_REQUIRED_LOCATION); //Don't want this to be involved with args
        }

        String hostName = serviceRequest.getHostName();
        String uriString = serviceRequest.getServiceUri();

        Uri uri = null;
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
                Log.d(TAG,"Headers: " + headers.toString());
                break;
            case SERVICE_SIGN_UP:
                uri = Uri.parse(String.format("%s://%s%s%s",
                        serviceRequest.getRequestTypeSecure() ? "https" : "http",
                        PRODUCTION_PARSE_HOSTNAME,
                        PRODUCTION_PARSE_API_VER,
                        uriString));
                uri = appendQueryBundle(uri, params);
                Log.d(TAG,"Headers: " + headers.toString());
                break;
            default:
                break;
        }

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
