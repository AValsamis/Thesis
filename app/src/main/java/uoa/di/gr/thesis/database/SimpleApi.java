package uoa.di.gr.thesis.database;

/**
 * Created by Sevle on 2/16/2015.
 */

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import uoa.di.gr.thesis.entities.AccelerometerStats;

public interface SimpleApi
{
    public static final String BASE_URL = "http://147.102.19.120:8080/api";

    @POST(Constants.URL_USERS)
    void testApi(@Body AccelerometerStats accelerometerStats, Callback<String> cb);

    /*(@GET(Constants.URL_EVENTS)
    void getEvents(Callback<List<Event>> dataCallback);

    @POST(Constants.POST_EVENT)
    void createEvent(@Body EventPost event, Callback<Event> cb);*/

}