package co.x22media.popularmovies.helpers;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kit on 21/10/15.
 */
public class JSONHTTPHelper {

    private final String LOG_TAG = JSONHTTPHelper.class.getSimpleName();
    private String mMethod;
    private URL mUrl;

    public JSONHTTPHelper(String method, String urlString) throws MalformedURLException {
        mMethod = method;
        mUrl = new URL(urlString);
    }

    public JSONObject executeForJSONResponse() throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        if (null == mMethod || null == mUrl) {
            Log.e(LOG_TAG, "Method or URL is null! How can we make a HTTP request like this?");
            return null;
        }

        try {
            urlConnection = (HttpURLConnection) mUrl.openConnection();
            urlConnection.setRequestMethod(mMethod);
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

            // attempt to parse JSON
            return new JSONObject(buffer.toString());
        }

        catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException: ", e);
        }

        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream: ", e);
                }
            }
        }

        return null;
    }
}
