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
import co.x22media.popularmovies.helpers.SharedPreferencesUtility;
import co.x22media.popularmovies.models.Movie;
import co.x22media.popularmovies.provider.MovieProvider;
import co.x22media.popularmovies.tasks.GetMoviesAsyncTask;

/**
 * Created by kit on 21/10/15.
 */
public class GridViewFragment extends Fragment
        implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = GridViewFragment.class.getSimpleName();
    private final int MOVIES_LOADER_ID = 0;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //SharedPreferencesUtility.resetAllPreferences(getActivity());
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        // get the current sort setting...
        mCurrentSortSetting = SharedPreferencesUtility.getCurrentSortSetting(getActivity());

        // Register a listener for shared preference changes,
        // but don't register the listener if it is already registered!
        if (null == mListener) {
            mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    String newSortSetting = SharedPreferencesUtility.getCurrentSortSetting(getActivity());
                    if (!newSortSetting.equals(mCurrentSortSetting)) {
                        Log.d(LOG_TAG, "Preferences changed! Current sort setting is " + newSortSetting);

                        // check if we need to query the server
                        if (!SharedPreferencesUtility.moviesCachedForSortSetting(getActivity(), newSortSetting)) {
                            Log.d(LOG_TAG, "Sort setting " + newSortSetting + " has not been queried from the API. Querying now.");
                            loadMoviesFromServer();
                        }

                        else {
                            Log.d(LOG_TAG, "Sort setting " + newSortSetting + " has data locally. Just restarting our loader.");
                            restartLoader();
                        }
                    }
                }
            };

            mPrefs.registerOnSharedPreferenceChangeListener(mListener);
        }

        // set the default setting if it is not set, triggering the refresh for a new install
        if (null == mCurrentSortSetting) {
            SharedPreferencesUtility.bootstrapSortSetting(getActivity());
        }

        else {
            // there is a current sort setting, so we have bootstrapped the app with data
            restartLoader();
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        mPrefs.unregisterOnSharedPreferenceChangeListener(mListener);
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

    /*
        LoaderManager.LoaderCallbacks<Cursor> method implementations
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri moviesUri = MovieProvider.getMovieDirUri();
        String selection = MovieProvider.Movie.KEY_SORT_SETTING + " = ?";
        String currentSortSetting = SharedPreferencesUtility.getCurrentSortSetting(getActivity());
        String[] selectionArgs = { currentSortSetting };
        String sortOrder = SharedPreferencesUtility.getCursorSortOrderForCurrentSortSetting(getActivity());
        return new CursorLoader(getActivity(), moviesUri, null, selection, selectionArgs, sortOrder);
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

    /*
        Logic methods
     */

    private void restartLoader() {
        getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
    }

    private Boolean loadMoviesFromServer() {
        // ensure onCreateLoader gets called again so the loader picks up any new sort order
        restartLoader();

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
}
