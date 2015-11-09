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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import co.x22media.popularmovies.MovieDetailActivity;
import co.x22media.popularmovies.R;
import co.x22media.popularmovies.adapters.MovieVideosAdapter;
import co.x22media.popularmovies.models.Movie;
import co.x22media.popularmovies.tasks.GetTrailersAndReviewsAsyncTask;

/**
 * Created by kit on 21/10/15.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public static final String DETAIL_URI = "URI";
    private static final int DETAIL_LOADER = 1;

    private ScrollView mScrollView;
    private Button mFavoriteButton;
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mRatingTextView;
    private TextView mSynopsisTextView;

    private LinearLayout mVideosLinearLayout;

    private Uri mUri;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (null != savedInstanceState) {
            String mUriString = savedInstanceState.getString("mUri");
            mUri = Uri.parse(mUriString);
        }

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("mUri", mUri.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // find our views
        mScrollView = (ScrollView)rootView.findViewById(R.id.movie_detail_scroll_container);
        mFavoriteButton = (Button)rootView.findViewById(R.id.favorite_button);
        mImageView = (ImageView)rootView.findViewById(R.id.movie_poster_image_view);
        mTitleTextView = (TextView)rootView.findViewById(R.id.movie_title_text_view);
        mReleaseDateTextView = (TextView)rootView.findViewById(R.id.movie_release_date_text_view);
        mRatingTextView = (TextView)rootView.findViewById(R.id.movie_rating_text_view);
        mSynopsisTextView = (TextView)rootView.findViewById(R.id.movie_synopsis_text_view);
        mVideosLinearLayout = (LinearLayout)rootView.findViewById(R.id.videos_linear_layout);

        return rootView;
    }

    private void bindViewToMovie(Movie m) {
        if (m.getIsFavorited()) {
            mFavoriteButton.setBackgroundColor(getResources().getColor(R.color.favorited_bg));
            mFavoriteButton.setTextColor(getResources().getColor(R.color.favorited_text));
            mFavoriteButton.setText(getString(R.string.movie_favorited_btn_text));
        }

        else {
            mFavoriteButton.setBackgroundColor(getResources().getColor(R.color.not_favorited_bg));
            mFavoriteButton.setTextColor(getResources().getColor(R.color.not_favorited_text));
            mFavoriteButton.setText(getString(R.string.movie_not_favorited_btn_text));
        }

        Picasso.with(getActivity())
                .load(m.getDerivedPosterURL())
                .placeholder(R.drawable.poster_placeholder)
                .into(mImageView);

        mTitleTextView.setText(m.getTitle());

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            c.setTime(sdf.parse(m.getReleaseDate()));
            mReleaseDateTextView.setText(String.valueOf(c.get(Calendar.YEAR)));
        }

        catch (ParseException e) {
            Log.i(LOG_TAG, "Unable to parse Release Date.");
            mReleaseDateTextView.setText(getString(R.string.null_release_date_label));
        }

        mRatingTextView.setText(String.valueOf(m.getUserRating()) + getString(R.string.rating_out_of_label));

        if (null != m.getSynopsis()) {
            mSynopsisTextView.setText(m.getSynopsis());
        }

        else {
            mSynopsisTextView.setText(getString(R.string.null_synopsis_label));
        }

        if (null != m.getReviewsJSONString()) {
            Log.d(LOG_TAG, m.getReviewsJSONString());
        }

        if (null != m.getVideosJSONString()) {
            Log.d(LOG_TAG, m.getVideosJSONString());
            try {
                JSONArray objs = new JSONArray(m.getVideosJSONString());
                MovieVideosAdapter adapter = new MovieVideosAdapter(getContext(), objs);
                adapter.renderVideosIntoLinearLayout(mVideosLinearLayout);

                if (adapter.getCount() > 0) {
                    JSONObject obj = adapter.getItem(0);
                    if (null != obj) {
                        MovieDetailActivity activity = (MovieDetailActivity)getActivity();
                        activity.setShareableTrailer(obj.getString("key"));
                    }
                }
            }

            catch (JSONException e) {
                Log.e(LOG_TAG, "Shit.", e);
            }
        }
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
                // check if we need to make a query to get the reviews and videos data
                if (null == m.getVideosJSONString() || null == m.getReviewsJSONString()) {
                    GetTrailersAndReviewsAsyncTask task = new GetTrailersAndReviewsAsyncTask(getActivity());
                    task.execute((long)m.getMovieID());
                }

                bindViewToMovie(m);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}