package co.x22media.popularmovies.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.x22media.popularmovies.provider.MovieProvider;

/**
 * Created by kit on 20/10/15.
 */
public class Movie implements Parcelable {

    private int movieID;
    private String title;
    private String posterPath;
    private String synopsis;
    private double popularity;
    private double userRating;
    private String releaseDate;
    private String sortSetting;

    public static Movie fromCursor(Cursor c) {
        final String LOG_TAG = Movie.class.getSimpleName();

        int idxMovieID = c.getColumnIndex(MovieProvider.Movie.KEY_ID);
        int idxTitle = c.getColumnIndex(MovieProvider.Movie.KEY_TITLE);
        int idxPosterPath = c.getColumnIndex(MovieProvider.Movie.KEY_POSTER_PATH);
        int idxSynopsis = c.getColumnIndex(MovieProvider.Movie.KEY_OVERVIEW);
        int idxPopularity = c.getColumnIndex(MovieProvider.Movie.KEY_POPULARITY);
        int idxUserRating = c.getColumnIndex(MovieProvider.Movie.KEY_USER_RATING);
        int idxReleaseDate = c.getColumnIndex(MovieProvider.Movie.KEY_RELEASE_DATE);
        int idxSortSetting = c.getColumnIndex(MovieProvider.Movie.KEY_SORT_SETTING);

        return new Movie(
                c.getInt(idxMovieID),
                c.getString(idxTitle),
                c.getString(idxPosterPath),
                c.getString(idxSynopsis),
                c.getDouble(idxPopularity),
                c.getDouble(idxUserRating),
                c.getString(idxReleaseDate),
                c.getString(idxSortSetting));
    }

    public Movie(int movieID,
                 String title,
                 String posterPath,
                 String synopsis,
                 double popularity,
                 double userRating,
                 String releaseDate,
                 String sortSetting) {

        this.movieID = movieID;
        this.title = title;
        this.posterPath = posterPath;

        this.synopsis = synopsis;
        this.popularity = popularity;
        this.userRating = userRating;
        this.releaseDate = releaseDate;

        this.sortSetting = sortSetting;
    }

    public ContentValues toContentValues() {
        ContentValues c = new ContentValues();
        c.put(MovieProvider.Movie.KEY_ID, this.getMovieID());
        c.put(MovieProvider.Movie.KEY_TITLE, this.getTitle());
        c.put(MovieProvider.Movie.KEY_POSTER_PATH, this.getPosterPath());
        c.put(MovieProvider.Movie.KEY_OVERVIEW, this.getSynopsis());
        c.put(MovieProvider.Movie.KEY_POPULARITY, this.getPopularity());
        c.put(MovieProvider.Movie.KEY_USER_RATING, this.getUserRating());
        c.put(MovieProvider.Movie.KEY_RELEASE_DATE, this.getReleaseDate());
        c.put(MovieProvider.Movie.KEY_SORT_SETTING, this.getSortSetting());
        return c;
    }

    public int getMovieID() {
        return movieID;
    }

    public String getTitle() {
        return title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public double getPopularity() {
        return popularity;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getSortSetting() { return sortSetting; }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Movie: ");
        builder.append("movieID=" + this.movieID);
        builder.append("\n");
        builder.append("title=" + this.title);
        builder.append("\n");
        builder.append("posterPath=" + this.posterPath);
        builder.append("\n");
        builder.append("synopsis=" + this.synopsis);
        builder.append("\n");
        builder.append("userRating=" + this.userRating);
        builder.append("\n");
        builder.append("releaseDate=" + this.releaseDate);

        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.movieID);
        dest.writeString(this.title);
        dest.writeString(this.posterPath);
        dest.writeString(this.synopsis);
        dest.writeDouble(this.userRating);
        dest.writeString(this.releaseDate);
    }

    protected Movie(Parcel in) {
        this.movieID = in.readInt();
        this.title = in.readString();
        this.posterPath = in.readString();
        this.synopsis = in.readString();
        this.userRating = in.readDouble();
        this.releaseDate = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
