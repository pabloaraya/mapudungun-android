package com.pabloaraya.mapudungun;

import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.QueryMap;

/**
 * Created by pablo on 8/26/15.
 */
public class Server {

    /* Config api REST */
    final static String CONFIG_HEADER = "Accept: application/json";
    final static String CONFIG_BASE_DOMAIN = "http://mapudungun.org";
    final static String URL_API_TRANSLATE = "/api";

    public interface ApiREST {

        @Headers(CONFIG_HEADER)
        @GET(URL_API_TRANSLATE)
        void translate(@QueryMap Map<String, String> params, Callback<ResponseTranslate> callback);
    }

    /* Method to call REST */
    public static ApiREST api(){

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(CONFIG_BASE_DOMAIN)
                //.setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        ApiREST apiRest = restAdapter.create(ApiREST.class);
        return apiRest;
    }
}


