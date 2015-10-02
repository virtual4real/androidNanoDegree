package com.virtual4real.moviemanager.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;

import com.virtual4real.moviemanager.database.DbService;
import com.virtual4real.moviemanager.database.MovieOrder;
import com.virtual4real.moviemanager.database.MovieSummary;
import com.virtual4real.moviemanager.database.MovieSummary$Table;
import com.virtual4real.moviemanager.database.UrlSettings;
import com.virtual4real.moviemanager.database.UrlSettings$Table;

/**
 * Created by ioanagosman on 28/09/15.
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    //private WeatherDbHelper mOpenHelper;

    static final int SETTINGS = 100;
    static final int MOVIE_SUMMARY = 101;
    static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    static final int LOCATION = 300;

    //private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static {
        //sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
//        sWeatherByLocationSettingQueryBuilder.setTables(
//                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
//                        WeatherContract.LocationEntry.TABLE_NAME +
//                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
//                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
//                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
//                        "." + WeatherContract.LocationEntry._ID);
    }

    //location.location_setting = ?
    /*private static final String sLocationSettingSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    //location.location_setting = ? AND date >= ?
    private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";

    //location.location_setting = ? AND date = ?
    private static final String sLocationSettingAndDaySelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";*/

/*    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        long startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
            selection = sLocationSettingWithStartDateSelection;
        }

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByLocationSettingAndDate(
            Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        long date = WeatherContract.WeatherEntry.getDateFromUri(uri);

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndDaySelection,
                new String[]{locationSetting, Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }*/

    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
//
//        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_SETTINGS, SETTINGS);
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIE_SUMMARY);
//        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);
//
//        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        //mOpenHelper = new WeatherDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {
        return "";

//        // Use the Uri Matcher to determine what kind of URI this is.
//        final int match = sUriMatcher.match(uri);
//
//        switch (match) {
//            // Student: Uncomment and fill out these two cases
//            case WEATHER_WITH_LOCATION_AND_DATE:
//                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
//            case WEATHER_WITH_LOCATION:
//                return WeatherContract.WeatherEntry.CONTENT_TYPE;
//            case WEATHER:
//                return WeatherContract.WeatherEntry.CONTENT_TYPE;
//            case LOCATION:
//                return WeatherContract.LocationEntry.CONTENT_TYPE;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor = null;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SETTINGS: {
                DbService service = new DbService();
                retCursor = service.GetUrlSetting();
                break;
            }
            case MOVIE_SUMMARY: {
                DbService service = new DbService();
                retCursor = service.GetMovieSummaries();
                break;
            }
        }

        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        //Cursor retCursor;
