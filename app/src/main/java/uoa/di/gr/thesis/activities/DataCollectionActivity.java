package uoa.di.gr.thesis.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.util.logging.Level;
import java.util.logging.Logger;

import uoa.di.gr.thesis.entities.User;
import uoa.di.gr.thesis.services.DataCollectionService;
import uoa.di.gr.thesis.services.RecognitionService;

public class DataCollectionActivity extends AppCompatActivity{

    private User user = new User();
    Intent i,ii;
    DataCollectionService dataCollectionService;
    RecognitionService activityRecognitionService;

    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user.setUsername(prefs.getString("username", "nobody"));

        i = new Intent(this, DataCollectionService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        this.startService(i);

        ii = new Intent(this, RecognitionService.class);
        bindService(ii, mConnection2, Context.BIND_AUTO_CREATE);
        this.startService(ii);

        Toast.makeText(this, "Started data collection", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
   }

    @Override
    public void onStop() {
        unbindService(mConnection);
        unbindService(mConnection2);
        super.onStop();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.i("ONSERVICECONNECTED","ONSERVICECONNECTED");
            DataCollectionService.LocalBinder binder = (DataCollectionService.LocalBinder) service;
            dataCollectionService = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    private ServiceConnection mConnection2 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.i("ONSERVICECONNECTED","ONSERVICECONNECTED");
            RecognitionService.LocalBinder binder = (RecognitionService.LocalBinder) service;
            activityRecognitionService = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    @Override
    public void onDestroy()
    {
        unbindService(mConnection);
        unbindService(mConnection2);
        super.onDestroy();
    }
}
