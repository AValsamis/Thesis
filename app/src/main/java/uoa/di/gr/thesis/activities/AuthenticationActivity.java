package uoa.di.gr.thesis.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import uoa.di.gr.thesis.R;
import uoa.di.gr.thesis.fragments.authenticate.LoginFragment;
import uoa.di.gr.thesis.fragments.authenticate.SignupFragment;
import uoa.di.gr.thesis.interfaces.LoginFragmentCallbacks;
import uoa.di.gr.thesis.interfaces.SignupFragmentCallbacks;

public class AuthenticationActivity  extends AppCompatActivity implements LoginFragmentCallbacks, SignupFragmentCallbacks {
    private static final String TAG="[AuthenticationActivity]";
    final FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent intent;
        if (preferences.getBoolean("log", false)) {
            Boolean isElderly=preferences.getBoolean("iselderly",false);

            if(isElderly) {
                startElderlyService();
            }
            else {
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

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
        Boolean isElderly=preferences.getBoolean("iselderly",false);

        Intent intent;
        if(isElderly) {
            startElderlyService();
        }
        else {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

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

    void startElderlyService(){
        Intent dialogIntent = new Intent(this, DataCollectionActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
        finish();
    }

    @Override
    public void onSignupSuccess() {
        Toast.makeText(AuthenticationActivity.this, "You have successfully signed up, please login", Toast.LENGTH_SHORT).show();
        clickedLogin();
    }
}
