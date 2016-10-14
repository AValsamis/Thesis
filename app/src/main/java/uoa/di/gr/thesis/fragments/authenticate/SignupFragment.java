package uoa.di.gr.thesis.fragments.authenticate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import uoa.di.gr.thesis.interfaces.SignupFragmentCallbacks;


public class SignupFragment extends DialogFragment implements SignupFragmentCallbacks{
    public static final String TAG = SignupFragment.class.getSimpleName();
    private SignupFragmentCallbacks mCallback;

    EditText _nameText;
    EditText _emailText;
    EditText _passwordText;
    Button _signupButton;
    TextView _loginLink;

    public SignupFragment() {
    }

    public static SignupFragment newInstance(String title) {
        SignupFragment f = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(TAG, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_signup, container, false);

        _nameText = (EditText) v.findViewById(R.id.input_name);
        _emailText = (EditText) v.findViewById(R.id.input_email);
        _passwordText = (EditText) v.findViewById(R.id.input_password);
        _signupButton = (Button) v.findViewById(R.id.btn_signup);
        _loginLink = (TextView) v.findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove soft input if it's present
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                mCallback.clickedLogin();
            }
        });
        setRetainInstance(true);
        return v;
    }

    //Dummy interface to register onDetach
    private static SignupFragmentCallbacks sDummyCallbacks = new SignupFragmentCallbacks() {
        @Override
        public void clickedLogin() {

        }

        @Override
        public void onSignupSuccess() {

        }
    };
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (SignupFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement signupFragmentCallbacks");
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
        _nameText.setFocusableInTouchMode(true);
        _nameText.requestFocus();
    }
    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        mCallback.onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    public void clickedLogin() {

    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);

        //   setResult(RESULT_OK, null);
        //  finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getActivity().getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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


}