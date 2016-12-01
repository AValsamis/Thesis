package uoa.di.gr.thesis.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
import uoa.di.gr.thesis.database.RestApiDispenser;
import uoa.di.gr.thesis.database.SimpleApi;
import uoa.di.gr.thesis.entities.AccelerometerStats;
import uoa.di.gr.thesis.entities.DataPacket;
import uoa.di.gr.thesis.entities.OrientationStats;
import uoa.di.gr.thesis.entities.SimpleResponse;
import uoa.di.gr.thesis.entities.User;
import uoa.di.gr.thesis.utils.CallbacksManager;

/**
 * Created by skand on 11/20/2016.
 */

public class DataCollectionActivity extends Activity implements SensorEventListener {

    private static final int ACCELEROMETER_INTERVAL = 200;
    private static final int ORIENTATION_INTERVAL = 200;
    protected final CallbacksManager callbacksManager = new CallbacksManager();

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

    @Override
    public void onCreate(Bundle icicle) {

        Log.i("INSIDE DATACOLLECTION","Hi");
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user.setUsername(prefs.getString("username", "nobody"));

        collectData();
        Calendar cal = Calendar.getInstance();

//      Intent intent = new Intent(this, DataSendingService.class);
//      pintent = PendingIntent.getService(this, 1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10*1000, pintent);

    }

    public void collectData()
    {
        try {

            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            mAccelerometer=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mMagnetic=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DataCollectionActivity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(DataCollectionActivity.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetic,  SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);

        super.onPause();

    }
    public void onStop(View view) {
        mSensorManager.unregisterListener(this);
        mSensorListener = null;
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if(accelerometerStatsArrayList.size() > 5)
        {
            dataPacket.setUser(user);
            dataPacket.setAccelerometerStats(accelerometerStatsArrayList);
            dataPacket.setOrientationStats(orientationStatsArrayList);
            final CallbacksManager.CancelableCallback<SimpleResponse> callback = callbacksManager.new CancelableCallback<SimpleResponse>() {
                @Override
                protected void onSuccess(SimpleResponse response, Response response2) {
                    if (response.getIsOk())
                        Toast.makeText(getApplicationContext(), "Success! " + response.getResponse(), Toast.LENGTH_SHORT).show();
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
            if(curTime-time > ACCELEROMETER_INTERVAL)
            {
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
            }
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

    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


//    public void stopCollection(View view)
//    {
//        try {
//            TextView tv = (TextView) this.findViewById(R.id.stopCollection);
//            tv.setVisibility(View.INVISIBLE);
//            showToast("Stopping the collection...");
//            mSensorManager.unregisterListener(this);
//            mSensorListener = null;
//            this.unregisterReceiver(this.PowerConnectionReceiver);
//            writeToFile("</logs>\n".getBytes(),fOut);
//            try {
//                fOut.close();
//            }
//            catch (IOException ex) {
//                Logger.getLogger(DataCollection.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            Intent intent = new Intent(this, DataSendingService.class);
//            PendingIntent pintent = PendingIntent.getService(this, 1234567, intent, 0);
//            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//            alarm.cancel(pintent);
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DataCollection.this);
//                    final SharedPreferences.Editor editor = prefs.edit();
//
//                    Set <String> servers = new HashSet();
//                    servers = prefs.getStringSet("servers", servers);
//
//                    boolean found=false;
//                    if(!isServerAvailable(server))
//                    {
//                        for(String ser : servers)
//                        {
//                            if(isServerAvailable(ser))
//                            {
//                                found=true;
//                                servers.add(server);
//                                server=ser;
//                                editor.putString("current_server", ser);
//                                servers.remove(ser);
//                                editor.commit();
//                            }
//                        }
//                        if(!found)
//                        {
//                            // Nothing done here yet...
//                            showToast("No servers are available at the moment...");
//                            finish();
//                        }
//
//                    }
//                    String file=readFromFile();
//                    HttpClient client = new DefaultHttpClient();
//                    HttpPost post = new HttpPost(server+"master/add");
//
//                    try
//                    {
//                        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
//                        nameValuePairs.add(new BasicNameValuePair("id",username));
//                        nameValuePairs.add(new BasicNameValuePair("data",file));
//                        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//                        HttpResponse response;
//                        response = client.execute(post);
//                        String result = EntityUtils.toString(response.getEntity());
//                    }
//                    catch (IOException ex)
//                    {
//                        Logger.getLogger(DataCollection.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    finish();
//                }
//
//            };
//            new Thread(runnable).start();
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(DataCollection.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalStateException ex) {
//            Logger.getLogger(DataCollection.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(DataCollectionActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "To exit the app you must first stop the data collection. To continue while app is on the background, just press the home button.", Toast.LENGTH_LONG).show();
    }

}
