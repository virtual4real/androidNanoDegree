package com.virtual4real.moviemanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.database.MovieReviewColumns;
import com.virtual4real.moviemanager.database.MovieSummaryColumns;
import com.virtual4real.moviemanager.database.MovieTrailerColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DETAIL_URI = "detail_uri";
    public static final String HAS_TWO_PANE = "two_pane";
    private static final int DETAIL_LOADER = 0;
    private static final int TRAILER_LOADER = 1;
    private static final int REVIEWS_LOADER = 2;
    private static final int EXTRA_DETAIL_LOADER = 3;

    private boolean bTwoPane = false;
    private ShareActionProvider mShareActionProvider;

    private Uri mUri;
    private Uri mUriTrailers;
    private Uri mUriReviews;
    private Uri mUriExtraDetails;

    private ShareActionUtility mShareActionUtility = new ShareActionUtility();

    @Bind(R.id.cards)
    public NestedScrollView viewCards;
    @Bind(R.id.appbar)
    public AppBarLayout appBar;

    @Bind(R.id.cardOverview)
    public CardView cardOverview;

    @Bind(R.id.cardTrailers)
    public CardView cardTrailers;

    @Bind(R.id.cardReviews)
    public CardView cardReviews;

    @Bind(R.id.set_fav)
    public FloatingActionButton fab;

    @Bind(R.id.movie_title)
    public TextView mTxtTitle;
    @Bind(R.id.original_title)
    public TextView mTxtOriginalTitle;
    @Bind(R.id.release_date)
    public TextView mReleaseDate;
    @Bind(R.id.overview)
    public TextView mOverview;

    @Bind(R.id.movie_rating_detail)
    public TextView mTxtRating;
    @Bind(R.id.rating_bar_detail)
    public RatingBar mRatingBar;

    @Bind(R.id.img_placeholder)
    public ImageView mImgPlaceholder;
    @Bind(R.id.img_thumbnail_detail)
    public ImageView mImgThumbnail;

    @Bind(R.id.toolbar)
    public Toolbar toolbar;
    @Bind(R.id.collapsing_toolbar)
    public CollapsingToolbarLayout collapsingToolbar;

    @Bind(R.id.trailer_layout)
    public GridLayout trailersLayout;

    @Bind(R.id.review_layout)
    public GridLayout reviewsLayout;

    public MovieDetailFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail_activity, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
            bTwoPane = arguments.getBoolean(MovieDetailFragment.HAS_TWO_PANE);

            long nid = MovieProvider.getMovieSummaryId(mUri);
            mUriTrailers = MovieProvider.getMovieTrailerUri(nid);
            mUriReviews = MovieProvider.getMovieReviewUri(nid);
            mUriExtraDetails = MovieProvider.getMovieDetailUri(nid);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setFavorite(fab);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_detail_fragment, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        mShareActionProvider.setShareIntent(mShareActionUtility.createShareIntent());
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null == mUri) {
            viewCards.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);
            appBar.setVisibility(View.INVISIBLE);
            return null;
        } else {
            viewCards.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            appBar.setVisibility(View.VISIBLE);
        }

        long nid = MovieProvider.getMovieSummaryId(mUri);
        Loader<Cursor> loader = null;

        switch (id) {
            case DETAIL_LOADER:
                loader = new CursorLoader(getActivity(), mUri, MovieProvider.getMovieSummaryProjection(),
                        null, null, null);
                break;
            case TRAILER_LOADER:
                loader = new CursorLoader(getActivity(), mUriTrailers, MovieProvider.getMovieTrailerProjection(),
                        null, null, null);
                break;
            case REVIEWS_LOADER:
                loader = new CursorLoader(getActivity(), mUriReviews, MovieProvider.getMovieReviewProjection(),
                        null, null, null);
                break;
            default:
                break;
        }


        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || 0 == data.getCount()) {
            if (((CursorLoader) loader).getUri() == mUriTrailers) {
                cardTrailers.setVisibility(View.GONE);
                mShareActionUtility.setNoTrailerPath(true);
            }

            if (((CursorLoader) loader).getUri() == mUriReviews) {
                cardReviews.setVisibility(View.GONE);
            }

            return;
        }

        if (((CursorLoader) loader).getUri() == mUri) {
            loadSummaryDetailsControls(loader, data);
            getLoaderManager().destroyLoader(DETAIL_LOADER);
            return;
        }

        if (((CursorLoader) loader).getUri() == mUriTrailers) {
            cardTrailers.setVisibility(View.VISIBLE);
            loadTrailersControls(loader, data);
            getLoaderManager().destroyLoader(TRAILER_LOADER);
            return;
        }

        if (((CursorLoader) loader).getUri() == mUriReviews) {
            cardReviews.setVisibility(View.VISIBLE);
            loadReviewControls(loader, data);
            getLoaderManager().destroyLoader(REVIEWS_LOADER);
            return;
        }

        if (null != mShareActionProvider && mShareActionUtility.isComplete()) {
            mShareActionProvider.setShareIntent(mShareActionUtility.createShareIntent());
        }

    }

    private void loadReviewControls(Loader<Cursor> loader, Cursor data) {
        if (0 == data.getCount() || !data.moveToFirst()) {
            return;
        }

        ReviewControlsViewer runs = new ReviewControlsViewer(data, getActivity(), reviewsLayout, getContext());
        runs.showReviews();

    }


    private void loadTrailersControls(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }


        List<TrailerControlsViewer.TrailerDescription> list =
                new ArrayList<TrailerControlsViewer.TrailerDescription>();

        do {
            list.add(new TrailerControlsViewer.TrailerDescription(
                    data.getString(data.getColumnIndex(MovieTrailerColumns.SOURCE)),
                    data.getString(data.getColumnIndex(MovieTrailerColumns.NAME))
            ));

        } while (data.moveToNext());


        if (0 < list.size()) {
            TrailerControlsViewer.TrailerDescription desc = list.get(0);
            mShareActionUtility.setTrailerPath(TrailerControlsViewer.getPathForTrailerMovieUrl(getContext(), desc.Source));
            mShareActionUtility.setTrailerTitle(desc.Name);
        }


        TrailerControlsViewer runs =
                new TrailerControlsViewer(list, getActivity(), trailersLayout, data.getCount(), getContext());
        runs.showTrailers();

    }


    private void loadSummaryDetailsControls(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();

        String sOriginalTitle = data.getString(data.getColumnIndex(MovieSummaryColumns.ORIGINAL_TITLE));
        String sTitle = data.getString(data.getColumnIndex(MovieSummaryColumns.TITLE));

        int nFavorite = data.getInt(data.getColumnIndex(MovieSummaryColumns.IS_FAVORITE));
        fab.setTag(nFavorite);
        setFavoriteBasedOnTag(fab);

        mTxtTitle.setText(sTitle);
        if (sTitle.equals(sOriginalTitle)) {
            mTxtOriginalTitle.setVisibility(View.INVISIBLE);
        } else {
            mTxtOriginalTitle.setText(sOriginalTitle);
        }

        mShareActionUtility.setMovieTitle(sTitle);

        mOverview.setText(data.getString(data.getColumnIndex(MovieSummaryColumns.OVERVIEW)));

        long nDate = data.getLong(data.getColumnIndex(MovieSummaryColumns.DATE_RELEASE));
        mReleaseDate.setText(MovieProvider.formatDate(nDate));

        String sPath = data.getString(data.getColumnIndex(MovieSummaryColumns.BACKDROP_PATH));

        if (null != sPath) {
            mImgPlaceholder.setVisibility(View.INVISIBLE);
            sPath = Utils.getBackdropImageUrl(getContext()) + sPath;

            Picasso.with(getContext())
                    .load(sPath)
                    .networkPolicy(
                            Utils.isConnected(getContext()) ?
                                    NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                            //.error()
                    .into(mImgPlaceholder, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (null != mImgPlaceholder) {
                                mImgPlaceholder.setVisibility(View.VISIBLE);
                            }

                        }

                        @Override
                        public void onError() {

                        }
                    });
        } else {
            mImgPlaceholder.setVisibility(View.GONE);
        }

        sPath = data.getString(data.getColumnIndex(MovieSummaryColumns.POSTER_PATH));

        if (null != sPath) {
            sPath = Utils.getBaseImageUrl(getContext()) + sPath;

            Picasso.with(getContext())
                    .load(sPath)
                    .networkPolicy(
                            Utils.isConnected(getContext()) ?
                                    NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                            //.error()
                    .into(mImgThumbnail);
        } else {
            mImgThumbnail.setVisibility(View.INVISIBLE);
        }

        float fRating = data.getFloat(data.getColumnIndex(MovieSummaryColumns.VOTE_AVERAGE));
        int nCount = data.getInt(data.getColumnIndex(MovieSummaryColumns.VOTE_COUNT));

        mTxtRating.setText(String.format("%.2f", fRating) + "/10" + " (" + Integer.toString(nCount) + " votes)");
        mRatingBar.setRating(fRating * (float) 0.5);


        if (!bTwoPane) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(sTitle);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
    }


    public void setFavorite(FloatingActionButton btn) {
        int nTag = (int) btn.getTag();

        if (0 == nTag) {
            btn.setImageResource(R.drawable.unlike);
            getContext().getContentResolver().update(mUri, MovieProvider.getMovieSummaryFavoriteContentValues(1), null, null);
            nTag = 1;
        } else {
            btn.setImageResource(R.drawable.like);
            getContext().getContentResolver().update(mUri, MovieProvider.getMovieSummaryFavoriteContentValues(0), null, null);
            nTag = 0;
        }

        btn.setTag(nTag);

    }

    public void setFavoriteBasedOnTag(FloatingActionButton btn) {
        int nTag = (int) btn.getTag();

        if (1 == nTag) {
            btn.setImageResource(R.drawable.unlike);
        } else {
            btn.setImageResource(R.drawable.like);
        }

    }



}
