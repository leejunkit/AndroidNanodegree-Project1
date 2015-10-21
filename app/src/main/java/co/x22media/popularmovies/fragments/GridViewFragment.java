package co.x22media.popularmovies.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import co.x22media.popularmovies.R;
import co.x22media.popularmovies.adapters.EndlessScrollListener;
import co.x22media.popularmovies.adapters.MovieGridAdapter;
import co.x22media.popularmovies.models.Movie;
import co.x22media.popularmovies.tasks.GetMoviesAsyncTask;

/**
 * Created by kit on 21/10/15.
 */
public class GridViewFragment extends Fragment
        implements AdapterView.OnItemClickListener {

    private final String LOG_TAG = GridViewFragment.class.getSimpleName();

    private MovieGridAdapter mAdapter;

    public GridViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        // pull out the GridView
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);

        // set the adapter
        mAdapter = new MovieGridAdapter(getActivity(), new ArrayList<Movie>());
        gridView.setAdapter(mAdapter);

        // set the endless scroll listener
        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                return loadMoviesAtPage(page);
            }
        });

        // set the onItemClick event
        gridView.setOnItemClickListener(this);

        // load the first page
        loadMoviesAtPage(1);

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(LOG_TAG, "Item clicked!");
    }

    private Boolean loadMoviesAtPage(int page) {
        new GetMoviesAsyncTask(page, new GetMoviesAsyncTask.GetMoviesTaskCallback() {
            @Override
            public void onTaskDone(Movie[] movies) {
                for (Movie m : movies) {
                    mAdapter.add(m);
                }
            }
        }).execute();

        return true;
    }
}
