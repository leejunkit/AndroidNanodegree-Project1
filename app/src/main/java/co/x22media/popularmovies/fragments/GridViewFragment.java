package co.x22media.popularmovies.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import co.x22media.popularmovies.MovieGridActivity;
import co.x22media.popularmovies.R;
import co.x22media.popularmovies.adapters.EndlessScrollListener;
import co.x22media.popularmovies.adapters.MovieGridAdapter;
import co.x22media.popularmovies.models.Movie;
import co.x22media.popularmovies.tasks.GetMoviesAsyncTask;

/**
 * Created by kit on 21/10/15.
 */
public class GridViewFragment extends Fragment
        implements AdapterView.OnItemClickListener {

    private final String LOG_TAG = GridViewFragment.class.getSimpleName();
    private TextView mErrorView;
    private GridView mGridView;
    private MovieGridAdapter mAdapter;

    private SharedPreferences mPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;
    private EndlessScrollListener mEndlessScrollListener;
    private BroadcastReceiver mInternetConnectivityStateListener;

    // Ugly stateful flag to indicate if we should reload data when Sort changes.
    // For some reason, if we initiate the reload in the preference
    // change event handler, it screws up the scroll
    // position of the GridView.
    private Boolean mShouldReloadData = false;

    private Boolean mShouldReloadDataWhenConnectivityIsPresent = false;

    public GridViewFragment() {
    }

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
                Log.d(LOG_TAG, "Preferences changed! Will reload data set.");
                invalidateAndReloadDataSet();
            }
        };

        mPrefs.registerOnSharedPreferenceChangeListener(mListener);

        // reload data set if necessary
        if (mShouldReloadData) {
            loadMoviesAtPage(1);
            mShouldReloadData = false;
        }
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
        mAdapter = new MovieGridAdapter(getActivity(), new ArrayList<Movie>());
        mGridView.setAdapter(mAdapter);

        // set the endless scroll listener
        mEndlessScrollListener = new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                return loadMoviesAtPage(page);
            }
        };

        mGridView.setOnScrollListener(mEndlessScrollListener);

        // set the onItemClick event
        mGridView.setOnItemClickListener(this);

        // load the first page
        loadMoviesAtPage(1);

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // fragments are supposed to be reusable views, so we put the
        // showMovieDetail method in the Activity instead
        MovieGridActivity activity = (MovieGridActivity) getActivity();
        activity.showDetailViewForMovie(mAdapter.getItem(position));
    }

    private void invalidateAndReloadDataSet() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        mEndlessScrollListener.invalidate();
        mShouldReloadData = true;
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
                            if (mShouldReloadDataWhenConnectivityIsPresent) {
                                Log.d(LOG_TAG, "We are going to reload data because the mShouldReloadDataWhenConnectivityIsPresent flag is set.");
                                invalidateAndReloadDataSet();
                                loadMoviesAtPage(1);
                                mShouldReloadDataWhenConnectivityIsPresent = false;
                            }
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
        // get the saved sort order
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String preferredSortOrder = prefs.getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_values_default));

        new GetMoviesAsyncTask(page, preferredSortOrder, new GetMoviesAsyncTask.GetMoviesTaskCallback() {
            @Override
            public void onTaskDone(Exception e, Movie[] movies) {
                if (null != e) {
                    Log.w(LOG_TAG, "Exception occurred attempting to communicate with API.", e);

                    // Only show error view if there is no items in the grid view.
                    if (mAdapter.getCount() == 0) {
                        mErrorView.setVisibility(View.VISIBLE);
                        mShouldReloadDataWhenConnectivityIsPresent = true;
                        return;
                    }

                    mEndlessScrollListener.setLoading(false);
                }

                mErrorView.setVisibility(View.GONE);

                // If there is an error, movies will be null.
                // We'll need to handle that.
                if (null != movies) {
                    for (Movie m : movies) {
                        mAdapter.add(m);
                    }
                }

            }
        }).execute();

        return true;
    }
}
