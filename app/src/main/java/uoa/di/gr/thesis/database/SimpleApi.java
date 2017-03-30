package uoa.di.gr.thesis.database;

/**
 * Created by Sevle on 2/16/2015.
 */

import android.telecom.Call;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import uoa.di.gr.thesis.entities.AccelerometerStats;
import uoa.di.gr.thesis.entities.DataPacket;
import uoa.di.gr.thesis.entities.SimpleResponse;
import uoa.di.gr.thesis.entities.User;
import uoa.di.gr.thesis.entities.Wifi;
import uoa.di.gr.thesis.entities.Zone;

public interface SimpleApi
{
    String BASE_URL = "http://192.168.1.72:8080/";


    @POST(Constants.URL_USERS)
    //void testApi(@Body String string, Callback<String> cb);
    void testApi(@Body AccelerometerStats accelerometerStats, Callback<String> cb);

    @POST(Constants.URL_REGISTER)
    void registerUser(@Body User user, Callback<SimpleResponse> cb);

    @POST(Constants.URL_LOGIN)
    @FormUrlEncoded
    void loginUser(@Field("username") String username, @Field("password") String  password, @Field("token") String token, Callback<SimpleResponse> cb);

    @Multipart
    @POST(Constants.URL_REGISTERZONE)
    void registerZone(@Part("wifi") List<Wifi> wifis, @Part("zone")Zone zone, Callback<SimpleResponse> cb);

    @Multipart
    @POST(Constants.URL_FIND_ZONE)
    SimpleResponse getZone(@Part("wifi") List<Wifi> signalStrengths, @Part("user")User user);

    @GET(Constants.URL_SAFE_ZONES)
    List<Zone> getSafeZones(@Path("user") String userId);

    @GET(Constants.URL_DANG_ZONES)
    List<Zone> getDangerZones(@Path("user") String userId);

    @POST(Constants.URL_SEND_DATA_PACKET)
    void sendDataPacket(@Body DataPacket dataPacket, Callback<SimpleResponse> cb);

//    @GET(Constants.URL_FALL_DETECTION)
//    void fallDetection(@Path("username") String username, Callback<SimpleResponse> cb);

    @GET(Constants.URL_IS_ELDERLY)
    boolean isElderly(@Path("username") String username);

    @GET(Constants.URL_SHOULD_RUN)
    void getShouldRunService(@Path("username") String username, Callback<SimpleResponse> cb);

    @POST(Constants.URL_START_SCAN)
    void startScanService(@Body User user, Callback<SimpleResponse> cb);

    @POST(Constants.URL_STOP_SCAN)
    void stopScanService(@Body User user, Callback<SimpleResponse> cb);



    /*(@GET(Constants.URL_EVENTS)
    void getEvents(Callback<List<Event>> dataCallback);

    @POST(Constants.POST_EVENT)
    void createEvent(@Body EventPost event, Callback<Event> cb);*/

}