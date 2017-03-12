package uoa.di.gr.thesis.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import uoa.di.gr.thesis.activities.DataCollectionActivity;

/**
 * Created by skand on 3/12/2017.
 */

public class StartDataCollectionServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent activityIntent = new Intent(context, DataCollectionActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean isElderly = prefs.getBoolean("iselderly",false);
            if(isElderly)
                context.startActivity(activityIntent);
        }
    }

}
