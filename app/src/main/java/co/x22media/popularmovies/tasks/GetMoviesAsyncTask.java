package co.x22media.popularmovies.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import co.x22media.popularmovies.R;
import co.x22media.popularmovies.helpers.JSONHTTPHelper;
import co.x22media.popularmovies.models.Movie;
import co.x22media.popularmovies.provider.MovieProvider;

/**
 * Created by kit on 20/10/15.
 */
public class GetMoviesAsyncTask extends AsyncTask<Void, Void, Movie[]> {
    private final String LOG_TAG = GetMoviesAsyncTask.class.getSimpleName();
    private final String BASE_URL_STRING = "http://api.themoviedb.org/3/discover/movie";

    private Context mContext;

    private int mRequestedPage;
    private String mSortOrder;
    private String mApiKey;

    private Exception mHttpException;
    private GetMoviesTaskCallback mCallback;

    public GetMoviesAsyncTask(Context context, int page, GetMoviesTaskCallback callback) {

        mContext = context;

        // get the API key
        mApiKey = context.getString(R.string.themoviedb_api_key);

        // get the saved sort order
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mSortOrder = prefs.getString(context.getString(R.string.pref_sort_order_key),
                context.getString(R.string.pref_sort_order_values_default));

        mRequestedPage = page;
        mCallback = callback;
    }

    @Override
    protected Movie[] doInBackground(Void... params) {
        Uri.Builder builder = Uri.parse(BASE_URL_STRING).buildUpon();

        builder.appendQueryParameter("sort_by", mSortOrder)
                .appendQueryParameter("page", String.valueOf(mRequestedPage))
                .appendQueryParameter("api_key", mApiKey);

        try {
            JSONHTTPHelper jsonHttpHelper = new JSONHTTPHelper("GET", builder.build().toString());
            JSONObject obj = jsonHttpHelper.executeForJSONResponse();

            Movie[] movies = parseJsonObjectToMovies(obj);
            saveMoviesToDatabase(movies);
            return movies;
        }

        catch (MalformedURLException e) {
            Log.e(LOG_TAG, "MalformedURLException: ", e);
        }

        catch (IOException e) {
            // Bubble up the exception to allow the UI to display an error message
            // Is this the best way to do it?
            mHttpException = e;
        }

        catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException: ", e);
        }

        return null;
    }

    private Movie[] parseJsonObjectToMovies(JSONObject obj) throws JSONException {
        JSONArray resultsArray = obj.getJSONArray("results");
        ArrayList<Movie> movies = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject result = resultsArray.getJSONObject(i);

            // prepare the variables
            String posterPath = result.getString("poster_path");
            String title = result.getString("title");
            String synopsis = result.getString("overview");
            double userRating = result.getDouble("vote_average");
            String releaseDate = result.getString("release_date");

            Movie m = new Movie(result.getInt("id"), title, posterPath, synopsis, userRating, releaseDate);
            movies.add(m);
        }

        return movies.toArray(new Movie[0]);
    }

    private int saveMoviesToDatabase(Movie[] movies) {
        ArrayList<ContentValues> moviesToSave = new ArrayList<>();
        for (Movie m : movies) {
            moviesToSave.add(m.toContentValues());
        }

        ContentValues[] cv = new ContentValues[moviesToSave.size()];
        moviesToSave.toArray(cv);
        int rows = mContext.getContentResolver().bulkInsert(MovieProvider.getMovieDirUri(), cv);
        Log.i(LOG_TAG, String.valueOf(rows) + " rows saved!");

        return rows;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        this.mCallback.onTaskDone(mHttpException, movies);

    }

    public interface GetMoviesTaskCallback {
        public void onTaskDone(Exception e, Movie[] movies);
    }
}
