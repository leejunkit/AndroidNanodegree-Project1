package co.x22media.popularmovies.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import co.x22media.popularmovies.R;
import co.x22media.popularmovies.adapters.MovieGridAdapter;
import co.x22media.popularmovies.models.Movie;
import co.x22media.popularmovies.provider.MovieProvider;
import co.x22media.popularmovies.tasks.GetMoviesAsyncTask;

/**
 * Created by kit on 21/10/15.
 */
public class GridViewFragment extends Fragment
        implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = GridViewFragment.class.getSimpleName();

    private TextView mErrorView;
    private GridView mGridView;
    private MovieGridAdapter mAdapter;

    private SharedPreferences mPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;
    //private EndlessScrollListener mEndlessScrollListener;
    private BroadcastReceiver mInternetConnectivityStateListener;

    // Ugly stateful flag to indicate if we should reload data when Sort changes.
    // For some reason, if we initiate the reload in the preference
    // change event handler, it screws up the scroll
    // position of the GridView.


    public GridViewFragment() {
    }

    /*
        Fragment Lifecycle
     */

    @Override
    public void onDestroy(){
        super.onDestroy();
        mPrefs.unregisterOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        // register a broadcast receiver to check Internet connectivity
        registerBroadcastReceiverForInternetConnectivity();

        // register a listener for shared preference changes
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(LOG_TAG, "Preferences changed! Should reload data set.");
            }
        };

        mPrefs.registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onPause() {
        unregisterBroadcastReceiverForInternetConnectivity();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        // pull out the error view
        mErrorView = (TextView) rootView.findViewById(R.id.error_textview);

        // pull out the GridView
        mGridView = (GridView) rootView.findViewById(R.id.gridView);

        // set the adapter
        mAdapter = new MovieGridAdapter(getActivity(), null, 0);
        mGridView.setAdapter(mAdapter);

        // set the onItemClick event
        mGridView.setOnItemClickListener(this);

        return rootView;
    }

    /*
        Event handlers
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // fragments are supposed to be reusable views, so we put the
        // showMovieDetail method in the Activity instead
       // MovieGridActivity activity = (MovieGridActivity) getActivity();
        //activity.showDetailViewForMovie(mAdapter.getItem(position));
    }

    private void registerBroadcastReceiverForInternetConnectivity() {
        mInternetConnectivityStateListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "Connectivity state changed!");
                if (null != intent.getExtras()) {
                    ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo ni = cm.getActiveNetworkInfo();
                    if (null != ni) {
                        Log.d(LOG_TAG, "We have an active network...");
                        if (ni.isConnected()) {
                            Log.d(LOG_TAG, "And the active network is connected.");
                        }

                        else {
                            Log.d(LOG_TAG, "But the active network is not connected.");
                        }
                    }

                    else {
                        Log.d(LOG_TAG, "We have no connected networks.");
                    }
                }
            }
        };

        getActivity().registerReceiver(mInternetConnectivityStateListener,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void unregisterBroadcastReceiverForInternetConnectivity() {
        getActivity().unregisterReceiver(mInternetConnectivityStateListener);
    }

    private Boolean loadMoviesAtPage(int page) {
        Log.d(LOG_TAG, "Load movies at page " + page);

        new GetMoviesAsyncTask(getActivity(), page, new GetMoviesAsyncTask.GetMoviesTaskCallback() {
            @Override
            public void onTaskDone(Exception e, Movie[] movies) {
                if (null != e) {
                    Log.w(LOG_TAG, "Exception occurred attempting to communicate with API.", e);
                }
            }
        }).execute();

        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // instantiate the loader
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieProvider.Movie.KEY_POPULARITY + " DESC";
        Uri moviesUri = MovieProvider.getMovieDirUri();

        return new CursorLoader(getActivity(), moviesUri, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            // we have no data
            Log.d(LOG_TAG, "Cursor returned no data!");
            loadMoviesAtPage(1);
        }

        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

     /*
        Fragment state management
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
