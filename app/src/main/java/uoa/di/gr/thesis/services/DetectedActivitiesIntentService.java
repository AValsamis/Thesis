package uoa.di.gr.thesis.services;
/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Date;

import retrofit.RetrofitError;
import retrofit.client.Response;
import uoa.di.gr.thesis.database.RestApiDispenser;
import uoa.di.gr.thesis.entities.Constants;
import uoa.di.gr.thesis.entities.RecognizedActivity;
import uoa.di.gr.thesis.entities.SimpleResponse;
import uoa.di.gr.thesis.entities.User;
import uoa.di.gr.thesis.utils.CallbacksManager;

/**
 *  IntentService for handling incoming intents that are generated as a result of requesting
 *  activity updates using
 *  {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = "DetectedActivitiesIS";
    protected final CallbacksManager callbacksManager = new CallbacksManager();

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
        // Log each activity.
        Log.i(TAG, "activities detected");
        for (DetectedActivity da: detectedActivities) {
            Log.i(TAG, Constants.getActivityString(
                    getApplicationContext(),
                    da.getType()) + " " + da.getConfidence() + "%"
            );
        }

        //Send activity information to Server
        DetectedActivity da = result.getMostProbableActivity();

        if (da.getConfidence()>75) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (preferences.getBoolean("log", false)) {
                String username = preferences.getString("username", "nobody");
                User user = new User();
                user.setUsername(username);
                RecognizedActivity recognizedActivity = new RecognizedActivity();
                recognizedActivity.setUser(user);
                recognizedActivity.setCertainty(da.getConfidence());
                recognizedActivity.setState(
                        Constants.getActivityString(
                                getApplicationContext(),
                                da.getType()));
                recognizedActivity.setTimestamp(new Date(result.getTime()));

                final CallbacksManager.CancelableCallback<SimpleResponse> callback = callbacksManager.new CancelableCallback<SimpleResponse>() {
                    @Override
                    protected void onSuccess(SimpleResponse response, Response response2) {
                        if (response.getOk()) {
                            Log.d(TAG,"Activity succesfully saved in server");
                        } else {
                            }
                        }

                    @Override
                    protected void onFailure(RetrofitError error) {

                    }
            };
                RestApiDispenser.getSimpleApiInstance().sendActivity(recognizedActivity, callback);
            }
        }

        // Broadcast the list of detected activities.
        //localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}