package co.x22media.popularmovies.helpers;

import android.net.Uri;
import android.net.Uri.Builder;

/**
 * Created by kit on 5/11/15.
 */
public class ExternalURLBuilder {
    public static String buildYoutubeThumbnailURLWithYoutubeId(String id) {
        return "http://img.youtube.com/vi/" + id + "/maxresdefault.jpg";
    }

    public static Uri buildYoutubeLinkWithYoutubeId(String id) {
        Builder b = Uri.parse("http://www.youtube.com/watch").buildUpon();
        b.appendQueryParameter("v", id);
        return b.build();
    }

}
