package uoa.di.gr.thesis.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class ZoneAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final Intent intnt = new Intent(context,
                ZoneDetetectionService.class);
        context.startService(intnt);

    }

}
