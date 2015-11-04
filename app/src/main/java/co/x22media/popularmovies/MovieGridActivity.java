package co.x22media.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import co.x22media.popularmovies.fragments.GridViewFragment;

public class MovieGridActivity extends AppCompatActivity {
    private final String LOG_TAG = MovieGridActivity.class.getSimpleName();
    private GridViewFragment mGridViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        if (savedInstanceState == null) {
            mGridViewFragment = new GridViewFragment();
        }

        else {
            mGridViewFragment = (GridViewFragment) getSupportFragmentManager().
                    getFragment(savedInstanceState, "mGridViewFragment");
        }

        if (!mGridViewFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mGridViewFragment)
                    .commit();
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

    public void showDetailViewWithUri(Uri uri) {
        Intent i = new Intent(this, co.x22media.popularmovies.MovieDetailActivity.class);
        i.setData(uri);
        startActivity(i);
    }
}
