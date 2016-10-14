package uoa.di.gr.thesis.fragments.authenticate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
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

import uoa.di.gr.thesis.R;
import uoa.di.gr.thesis.interfaces.LoginFragmentCallbacks;


//http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
//http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
//http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
public class LoginFragment extends Fragment implements LoginFragmentCallbacks {
    public static final String TAG = LoginFragment.class.getSimpleName();
    private static final int REQUEST_SIGNUP = 0;
    private LoginFragmentCallbacks mCallback;

    EditText _nameText;
    EditText _emailText;
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


        _emailText = (EditText) v.findViewById(R.id.input_email);
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
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        mCallback.onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
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
        _emailText.setFocusableInTouchMode(true);
        _emailText.requestFocus();
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);

    }

    public void onLoginFailed() {
        Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
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
