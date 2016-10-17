package uoa.di.gr.thesis.database;

/**
 * Created by Sevle on 2/16/2015.
 */

import com.squareup.okhttp.ResponseBody;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import uoa.di.gr.thesis.entities.AccelerometerStats;
import uoa.di.gr.thesis.entities.SimpleResponse;
import uoa.di.gr.thesis.entities.User;
import uoa.di.gr.thesis.entities.Zone;

public interface SimpleApi
{
    public static final String BASE_URL = "http://pclab120.telecom.ece.ntua.gr:8080/";


    @POST(Constants.URL_USERS)
    //void testApi(@Body String string, Callback<String> cb);
     void testApi(@Body AccelerometerStats accelerometerStats, Callback<String> cb);

    @POST(Constants.URL_REGISTER)
    void registerUser(@Body User user, Callback<SimpleResponse> cb);

    @POST(Constants.URL_LOGIN)
    @FormUrlEncoded
    void loginUser(@Field("username") String username, @Field("password") String  password, Callback<SimpleResponse> cb);

    @POST(Constants.URL_REGISTERZONE)
    void registerDangerZone(@Body List<Zone> signalStrengths, Callback<SimpleResponse> cb);
    /*(@GET(Constants.URL_EVENTS)
    void getEvents(Callback<List<Event>> dataCallback);

    @POST(Constants.POST_EVENT)
    void createEvent(@Body EventPost event, Callback<Event> cb);*/

}