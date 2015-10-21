package co.x22media.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import co.x22media.popularmovies.fragments.MovieDetailFragment;
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
            MovieDetailFragment frag = new MovieDetailFragment();
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
}
