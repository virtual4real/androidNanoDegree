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
    static final int LOCATION = 300;


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
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/*", MOVIE_DETAIL_ID);
//
//        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
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
            case MOVIE_SUMMARY: {
                DbService service = new DbService();

                int nOrder = MovieContract.MovieSummaryEntry.GetSortTypeIntFromInterface(sortOrder);
                //TODO: get the page from the selection parameter
                retCursor = service.GetMovieSummaries(nOrder, 1);
                break;
            }

            case MOVIE_DETAIL_ID: {
                DbService service = new DbService();
                retCursor = service.GetMovieSummaryCursorByMovieId(Long.parseLong(selection));
                break;
            }
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    private UrlSettings getUrlSetting(ContentValues values) {
        UrlSettings url = new UrlSettings();

        url.setDateUpdated(MovieContract.normalizeDate(System.currentTimeMillis()));
        ;
        url.setBaseUrl(values.getAsString(UrlSettings$Table.BASEURL));
        url.setSecureBaseUrl(values.getAsString(UrlSettings$Table.SECUREBASEURL));

        url.setLogoSizeUrl(values.getAsString(UrlSettings$Table.LOGOSIZEURL));
        url.setBackdropSizeUrl(values.getAsString(UrlSettings$Table.BACKDROPSIZEURL));
        url.setPosterSizeUrl(values.getAsString(UrlSettings$Table.POSTERSIZEURL));
        url.setProfileSizeUrl(values.getAsString(UrlSettings$Table.PROFILESIZEURL));
        url.setStillSizeUrl(values.getAsString(UrlSettings$Table.STILLSIZEURL));

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
                returnUri = MovieContract.SettingEntry.buildSettingUri(id);
                break;
            }
            case MOVIE_SUMMARY: {
                DbService service = new DbService();
                MovieSummary movie = getMovieSummary(service, values);
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
                UrlSettings url = getUrlSetting(values);
                service.InsertUrlSetting(url);
                break;
            }
            case MOVIE_SUMMARY: {
                DbService service = new DbService();
                MovieSummary movie = getMovieSummary(service, values);
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
                    int page = values[0].getAsInteger(MovieContract.MovieSummaryEntry.PAGINATION_CURRENT_PAGE);
                    int totalPages = values[0].getAsInteger(MovieContract.MovieSummaryEntry.PAGINATION_TOTAL_PAGES);
                    int totalResults = values[0].getAsInteger(MovieContract.MovieSummaryEntry.PAGINATION_TOTAL_RESULTS);
                    int sortType = values[0].getAsInteger(MovieContract.MovieSummaryEntry.SORT_TYPE);

                    DbService service = new DbService();

                    service.DeleteMovieOrder(page, sortType);

                    for (int i = 1; i < values.length; i++) {
                        MovieSummary movieSummary = getMovieSummary(service, values[i]);
                        service.InsertMovieSummary(movieSummary);

                        MovieOrder movieOrder = new MovieOrder();
                        movieOrder.setDateUpdated(MovieContract.normalizeDate(System.currentTimeMillis()));
                        movieOrder.setMovieSummary(movieSummary);
                        movieOrder.setPage(page);
                        movieOrder.setPosition(values[i].getAsInteger(MovieContract.MovieSummaryEntry.MOVIE_SUMMARY_POSITION));
                        movieOrder.setSortType(sortType);

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

    private MovieSummary getMovieSummary(DbService service, ContentValues values) {
        long movieId = values.getAsLong(MovieSummary$Table.MOVIEID);
        MovieSummary movieSummary = service.GetMovieSummaryByMovieId(movieId);

        if (null == movieSummary) {
            movieSummary = new MovieSummary();
            movieSummary.setDateUpdated(System.currentTimeMillis());
        }

        movieSummary.setMovieId(values.getAsInteger(MovieSummary$Table.MOVIEID));
        movieSummary.setOriginalTitle(values.getAsString(MovieSummary$Table.ORIGINALTITLE));
        movieSummary.setTitle(values.getAsString(MovieSummary$Table.TITLE));
        movieSummary.setPosterPath(values.getAsString(MovieSummary$Table.POSTERPATH));
        movieSummary.setPopularity(values.getAsDouble(MovieSummary$Table.POPULARITY));
        movieSummary.setVoteAverage(values.getAsDouble(MovieSummary$Table.VOTEAVERAGE));
        movieSummary.setVoteCount(values.getAsInteger(MovieSummary$Table.VOTECOUNT));
        movieSummary.setBackdropPath(values.getAsString(MovieSummary$Table.BACKDROPPATH));
        movieSummary.setOverview(values.getAsString(MovieSummary$Table.OVERVIEW));

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String sDate = values.getAsString(MovieSummary$Table.RELEASEDATE);
            Date dt = null;
            if (null != sDate) {
                dt = f.parse(sDate);

                Calendar cal = Calendar.getInstance();
                cal.setTime(dt);

                movieSummary.setYearOfRelease(cal.get(Calendar.YEAR));
            }
            movieSummary.setReleaseDate(null == dt ? MovieContract.normalizeDate(System.currentTimeMillis()) :
                    MovieContract.normalizeDate(dt.getTime()));
        } catch (ParseException exex) {
            movieSummary.setReleaseDate(MovieContract.normalizeDate(System.currentTimeMillis()));
        }

        return movieSummary;
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
