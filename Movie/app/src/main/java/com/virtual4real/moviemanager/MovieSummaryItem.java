package com.virtual4real.moviemanager;

import android.database.Cursor;

import com.virtual4real.moviemanager.database.MovieSummary;
import com.virtual4real.moviemanager.database.MovieSummary$Table;

/**
 * Created by ioanagosman on 24/09/15.
 */
public class MovieSummaryItem {
    private String mName;
    private String mThumbnail;
    private int nMovieId;
    private float dRating;

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

    public static MovieSummaryItem fromCursor(Cursor cursor) {
        MovieSummaryItem item = new MovieSummaryItem();

        item.setName(cursor.getString(cursor.getColumnIndex(MovieSummary$Table.TITLE)));
        item.setThumbnail(cursor.getString(cursor.getColumnIndex(MovieSummary$Table.POSTERPATH)));
        item.setMovieId(cursor.getInt(cursor.getColumnIndex(MovieSummary$Table.MOVIEID)));
        item.setRating(cursor.getFloat(cursor.getColumnIndex(MovieSummary$Table.VOTEAVERAGE)));

        return item;
    }
}
