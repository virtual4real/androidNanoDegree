package com.virtual4real.moviemanager;

import android.content.Intent;

/**
 * Created by ioanagosman on 16/11/15.
 */
public class ShareActionUtility {

    private String mMovieTitle;
    private String mTrailerTitle;
    private String mTrailerPath;
    private boolean bNoTrailerPath;

    public String getTrailerTitle() {
        return mTrailerTitle;
    }

    public void setTrailerTitle(String mTrailerTitle) {
        this.mTrailerTitle = mTrailerTitle;
    }

    public boolean isNoTrailerPath() {
        return bNoTrailerPath;
    }

    public void setNoTrailerPath(boolean bNoTrailerPath) {
        this.bNoTrailerPath = bNoTrailerPath;
    }

    public String getTrailerPath() {
        return mTrailerPath;
    }

    public void setTrailerPath(String mTrailerPath) {
        this.mTrailerPath = mTrailerPath;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public void setMovieTitle(String mMovieTitle) {
        this.mMovieTitle = mMovieTitle;
    }

    public ShareActionUtility() {
        mMovieTitle = null;
        mTrailerPath = null;
        bNoTrailerPath = false;
    }


    public boolean isComplete() {
        return (null != mMovieTitle && null != mTrailerPath && !bNoTrailerPath) ||
                (null != mMovieTitle && null == mTrailerPath && bNoTrailerPath);
    }

    public boolean isPartial() {
        return (!bNoTrailerPath && (null == mMovieTitle || null == mTrailerPath)) ||
                (bNoTrailerPath && null == mMovieTitle);
    }

    public Intent createShareIntent() {
        Intent myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType("text/plain");
        myShareIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        myShareIntent.putExtra(Intent.EXTRA_SUBJECT, mMovieTitle);

        if (null != mTrailerPath) {
            myShareIntent.putExtra(Intent.EXTRA_TEXT, mTrailerTitle + "  " + mTrailerPath);
        }

        return myShareIntent;
    }
}
