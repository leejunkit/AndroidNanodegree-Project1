package co.x22media.popularmovies.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseIntArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import co.x22media.popularmovies.R;
import co.x22media.popularmovies.helpers.JSONHTTPHelper;
import co.x22media.popularmovies.helpers.SharedPreferencesUtility;
import co.x22media.popularmovies.models.Movie;
import co.x22media.popularmovies.provider.MovieProvider;

/**
 * Created by kit on 20/10/15.
 */
public class GetMoviesAsyncTask extends AsyncTask<Void, Void, Movie[]> {
    private final String LOG_TAG = GetMoviesAsyncTask.class.getSimpleName();
    private final String BASE_URL_STRING = "http://api.themoviedb.org/3/discover/movie";

    private Context mContext;
    private String mSortSetting;
    private String mApiKey;

    private Exception mHttpException;
    private GetMoviesTaskCallback mCallback;

    public GetMoviesAsyncTask(Context context, GetMoviesTaskCallback callback) {
        super();
        mContext = context;

        // get the API key
        mApiKey = context.getString(R.string.themoviedb_api_key);

        // get the saved sort order
        mSortSetting = SharedPreferencesUtility.getCurrentSortSetting(mContext);
        if (mContext.getString(R.string.pref_sort_setting_values_favorities).equals(mSortSetting)) {
            throw new IllegalStateException("Trying to query the server for a sort setting of 'favorities'. This is definitely an error.");
        }

        mCallback = callback;
    }

    @Override
    protected Movie[] doInBackground(Void... params) {
        ArrayList<Movie> movies = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            ArrayList<Movie> moviePage = executeHTTPRequestForMoviesAtPage(i);
            movies.addAll(moviePage);
        }

        Movie[] m = new Movie[movies.size()];
        saveMoviesToDatabase(movies.toArray(m));
        return m;
    }

    private ArrayList<Movie> executeHTTPRequestForMoviesAtPage(int page) {
        Uri.Builder builder = Uri.parse(BASE_URL_STRING).buildUpon();

        builder.appendQueryParameter("sort_by", mSortSetting)
                // Really weird results if we don't lower-bound the vote_count,
                // 1 person could vote 10 and a really strange, sometimes
                // adult movie could appear in the listing!!
                .appendQueryParameter("vote_count.gte", "200")
                .appendQueryParameter("page", String.valueOf(page))
                .appendQueryParameter("api_key", mApiKey);

        try {
            String urlString = builder.build().toString();
            Log.d(LOG_TAG, "URL to query is " + urlString);
            JSONHTTPHelper jsonHttpHelper = new JSONHTTPHelper("GET", urlString);
            JSONObject obj = jsonHttpHelper.executeForJSONResponse();

            return parseJsonObjectToMovies(obj);
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

    private ArrayList<Movie> parseJsonObjectToMovies(JSONObject obj) throws JSONException {
        JSONArray resultsArray = obj.getJSONArray("results");
        ArrayList<Movie> movies = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject result = resultsArray.getJSONObject(i);

            // prepare the variables
            String posterPath = result.getString("poster_path");
            String title = result.getString("title");
            String synopsis = result.getString("overview");
            double popularity = result.getDouble("popularity");
            double userRating = result.getDouble("vote_average");
            String releaseDate = result.getString("release_date");

            Movie m = new Movie(
                    result.getInt("id"),
                    title,
                    posterPath,
                    synopsis,
                    popularity,
                    userRating,
                    releaseDate,
                    mSortSetting,
                    false,
                    null,
                    null);

            movies.add(m);
        }

        return movies;
    }

    private int saveMoviesToDatabase(Movie[] movies) {

        // Because there's some overlap in movies returned for "most popular"
        // and "highest rated", we do a check so we do not insert movies
        // that are already existing in our DB. (Yes this is a lazy
        // workaround, I'm sorry)

        String[] projection = { "_id" };
        Cursor c = mContext.getContentResolver().query(MovieProvider.getMovieDirUri(), projection, null, null, null);
        SparseIntArray existingIdsArr = new SparseIntArray();
        while (c.moveToNext()) {
            existingIdsArr.append(c.getInt(0), 1);
        }

        c.close();

        // For some strange reason the API returns duplicate objects,
        // we implement a check to ensure that we do
        // not insert them more than once.
        SparseIntArray arr = new SparseIntArray();
        ArrayList<ContentValues> moviesToSave = new ArrayList<>();

        for (Movie m : movies) {
            int movieID = m.getMovieID();
            if (0 == arr.get(movieID)) {
                if (0 == existingIdsArr.get(movieID)) {
                    moviesToSave.add(m.toContentValues());
                    arr.put(movieID, 1);
                }
            }
        }

        ContentValues[] cv = new ContentValues[moviesToSave.size()];
        moviesToSave.toArray(cv);
        int rows = mContext.getContentResolver().bulkInsert(MovieProvider.getMovieDirUri(), cv);
        Log.i(LOG_TAG, String.valueOf(rows) + " rows saved!");

        return rows;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        SharedPreferencesUtility.setMoviesCachedForSortSetting(mContext, mSortSetting);
        this.mCallback.onTaskDone(mHttpException, movies);
    }

    public interface GetMoviesTaskCallback {
        public void onTaskDone(Exception e, Movie[] movies);
    }
}
