package co.x22media.popularmovies.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
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
    private MovieGridAdapter mAdapter;

    private SharedPreferences mPrefs;
    private String mCurrentSortSetting;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;

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

        // get the current sort setting...
        mCurrentSortSetting = mPrefs.getString(getString(R.string.pref_sort_order_key), null);

        // Register a listener for shared preference changes,
        // but don't register the listener if it is already registered!
        if (null == mListener) {
            mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    String newSortSetting = sharedPreferences.getString(getString(R.string.pref_sort_order_key),
                            getString(R.string.pref_sort_order_values_default));
                    if (!newSortSetting.equals(mCurrentSortSetting)) {
                        Log.d(LOG_TAG, "Preferences changed! Should reload data set.");
                        refreshMoviesFromServer();
                    }
                }
            };

            mPrefs.registerOnSharedPreferenceChangeListener(mListener);
        }

        // set the default setting if it is not set, triggering the refresh for a new install
        if (null == mCurrentSortSetting) {
            SharedPreferences.Editor e = mPrefs.edit();
            e.putString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_values_default));
            e.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        // pull out the error view
        mErrorView = (TextView) rootView.findViewById(R.id.error_textview);

        // pull out the GridView
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);

        // set the adapter
        mAdapter = new MovieGridAdapter(getActivity(), null, 0);
        gridView.setAdapter(mAdapter);

        // set the onItemClick event
        gridView.setOnItemClickListener(this);

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

    private Boolean refreshMoviesFromServer() {

        // remove all movie objects in the database first
        int rowsDeleted = getContext().getContentResolver()
                .delete(MovieProvider.getMovieDirUri(), null, null);
        Log.d(LOG_TAG, "Delete all movies: " + String.valueOf(rowsDeleted) + " rows deleted.");

        // ensure onCreateLoader gets called again so the loader picks up any new sort order
        getLoaderManager().restartLoader(0, null, this);

        new GetMoviesAsyncTask(getActivity(), new GetMoviesAsyncTask.GetMoviesTaskCallback() {
            @Override
            public void onTaskDone(Exception e, Movie[] movies) {
                if (null != e) {
                    Log.w(LOG_TAG, "Exception occurred attempting to communicate with API.", e);
                    mErrorView.setVisibility(View.VISIBLE);
                }
            }
        }).execute();

        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // instantiate the loader
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder;
        String defaultSortSetting = getString(R.string.pref_sort_order_values_default);
        String currentSortSetting = mPrefs.getString(getString(R.string.pref_sort_order_key), null);
        if (defaultSortSetting.equals(currentSortSetting)) {
            sortOrder = MovieProvider.Movie.KEY_POPULARITY;
        }

        else {
            sortOrder = MovieProvider.Movie.KEY_USER_RATING;
        }

        sortOrder += " DESC";

        Uri moviesUri = MovieProvider.getMovieDirUri();
        return new CursorLoader(getActivity(), moviesUri, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            // we have no data
            Log.d(LOG_TAG, "Cursor returned no data.");
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
