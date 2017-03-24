package uoa.di.gr.thesis.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import retrofit.RetrofitError;
import retrofit.client.Response;
import uoa.di.gr.thesis.database.RestApiDispenser;
import uoa.di.gr.thesis.entities.SimpleResponse;
import uoa.di.gr.thesis.utils.CallbacksManager;

/**
 * Created by skand on 3/15/2017.
 */

public class FallDetectionService extends Service {

    private final IBinder mBinder = new FallDetectionService.LocalBinder();
    protected final CallbacksManager callbacksManager = new CallbacksManager();

    public class LocalBinder extends Binder {
        public FallDetectionService getService() {
            // Return this instance of LocalService so clients can call public methods
            return FallDetectionService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        final CallbacksManager.CancelableCallback<SimpleResponse> callback = callbacksManager.new CancelableCallback<SimpleResponse>() {
            @Override
            protected void onSuccess(SimpleResponse response, Response response2) {
                System.out.println(response.getResponse());
            }

            @Override
            protected void onFailure(RetrofitError error) {
            }
        };
        Log.d("FallDetectionService","Fall detection service called");
        RestApiDispenser.getSimpleApiInstance().fallDetection(prefs.getString("username", "nobody"), callback);
        return START_NOT_STICKY;
    }
}
