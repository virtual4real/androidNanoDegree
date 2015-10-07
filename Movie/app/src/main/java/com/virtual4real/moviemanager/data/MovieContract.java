package com.virtual4real.moviemanager.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import com.virtual4real.moviemanager.sync.restapi.RestApiContract;

/**
 * Created by ioanagosman on 28/09/15.
 */
public class MovieContract {
    //name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.virtual4real.moviemanager.app";

    //base of all URI's which apps will use to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_SETTINGS = "settings";
    public static final String PATH_MOVIES = "movies";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the settings table */
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

    public static final class MovieSummaryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;


        public static final String PAGINATION_TOTAL_RESULTS = "TOTAL_RESULTS";
        public static final String PAGINATION_TOTAL_PAGES = "TOTAL_PAGES";
        public static final String PAGINATION_CURRENT_PAGE = "CURRENT_PAGE";

        public static final String MOVIE_SUMMARY_POSITION = "POSITION";
        public static final String SORT_TYPE = "SORTTYPE";

        public static final String SORT_KEY_POPULARITY_ASC = "popularity.asc";
        public static final String SORT_KEY_POPULARITY_DESC = "popularity.desc";
        public static final String SORT_KEY_VOTE_ASC = "vote_average.asc";
        public static final String SORT_KEY_VOTE_DESC = "vote_average.desc";
        public static final String SORT_KEY_RELEASE_ASC = "release_date.asc";
        public static final String SORT_KEY_RELEASE_DESC = "release_date.desc";


        public static Uri buildMovieSummaryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getMovieSummaryId(Uri uri) {
            return Integer.parseInt(uri.getLastPathSegment());
        }

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




    /* Inner class that defines the table contents of the movie table */
//    public static final class MovieEntry implements BaseColumns {
//
//        public static final Uri CONTENT_URI =
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
//
//        public static final String CONTENT_TYPE =
//                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
//        public static final String CONTENT_ITEM_TYPE =
//                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
//
//        public static final String TABLE_NAME = "movie";
//
//        // Column with the foreign key into the location table.
//        public static final String COLUMN_LOC_KEY = "location_id";
//        // Date, stored as long in milliseconds since the epoch
//        public static final String COLUMN_DATE = "date";
//        // Weather id as returned by API, to identify the icon to be used
//        public static final String COLUMN_WEATHER_ID = "weather_id";
//
//        // Short description and long description of the weather, as provided by API.
//        // e.g "clear" vs "sky is clear".
//        public static final String COLUMN_SHORT_DESC = "short_desc";
//
//        // Min and max temperatures for the day (stored as floats)
//        public static final String COLUMN_MIN_TEMP = "min";
//        public static final String COLUMN_MAX_TEMP = "max";
//
//        // Humidity is stored as a float representing percentage
//        public static final String COLUMN_HUMIDITY = "humidity";
//
//        // Humidity is stored as a float representing percentage
//        public static final String COLUMN_PRESSURE = "pressure";
//
//        // Windspeed is stored as a float representing windspeed  mph
//        public static final String COLUMN_WIND_SPEED = "wind";
//
//        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
//        public static final String COLUMN_DEGREES = "degrees";
//
//        public static Uri buildWeatherUri(long id) {
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }
//
//        /*
//            Student: This is the buildWeatherLocation function you filled in.
//         */
//        public static Uri buildMovie(String locationSetting) {
//            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
//        }
//
//        public static Uri buildWeatherLocationWithStartDate(
//                String locationSetting, long startDate) {
//            long normalizedDate = normalizeDate(startDate);
//            return CONTENT_URI.buildUpon().appendPath(locationSetting)
//                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
//        }
//
//        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
//            return CONTENT_URI.buildUpon().appendPath(locationSetting)
//                    .appendPath(Long.toString(normalizeDate(date))).build();
//        }
//
//        public static String getLocationSettingFromUri(Uri uri) {
//            return uri.getPathSegments().get(1);
//        }
//
//        public static long getDateFromUri(Uri uri) {
//            return Long.parseLong(uri.getPathSegments().get(2));
//        }
//
//        public static long getStartDateFromUri(Uri uri) {
//            String dateString = uri.getQueryParameter(COLUMN_DATE);
//            if (null != dateString && dateString.length() > 0)
//                return Long.parseLong(dateString);
//            else
//                return 0;
//        }
//    }
}
