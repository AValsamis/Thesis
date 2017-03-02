package uoa.di.gr.thesis.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uoa.di.gr.thesis.entities.Wifi;
import uoa.di.gr.thesis.interfaces.RegisterZoneCallbacks;

/**
 * Created by Angelos on 11/21/2016.
 */

public class RegisterZone extends HandlerThread {
    protected final int SCAN_TIMES=10;

    private Handler mWorkerHandler;
    private Handler mResponseHandler;
    private Boolean isSafe;
    WifiManager wifiManager;
    private static final String TAG = RegisterZone.class.getSimpleName();
    //private Map<ImageView, String> mRequestMap = new HashMap<ImageView, String>();
    private RegisterZoneCallbacks mCallback;


    public RegisterZone(Context context, Boolean isSafe, Handler responseHandler, RegisterZoneCallbacks callback) {
        super(TAG);
        this.isSafe=isSafe;
        mResponseHandler = responseHandler;
        mCallback = callback;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

    }

    public void queueTask() {

        mWorkerHandler.obtainMessage()
                .sendToTarget();
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Message m = new Message();
                m.copyFrom(msg);
                handleRequest();
                m.recycle();
                return true;
            }
        });
    }


    private void handleRequest() {

        final List<Wifi> wifis = new ArrayList<Wifi>();
        List<String> wifiNames = new ArrayList();
        for (int i = 0; i < SCAN_TIMES; i++) {
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
                    signalStrengths.add((double) scanResult.level);
                    Wifi wifi = new Wifi();
                    wifi.setMacAddress(scanResult.BSSID);
                    wifi.setName(scanResult.SSID);
                    wifi.setSignalStrength(signalStrengths);
                    wifis.add(wifi);
                } else {
                    for (Wifi wifi : wifis) {
                        if (wifi.getName().equals(scanResult.SSID)) {
                            wifi.getSignalStrength().add((double) scanResult.level);
                        }
                    }
                }

            }
            Log.i(android.os.Process.getThreadPriority(android.os.Process.myTid()) +" DEBUG", Arrays.asList(wifis).toString());

        }
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onGatherWifiList(wifis,isSafe);
            }
        });
    }
}
