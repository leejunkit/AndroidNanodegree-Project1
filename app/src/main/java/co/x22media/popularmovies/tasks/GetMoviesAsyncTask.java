package co.x22media.popularmovies.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

import co.x22media.popularmovies.helpers.JSONHTTPHelper;
import co.x22media.popularmovies.models.Movie;

/**
 * Created by kit on 20/10/15.
 */
public class GetMoviesAsyncTask extends AsyncTask<Void, Void, Movie[]> {
    private final String LOG_TAG = GetMoviesAsyncTask.class.getSimpleName();
    private final String BASE_URL_STRING = "http://api.themoviedb.org/3/discover/movie";

    private int mRequestedPage;
    private String mSortOrder;
    private GetMoviesTaskCallback mCallback;


    public GetMoviesAsyncTask(int page, String sortOrder, GetMoviesTaskCallback callback) {
        mRequestedPage = page;
        mSortOrder = sortOrder;
        mCallback = callback;
    }

    @Override
    protected Movie[] doInBackground(Void... params) {
        Uri.Builder builder = Uri.parse(BASE_URL_STRING).buildUpon();
        builder.appendQueryParameter("sort_by", mSortOrder)
                .appendQueryParameter("page", String.valueOf(mRequestedPage))
                .appendQueryParameter("api_key", "***REMOVED***");

        try {
            JSONHTTPHelper jsonHttpHelper = new JSONHTTPHelper("GET", builder.build().toString());
            JSONObject obj = jsonHttpHelper.executeForJSONResponse();
            return this.parseJsonObjectToMovies(obj);

        }

        catch (MalformedURLException e) {
            Log.e(LOG_TAG, "MalformedURLException: ", e);
        }

        catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException: ", e);
        }

        return null;
    }

    private Movie[] parseJsonObjectToMovies(JSONObject obj) throws JSONException {
        JSONArray resultsArray = obj.getJSONArray("results");

        ArrayList<Movie> movies = new ArrayList<Movie>();

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject result = resultsArray.getJSONObject(i);

            // parse the poster URL
            String basePosterURL = "http://image.tmdb.org/t/p/w185";
            String posterURLString = basePosterURL + result.getString("poster_path");
            Movie m = new Movie(result.getInt("id"), posterURLString);

            movies.add(m);
        }

        return movies.toArray(new Movie[0]);
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        this.mCallback.onTaskDone(movies);
    }

    public interface GetMoviesTaskCallback {
        public void onTaskDone(Movie[] movies);
    }
}
