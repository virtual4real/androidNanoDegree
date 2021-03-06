package com.virtual4real.moviemanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.sync.MovieManagerSyncAdapter;
import com.virtual4real.moviemanager.sync.restapi.RestApiContract;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieSummaryActivity extends AppCompatActivity implements MovieSummaryFragment.CallbackSummary,
        MovieDetailFragment.CallbackDetail {

    private boolean mTwoPane = false;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String SUMMARYFRAGMENT_TAG = "DFSUMTAG";

    private String mMinDate;
    private String mMaxDate;
    private int mMinVotes;
    private boolean mIsVideo;
    private boolean mIsAdult;

    @Nullable
    @Bind(R.id.summary_tablet_toolbar)
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_summary);

        LoadPreferences();

        if (null != findViewById(R.id.movie_detail_container)) {
            mTwoPane = true;

            ButterKnife.bind(this);
            setSupportActionBar(toolbar);

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

            int nWidth = (mTwoPane ? (dMetrics.widthPixels / 3) : dMetrics.widthPixels);

            Bundle args = new Bundle();
            args.putInt(MovieSummaryFragment.TOTAL_WIDTH, nWidth);
            args.putBoolean(MovieSummaryFragment.TWO_PANE, mTwoPane);

            MovieSummaryFragment summaryFragment = new MovieSummaryFragment();
            summaryFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_summary, summaryFragment, SUMMARYFRAGMENT_TAG)
                    .commit();
        }


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
    public void onItemSelected(Uri dateUri, int nIndex) {
        long nMovieId = 0;

        if (null != dateUri) {
            nMovieId = MovieProvider.getMovieSummaryId(dateUri);
            MovieManagerSyncAdapter.syncImmediately(getApplicationContext(), nMovieId);
        }



        if (mTwoPane) {

            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.DETAIL_URI, dateUri);
            args.putBoolean(MovieDetailFragment.HAS_TWO_PANE, mTwoPane);
            args.putInt(MovieDetailFragment.LIST_POSITION, nIndex);

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
    public void onItemSummaryFavoriteChanged(Uri dateUri) {
        if (null == dateUri || !mTwoPane) {
            return;
        }
        long nMovieId = MovieProvider.getMovieSummaryId(dateUri);

        MovieDetailFragment detailFrag =
                (MovieDetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        if (null != detailFrag) {
            String sortOrder = Utils.getOrderAndSortFromPreferences(getApplicationContext());
            if (sortOrder.equals(RestApiContract.SORT_KEY_FAVORITE_DESC) ||
                    sortOrder.equals(RestApiContract.SORT_KEY_FAVORITE_ASC)) {

                if (MovieProvider.getMovieSummaryId(detailFrag.getUri()) == nMovieId) {
                    onItemSelected(null, 0);
                }
            } else {
                detailFrag.RefreshFavorite(nMovieId);
            }
        }
    }

    @Override
    public void onItemDetailFavoriteChanged(Uri dateUri, int nPosition, int nFavorite) {
        if (null == dateUri || !mTwoPane) {
            return;
        }

        MovieSummaryFragment summFrag =
                (MovieSummaryFragment) getSupportFragmentManager().findFragmentByTag(SUMMARYFRAGMENT_TAG);
        if (null != summFrag) {
            summFrag.RefreshAdapter(nPosition, nFavorite);
        }


        MovieDetailFragment detailFrag =
                (MovieDetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        if (null != detailFrag) {
            String sortOrder = Utils.getOrderAndSortFromPreferences(getApplicationContext());
            if ((sortOrder.equals(RestApiContract.SORT_KEY_FAVORITE_DESC) ||
                    sortOrder.equals(RestApiContract.SORT_KEY_FAVORITE_ASC)) && detailFrag.getUri() == dateUri) {

                onItemSelected(null, 0);

            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        MovieSummaryFragment summaryFragment =
                (MovieSummaryFragment) getSupportFragmentManager().findFragmentByTag(SUMMARYFRAGMENT_TAG);

        if (!mTwoPane && null != summaryFragment) {
            summaryFragment.RefreshAdapter(1, 0);
        }

        if (!PreferencesWereModified()) {
            return;
        }

        if (null != summaryFragment) {
            MovieManagerSyncAdapter.syncImmediately(getApplicationContext(), Utils.getOrderAndSortFromPreferences(getApplicationContext()), 1);
        }

        if (mTwoPane) {
            onItemSelected(null, 0);
        }

    }


    private boolean PreferencesWereModified() {
        return mMinDate != Utils.getMinDate(getApplicationContext()) ||
                mMaxDate != Utils.getMaxDate(getApplicationContext()) ||
                mMinVotes != Utils.getMinVotes(getApplicationContext()) ||
                mIsAdult != Utils.getIncludeAdult(getApplicationContext()) ||
                mIsVideo != Utils.getIncludeVideo(getApplicationContext());
    }


    private void LoadPreferences() {
        mMinDate = Utils.getMinDate(getApplicationContext());
        mMaxDate = Utils.getMaxDate(getApplicationContext());
        mMinVotes = Utils.getMinVotes(getApplicationContext());
        mIsAdult = Utils.getIncludeAdult(getApplicationContext());
        mIsVideo = Utils.getIncludeVideo(getApplicationContext());
    }



}
