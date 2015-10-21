package com.virtual4real.moviemanager.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;

import com.virtual4real.moviemanager.R;
import com.virtual4real.moviemanager.database.DbService;
import com.virtual4real.moviemanager.database.MovieOrder;
import com.virtual4real.moviemanager.database.MovieSummary;
import com.virtual4real.moviemanager.database.MovieSummary$Table;
import com.virtual4real.moviemanager.database.SyncOperation;
import com.virtual4real.moviemanager.database.SyncOperation$Table;
import com.virtual4real.moviemanager.database.UrlSettings;
import com.virtual4real.moviemanager.database.UrlSettings$Table;
import com.virtual4real.moviemanager.sync.DataTransformer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by ioanagosman on 28/09/15.
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    //private WeatherDbHelper mOpenHelper;

    static final int SETTINGS = 100;
    static final int MOVIE_SUMMARY = 101;
    static final int MOVIE_DETAIL_ID = 102;
    static final int OPERATIONS = 103;


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_SETTINGS, SETTINGS);
        matcher.addURI(authority, MovieContract.PATH_OPERATIONS, OPERATIONS);
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIE_SUMMARY);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/*", MOVIE_DETAIL_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SETTINGS:
                return MovieContract.SettingEntry.CONTENT_ITEM_TYPE;
            case MOVIE_SUMMARY:
                return MovieContract.MovieSummaryEntry.CONTENT_TYPE;
            case MOVIE_DETAIL_ID:
                return MovieContract.MovieSummaryEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
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
            case OPERATIONS: {
                DbService service = new DbService();

                //TODO: put these strings in resources
                String sMaxDate = getParameter(selectionArgs, 0, getContext().getString(R.string.default_max_date));
                String sMinDate = getParameter(selectionArgs, 1, getContext().getString(R.string.default_min_date));
                int nVotes = Integer.parseInt(getParameter(selectionArgs, 2, getContext().getString(R.string.default_min_votes)));
                boolean bAdult = Boolean.parseBoolean(getParameter(selectionArgs, 3, getContext().getString(R.string.default_adult)));
                boolean bVideo = Boolean.parseBoolean(getParameter(selectionArgs, 4, getContext().getString(R.string.default_video)));

                retCursor = service.GetOperations(sMinDate, sMaxDate, nVotes, bAdult, bVideo);
                break;
            }
            case MOVIE_SUMMARY: {
                DbService service = new DbService();

                int nOrder = MovieContract.MovieOrderEntry.GetSortTypeIntFromInterface(selectionArgs[1]);
                int nPage = Integer.parseInt(selectionArgs[0]);
                //TODO: other paramters from selectionArgs
                retCursor = service.GetMovieSummaries(nOrder, nPage);
                break;
            }

            case MOVIE_DETAIL_ID: {
                DbService service = new DbService();
                retCursor = service.GetMovieSummaryCursorByMovieId(Long.parseLong(selection));
                break;
            }
        }

        if (null != retCursor) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    /**
     * Helper method to get the value at a position in an array or a default value
     *
     * @param args          the array in which to search for a value
     * @param nPos          the position where the value should be found
     * @param sDefaultValue if the value was not found at the specified position, return the default value
     * @return
     */
    private String getParameter(String[] args, int nPos, String sDefaultValue) {
        if (null == args || args.length < nPos + 1) {
            return sDefaultValue;
        }

        return args[nPos];
    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (match) {
            case SETTINGS: {
                DbService service = new DbService();
                UrlSettings url = DataTransformer.getUrlSetting(values);
                long id = service.InsertUrlSetting(url);
                returnUri = MovieContract.SettingEntry.buildSettingUri(id);
                break;
            }
            case OPERATIONS: {
                DbService service = new DbService();
                SyncOperation ops = DataTransformer.getSyncOperation(values);
                long id = service.InsertSyncOperation(ops);
                returnUri = MovieContract.SyncOperationEntry.buildSettingUri(id);
                break;
            }
            case MOVIE_SUMMARY: {
                DbService service = new DbService();
                MovieSummary movie = DataTransformer.getMovieSummary(service, values);
                movie.save();
                returnUri = MovieContract.MovieSummaryEntry.buildMovieSummaryUri(movie.getMovieId());
                break;
            }
            default:
                break;


        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int nCount = 0;

        String[] vIds = (null == selection ? null : selection.split(Pattern.quote(";")));

        switch (match) {
            case SETTINGS: {
                DbService service = new DbService();
                if (null == vIds) {
                    service.DeleteAllUrlSettings();
                } else {
                    for (int i = 0; i < vIds.length; i++) {
                        service.DeleteUrlSettings(Long.parseLong(vIds[i]));
                        nCount++;
                    }
                }
                break;
            }
            case OPERATIONS: {
                DbService service = new DbService();
                service.DeleteAllSyncOperationAndOrders();
                break;
            }
            case MOVIE_SUMMARY: {
                DbService service = new DbService();
                if (null == vIds) {
                    service.DeleteAllMovieSummary();
                } else {
                    for (int i = 0; i < vIds.length; i++) {
                        service.DeleteMovieSummary(Long.parseLong(vIds[i]));
                        nCount++;
                    }
                }
                break;
            }
            default:
                return 0;


        }

        getContext().getContentResolver().notifyChange(uri, null);
        return nCount;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SETTINGS: {
                DbService service = new DbService();
                UrlSettings url = DataTransformer.getUrlSetting(values);
                service.InsertUrlSetting(url);
                break;
            }
            case MOVIE_SUMMARY: {
                DbService service = new DbService();
                MovieSummary movie = DataTransformer.getMovieSummary(service, values);
                service.InsertMovieSummary(movie);
                break;
            }
            default:
                return 0;


        }

        getContext().getContentResolver().notifyChange(uri, null);
        return 1;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int nInsert = 0;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE_SUMMARY:
                try {
                    //TODO: store total pages and total results (maybe in app settings)
                    int page = values[0].getAsInteger(MovieContract.MovieOrderEntry.PAGINATION_CURRENT_PAGE);
                    int totalPages = values[0].getAsInteger(MovieContract.MovieOrderEntry.PAGINATION_TOTAL_PAGES);
                    int totalResults = values[0].getAsInteger(MovieContract.MovieOrderEntry.PAGINATION_TOTAL_RESULTS);
                    int sortType = values[0].getAsInteger(MovieContract.MovieOrderEntry.SORT_TYPE);
                    long operationid = values[0].getAsLong(MovieContract.MovieOrderEntry.OPERATION_ID);

                    DbService service = new DbService();

                    SyncOperation sync = service.GetSyncOperationById(operationid);

                    service.DeleteMovieOrder(page, sortType);

                    for (int i = 1; i < values.length; i++) {
                        MovieSummary movieSummary = DataTransformer.getMovieSummary(service, values[i]);
                        service.InsertMovieSummary(movieSummary);

                        MovieOrder movieOrder = new MovieOrder();
                        movieOrder.setDateUpdated(MovieContract.normalizeDate(System.currentTimeMillis()));
                        movieOrder.setMovieSummary(movieSummary);
                        movieOrder.setPage(page);
                        movieOrder.setPosition(values[i].getAsInteger(MovieContract.MovieOrderEntry.MOVIE_SUMMARY_POSITION));
                        movieOrder.setSortType(sortType);
                        movieOrder.setSyncOperation(sync);

                        service.InsertMovieOrder(movieOrder, movieSummary);
                        nInsert++;
                    }

                    getContext().getContentResolver().notifyChange(uri, null);

                } catch (Exception e) {
                    return 0;
                }

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return nInsert;
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
