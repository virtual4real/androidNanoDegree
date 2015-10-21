package com.virtual4real.moviemanager.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import com.virtual4real.moviemanager.sync.restapi.RestApiContract;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by ioanagosman on 28/09/15.
 */

/**
 * contract for the content provider used for movies
 */
public class MovieContract {
    //name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.virtual4real.moviemanager.app";

    //base of all URI's which apps will use to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_SETTINGS = "settings";
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_ORDER_MOVIES = "movies_order";
    public static final String PATH_OPERATIONS = "operations";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static String formatDate(long dateInMilliseconds) {
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }

    /**
     * Inner class that defines the table contents of the settings table
     */
    public static final class SettingEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SETTINGS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SETTINGS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SETTINGS;

        public static Uri buildSettingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * Inner class that defines the table contents of the movie order table
     */
    public static final class MovieOrderEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDER_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ORDER_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ORDER_MOVIES;


        public static final String PAGINATION_TOTAL_RESULTS = "TOTAL_RESULTS";
        public static final String PAGINATION_TOTAL_PAGES = "TOTAL_PAGES";
        public static final String PAGINATION_CURRENT_PAGE = "CURRENT_PAGE";

        public static final String MOVIE_SUMMARY_POSITION = "POSITION";
        public static final String SORT_TYPE = "SORTTYPE";
        public static final String OPERATION_ID = "OPERATIONID";

        public static final String SORT_KEY_POPULARITY_ASC = "popularity.asc";
        public static final String SORT_KEY_POPULARITY_DESC = "popularity.desc";
        public static final String SORT_KEY_VOTE_ASC = "vote_average.asc";
        public static final String SORT_KEY_VOTE_DESC = "vote_average.desc";
        public static final String SORT_KEY_RELEASE_ASC = "release_date.asc";
        public static final String SORT_KEY_RELEASE_DESC = "release_date.desc";


        public static Uri buildMovieOrderUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Get sort type stored in db based on the string from rest api
         *
         * @param sortType string from content provider defining the possible sort types
         * @return integer representing the sort type as it is stored in the database
         */
        public static int GetSortTypeIntFromInterface(String sortType) {
            if (null == sortType) {
                return 0;
            }

            if (sortType.equals(SORT_KEY_POPULARITY_DESC)) {
                return 1;
            }

            if (sortType.equals(SORT_KEY_VOTE_DESC)) {
                return 2;
            }

            if (sortType.equals(SORT_KEY_POPULARITY_ASC)) {
                return 3;
            }

            if (sortType.equals(SORT_KEY_VOTE_ASC)) {
                return 4;
            }

            if (sortType.equals(SORT_KEY_RELEASE_ASC)) {
                return 5;
            }

            if (sortType.equals(SORT_KEY_RELEASE_DESC)) {
                return 6;
            }

            return 0;

        }

        /**
         * Get sort type stored in db based on the string from rest api
         * @param sortType string from rest api defining the possible sort types
         * @return integer representing the sort type as it is stored in the database
         */
        public static int GetSortTypeInt(String sortType) {
            if (null == sortType) {
                return 0;
            }

            if (sortType.equals(RestApiContract.SORT_KEY_POPULARITY_DESC)) {
                return 1;
            }

            if (sortType.equals(RestApiContract.SORT_KEY_VOTE_DESC)) {
                return 2;
            }

            if (sortType.equals(RestApiContract.SORT_KEY_POPULARITY_ASC)) {
                return 3;
            }

            if (sortType.equals(RestApiContract.SORT_KEY_VOTE_ASC)) {
                return 4;
            }

            if (sortType.equals(RestApiContract.SORT_KEY_RELEASE_ASC)) {
                return 5;
            }

            if (sortType.equals(RestApiContract.SORT_KEY_RELEASE_DESC)) {
                return 6;
            }

            return 0;
        }
    }

    /**
     * Inner class for movie summary entity
     */
    public static final class MovieSummaryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;


        public static Uri buildMovieSummaryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getMovieSummaryId(Uri uri) {
            return Integer.parseInt(uri.getLastPathSegment());
        }

    }

    /**
     * Defining operations with SyncOperation table
     */
    public static final class SyncOperationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_OPERATIONS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_OPERATIONS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_OPERATIONS;

        public static Uri buildSettingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }



}
