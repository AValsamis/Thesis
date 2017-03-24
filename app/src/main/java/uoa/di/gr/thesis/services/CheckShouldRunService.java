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
 * Created by skand on 3/24/2017.
 */

public class CheckShouldRunService extends Service {

    private final IBinder mBinder = new CheckShouldRunService.LocalBinder();
    protected final CallbacksManager callbacksManager = new CallbacksManager();

    public class LocalBinder extends Binder {
        public CheckShouldRunService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CheckShouldRunService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//
//        Log.d("CheckShouldRunService","CheckShouldRunService called");
//        final CallbacksManager.CancelableCallback<SimpleResponse> callback = callbacksManager.new CancelableCallback<SimpleResponse>() {
//            @Override
//            protected void onSuccess(SimpleResponse response, Response response2) {
//                if (response.getOk()) {
//                    collectData();
//                    toFinishService = false;
//                }
//                else{
//                    toFinishService = true;
//                    stopSelf();
//                }
//            }
//            @Override
//            protected void onFailure(RetrofitError error) {
//            }
//        };
//        RestApiDispenser.getSimpleApiInstance().getShouldRunService(prefs.getString("username", "nobody"), callback);
//
//    }
}
