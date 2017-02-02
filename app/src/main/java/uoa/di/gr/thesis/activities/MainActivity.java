package uoa.di.gr.thesis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import uoa.di.gr.thesis.R;
import uoa.di.gr.thesis.entities.Wifi;
import uoa.di.gr.thesis.fragments.main.MainFragment;
import uoa.di.gr.thesis.interfaces.MainFragmentCallbacks;
import uoa.di.gr.thesis.interfaces.RegisterZoneCallbacks;
import uoa.di.gr.thesis.utils.CallbacksManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RegisterZoneCallbacks, MainFragmentCallbacks {

    private static final String TAG = "[AuthenticationActivity]";
    final FragmentManager fm = getSupportFragmentManager();

    protected final CallbacksManager callbacksManager = new CallbacksManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setContentView(R.layout.activity_main);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager();

        final FragmentTransaction ft = fm.beginTransaction();
        MainFragment mainFragment = (MainFragment) fm.findFragmentByTag(MainFragment.TAG);
        if (mainFragment == null) {
            System.out.println(TAG + "Create Login Instance");
            mainFragment = MainFragment.newInstance(MainFragment.TAG);
            ft.replace(R.id.frame_container, mainFragment, MainFragment.TAG);
        }
        ft.commit();
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
            Intent intent = new Intent(this, ZoneActivity.class);
            startActivity(intent);
            this.finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onGatherWifiList(List<Wifi> wifis) {
        MainFragment articleFrag = (MainFragment)
                getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);

        if (articleFrag != null) {
            articleFrag.onGatherWifiList(wifis);
        }
    }

    @Override
    public void onRegisterZoneSuccess(String response) {
        MainFragment articleFrag = (MainFragment)
                getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);

        if (articleFrag != null) {
            articleFrag.onRegisterZoneSuccess(response);
        }
    }

    @Override
    public void onRegisterZoneFailure(String response) {
        MainFragment articleFrag = (MainFragment)
                getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);

        if (articleFrag != null) {
            articleFrag.onRegisterZoneFailure(response);
        }
    }

    @Override
    public void startDataCollectionFragment() {
        Intent dialogIntent = new Intent(this, DataCollectionActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
    }
}
