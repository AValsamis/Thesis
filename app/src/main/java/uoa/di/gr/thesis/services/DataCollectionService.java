package uoa.di.gr.thesis.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit.RetrofitError;
import retrofit.client.Response;
import uoa.di.gr.thesis.R;
import uoa.di.gr.thesis.activities.DataCollectionActivity;
import uoa.di.gr.thesis.activities.MainActivity;
import uoa.di.gr.thesis.database.RestApiDispenser;
import uoa.di.gr.thesis.entities.AccelerometerStats;
import uoa.di.gr.thesis.entities.DataPacket;
import uoa.di.gr.thesis.entities.OrientationStats;
import uoa.di.gr.thesis.entities.SimpleResponse;
import uoa.di.gr.thesis.entities.User;
import uoa.di.gr.thesis.utils.CallbacksManager;

/**
 * Created by skand on 3/2/2017.
 */

public class DataCollectionService extends Service implements SensorEventListener {

    private static final int ACCELEROMETER_INTERVAL = 350;
    private static final int ORIENTATION_INTERVAL = 200;
    protected final CallbacksManager callbacksManager = new CallbacksManager();
    private final IBinder mBinder = new LocalBinder();

    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mMagnetic;
    public SensorEventListener mSensorListener ;
    private FileOutputStream fOut = null;
    private PendingIntent pintent;
    private File myFile;
    private long time;
    private User user = new User();
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private DataPacket dataPacket = new DataPacket();
    private ArrayList<AccelerometerStats> accelerometerStatsArrayList = new ArrayList<>();
    private ArrayList<OrientationStats> orientationStatsArrayList = new ArrayList<>();
    Handler h = new Handler();
    boolean isRunning = false;
    boolean toFinishService = false;
    int delay = 10000; //milliseconds
    String o = Integer.toString(new Object().hashCode());
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public DataCollectionService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DataCollectionService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user.setUsername(prefs.getString("username", "nobody"));

        if (!isRunning) {
            isRunning = true;

            collectData();
            Calendar cal = Calendar.getInstance();
            new Thread()
            {
                public void run() {
                    while (true){
                        final CallbacksManager.CancelableCallback<SimpleResponse> callback = callbacksManager.new CancelableCallback<SimpleResponse>() {
                            @Override
                            protected void onSuccess(SimpleResponse response, Response response2) {
                                if (response.getOk()) {
                                    toFinishService = false;
                                }
                                else{
                                    toFinishService = true;
                                    stopSelf();
                                }
                            }
                            @Override
                            protected void onFailure(RetrofitError error) {
                            }
                        };
                        RestApiDispenser.getSimpleApiInstance().getShouldRunService(user.getUsername(), callback);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return mBinder;
    }

    public void collectData()
    {
        try {
                mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                mAccelerometer=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetic=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                mSensorManager.registerListener(this, mMagnetic,  SensorManager.SENSOR_DELAY_NORMAL);
                AlarmManager alarmManager=(AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent2 = new Intent(this.getApplicationContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent2, 0);
                // every 9 minutes we search the last 10 minutes of accelerometer_stats to include
                // falls that may occur between two batches of 10 minutes
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),60000,
                        pendingIntent);

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DataCollectionActivity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(DataCollectionActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        Log.i(o, "Service sensor changed");

        if (toFinishService) unregisterListeners();

        if(accelerometerStatsArrayList.size() > 5)
        {
            dataPacket.setUser(user);
            dataPacket.setAccelerometerStats(accelerometerStatsArrayList);
            dataPacket.setOrientationStats(orientationStatsArrayList);
            final CallbacksManager.CancelableCallback callback = callbacksManager.new CancelableCallback() {

                @Override
                protected void onSuccess(Object o, Response response) {

                }

                @Override
                protected void onSuccess(SimpleResponse response, Response response2) {
                    if (response.getOk()){}
//                        Toast.makeText(getApplicationContext(), "Success! " + response.getResponse(), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), response.getResponse(), Toast.LENGTH_SHORT).show();

                }

                @Override
                protected void onFailure(RetrofitError error) {

                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            };
            RestApiDispenser.getSimpleApiInstance().sendDataPacket(dataPacket,callback);

            Log.i("DATAPACKET: " ,dataPacket.toString());
            dataPacket = new DataPacket();
            accelerometerStatsArrayList = new ArrayList<>();
            orientationStatsArrayList = new ArrayList<>();
        }
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            long curTime=System.currentTimeMillis();
//            if(curTime-time > ACCELEROMETER_INTERVAL)
//            {
            Log.i("TIMES ARE: ",curTime+"  "+time);
            try {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);

                AccelerometerStats accelerometerStats = new AccelerometerStats(Float.toString(x),Float.toString(y),Float.toString(z),new Timestamp(curTime).toString());
                accelerometerStatsArrayList.add(accelerometerStats);
                Log.i("Accelerometer stats: " ,accelerometerStats.toString());
                mLastAccelerometerSet = true;
                time=curTime;
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(DataCollectionActivity.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalStateException ex) {
                Logger.getLogger(DataCollectionActivity.class.getName()).log(Level.SEVERE, null, ex);
            }
//            }
        }
        else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            long curTime=System.currentTimeMillis();

            if(curTime-time > ORIENTATION_INTERVAL) {
                System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
                mLastMagnetometerSet = true;
                time=curTime;
            }
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            OrientationStats orientationStats = new OrientationStats(Float.toString(mOrientation[0]), Float.toString(mOrientation[1]), Float.toString(mOrientation[2]),new Timestamp(System.currentTimeMillis()).toString());
            orientationStatsArrayList.add(orientationStats);
            Log.i("Orientation stats: " ,orientationStats.toString());
            mLastMagnetometerSet = false;
            mLastAccelerometerSet = false;
        }

    }

    public void unregisterListeners()
    {
        mSensorManager.unregisterListener(this);
        isRunning = false;
        new Thread()
        {
            public void run() {
                while (true){
                    final CallbacksManager.CancelableCallback<SimpleResponse> callback = callbacksManager.new CancelableCallback<SimpleResponse>() {
                        @Override
                        protected void onSuccess(SimpleResponse response, Response response2) {
                            if (response.getOk()) {
                                toFinishService = false;
                                collectData();
                                stopSelf();
                            }
                            else{
                                toFinishService = true;
//                                stopSelf();
                            }
                        }
                        @Override
                        protected void onFailure(RetrofitError error) {
                        }
                    };
                    RestApiDispenser.getSimpleApiInstance().getShouldRunService(user.getUsername(), callback);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        AlarmManager alarmManager=(AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
        alarmManager.cancel(pendingIntent);

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        isRunning=false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
