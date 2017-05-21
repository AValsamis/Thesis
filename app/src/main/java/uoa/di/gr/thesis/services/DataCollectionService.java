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
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit.RetrofitError;
import retrofit.client.Response;
import uoa.di.gr.thesis.activities.DataCollectionActivity;
import uoa.di.gr.thesis.database.RestApiDispenser;
import uoa.di.gr.thesis.entities.AccelerometerStats;
import uoa.di.gr.thesis.entities.DataPacket;
import uoa.di.gr.thesis.entities.SimpleResponse;
import uoa.di.gr.thesis.entities.User;
import uoa.di.gr.thesis.utils.CallbacksManager;

public class DataCollectionService extends Service implements SensorEventListener {

    protected final CallbacksManager callbacksManager = new CallbacksManager();
    private final IBinder mBinder = new LocalBinder();

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private User user = new User();
    private float[] mLastAccelerometer = new float[3];
    private DataPacket dataPacket = new DataPacket();
    private ArrayList<AccelerometerStats> accelerometerStatsArrayList = new ArrayList<>();
    public Handler handler = null;
    public static Runnable runnable = null;
    boolean isRunning = false;
    boolean hasInstantiatedListeners = false;
    private final static int PACKET_SIZE = 20;
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
            handler = new Handler();
            runnable = new Runnable() {
                public void run() {
                    final CallbacksManager.CancelableCallback<SimpleResponse> callback = callbacksManager.new CancelableCallback<SimpleResponse>() {
                        @Override
                        protected void onSuccess(SimpleResponse response, Response response2) {
                            if (response.getOk()) {
                                if (!hasInstantiatedListeners)
                                {
                                    hasInstantiatedListeners =true;
                                    collectData();
                                }
                            } else {
                                if (hasInstantiatedListeners) {
                                    unregisterListeners();
                                    hasInstantiatedListeners =false;
                                }
                            }
                        }

                        @Override
                        protected void onFailure(RetrofitError error) {
                        }
                    };
                    RestApiDispenser.getSimpleApiInstance().getShouldRunService(user.getUsername(), callback);

//                    handler.postDelayed(runnable, delay);
                }
            };
            handler.postDelayed(runnable, 15000);
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void collectData()
    {
        try {
                mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                mAccelerometer=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

                // alarmManager for zone detection
                AlarmManager zoneAlarm=(AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent3 = new Intent(this.getApplicationContext(), ZoneAlarmReceiver.class);
                PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent3, 0);
                zoneAlarm.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),60000, pendingIntent2);


        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DataCollectionActivity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(DataCollectionActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
//        Log.i(o, "Service sensor changed");

        if(accelerometerStatsArrayList.size() > PACKET_SIZE)
        {
            dataPacket.setUser(user);
            dataPacket.setAccelerometerStats(accelerometerStatsArrayList);
            final CallbacksManager.CancelableCallback callback = callbacksManager.new CancelableCallback<SimpleResponse>() {
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

//            Log.i("DATAPACKET: " ,dataPacket.toString());
            dataPacket = new DataPacket();
            accelerometerStatsArrayList = new ArrayList<>();
        }
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            long curTime=System.currentTimeMillis();
            try {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);

                AccelerometerStats accelerometerStats = new AccelerometerStats(Float.toString(x),Float.toString(y),Float.toString(z),new Timestamp(curTime).toString());
                accelerometerStatsArrayList.add(accelerometerStats);
//                Log.i("Accelerometer stats: " ,accelerometerStats.toString());
            } catch (IllegalArgumentException | IllegalStateException ex) {
                ex.printStackTrace();
                Logger.getLogger(DataCollectionActivity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void unregisterListeners()
    {
        mSensorManager.unregisterListener(this);
        isRunning = false;
        Intent intent = new Intent(this, ZoneAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
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
