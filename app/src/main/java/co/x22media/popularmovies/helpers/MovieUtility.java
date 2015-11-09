package co.x22media.popularmovies.helpers;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.x22media.popularmovies.models.Movie;

/**
 * Created by kit on 9/11/15.
 */
public class MovieUtility {
    private static final String LOG_TAG = MovieUtility.class.getSimpleName();

    public static Intent getIntentForFirstVideoFromMovie(Movie m) {
        String videosJSONString = m.getVideosJSONString();
        if (null != videosJSONString) {
            try {
                JSONArray objs = new JSONArray(videosJSONString);
                if (objs.length() > 0) {
                    JSONObject obj = objs.getJSONObject(0);
                    String youtubeID = obj.getString("key");
                    Uri uri = ExternalURLBuilder.buildYoutubeLinkWithYoutubeId(youtubeID);

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, uri.toString());
                    return shareIntent;
                }
            }

            catch (JSONException e) {
                Log.w(LOG_TAG, "JSONException: ", e);
            }
        }


        return null;
    }
}
