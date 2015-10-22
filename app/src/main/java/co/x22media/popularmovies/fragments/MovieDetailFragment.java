package co.x22media.popularmovies.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;

import co.x22media.popularmovies.R;
import co.x22media.popularmovies.models.Movie;

/**
 * Created by kit on 21/10/15.
 */
public class MovieDetailFragment extends Fragment {

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Movie m = getArguments().getParcelable("movieParam");

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // find the ImageView
        ImageView iv = (ImageView)rootView.findViewById(R.id.movie_poster_image_view);
        Picasso.with(getActivity())
                .load(m.getPosterURL())
                .placeholder(R.drawable.poster_placeholder)
                .into(iv);

        // find the Title TextView
        TextView titleTextView = (TextView)rootView.findViewById(R.id.movie_title_text_view);
        titleTextView.setText(m.getTitle());

        // find the Release Date TextView
        TextView releaseDateTextView = (TextView)rootView.findViewById(R.id.movie_release_date_text_view);
        java.util.Date releaseDate = m.getReleaseDate();
        if (null != releaseDate) {
            Calendar c = Calendar.getInstance();
            c.setTime(m.getReleaseDate());
            releaseDateTextView.setText(String.valueOf(c.get(Calendar.YEAR)));
        }

        else {
            releaseDateTextView.setText(getString(R.string.null_release_date_label));
        }

        // find the Rating TextView
        TextView ratingTextView = (TextView)rootView.findViewById(R.id.movie_rating_text_view);
        ratingTextView.setText(String.valueOf(m.getUserRating()) + getString(R.string.rating_out_of_label));

        // find the Synopsis TextView
        TextView synopsisTextView = (TextView)rootView.findViewById(R.id.movie_synopsis_text_view);
        if (null != m.getSynopsis()) {
            synopsisTextView.setText(m.getSynopsis());
        }

        else {
            synopsisTextView.setText(getString(R.string.null_synopsis_label));
        }

        return rootView;
    }
}