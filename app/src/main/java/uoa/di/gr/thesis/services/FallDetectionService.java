package uoa.di.gr.thesis.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

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
        new Thread()
        {
            public void run() {
                RestApiDispenser.getSimpleApiInstance().fallDetection(prefs.getString("username","nobody"));
            }

        }.start();
        return Service.START_NOT_STICKY;
    }
}
