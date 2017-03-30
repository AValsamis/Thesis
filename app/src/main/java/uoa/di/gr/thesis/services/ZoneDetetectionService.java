package uoa.di.gr.thesis.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uoa.di.gr.thesis.activities.DataCollectionActivity;
import uoa.di.gr.thesis.database.RestApiDispenser;
import uoa.di.gr.thesis.entities.SimpleResponse;
import uoa.di.gr.thesis.entities.User;
import uoa.di.gr.thesis.entities.Wifi;

/**
 * Created by skand on 3/20/2017.
 */

public class ZoneDetetectionService  extends Service {

    private final IBinder mBinder = new ZoneDetetectionService.LocalBinder();
    protected final int SCAN_TIMES=10;

    public class LocalBinder extends Binder {
        public ZoneDetetectionService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ZoneDetetectionService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String connectivity_context = WIFI_SERVICE;
        final WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(connectivity_context);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        new Thread()
        {
            public void run() {
                List<Wifi> wifis = new ArrayList<Wifi>();
                List <String> wifiNames = new ArrayList();
                for(int i = 0 ; i < SCAN_TIMES; i++) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    wifiManager.startScan();
                    List<ScanResult> s;
                    s = wifiManager.getScanResults();

                    for (ScanResult scanResult : s) {
                        if (!wifiNames.contains(scanResult.SSID)) {
                            wifiNames.add(scanResult.SSID);
                            ArrayList<Double> signalStrengths = new ArrayList();
                            signalStrengths.add((double)scanResult.level);
                            Wifi wifi = new Wifi();
                            wifi.setMacAddress(scanResult.BSSID);
                            wifi.setName(scanResult.SSID);
                            wifi.setSignalStrength(signalStrengths);
                            wifis.add(wifi);
                        } else {
                            for (Wifi wifi : wifis) {
                                if (wifi.getName().equals(scanResult.SSID)) {
                                    wifi.getSignalStrength().add((double)scanResult.level);
                                }
                            }
                        }
                    }
                    Log.i("DEBUG", Arrays.asList(wifis).toString());
                }
                User user = new User();
                user.setUsername(prefs.getString("username","nobody"));
                SimpleResponse zone = RestApiDispenser.getSimpleApiInstance().getZone(wifis, user);
                Log.i("ZONE IS: ", zone.toString());
            }

        }.start();
        return Service.START_STICKY;
    }


}
