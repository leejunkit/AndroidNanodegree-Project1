package co.x22media.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by kit on 20/10/15.
 */
public class Movie implements Parcelable {
    private int movieID;
    private String title;
    private String posterURL;
    private String synopsis;
    private double userRating;
    private Date releaseDate;

    public Movie(int movieID, String title, String posterURLString, String synopsis, double userRating, Date releaseDate) {
        this.movieID = movieID;
        this.title = title;
        this.posterURL = posterURLString;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
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

    public double getUserRating() {
        return userRating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getPosterURL() {
        return posterURL;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Movie: ");
        builder.append("movieID=" + this.movieID);
        builder.append("\n");
        builder.append("title=" + this.title);
        builder.append("\n");
        builder.append("posterURL=" + this.posterURL);
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
        dest.writeString(this.posterURL);
        dest.writeString(this.synopsis);
        dest.writeDouble(this.userRating);
        dest.writeLong(releaseDate != null ? releaseDate.getTime() : -1);
    }

    protected Movie(Parcel in) {
        this.movieID = in.readInt();
        this.title = in.readString();
        this.posterURL = in.readString();
        this.synopsis = in.readString();
        this.userRating = in.readDouble();
        long tmpReleaseDate = in.readLong();
        this.releaseDate = tmpReleaseDate == -1 ? null : new Date(tmpReleaseDate);
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
