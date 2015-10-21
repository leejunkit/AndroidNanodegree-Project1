package co.x22media.popularmovies.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
    private GridView mGridView;
    private MovieGridAdapter mAdapter;

    private SharedPreferences mPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;
    private EndlessScrollListener mEndlessScrollListener;

    // Ugly stateful flag to indicate if we should reload data when Sort changes.
    // For some reason, if we initiate the reload in the preference
    // change event handler, it screws up the scroll
    // position of the GridView.
    private Boolean mShouldReloadData = false;

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
        if (mShouldReloadData) {
            loadMoviesAtPage(1);
            mShouldReloadData = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(LOG_TAG, "Preferences changed! Will reload data set.");
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                mEndlessScrollListener.invalidate();
                mShouldReloadData = true;
            }
        };

        mPrefs.registerOnSharedPreferenceChangeListener(mListener);

        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

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

    private Boolean loadMoviesAtPage(int page) {
        // get the saved sort order
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String preferredSortOrder = prefs.getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_values_default));
        Log.d(LOG_TAG, "Page is " + String.valueOf(page));
        new GetMoviesAsyncTask(page, preferredSortOrder, new GetMoviesAsyncTask.GetMoviesTaskCallback() {
            @Override
            public void onTaskDone(Movie[] movies) {
                for (Movie m : movies) {
                    mAdapter.add(m);
                }
            }
        }).execute();

        return true;
    }
}
