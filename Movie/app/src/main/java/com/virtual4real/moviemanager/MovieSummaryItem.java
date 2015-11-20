package com.virtual4real.moviemanager;

import android.database.Cursor;

import com.virtual4real.moviemanager.database.MovieSummaryColumns;


/**
 * Created by ioanagosman on 24/09/15.
 */
public class MovieSummaryItem {
    private String mName;
    private String mThumbnail;
    private int nMovieId;
    private int nYear;
    private float dRating;
    private int nFavorite;

    public int getFavorite() {
        return nFavorite;
    }

    public void setFavorite(int n) {
        nFavorite = n;
    }

    public int getMovieId() {
        return nMovieId;
    }

    public void setMovieId(int nMovieId) {
        this.nMovieId = nMovieId;
    }

    public float getRating() {
        return dRating;
    }

    public void setRating(float dRating) {
        this.dRating = dRating;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.mThumbnail = thumbnail;
    }

    public int getYear() {
        return nYear;
    }

    public void setYear(int nYear) {
        this.nYear = nYear;
    }

    public static MovieSummaryItem fromCursor(Cursor cursor) {
        MovieSummaryItem item = new MovieSummaryItem();

        item.setName(cursor.getString(cursor.getColumnIndex(MovieSummaryColumns.TITLE)));
        item.setThumbnail(cursor.getString(cursor.getColumnIndex(MovieSummaryColumns.POSTER_PATH)));
        item.setMovieId(cursor.getInt(cursor.getColumnIndex(MovieSummaryColumns.MOVIE_ID)));
        item.setRating(cursor.getFloat(cursor.getColumnIndex(MovieSummaryColumns.VOTE_AVERAGE)));
        item.setYear(cursor.getInt(cursor.getColumnIndex(MovieSummaryColumns.YEAR_OF_RELEASE)));
        item.setFavorite(cursor.getInt(cursor.getColumnIndex(MovieSummaryColumns.IS_FAVORITE)));

        return item;
    }
}
