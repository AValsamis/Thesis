package uoa.di.gr.thesis.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import uoa.di.gr.thesis.database.RestApiDispenser;

/**
 * Created by skand on 3/15/2017.
 */

public class FallAlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        final Intent intnt = new Intent(context,
                FallDetectionService.class);
        context.startService(intnt);

    }
}
