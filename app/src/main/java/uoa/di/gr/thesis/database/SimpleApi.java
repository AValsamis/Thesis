package uoa.di.gr.thesis.database;

/**
 * Created by Sevle on 2/16/2015.
 */

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import uoa.di.gr.thesis.entities.AccelerometerStats;
import uoa.di.gr.thesis.entities.Zone;

public interface SimpleApi
{
    public static final String BASE_URL = "http://192.168.1.64:8080/";


    @POST(Constants.URL_USERS)
    //void testApi(@Body String string, Callback<String> cb);
     void testApi(@Body AccelerometerStats accelerometerStats, Callback<String> cb);

    @GET(Constants.URL_REGISTER)
    void registerUser(@Path("username") String username,@Path("name") String name,@Path("surname") String surname, @Path("password") String password, Callback<String> cb);

    @POST(Constants.URL_REGISTERZONE)
    void registerDangerZone(@Body List<Zone> signalStrengths, Callback<String> cb);
    /*(@GET(Constants.URL_EVENTS)
    void getEvents(Callback<List<Event>> dataCallback);

    @POST(Constants.POST_EVENT)
    void createEvent(@Body EventPost event, Callback<Event> cb);*/

}