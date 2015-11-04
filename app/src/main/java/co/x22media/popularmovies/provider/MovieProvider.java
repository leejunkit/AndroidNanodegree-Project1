package co.x22media.popularmovies.provider;

import android.content.ContentUris;
import android.net.Uri;

import de.triplet.simpleprovider.AbstractProvider;
import de.triplet.simpleprovider.Column;
import de.triplet.simpleprovider.Table;

/**
 * Created by kit on 30/10/15.
 */
public class MovieProvider extends AbstractProvider {
    public static final String CONTENT_AUTHORITY = "co.x22media.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    @Override
    protected String getAuthority() {
        return CONTENT_AUTHORITY;
    }

    @Override
    protected int getSchemaVersion() {
        return 4;
    }

    public static Uri getMovieDirUri() {
        return BASE_CONTENT_URI.buildUpon().appendPath("movies").build();
    }

    public static Uri buildMovieUri(long id) {
        return ContentUris.withAppendedId(getMovieDirUri(), id);
    }

    @Table
    public class Movie {

        @Column(value = Column.FieldType.INTEGER, primaryKey = true)
        public static final String KEY_ID = "_id";

        @Column(value = Column.FieldType.TEXT, since = 2)
        public static final String KEY_SORT_SETTING = "sort_setting";

        @Column(Column.FieldType.TEXT)
        public static final String KEY_TITLE = "title";

        @Column(Column.FieldType.TEXT)
        public static final String KEY_OVERVIEW = "overview";

        @Column(Column.FieldType.REAL)
        public static final String KEY_POPULARITY = "popularity";

        @Column(Column.FieldType.REAL)
        public static final String KEY_USER_RATING = "vote_average";

        @Column(Column.FieldType.TEXT)
        public static final String KEY_RELEASE_DATE = "release_date";

        @Column(Column.FieldType.TEXT)
        public static final String KEY_POSTER_PATH = "poster_path";

        @Column(value = Column.FieldType.INTEGER, since = 3)
        public static final String KEY_FAVORITE = "favorite";

        @Column(value = Column.FieldType.TEXT, since = 4)
        public static final String KEY_REVIEWS_JSON = "reviews_json";

        @Column(value = Column.FieldType.TEXT, since = 4)
        public static final String KEY_VIDEOS_JSON = "videos_json";
    }
}
