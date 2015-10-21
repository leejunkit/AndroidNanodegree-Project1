package co.x22media.popularmovies.models;

/**
 * Created by kit on 20/10/15.
 */
public class Movie {
    private int movieID;
    private String posterURL;


    public Movie(int movieID, String posterURLString) {
        this.movieID = movieID;
        this.posterURL = posterURLString;
    }

    public String getPosterURL() {
        return posterURL;
    }
}
