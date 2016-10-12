package uoa.di.gr.thesis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;
import uoa.di.gr.thesis.database.RestApiDispenser;
import uoa.di.gr.thesis.database.SimpleApi;
import uoa.di.gr.thesis.entities.AccelerometerStats;
import uoa.di.gr.thesis.utils.CallbacksManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected final CallbacksManager callbacksManager = new CallbacksManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

             /*   IntentFilter i = new IntentFilter();
                i.addAction (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(new BroadcastReceiver(){
                    public void onReceive(Context c, Intent i){
                        // Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event occurs
                        WifiManager w = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
                        presentResults(w.getScanResults()); // your method to handle Scan results
                        if (true) w.startScan(); // relaunch scan immediately
                        else { /* Schedule the scan to be run later here }
                    }
                }, i );


                // Launch  wifiscanner the first time here (it will call the broadcast receiver above)
                WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                boolean a = wm.startScan();




            }});*/
                final CallbacksManager.CancelableCallback<String> callback = callbacksManager.new CancelableCallback<String>() {
                    @Override
                    protected void onSuccess(String response, Response response2) {
                        Toast.makeText(getApplicationContext(), "Success! " + response, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onFailure(RetrofitError error) {
                        Toast.makeText(getApplicationContext(), "Process failed, please try later." + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                };
                apiFor(callback).registerUser("Sevle","ang","val","1234", callback);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void presentResults(List<ScanResult> wifiList){
        TextView textView = (TextView) findViewById(R.id.textViewAxtarmas);
        textView.setText("");
        String s = "";
        // Level of a Scan Result
        for (ScanResult scanResult : wifiList) {
            int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
            //s=s.concat("Level is " + level + " out of 5\n");
            s=s.concat(scanResult.SSID +": Level is " + scanResult.BSSID +" "+scanResult.level + " out of 5");
            System.out.println(scanResult.SSID +": Level is " + scanResult.BSSID +" "+scanResult.level + " out of 5");

        }


        textView.setText(s);
    }
    protected SimpleApi apiFor(CallbacksManager.CancelableCallback<?> callback) {
        callbacksManager.addCallback(callback);
        // return your retrofit API
        return RestApiDispenser.createService(SimpleApi.class, SimpleApi.BASE_URL, "mobile_app", "mobile!@#");
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
