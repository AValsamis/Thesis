package uoa.di.gr.thesis.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;
import uoa.di.gr.thesis.R;
import uoa.di.gr.thesis.database.RestApiDispenser;
import uoa.di.gr.thesis.database.SimpleApi;
import uoa.di.gr.thesis.entities.SimpleResponse;
import uoa.di.gr.thesis.entities.User;
import uoa.di.gr.thesis.entities.Wifi;
import uoa.di.gr.thesis.entities.Zone;
import uoa.di.gr.thesis.utils.CallbacksManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected final CallbacksManager callbacksManager = new CallbacksManager();
    public String zoneName = "";
    protected final int SCAN_TIMES=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                });
                builder.show();
            }
        }
        
        FloatingActionButton dangerous = (FloatingActionButton) findViewById(R.id.dangerous);
        dangerous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

//                final String zoneName = "";
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Please enter a name for the zone: ");

                // Set up the input
                final EditText input = new EditText(MainActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        zoneName = input.getText().toString();
                        if(zoneName.isEmpty())
                        {
                            Toast.makeText(getApplicationContext(), "Please enter a valid name...", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                        else {
                            new AlertDialog.Builder(MainActivity.this).setMessage("Zone " + zoneName + " will be created.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Toast.makeText(getApplicationContext(), "Please stay still and wait while zone is being registered...", Toast.LENGTH_LONG);
                                    final CallbacksManager.CancelableCallback<SimpleResponse> callback2 = callbacksManager.new CancelableCallback<SimpleResponse>() {
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



                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                                    Zone zone = new Zone();
                                    zone.setFriendlyName(zoneName);
                                    zone.setIsSafe(0);
                                    User user = new User();
                                    user.setUsername(prefs.getString("username", "nobody"));
                                    zone.setUser(user);

                                    // TODO use asynctask here

                                    List<Wifi> wifis = new ArrayList<Wifi>();
                                    List <String> wifiNames = new ArrayList();
                                    for(int i = 0 ; i < SCAN_TIMES; i++) {
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        String connectivity_context = WIFI_SERVICE;

                                        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(connectivity_context);

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
                                    apiFor(callback2).registerZone(wifis, zone, callback2);

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


        FloatingActionButton safe = (FloatingActionButton) findViewById(R.id.safe);
        safe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

//                final String zoneName = "";
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Please enter a name for the zone: ");

                // Set up the input
                final EditText input = new EditText(MainActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        zoneName = input.getText().toString();
                        if(zoneName.isEmpty())
                        {
                            Toast.makeText(getApplicationContext(), "Please enter a valid name...", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                        else {
                            new AlertDialog.Builder(MainActivity.this).setMessage("Zone " + zoneName + " will be created.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Please stay still and wait while zone is being registered...", Toast.LENGTH_LONG);
                                    final CallbacksManager.CancelableCallback<SimpleResponse> callback2 = callbacksManager.new CancelableCallback<SimpleResponse>() {
                                        @Override
                                        protected void onSuccess(SimpleResponse response, Response response2) {
                                            if (response.getIsOk())
                                                Toast.makeText(getApplicationContext(), "Success! " + response.getResponse(), Toast.LENGTH_SHORT).show();
                                            else
                                                Toast.makeText(getApplicationContext(), response.getResponse(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        protected void onFailure(RetrofitError error) {

                                            Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    };


                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                                    Zone zone = new Zone();
                                    zone.setFriendlyName(zoneName);
                                    zone.setIsSafe(1);
                                    User user = new User();
                                    user.setUsername(prefs.getString("username", "nobody"));
                                    zone.setUser(user);

                                    // TODO use asynctask here

                                    List<Wifi> wifis = new ArrayList<Wifi>();
                                    List <String> wifiNames = new ArrayList();
                                    for(int i = 0 ; i < SCAN_TIMES; i++) {
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        String connectivity_context = WIFI_SERVICE;

                                        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(connectivity_context);

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
                                    apiFor(callback2).registerZone(wifis, zone, callback2);

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });

        FloatingActionButton findZone = (FloatingActionButton) findViewById(R.id.findZone);
        findZone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Calculating zone...", Toast.LENGTH_LONG);
                // TODO use asynctask here
                List<Wifi> wifis = new ArrayList<Wifi>();
                List <String> wifiNames = new ArrayList();
                for(int i = 0 ; i < SCAN_TIMES; i++) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String connectivity_context = WIFI_SERVICE;

                    final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(connectivity_context);

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
                String zone = apiFor(null).getZone(wifis);
                Toast.makeText(getApplicationContext(), "Elderly is in zone with name: " + zone, Toast.LENGTH_SHORT).show();
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

//    protected void presentResults(List<ScanResult> wifiList){
//        TextView textView = (TextView) findViewById(R.id.textViewAxtarmas);
//        textView.setText("");
//        String s = "";
//        // Level of a Scan Result
//        for (ScanResult scanResult : wifiList) {
//            int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
//            //s=s.concat("Level is " + level + " out of 5\n");
//            s=s.concat(scanResult.SSID +": Level is " + scanResult.BSSID +" "+scanResult.level + " out of 5");
//            System.out.println(scanResult.SSID +": Level is " + scanResult.BSSID +" "+scanResult.level + " out of 5");
//
//        }
//
//
//        textView.setText(s);
//    }
    protected SimpleApi apiFor(CallbacksManager.CancelableCallback<?> callback) {
        if(callback!=null) {
            callbacksManager.addCallback(callback);
        }
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
