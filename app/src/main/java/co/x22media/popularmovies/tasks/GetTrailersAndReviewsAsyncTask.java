package co.x22media.popularmovies.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

import co.x22media.popularmovies.R;
import co.x22media.popularmovies.helpers.JSONHTTPHelper;
import co.x22media.popularmovies.provider.MovieProvider;

/**
 * Created by kit on 4/11/15.
 * Pass in the movie ID to get trailers and reviews of the movie inserted into the DB.
 */
public class GetTrailersAndReviewsAsyncTask extends AsyncTask<Long, Void, Void> {
    private final String LOG_TAG = GetTrailersAndReviewsAsyncTask.class.getSimpleName();
    private enum MovieDetail {Video, Review};
    private final String BASE_URL_STRING = "http://api.themoviedb.org/3/movie/";

    private Context mContext;
    private String mApiKey;

    public GetTrailersAndReviewsAsyncTask(Context context) {
        super();
        mContext = context;
        mApiKey = context.getString(R.string.themoviedb_api_key);
    }

    @Override
    protected Void doInBackground(Long... params) {
        Long movieID = params[0];
        JSONArray reviewsJSONArray = executeHTTPRequestForDetailsOfMovie(movieID, MovieDetail.Review);
        JSONArray videosJSONArray = executeHTTPRequestForDetailsOfMovie(movieID, MovieDetail.Video);

        // build the URI for update
        Uri uri = MovieProvider.buildMovieUri(movieID);

        // build the ContentValues object
        ContentValues cv = new ContentValues();
        if (null != reviewsJSONArray) {
            cv.put(MovieProvider.Movie.KEY_REVIEWS_JSON, reviewsJSONArray.toString());
        }

        if (null != videosJSONArray) {
            cv.put(MovieProvider.Movie.KEY_VIDEOS_JSON, videosJSONArray.toString());
        }

        mContext.getContentResolver().update(uri, cv, null, null);
        return null;
    }

    private JSONArray executeHTTPRequestForDetailsOfMovie(long movieID, MovieDetail detail) {
        Uri.Builder builder = Uri.parse(BASE_URL_STRING).buildUpon();
        builder.appendPath(String.valueOf(movieID));
        if (detail == MovieDetail.Review) {
            builder.appendPath("reviews");
        }

        else {
            builder.appendPath("videos");
        }

        builder.appendQueryParameter("api_key", mApiKey);

        try {
            String urlString = builder.build().toString();
            Log.d(LOG_TAG, "URL to query is " + urlString);
            JSONHTTPHelper jsonhttpHelper = new JSONHTTPHelper("GET", urlString);
            JSONObject obj = jsonhttpHelper.executeForJSONResponse();

            return parseResultsArrayFromJSONObject(obj);
        }

        catch (MalformedURLException e) {
            Log.e(LOG_TAG, "MalformedURLException: ", e);
        }

        catch (IOException e) {
            Log.e(LOG_TAG, "IOException: ", e);
        }

        catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException: ", e);
        }

        return null;
    }

    private JSONArray parseResultsArrayFromJSONObject(JSONObject obj) throws JSONException {
        return obj.getJSONArray("results");
    }
}
