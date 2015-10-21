package co.x22media.popularmovies.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import co.x22media.popularmovies.models.Movie;

/**
 * Created by kit on 20/10/15.
 */
public class GetMoviesAsyncTask extends AsyncTask<Void, Void, Movie[]> {
    private final String LOG_TAG = GetMoviesAsyncTask.class.getSimpleName();
    private GetMoviesTaskCallback mCallback;

    public GetMoviesAsyncTask(GetMoviesTaskCallback callback) {
        mCallback = callback;
    }

    @Override
    protected Movie[] doInBackground(Void... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        if (params == null) {
            return null;
        }

        try {
            URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=***REMOVED***");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            Movie[] movies = parseJsonStringToMovies(buffer.toString());
            return movies;

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Parsing Error ", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    private Movie[] parseJsonStringToMovies(String jsonString) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);
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