//        switch (sUriMatcher.match(uri)) {
//            // "weather/*/*"
//            case WEATHER_WITH_LOCATION_AND_DATE:
//            {
//                retCursor = getWeatherByLocationSettingAndDate(uri, projection, sortOrder);
//                break;
//            }
//            // "weather/*"
//            case WEATHER_WITH_LOCATION: {
//                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
//                break;
//            }
//            // "weather"
//            case WEATHER: {
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        WeatherContract.WeatherEntry.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder
//                );
//                break;
//            }
//            // "location"
//            case LOCATION: {
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        WeatherContract.LocationEntry.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder
//                );
//                break;
//            }
//
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    private UrlSettings getUrlSetting(ContentValues values) {
        UrlSettings url = new UrlSettings();

        url.setBaseUrl(values.getAsString(UrlSettings$Table.BASEURL));
        url.setSecureBaseUrl(values.getAsString(UrlSettings$Table.SECUREBASEURL));

        return url;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (match) {
            case SETTINGS: {
                DbService service = new DbService();

                UrlSettings url = getUrlSetting(values);
                long id = service.InsertUrlSetting(url);
                service.DeleteUrlSettings(id);
                returnUri = MovieContract.SettingEntry.buildSettingUri(id);
                break;
            }
            case MOVIE_SUMMARY: {
                break;
            }
            default:
                break;


        }

//        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
//        final int match = sUriMatcher.match(uri);
//        Uri returnUri;
//
//        switch (match) {
//            case WEATHER: {
//                normalizeDate(values);
//                long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
//                if ( _id > 0 )
//                    returnUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
//                else
//                    throw new android.database.SQLException("Failed to insert row into " + uri);
//                break;
//            }
//            case LOCATION: {
//                long _id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);
//                if ( _id > 0 )
//                    returnUri = WeatherContract.LocationEntry.buildLocationUri(_id);
//                else
//                    throw new android.database.SQLException("Failed to insert row into " + uri);
//                break;
//            }
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//        getContext().getContentResolver().notifyChange(uri, null);
//        return returnUri;
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
//        final int match = sUriMatcher.match(uri);
//        int rowsDeleted;
//        // this makes delete all rows return the number of rows deleted
//        if ( null == selection ) selection = "1";
//        switch (match) {
//            case WEATHER:
//                rowsDeleted = db.delete(
//                        WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            case LOCATION:
//                rowsDeleted = db.delete(
//                        WeatherContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//        // Because a null deletes all rows
//        if (rowsDeleted != 0) {
//            getContext().getContentResolver().notifyChange(uri, null);
//        }
//        return rowsDeleted;
        return 0;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
//        if (values.containsKey(WeatherContract.WeatherEntry.COLUMN_DATE)) {
//            long dateValue = values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
//            values.put(WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.normalizeDate(dateValue));
//        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
//        final int match = sUriMatcher.match(uri);
//        int rowsUpdated;
//
//        switch (match) {
//            case WEATHER:
//                normalizeDate(values);
//                rowsUpdated = db.update(WeatherContract.WeatherEntry.TABLE_NAME, values, selection,
//                        selectionArgs);
//                break;
//            case LOCATION:
//                rowsUpdated = db.update(WeatherContract.LocationEntry.TABLE_NAME, values, selection,
//                        selectionArgs);
//                break;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//        if (rowsUpdated != 0) {
//            getContext().getContentResolver().notifyChange(uri, null);
//        }
//        return rowsUpdated;
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
//        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE_SUMMARY:
                int page = values[0].getAsInteger(MovieContract.MovieSummaryEntry.PAGINATION_CURRENT_PAGE);
                int totalPages = values[0].getAsInteger(MovieContract.MovieSummaryEntry.PAGINATION_TOTAL_PAGES);
                int totalResults = values[0].getAsInteger(MovieContract.MovieSummaryEntry.PAGINATION_TOTAL_RESULTS);
                int sortType = values[0].getAsInteger(MovieContract.MovieSummaryEntry.SORT_TYPE);

                DbService service = new DbService();

                service.DeleteMovieOrder(page, sortType);

                for (int i = 1; i < values.length; i++) {
                    long movieId = values[i].getAsLong(MovieSummary$Table.MOVIEID);
                    MovieSummary movieSummary = service.GetMovieSummaryByMovieId(movieId);
                    if (null == movieSummary) {
                        movieSummary = new MovieSummary();
                    }

                    movieSummary.setMovieId(values[i].getAsInteger(MovieSummary$Table.MOVIEID));
                    movieSummary.setOriginalTitle(values[i].getAsString(MovieSummary$Table.ORIGINALTITLE));
                    movieSummary.setTitle(values[i].getAsString(MovieSummary$Table.TITLE));
                    movieSummary.setPosterPath(values[i].getAsString(MovieSummary$Table.POSTERPATH));
                    movieSummary.setPopularity(values[i].getAsDouble(MovieSummary$Table.POPULARITY));
                    movieSummary.setVoteAverage(values[i].getAsDouble(MovieSummary$Table.VOTEAVERAGE));
                    movieSummary.setVoteCount(values[i].getAsInteger(MovieSummary$Table.VOTECOUNT));
                    //movieSummary.setReleaseDate(values[i].getAsInteger(MovieSummary$Table.RELEASEDATE));
                    //movieSummary.setDateUpdated(200);

                    service.InsertMovieSummary(movieSummary);

                    MovieOrder movieOrder = new MovieOrder();
                    //movieOrder.setDateUpdated(200);
                    movieOrder.setMovieSummary(movieSummary);
                    movieOrder.setPage(page);
                    movieOrder.setPosition(values[i].getAsInteger(MovieContract.MovieSummaryEntry.MOVIE_SUMMARY_POSITION));
                    movieOrder.setSortType(sortType);

                    service.InsertMovieOrder(movieOrder, movieSummary);
                }

//                db.beginTransaction();
//                int returnCount = 0;
//                try {
//                    for (ContentValues value : values) {
//                        normalizeDate(value);
//                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
//                        if (_id != -1) {
//                            returnCount++;
//                        }
//                    }
//                    db.setTransactionSuccessful();
//                } finally {
//                    db.endTransaction();
//                }
//                getContext().getContentResolver().notifyChange(uri, null);
//                return returnCount;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return 0;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        //mOpenHelper.close();
        super.shutdown();
    }
}
