package co.x22media.popularmovies.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import co.x22media.popularmovies.R;
import co.x22media.popularmovies.helpers.MovieUtility;
import co.x22media.popularmovies.models.Movie;

/**
 * Created by kit on 6/11/15.
 */
public class MovieDetailContainerFragment extends Fragment {
    private final String LOG_TAG = MovieDetailContainerFragment.class.getSimpleName();
    private final String[] TAB_TITLES = { "DETAILS", "REVIEWS" };
    private PagerAdapter mAdapter;
    private ViewPager mViewPager;
    private Uri mUri;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != savedInstanceState) {
            String mUriString = savedInstanceState.getString("mUri");
            mUri = Uri.parse(mUriString);
        }

        else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the share provider and set the intent
        ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (null != mUri) {
            Cursor c = getActivity().getContentResolver().query(mUri, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                Movie m = Movie.fromCursor(c);
                provider.setShareIntent(MovieUtility.getIntentForFirstVideoFromMovie(m));
            }

            c.close();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null != mUri) {
            outState.putString("mUri", mUri.toString());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail_container, container, false);

        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                Fragment frag;
                Bundle b = new Bundle();

                if (null != mUri) {
                    b.putParcelable(MovieDetailFragment.DETAIL_URI, mUri);
                }

                if (position == 0) {
                    frag = new MovieDetailFragment();
                }

                else {
                    frag = new MovieReviewsFragment();
                }

                frag.setArguments(b);
                return frag;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public String getPageTitle(int position) {
                return TAB_TITLES[position];
            }
        };

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.movie_detail_view_pager);
        viewPager.setAdapter(mAdapter);
        mViewPager = viewPager;

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.movie_detail_tabs);
        tabLayout.setTabsFromPagerAdapter(mAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        return rootView;
    }
}
