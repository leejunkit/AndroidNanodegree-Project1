package co.x22media.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import co.x22media.popularmovies.adapters.MovieGridAdapter;
import co.x22media.popularmovies.models.Movie;
import co.x22media.popularmovies.tasks.GetMoviesAsyncTask;


public class MovieGridActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new GridViewFragment())
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
    public static class GridViewFragment extends Fragment {

        private MovieGridAdapter mAdapter;

        public GridViewFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

            // pull out the GridView
            GridView gridView = (GridView) rootView.findViewById(R.id.gridView);


            mAdapter = new MovieGridAdapter(getActivity(), new ArrayList<Movie>());
            gridView.setAdapter(mAdapter);

            new GetMoviesAsyncTask(new GetMoviesAsyncTask.GetMoviesTaskCallback() {
                @Override
                public void onTaskDone(Movie[] movies) {
                    for (Movie m : movies) {
                        mAdapter.add(m);
                    }
                }
            }).execute();



            return rootView;
        }
    }
}
