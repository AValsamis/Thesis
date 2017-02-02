package uoa.di.gr.thesis.fragments.zones;

/**
 * Created by Angelos on 11/21/2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import retrofit.RetrofitError;
import uoa.di.gr.thesis.database.RestApiDispenser;
import uoa.di.gr.thesis.database.SimpleApi;
import uoa.di.gr.thesis.entities.Zone;
import uoa.di.gr.thesis.utils.CallbacksManager;

public  class ZoneFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Zone>> {

        protected final CallbacksManager callbacksManager = new CallbacksManager();

        private static final String IS_SAFE = "issafe";
        static Boolean issafe;
        ZoneArray mAdapter;


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ZoneFragment newInstance(Boolean isSafe) {

            ZoneFragment fragment = new ZoneFragment();
            Bundle args = new Bundle();
            args.putBoolean(IS_SAFE, isSafe);
            fragment.setArguments(args);
            return fragment;
        }




        @Override
        public void onActivityCreated(Bundle savedInstanceState) {

            super.onActivityCreated(savedInstanceState);
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                issafe = bundle.getBoolean(IS_SAFE);
            }
            // Create an empty adapter we will use to display the loaded data.
            mAdapter = new ZoneArray(getActivity());
            setListAdapter(mAdapter);
            // Start out with a progress indicator.
            setListShown(false);
            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(0, null, this);
        }
        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            // Insert desired behavior here.
            Log.i("DataListFragment", "Item clicked: " + id);
        }

        @Override
        public Loader<List<Zone>> onCreateLoader(int arg0, Bundle arg1) {
            System.out.println("GroupFragment.onCreateLoader");
            return new DataListLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<Zone>> arg0, List<Zone> data) {
            mAdapter.setData(data);
            System.out.println("GroupFragment.onLoadFinished");
            // The list should now be shown.
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Zone>> arg0) {
            mAdapter.setData(null);
        }


        public static class DataListLoader extends AsyncTaskLoader<List<Zone>> {

            List<Zone> mModels;

            public DataListLoader(Context context) {
                super(context);
            }

            @Override
            public List<Zone> loadInBackground() {

                System.out.println("GroupFragmentAsyncTask.loadInBackground");
                SimpleApi simpleApi = RestApiDispenser.getSimpleApiInstance();
                List<Zone> zones = null;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
                String username=prefs.getString("username", "nobody");

                try {
                    if(issafe)
                        zones = simpleApi.getSafeZones(username);
                    else
                        zones = simpleApi.getDangerZones(username);
                }catch(RetrofitError e)
                {
                    return null;
                }

                return zones;
            }


            /**
             * Called when there is new data to deliver to the client.  The
             * super class will take care of delivering it; the implementation
             * here just adds a little more logic.
             */
            @Override public void deliverResult(List<Zone> listOfData) {
                if (isReset()) {
                    // An async query came in while the loader is stopped.  We
                    // don't need the result.
                    if (listOfData != null) {
                        onReleaseResources(listOfData);
                    }
                }
                List<Zone> oldApps = listOfData;
                mModels = listOfData;

                if (isStarted()) {
                    // If the Loader is currently started, we can immediately
                    // deliver its results.
                    super.deliverResult(listOfData);
                }

                // At this point we can release the resources associated with
                // 'oldApps' if needed; now that the new result is delivered we
                // know that it is no longer in use.
                if (oldApps != null) {
                    onReleaseResources(oldApps);
                }
            }

            /**
             * Handles a request to start the Loader.
             */
            @Override protected void onStartLoading() {
                if (mModels != null) {
                    // If we currently have a result available, deliver it
                    // immediately.
                    deliverResult(mModels);
                }


                if (takeContentChanged() || mModels == null) {
                    // If the data has changed since the last time it was loaded
                    // or is not currently available, start a load.
                    forceLoad();
                }
            }

            /**
             * Handles a request to stop the Loader.
             */
            @Override protected void onStopLoading() {
                // Attempt to cancel the current load task if possible.
                cancelLoad();
            }

            /**
             * Handles a request to cancel a load.
             */
            @Override public void onCanceled(List<Zone> apps) {
                super.onCanceled(apps);

                // At this point we can release the resources associated with 'apps'
                // if needed.
                onReleaseResources(apps);
            }

            /**
             * Handles a request to completely reset the Loader.
             */
            @Override protected void onReset() {
                super.onReset();

                // Ensure the loader is stopped
                onStopLoading();

                // At this point we can release the resources associated with 'apps'
                // if needed.
                if (mModels != null) {
                    onReleaseResources(mModels);
                    mModels = null;
                }
            }

            /**
             * Helper function to take care of releasing resources associated
             * with an actively loaded data set.
             */
            protected void onReleaseResources(List<Zone> apps) {}
        }
    }
