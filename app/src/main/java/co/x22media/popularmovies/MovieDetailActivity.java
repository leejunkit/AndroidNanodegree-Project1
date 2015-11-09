package co.x22media.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import co.x22media.popularmovies.adapters.TrailerViewTag;
import co.x22media.popularmovies.fragments.MovieDetailContainerFragment;
import co.x22media.popularmovies.fragments.MovieDetailFragment;
import co.x22media.popularmovies.helpers.ExternalURLBuilder;
import co.x22media.popularmovies.provider.MovieProvider;


public class MovieDetailActivity extends AppCompatActivity {
    private final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private Uri mUri;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            String mUriString = savedInstanceState.getString("mUri");
            mUri = Uri.parse(mUriString);
        }

        else {
            Uri uri;
            Intent i = getIntent();
            if (null != i) {
                uri = i.getData();
                mUri = uri;
            }
        }

        setContentView(R.layout.activity_movie_detail);

        // set up the fragment

        Fragment f = new MovieDetailContainerFragment();
        Bundle b = new Bundle();
        b.putParcelable(MovieDetailFragment.DETAIL_URI, mUri);
        f.setArguments(b);

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, f)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mUri", mUri.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

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

    public void favoriteButtonClicked(View view) {
        // check if movie is already favorited or not
        String[] projection = { MovieProvider.Movie.KEY_FAVORITE };
        Cursor c = getContentResolver().query(mUri, projection, null, null, null);
        if (c.moveToFirst()) {
            int favorited = c.getInt(0);
            if (favorited == 0) {
                ContentValues cv = new ContentValues();
                cv.put(MovieProvider.Movie.KEY_FAVORITE, true);
                int rowsUpdated = getContentResolver().update(mUri, cv, null, null);
                Log.d(LOG_TAG, "Movie is favorited. (" + rowsUpdated + " rows updated)");
            }

            else {
                ContentValues cv = new ContentValues();
                cv.put(MovieProvider.Movie.KEY_FAVORITE, false);
                int rowsUpdated = getContentResolver().update(mUri, cv, null, null);
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

    public void setShareableTrailer(String youtubeId) {
        if (null != mShareActionProvider) {
            mShareActionProvider.setShareIntent(createTrailerShareIntent(youtubeId));
        }
    }

    private Intent createTrailerShareIntent(String youtubeId) {
        Uri link = ExternalURLBuilder.buildYoutubeLinkWithYoutubeId(youtubeId);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, link.toString());
        return shareIntent;
    }
}
