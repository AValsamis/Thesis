package uoa.di.gr.thesis.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import uoa.di.gr.thesis.R;
import uoa.di.gr.thesis.database.RestApiDispenser;
import uoa.di.gr.thesis.fragments.authenticate.LoginFragment;
import uoa.di.gr.thesis.fragments.authenticate.SignupFragment;
import uoa.di.gr.thesis.interfaces.LoginFragmentCallbacks;
import uoa.di.gr.thesis.interfaces.SignupFragmentCallbacks;


/**
 * Created by Sevle on 7/14/2015.
 */
public class AuthenticationActivity  extends AppCompatActivity implements LoginFragmentCallbacks, SignupFragmentCallbacks {
    private static final String TAG="[AuthenticationActivity]";
    final FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);
        // TODO check
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent intent;
        if (preferences.getBoolean("log", false)) {
            String username=preferences.getString("username","nobody");
            Log.i("USERNAME",username);
            boolean isElderly = RestApiDispenser.getSimpleApiInstance().isElderly(username);
            if(isElderly) {
                intent = new Intent(this, MainActivity2.class);
            }
            else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            this.finish();

        }

        getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        LoginFragment loginFragment = (LoginFragment) fm.findFragmentByTag(LoginFragment.TAG);
        if (loginFragment == null) {
            System.out.println(TAG + "Create Login Instance");
            loginFragment = LoginFragment.newInstance(LoginFragment.TAG);
            ft.replace(R.id.frame_container2, loginFragment, LoginFragment.TAG);
        }
        ft.commit();

    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (fm.getBackStackEntryCount() != 0) {
            fm.popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    //FRAGMENT "LOGIN" CALLBACKS
    @Override
    public void clickedRegister() {
        final FragmentTransaction ft = fm.beginTransaction();
        SignupFragment signupFragment = (SignupFragment) fm.findFragmentByTag(SignupFragment.TAG);
        if (signupFragment == null) {
            System.out.println(TAG + "Create SignupFragment Instance");
            signupFragment = SignupFragment.newInstance(SignupFragment.TAG);
            ft.replace(R.id.frame_container2, signupFragment, SignupFragment.TAG);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onLoginSuccess() {
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username=preferences.getString("username","nobody");
        boolean isElderly = RestApiDispenser.getSimpleApiInstance().isElderly(username);
        Intent intent;
        if(isElderly) {
            intent = new Intent(this, MainActivity2.class);
        }
        else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        this.finish();

    }

    //FRAGMENT "REGISTER" CALLBACKS
    public void clickedLogin() {
        if (fm.getBackStackEntryCount() != 0) {
            fm.popBackStack();
        }
        else {
            final FragmentTransaction ft = fm.beginTransaction();
            LoginFragment loginFragment = (LoginFragment) fm.findFragmentByTag(LoginFragment.TAG);
            if (loginFragment == null) {
                System.out.println(TAG + "Create Login Instance");
                loginFragment = LoginFragment.newInstance(LoginFragment.TAG);
                ft.replace(R.id.frame_container2, loginFragment, LoginFragment.TAG);
                ft.addToBackStack(null);
                ft.commit();
            }
        }
    }

    @Override
    public void onSignupSuccess() {
        Toast.makeText(AuthenticationActivity.this, "You have successfully signed up, please login", Toast.LENGTH_SHORT).show();
        clickedLogin();
    }
}
