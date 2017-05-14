package uoa.di.gr.thesis.database;

import retrofit.RestAdapter;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;


import retrofit.client.OkClient;

public class RestApiDispenser {

    private static final String API_URL = SimpleApi.BASE_URL;
    private static RestAdapter restAdapterInstance = null;
    private static SimpleApi simpleApiInstance;

    public static RestAdapter getRestAdapter()
    {
        if (restAdapterInstance == null)
        {
            OkHttpClient s = new OkHttpClient();
            s.setConnectTimeout(10, TimeUnit.SECONDS);
            s.setReadTimeout(10, TimeUnit.SECONDS);
            s.setWriteTimeout(10, TimeUnit.SECONDS);
            RestAdapter.Builder builder = new RestAdapter.Builder().setEndpoint(API_URL).setClient(new OkClient(s));
            restAdapterInstance = builder.build();
        }
        return restAdapterInstance;
    }

    public static SimpleApi getSimpleApiInstance()
    {
        if(simpleApiInstance==null) {
            simpleApiInstance = getRestAdapter().create(SimpleApi.class);
        }
        return simpleApiInstance;
    }

}