package com.virtual4real.moviemanager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.database.MovieSummaryColumns;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final String DETAIL_URI = "detail_uri";
    private static final int DETAIL_LOADER = 0;

    private Uri mUri;
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


    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail_activity, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        }

        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            long nid = MovieProvider.getMovieSummaryId(mUri);

            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MovieProvider.getMovieSummaryProjection(),
                    null,//Long.toString(nid),
                    null,
                    null
            );

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String sOriginalTitle = data.getString(data.getColumnIndex(MovieSummaryColumns.ORIGINAL_TITLE));
            String sTitle = data.getString(data.getColumnIndex(MovieSummaryColumns.TITLE));

            mTxtTitle.setText(sTitle);
            if (sTitle.equals(sOriginalTitle)) {
                mTxtOriginalTitle.setVisibility(View.INVISIBLE);
            } else {
                mTxtOriginalTitle.setText(sOriginalTitle);
            }


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
                                mImgPlaceholder.setVisibility(View.VISIBLE);
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


//            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
//            if (mShareActionProvider != null) {
//                mShareActionProvider.setShareIntent(createShareForecastIntent());
//            }

            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            collapsingToolbar.setTitle(sTitle);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
