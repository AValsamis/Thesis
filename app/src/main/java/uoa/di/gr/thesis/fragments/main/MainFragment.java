package uoa.di.gr.thesis.fragments.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;
import uoa.di.gr.thesis.R;
import uoa.di.gr.thesis.database.RestApiDispenser;
import uoa.di.gr.thesis.entities.SimpleResponse;
import uoa.di.gr.thesis.entities.User;
import uoa.di.gr.thesis.entities.Wifi;
import uoa.di.gr.thesis.entities.Zone;
import uoa.di.gr.thesis.interfaces.MainFragmentCallbacks;
import uoa.di.gr.thesis.interfaces.RegisterZoneCallbacks;
import uoa.di.gr.thesis.utils.CallbacksManager;
import uoa.di.gr.thesis.utils.RegisterZone;

import static android.content.Context.WIFI_SERVICE;

public class MainFragment extends Fragment  implements RegisterZoneCallbacks, MainFragmentCallbacks {
    public static final String TAG = MainFragment.class.getSimpleName();
    private RegisterZoneCallbacks registerZoneCallback;
    private MainFragmentCallbacks mainFragmentCallback;
    protected final CallbacksManager callbacksManager = new CallbacksManager();
    AppCompatButton collection;
    TextView test;
    boolean started=false;
    public String zoneName = "";
    protected final int SCAN_TIMES=10;
    ProgressDialog progressDialog ;

    RegisterZone mWorkerThread;

    public MainFragment() {
    }

    public static MainFragment newInstance(String title) {
        MainFragment f = new MainFragment();
        Bundle args = new Bundle();
        args.putString(TAG, title);
        f.setArguments(args);
        return (f);
    }

    private static MainFragmentCallbacks sMainFragmentCallbacks = new MainFragmentCallbacks() {
        @Override
        public void startDataCollectionFragment() {

        }
    };


        //Dummy interface to register onDetach
    private static RegisterZoneCallbacks sDummyCallbacks = new RegisterZoneCallbacks() {
        @Override
        public void onGatherWifiList(List<Wifi> wifis, Boolean isSafe) {

        }

        @Override
        public void onRegisterZoneSuccess(String response) {

        }

        @Override
        public void onRegisterZoneFailure(String response) {

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            registerZoneCallback = (RegisterZoneCallbacks) activity;
            mainFragmentCallback = (MainFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Reset the active callback interface to the dummy implementation.
        registerZoneCallback = sDummyCallbacks;
        mainFragmentCallback = sMainFragmentCallbacks;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        progressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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



        FloatingActionButton dangerous = (FloatingActionButton) getActivity().findViewById(R.id.dangerous);
        dangerous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Please enter a name for the zone: ");

                // Set up the input
                final EditText input = new EditText(getActivity());
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
                            Toast.makeText(getContext(), "Please enter a valid name...", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                        else {
                            new AlertDialog.Builder(getActivity()).setMessage("Zone " + zoneName + " will be created.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            initiateRegisterZoneService(false);
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


        FloatingActionButton safe = (FloatingActionButton) getActivity().findViewById(R.id.safe);
        safe.setOnClickListener(new View.OnClickListener()  {

            @Override
            public void onClick(View view) {
//                final String zoneName = "";
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Please enter a name for the zone: ");

                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK",


                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        zoneName = input.getText().toString();
                        if(zoneName.isEmpty())
                        {
                            Toast.makeText(getActivity(), "Please enter a valid name...", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                        else {
                            View view = getActivity().getCurrentFocus();
                            if (view != null) {
                                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                            new AlertDialog.Builder(getActivity()).setMessage("Zone " + zoneName + " will be created.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            initiateRegisterZoneService(true);
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

        collection = (AppCompatButton) getActivity().findViewById(R.id.collection);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = prefs.getString("username", "nobody");

        final CallbacksManager.CancelableCallback<SimpleResponse> callback = callbacksManager.new CancelableCallback<SimpleResponse>() {
            @Override
            protected void onSuccess(SimpleResponse response, Response response2) {
                if (response.getOk()) {
                    started = true;
                    collection.setText("Stop Scanning");
                }
                else{
                    started = false;
                    collection.setText("Start Scanning");
                }
            }
            @Override
            protected void onFailure(RetrofitError error) {
            }
        };
        RestApiDispenser.getSimpleApiInstance().getShouldRunService(username, callback);

        collection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String username = prefs.getString("username", "nobody");
                User user = new User();
                user.setUsername(username);
                if (!started) {
                    final CallbacksManager.CancelableCallback<SimpleResponse> callback2 = callbacksManager.new CancelableCallback<SimpleResponse>() {
                        @Override
                        protected void onSuccess(SimpleResponse response, Response response2) {
                            if (response.getOk()) {
                                collection.setText("Stop Scanning");
                                started = true;
                            }
                        }

                        @Override
                        protected void onFailure(RetrofitError error) {

                        }
                    };

                    RestApiDispenser.getSimpleApiInstance().startScanService(user, callback2);
                }
                else
                {
                     final CallbacksManager.CancelableCallback<SimpleResponse> callback3 = callbacksManager.new CancelableCallback<SimpleResponse>() {
                            @Override
                            protected void onSuccess(SimpleResponse response, Response response2) {
                                if (response.getOk()) {
                                    collection.setText("Start Scanning");
                                    started = false;
                                }
                            }

                            @Override
                            protected void onFailure(RetrofitError error) {

                            }
                        };
                        RestApiDispenser.getSimpleApiInstance().stopScanService(user, callback3);
                    }
                }
        });

        setRetainInstance(true);

        return  v;
    }

    public void initiateRegisterZoneService(Boolean isSafe)
    {
        mWorkerThread = new RegisterZone(getActivity(), isSafe, new Handler(),registerZoneCallback);
        mWorkerThread.start();
        mWorkerThread.prepareHandler();
        mWorkerThread.queueTask();
        progressDialog.setMessage("Saving zone...");
        progressDialog.show();
    }

    @Override
    public void onGatherWifiList(List<Wifi> wifis, Boolean isSafe) {
        final CallbacksManager.CancelableCallback<SimpleResponse> callback2 = callbacksManager.new CancelableCallback<SimpleResponse>() {
            @Override
            protected void onSuccess(SimpleResponse response, Response response2) {
                progressDialog.dismiss();
                if (response.getOk())
                    registerZoneCallback.onRegisterZoneSuccess(response.getResponse());
                else
                    registerZoneCallback.onRegisterZoneFailure(response.getResponse());
            }

            @Override
            protected void onFailure(RetrofitError error) {
                progressDialog.dismiss();
                registerZoneCallback.onRegisterZoneFailure(error.getLocalizedMessage());
            }
        };

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final Zone zone = new Zone();
        zone.setFriendlyName(zoneName);
        if (isSafe)
            zone.setIsSafe(1);
        else
            zone.setIsSafe(0);
        User user = new User();
        user.setUsername(prefs.getString("username", "nobody"));
        zone.setUser(user);
        RestApiDispenser.getSimpleApiInstance().registerZone(wifis, zone, callback2);

    }

    @Override
    public void onRegisterZoneSuccess(String response) {
        Toast.makeText(getActivity(),"Success! " + response,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRegisterZoneFailure(String response) {
        Toast.makeText(getActivity(),"Failure! " + response,Toast.LENGTH_LONG).show();

    }

    @Override
    public void startDataCollectionFragment() {

    }
}




