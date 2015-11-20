package com.virtual4real.moviemanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.sync.MovieManagerSyncAdapter;

public class MovieSummaryActivity extends AppCompatActivity implements MovieSummaryFragment.Callback {

    private boolean mTwoPane = false;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String SUMMARYFRAGMENT_TAG = "DFSUMTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_summary);

        if (null != findViewById(R.id.movie_detail_container)) {
            mTwoPane = true;

            if (null == savedInstanceState) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;
        }

        if (null != findViewById(R.id.movie_summary)) {

            DisplayMetrics dMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

            int nWidth = (mTwoPane ? (dMetrics.widthPixels * (1 / 3)) : dMetrics.widthPixels);

            Bundle args = new Bundle();
            args.putInt(MovieSummaryFragment.TWO_PANE, nWidth);

            MovieSummaryFragment summaryFragment = new MovieSummaryFragment();
            summaryFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_summary, summaryFragment, SUMMARYFRAGMENT_TAG)
                    .commit();
        }

        /*
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        * */

        MovieManagerSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        long nMovieId = MovieProvider.getMovieSummaryId(dateUri);
        MovieManagerSyncAdapter.syncImmediately(getApplicationContext(), nMovieId);

        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.DETAIL_URI, dateUri);
            args.putBoolean(MovieDetailFragment.HAS_TWO_PANE, mTwoPane);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class).setData(dateUri);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//
//        ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
//        if ( null != ff ) {
//            ff.onLocationChanged();
//        }
        MovieDetailFragment df = (MovieDetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        df = null;
//        if ( null != df ) {
//            df.onLocationChanged(location);
//        }
    }



}
