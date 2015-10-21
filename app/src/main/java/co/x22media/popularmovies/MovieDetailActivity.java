package co.x22media.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;

import co.x22media.popularmovies.models.Movie;


public class MovieDetailActivity extends ActionBarActivity {
    private final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Movie m = getIntent().getParcelableExtra("movieParam");
        setTitle(m.getTitle());

        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {

            // create the fragment and put the Movie object inside
            // (I still can't believe I can't simply pass objects via constructor parameters)
            PlaceholderFragment frag = new PlaceholderFragment();
            Bundle b = new Bundle();
            b.putParcelable("movieParam", m);
            frag.setArguments(b);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_scroll_container, frag)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
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
            Calendar c = Calendar.getInstance();
            c.setTime(m.getReleaseDate());
            releaseDateTextView.setText(String.valueOf(c.get(Calendar.YEAR)));

            // find the Rating TextView
            TextView ratingTextView = (TextView)rootView.findViewById(R.id.movie_rating_text_view);
            ratingTextView.setText(String.valueOf(m.getUserRating()) + " out of 10");

            // find the Synopsis TextView
            TextView synopsisTextView = (TextView)rootView.findViewById(R.id.movie_synopsis_text_view);
            synopsisTextView.setText(m.getSynopsis());
            return rootView;
        }
    }
}
