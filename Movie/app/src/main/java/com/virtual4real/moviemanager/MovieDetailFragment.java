package com.virtual4real.moviemanager;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.virtual4real.moviemanager.data.MovieContract;
import com.virtual4real.moviemanager.database.MovieSummary;
import com.virtual4real.moviemanager.database.MovieSummary$Table;


public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final String DETAIL_URI = "detail_uri";
    private static final int DETAIL_LOADER = 0;

    private Uri mUri;
    private TextView mTxtTitle;
    private TextView mTxtOriginalTitle;
    private TextView mReleaseDate;
    private TextView mOverview;

    private TextView mTxtRating;
    private RatingBar mRatingBar;

    private ImageView mImgPlaceholder;
    private ImageView mImgThumbnail;


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
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        }

        mTxtTitle = (TextView) rootView.findViewById(R.id.movie_title);
        mTxtOriginalTitle = (TextView) rootView.findViewById(R.id.original_title);
        mOverview = (TextView) rootView.findViewById(R.id.overview);
        mReleaseDate = (TextView) rootView.findViewById(R.id.release_date);

        mRatingBar = (RatingBar) rootView.findViewById(R.id.rating_bar_detail);
        mTxtRating = (TextView) rootView.findViewById(R.id.movie_rating_detail);

        mImgThumbnail = (ImageView) rootView.findViewById(R.id.img_thumbnail_detail);
        mImgPlaceholder = (ImageView) rootView.findViewById(R.id.img_placeholder);

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
            long nid = MovieContract.MovieSummaryEntry.getMovieSummaryId(mUri);

            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    Long.toString(nid),
                    null,
                    null
            );

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String sOriginalTitle = data.getString(data.getColumnIndex(MovieSummary$Table.ORIGINALTITLE));
            String sTitle = data.getString(data.getColumnIndex(MovieSummary$Table.TITLE));

            mTxtTitle.setText(sTitle);
            if (sTitle.equals(sOriginalTitle)) {
                mTxtOriginalTitle.setVisibility(View.INVISIBLE);
            } else {
                mTxtOriginalTitle.setText(sOriginalTitle);
            }


            mOverview.setText(data.getString(data.getColumnIndex(MovieSummary$Table.OVERVIEW)));

            long nDate = data.getLong(data.getColumnIndex(MovieSummary$Table.RELEASEDATE));
            mReleaseDate.setText(MovieContract.formatDate(nDate));

            String sPath = data.getString(data.getColumnIndex(MovieSummary$Table.BACKDROPPATH));

            if (null != sPath) {
                sPath = Utils.getBackdropImageUrl(getContext()) + sPath;

                Picasso.with(getContext())
                        .load(sPath)
                        .into(mImgPlaceholder);
            } else {
                mImgPlaceholder.setVisibility(View.GONE);
            }

            sPath = data.getString(data.getColumnIndex(MovieSummary$Table.POSTERPATH));

            if (null != sPath) {
                sPath = Utils.getBaseImageUrl(getContext()) + sPath;

                Picasso.with(getContext())
                        .load(sPath)
                        .into(mImgThumbnail);
            } else {
                mImgThumbnail.setVisibility(View.INVISIBLE);
            }

            float fRating = data.getFloat(data.getColumnIndex(MovieSummary$Table.VOTEAVERAGE));
            int nCount = data.getInt(data.getColumnIndex(MovieSummary$Table.VOTECOUNT));

            mTxtRating.setText(String.format("%.2f", fRating) + "/10" + " (" + Integer.toString(nCount) + " votes)");
            mRatingBar.setRating(fRating * (float) 0.5);


//            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
//            if (mShareActionProvider != null) {
//                mShareActionProvider.setShareIntent(createShareForecastIntent());
//            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
