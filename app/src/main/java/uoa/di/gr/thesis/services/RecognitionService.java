package uoa.di.gr.thesis.services;

/**
 * Created by Angelos on 4/9/2017.
 */

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

import java.util.List;

import uoa.di.gr.thesis.entities.ActivityRecPoint;

/**
 * Created by koemdzhiev on 05/06/16.
 */
public class RecognitionService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private LocalBroadcastManager mLocalBroadcastManager;
    private List<ActivityRecPoint> activityRecPoints;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public RecognitionService getService() {
            // Return this instance of LocalService so clients can call public methods
            return RecognitionService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return mBinder;
    }


        private static final String TAG = RecognitionService.class.getSimpleName();
        public final static int DETECTION_INTERVAL_IN_MILLISECONDS = 0;
        private GoogleApiClient googleApiClient;

        public RecognitionService() {
            super();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            initLocationClient();
            return START_STICKY;
        }



        private void initLocationClient() {
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient
                        .Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(ActivityRecognition.API)
                        .build();
            }

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }

        @Override
        public void onConnected(Bundle bundle) {
            Log.i(TAG, "Connected to GoogleApiClient");
            if (googleApiClient != null && googleApiClient.isConnected()) {
                ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                        googleApiClient,
                        DETECTION_INTERVAL_IN_MILLISECONDS,
                        getActivityDetectionPendingIntent()
                ).setResultCallback(this);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, "Connection suspended");
            googleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        }

        private PendingIntent getActivityDetectionPendingIntent() {
            Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

            // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
            // requestActivityUpdates() and removeActivityUpdates().
            return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        @Override
        public void onResult(Status status) {
            Log.i(TAG, "onResult = " + status.getStatusMessage());
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.i(TAG, "onDestroy");
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                    googleApiClient,
                    getActivityDetectionPendingIntent()
            ).setResultCallback(this);
        }
    }


