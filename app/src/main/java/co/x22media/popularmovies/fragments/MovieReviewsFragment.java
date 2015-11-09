package co.x22media.popularmovies.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.x22media.popularmovies.R;
import co.x22media.popularmovies.models.Movie;

public class MovieReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MovieReviewsFragment.class.getSimpleName();
    private static final int REVIEWS_LOADER = 1;
    private Uri mUri;
    private ListView mListView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (null != savedInstanceState) {
            String mUriString = savedInstanceState.getString("mUri");
            mUri = Uri.parse(mUriString);
        }

        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null != mUri) {
            outState.putString("mUri", mUri.toString());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_reviews, container, false);
        mListView = (ListView) rootView.findViewById(R.id.movie_reviews_list_view);


        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            Movie m = Movie.fromCursor(data);
            if (null != m) {

                String reviewsJSONString = m.getReviewsJSONString();
                try {
                    if (null != reviewsJSONString) {
                        JSONArray jsonArray = new JSONArray(reviewsJSONString);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                R.layout.list_item_review, R.id.movie_review_text_view);

                        ArrayList<String> arr = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject o = jsonArray.getJSONObject(i);
                            String review = o.getString("content") + " — " + o.getString("author");
                            arr.add(review);
                        }

                        adapter.addAll(arr);
                        mListView.setAdapter(adapter);
                    }
                }

                catch (JSONException e) {
                    Log.e(LOG_TAG, "Shit.", e);
                }
            }
        }

        data.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
