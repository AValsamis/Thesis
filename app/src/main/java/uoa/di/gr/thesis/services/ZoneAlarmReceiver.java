package uoa.di.gr.thesis.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by skand on 3/20/2017.
 */

public class ZoneAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final Intent intnt = new Intent(context,
                ZoneDetetectionService.class);
        context.startService(intnt);

    }

}
