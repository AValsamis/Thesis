package uoa.di.gr.thesis.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.logging.Level;
import java.util.logging.Logger;

import uoa.di.gr.thesis.R;
import uoa.di.gr.thesis.entities.User;
import uoa.di.gr.thesis.services.DataCollectionService;

/**
 * Created by skand on 11/20/2016.
 */

public class DataCollectionActivity extends Activity {

    private User user = new User();
    Intent i;
    DataCollectionService dataCollectionService;
    boolean mBound = false;
    AppCompatButton collectionButton;
    boolean isServiceRunning = false;

    @Override
    public void onCreate(Bundle icicle) {

        Log.i("INSIDE DATACOLLECTION", "Hi");
        super.onCreate(icicle);
        setContentView(R.layout.data_collection);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user.setUsername(prefs.getString("username", "nobody"));

        collectionButton = (AppCompatButton) this.findViewById(R.id.stopCollection);
        collectionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startStopCollection(view);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("STARTED","STARTED");

        i = new Intent(this, DataCollectionService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        // potentially add data to the intent
//        i.putExtra("KEY1", "Value to be used by the service");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isServiceRunning = prefs.getBoolean("PREF_IS_RUNNING",false);
        if (isServiceRunning) {
            collectionButton.setText("Stop scanning");
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    public void startStopCollection(View view) {
        try
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            isServiceRunning = prefs.getBoolean("PREF_IS_RUNNING",false);

//            if(mBound) {
                if (isServiceRunning) {
                    Log.i("TESTTTTTTTTTT", "TESTTTTTTTTTTTTTT");
                    dataCollectionService.unregisterListeners();
                    showToast("Stopping the collection...");
                    collectionButton.setText("Start scanning");
                } else {
                    this.startService(i);
                    collectionButton.setText("Stop scanning");

                }
//            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DataCollectionActivity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(DataCollectionActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(DataCollectionActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.i("ONSERVICECONNECTED","ONSERVICECONNECTED");
            DataCollectionService.LocalBinder binder = (DataCollectionService.LocalBinder) service;
            dataCollectionService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
