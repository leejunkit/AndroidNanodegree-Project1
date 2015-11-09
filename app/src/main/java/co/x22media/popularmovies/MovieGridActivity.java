package co.x22media.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import co.x22media.popularmovies.adapters.TrailerViewTag;
import co.x22media.popularmovies.fragments.GridViewFragment;
import co.x22media.popularmovies.fragments.MovieDetailContainerFragment;
import co.x22media.popularmovies.fragments.MovieDetailFragment;
import co.x22media.popularmovies.helpers.ExternalURLBuilder;
import co.x22media.popularmovies.provider.MovieProvider;

public class MovieGridActivity extends AppCompatActivity implements GridViewFragment.Callback {
    private final String LOG_TAG = MovieGridActivity.class.getSimpleName();
    private final String FRAGMENT_TAG = "TwoPaneDetailFragmentTag";
    private boolean mTwoPane;
    private GridViewFragment mGridViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        FragmentManager fm = getSupportFragmentManager();

        mGridViewFragment = (GridViewFragment) fm.findFragmentById(R.id.movie_grid_fragment_framelayout);

        mTwoPane = null != findViewById(R.id.movie_detail_container);
        if (mTwoPane) {
            if (savedInstanceState == null) {
                MovieDetailContainerFragment frag = new MovieDetailContainerFragment();
                fm.beginTransaction().replace(R.id.movie_detail_container, frag, FRAGMENT_TAG).commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_grid, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, "mGridViewFragment", mGridViewFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, co.x22media.popularmovies.SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri movieURI) {
        showDetailViewWithUri(movieURI);
    }

    public void showDetailViewWithUri(Uri uri) {
        if (mTwoPane) {

            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.DETAIL_URI, uri);
            MovieDetailContainerFragment fragment = new MovieDetailContainerFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, FRAGMENT_TAG)
                    .commit();

        }

        else {
            Intent i = new Intent(this, co.x22media.popularmovies.MovieDetailActivity.class);
            i.setData(uri);
            startActivity(i);
        }
    }

    // Question: Duplicate code from MovieDetailActivity, because this Activity
    // is handling Detail view for two-pane mode. How to code reuse?

    public void favoriteButtonClicked(View view) {
        // check if movie is already favorited or not
        String[] projection = { MovieProvider.Movie.KEY_FAVORITE };
        Uri uri = (Uri)view.getTag();
        Cursor c = getContentResolver().query(uri, projection, null, null, null);
        if (c.moveToFirst()) {
            int favorited = c.getInt(0);
            if (favorited == 0) {
                ContentValues cv = new ContentValues();
                cv.put(MovieProvider.Movie.KEY_FAVORITE, true);
                int rowsUpdated = getContentResolver().update(uri, cv, null, null);
                Log.d(LOG_TAG, "Movie is favorited. (" + rowsUpdated + " rows updated)");
            }

            else {
                ContentValues cv = new ContentValues();
                cv.put(MovieProvider.Movie.KEY_FAVORITE, false);
                int rowsUpdated = getContentResolver().update(uri, cv, null, null);
                Log.d(LOG_TAG, "Movie is unfavorited. (" + rowsUpdated + " rows updated)");
            }
        }

        c.close();
    }

    public void trailerButtonClicked(View view) {
        // get the youtube id from the view's tag
        TrailerViewTag vh = (TrailerViewTag) view.getTag();
        Uri uri = ExternalURLBuilder.buildYoutubeLinkWithYoutubeId(vh.youtubeID);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}
