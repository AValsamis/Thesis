package uoa.di.gr.thesis.fragments.authenticate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit.RetrofitError;
import retrofit.client.Response;
import uoa.di.gr.thesis.R;
import uoa.di.gr.thesis.database.RestApiDispenser;
import uoa.di.gr.thesis.database.SimpleApi;
import uoa.di.gr.thesis.entities.SimpleResponse;
import uoa.di.gr.thesis.interfaces.LoginFragmentCallbacks;
import uoa.di.gr.thesis.utils.CallbacksManager;


//http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
public class LoginFragment extends Fragment implements LoginFragmentCallbacks {
    public static final String TAG = LoginFragment.class.getSimpleName();
    private static final int REQUEST_SIGNUP = 0;
    private LoginFragmentCallbacks mCallback;
    protected final CallbacksManager callbacksManager = new CallbacksManager();

    EditText _nameText;
    EditText _usernameText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;

    public LoginFragment() {
    }

    public static LoginFragment newInstance(String title) {
        LoginFragment f = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(TAG, title);
        f.setArguments(args);
        return (f);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_login, container, false);


        _usernameText = (EditText) v.findViewById(R.id.input_username);
        _passwordText = (EditText) v.findViewById(R.id.input_password);
        _loginButton = (Button) v.findViewById(R.id.btn_login);
        _signupLink = (TextView) v.findViewById(R.id.link_signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                mCallback.clickedRegister();
            }
        });
        setRetainInstance(true);
        return v;
    }

    //Dummy interface to register onDetach
    private static LoginFragmentCallbacks sDummyCallbacks = new LoginFragmentCallbacks() {
        @Override
        public void clickedRegister() {

        }

        @Override
        public void onLoginSuccess() {

        }
    };

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed("Correct errors in form and try again");
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.putBoolean("log",true);
        editor.apply();

        final CallbacksManager.CancelableCallback<SimpleResponse> callback = callbacksManager.new CancelableCallback<SimpleResponse>() {
            @Override
            protected void onSuccess(SimpleResponse response, Response response2) {
                System.out.println(response.getResponse());
                if (response.getIsOk())
                    onLoginSuccess();
                else
                    onLoginFailed(response.getResponse());
                progressDialog.dismiss();
            }
            @Override
            protected void onFailure(RetrofitError error) {
                onLoginFailed(error.getResponse().getReason());
                progressDialog.dismiss();
            }
        };
        RestApiDispenser.getSimpleApiInstance().loginUser(username,password, callback);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (LoginFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Reset the active callback interface to the dummy implementation.
        mCallback = sDummyCallbacks;

    }

    @Override
    public void onResume(){
        super.onResume();
        _usernameText.setFocusableInTouchMode(true);
        _usernameText.requestFocus();
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        mCallback.onLoginSuccess();
    }

    public void onLoginFailed(String reason) {
        Toast.makeText(getActivity(), "Login failed: "+ reason, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

      /*  if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _usernameText.setError("enter a valid email address");
            valid = false;
        } else {
            _usernameText.setError(null);
        }*/
        if (username.isEmpty() || username.length() < 4 || username.length() > 10) {
            _usernameText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    public void clickedRegister() {
    }


}
