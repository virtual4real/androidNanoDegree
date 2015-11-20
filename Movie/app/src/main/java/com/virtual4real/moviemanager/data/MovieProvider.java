package com.virtual4real.moviemanager.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.format.Time;

import com.virtual4real.moviemanager.database.MovieDatabase;
import com.virtual4real.moviemanager.database.MovieOrderColumns;
import com.virtual4real.moviemanager.database.MovieReviewColumns;
import com.virtual4real.moviemanager.database.MovieSummaryColumns;
import com.virtual4real.moviemanager.database.MovieTrailerColumns;
import com.virtual4real.moviemanager.database.SyncOperationColumns;
import com.virtual4real.moviemanager.database.UrlSettingsColumns;
import com.virtual4real.moviemanager.sync.restapi.RestApiContract;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyBulkInsert;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.NotifyUpdate;
import net.simonvt.schematic.annotation.TableEndpoint;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by ioanagosman on 28/09/15.
 */
@ContentProvider(authority = MovieProvider.AUTHORITY,
        database = MovieDatabase.class,
        name = "MovieProvider",
        packageName = "com.virtual4real.moviemanager.provider")
public final class MovieProvider {

    public MovieProvider() {
    }

    public static final int FAVOURITE_SEARCH = 7;

    public static final String AUTHORITY = "com.virtual4real.moviemanager.app";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static String[] getMovieSummaryProjection() {
        String[] cols = new String[]{
                MovieSummaryColumns.ID, MovieSummaryColumns.BACKDROP_PATH, MovieSummaryColumns.DATE_RELEASE,
                MovieSummaryColumns.DATE_UPDATED, MovieSummaryColumns.MOVIE_ID, MovieSummaryColumns.ORIGINAL_TITLE,
                MovieSummaryColumns.OVERVIEW, MovieSummaryColumns.POPULARITY, MovieSummaryColumns.POSTER_PATH,
                MovieSummaryColumns.TITLE, MovieSummaryColumns.VOTE_AVERAGE, MovieSummaryColumns.VOTE_COUNT,
                MovieSummaryColumns.IS_FAVORITE, MovieSummaryColumns.YEAR_OF_RELEASE};

        return getColsWithTable(cols, MovieDatabase.Tables.MOVIE_SUMMARY);
    }

    public static String[] getMovieTrailerProjection() {
        String[] cols = new String[]{
                MovieTrailerColumns.ID + " as _id ", MovieTrailerColumns.NAME, MovieTrailerColumns.SIZE,
                MovieTrailerColumns.SOURCE, MovieTrailerColumns.TYPE};

        return getColsWithTable(cols, MovieDatabase.Tables.MOVIE_TRAILER);

    }

    public static String[] getMovieReviewProjection() {
        String[] cols = new String[]{
                MovieReviewColumns.ID + " as _id ", MovieReviewColumns.AUTHOR, MovieReviewColumns.URL,
                MovieReviewColumns.CONTENT, MovieReviewColumns.REVIEW_ID};

        return getColsWithTable(cols, MovieDatabase.Tables.MOVIE_REVIEW);

    }

    public static ContentValues getMovieSummaryFavoriteContentValues(int nValue) {
        ContentValues cv = new ContentValues();
        cv.put(MovieSummaryColumns.IS_FAVORITE, nValue);

        return cv;
    }

    private static String[] getColsWithTable(String[] cols, String sTable) {
        String[] result = new String[cols.length];

        for (int i = 0; i < cols.length; i++) {
            result[i] = sTable + "." + cols[i];
        }

        return result;
    }


    interface Path {
        String SETTINGS = "settings";
        String MOVIE_SUMMARY = "movie_summary";
        String ORDER_MOVIES = "movies_order";
        String SYNC_OPERATIONS = "operations";
        String MOVIE_DETAIL = "moviedetail";
        String MOVIE_TRAILER = "movietrailer";
        String MOVIE_REVIEW = "moviereview";
        String BY_ORDER_PAGE = "byorderandpage";
        String BY_FAVORITES = "byfavorites";
    }

    public static Uri getMovieSummaryUri() {
        return BASE_CONTENT_URI.buildUpon().appendPath(Path.MOVIE_SUMMARY).build();
    }


    public static Uri getMovieOrderUri() {
        return BASE_CONTENT_URI.buildUpon().appendPath(Path.ORDER_MOVIES).build();
    }

    public static Uri getSeetingsUri() {
        return BASE_CONTENT_URI.buildUpon().appendPath(Path.SETTINGS).build();
    }

    public static Uri getMovieDetailUri(long movieId) {
        return ContentUris.withAppendedId(BASE_CONTENT_URI.buildUpon().appendPath(Path.MOVIE_DETAIL).build(), movieId);
    }

    public static Uri getMovieTrailerUri(long movieId) {
        return ContentUris.withAppendedId(BASE_CONTENT_URI.buildUpon().appendPath(Path.MOVIE_TRAILER).build(), movieId);
    }

    public static Uri getMovieReviewUri(long movieId) {
        return ContentUris.withAppendedId(BASE_CONTENT_URI.buildUpon().appendPath(Path.MOVIE_REVIEW).build(), movieId);
    }

    public static Uri getOperationUri() {
        return BASE_CONTENT_URI.buildUpon().appendPath(Path.SYNC_OPERATIONS).build();
    }

    public static Uri buildMovieSummaryUri(long id) {
        return ContentUris.withAppendedId(getMovieSummaryUri(), id);
    }


    public static Uri buildMovieSummaryUri(int nPage, int nSortType) {
        return ContentUris.withAppendedId(ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(Path.MOVIE_SUMMARY).appendPath(Path.BY_ORDER_PAGE).build(),
                nPage), nSortType);
    }

    public static Uri buildMovieSummaryUriForFavorites(int nFavorites) {
        return ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(Path.MOVIE_SUMMARY).appendPath(Path.BY_FAVORITES).build(),
                nFavorites);

    }


    public static int getMovieSummaryId(Uri uri) {
        return Integer.parseInt(uri.getLastPathSegment());
    }

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

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = MovieDatabase.Tables.URL_SETTINGS)
    public static class UrlSettings {

        @ContentUri(
                path = Path.SETTINGS,
                type = "vnd.android.cursor.dir/settings",
                defaultSort = UrlSettingsColumns.DATE_UPDATED + " DESC")
        public static final Uri CONTENT_URI = buildUri(Path.SETTINGS);

        @InexactContentUri(
                path = Path.SETTINGS + "/#",
                name = "ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = UrlSettingsColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.SETTINGS, String.valueOf(id));
        }

    }

    @TableEndpoint(table = MovieDatabase.Tables.SYNC_OPERATION)
    public static class SyncOperation {

        @ContentUri(
                path = Path.SYNC_OPERATIONS,
                type = "vnd.android.cursor.dir/settings",
                defaultSort = SyncOperationColumns.DATE_UPDATED + " DESC")
        public static final Uri CONTENT_URI = buildUri(Path.SYNC_OPERATIONS);

        @InexactContentUri(
                path = Path.SYNC_OPERATIONS + "/#",
                name = "ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = SyncOperationColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.SYNC_OPERATIONS, String.valueOf(id));
        }

    }

    @TableEndpoint(table = MovieDatabase.Tables.MOVIE_SUMMARY)
    public static class MovieSummary {

        @ContentUri(
                path = Path.MOVIE_SUMMARY,
                type = "vnd.android.cursor.dir/moviesummary")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIE_SUMMARY);

        @InexactContentUri(
                name = "MOVIE_SUMMARY_ID",
                path = Path.MOVIE_SUMMARY + "/#",
                type = "vnd.android.cursor.item/moviesummary",
                whereColumn = MovieSummaryColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.MOVIE_SUMMARY, String.valueOf(id));
        }

        @InexactContentUri(
                name = "MOVIE_SUMMARY_ORDER_PAGE",
                path = Path.MOVIE_SUMMARY + "/" + Path.BY_ORDER_PAGE + "/#/#",
                type = "vnd.android.cursor.dir/moviesummary",
                whereColumn = {MovieDatabase.Tables.MOVIE_ORDER + "." + MovieOrderColumns.SORT_TYPE,
                        MovieDatabase.Tables.MOVIE_ORDER + "." + MovieOrderColumns.PAGE},
                pathSegment = {3, 2},
                join = " INNER JOIN " + MovieDatabase.Tables.MOVIE_ORDER + " ON " +
                        MovieDatabase.Tables.MOVIE_SUMMARY + "." + MovieSummaryColumns.ID + " = " +
                        MovieDatabase.Tables.MOVIE_ORDER + "." + MovieOrderColumns.MOVIE_SUMMARY_ID)
        public static Uri fromList(String sortType, long page) {
            return buildUri(Path.MOVIE_SUMMARY, Path.BY_ORDER_PAGE, sortType, String.valueOf(page));
        }

        @InexactContentUri(
                name = "MOVIE_SUMMARY_FAVORITES",
                path = Path.MOVIE_SUMMARY + "/" + Path.BY_FAVORITES + "/#",
                type = "vnd.android.cursor.dir/byfavorites",
                whereColumn = MovieDatabase.Tables.MOVIE_SUMMARY + "." + MovieSummaryColumns.IS_FAVORITE,
                pathSegment = 2)
        public static Uri fromList(int nFavorite) {
            return buildUri(Path.MOVIE_SUMMARY, Path.BY_FAVORITES, String.valueOf(nFavorite));
        }

//        @NotifyInsert(paths = Path.MOVIE_SUMMARY) public static Uri[] onInsert(ContentValues values) {
//            //final long summaryId = values.getAsLong(MovieSummaryColumns.ID);
//            return new Uri[] {
//                    uri
//                    //withId(summaryId),
//            };
//        }

        @NotifyBulkInsert(paths = Path.MOVIE_SUMMARY)
        public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] values, long[] ids) {
            return new Uri[]{
                    uri,
            };
        }

        @NotifyUpdate(paths = Path.MOVIE_SUMMARY + "/#")
        public static Uri[] onUpdate(Context context,
                                     Uri uri, String where, String[] whereArgs) {
            final long summaryId = Long.valueOf(uri.getPathSegments().get(1));

            return new Uri[]{
                    withId(summaryId),
            };
        }

        @NotifyDelete(paths = Path.MOVIE_SUMMARY + "/#")
        public static Uri[] onDelete(Context context,
                                     Uri uri) {
            final long summaryId = Long.valueOf(uri.getPathSegments().get(1));


            return new Uri[]{
                    withId(summaryId)
            };
        }
    }

    @TableEndpoint(table = MovieDatabase.Tables.MOVIE_ORDER)
    public static class MovieOrder {

        @ContentUri(
                path = Path.ORDER_MOVIES,
                type = "vnd.android.cursor.dir/movieorder")
        public static final Uri CONTENT_URI = buildUri(Path.ORDER_MOVIES);

        @InexactContentUri(
                name = "MOVIE_ORDER_ID",
                path = Path.ORDER_MOVIES + "/#",
                type = "vnd.android.cursor.item/movieorder",
                whereColumn = MovieOrderColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.ORDER_MOVIES, String.valueOf(id));
        }

        @InexactContentUri(
                name = "MOVIE_ORDER_PAGE",
                path = Path.ORDER_MOVIES + "/" + Path.BY_ORDER_PAGE + "/#/#",
                type = "vnd.android.cursor.dir/movieorder",
                whereColumn = MovieOrderColumns.SORT_TYPE + "," + MovieOrderColumns.PAGE,
                pathSegment = 2)
        public static Uri fromList(String sortType, long page) {
            return buildUri(Path.ORDER_MOVIES, Path.BY_ORDER_PAGE, sortType, String.valueOf(page));
        }

//        @NotifyInsert(paths = Path.ORDER_MOVIES) public static Uri[] onInsert(ContentValues values) {
//            //final long orderId = values.getAsLong(MovieOrderColumns.ID);
//            return new Uri[] {
//                    withId(orderId),
//            };
//        }

        @NotifyBulkInsert(paths = Path.ORDER_MOVIES)
        public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] values, long[] ids) {
            return new Uri[]{
                    uri,
            };
        }

        @NotifyUpdate(paths = Path.ORDER_MOVIES + "/#")
        public static Uri[] onUpdate(Context context,
                                     Uri uri, String where, String[] whereArgs) {
            final long orderId = Long.valueOf(uri.getPathSegments().get(1));

            return new Uri[]{
                    withId(orderId),
            };
        }

        @NotifyDelete(paths = Path.ORDER_MOVIES + "/#")
        public static Uri[] onDelete(Context context,
                                     Uri uri) {
            final long orderId = Long.valueOf(uri.getPathSegments().get(1));


            return new Uri[]{
                    withId(orderId)
            };
        }
    }


    @TableEndpoint(table = MovieDatabase.Tables.MOVIE_DETAIL)
    public static class MovieDetail {

        @ContentUri(
                path = Path.MOVIE_DETAIL,
                type = "vnd.android.cursor.dir/moviedetail")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIE_DETAIL);

        @InexactContentUri(
                name = "MOVIE_DETAIL_ID",
                path = Path.MOVIE_DETAIL + "/#",
                type = "vnd.android.cursor.item/moviedetail",
                whereColumn = MovieSummaryColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.MOVIE_DETAIL, String.valueOf(id));
        }

    }

    @TableEndpoint(table = MovieDatabase.Tables.MOVIE_TRAILER)
    public static class MovieTrailer {

        @ContentUri(
                path = Path.MOVIE_TRAILER,
                type = "vnd.android.cursor.dir/movietrailer")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIE_TRAILER);

        @InexactContentUri(
                name = "MOVIE_TRAILER_ID",
                path = Path.MOVIE_TRAILER + "/#",
                type = "vnd.android.cursor.item/movietrailer",
                whereColumn = MovieSummaryColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.MOVIE_TRAILER, String.valueOf(id));
        }

    }

    @TableEndpoint(table = MovieDatabase.Tables.MOVIE_REVIEW)
    public static class MovieReview {

        @ContentUri(
                path = Path.MOVIE_REVIEW,
                type = "vnd.android.cursor.dir/moviereview")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIE_REVIEW);

        @InexactContentUri(
                name = "MOVIE_REVIEW_ID",
                path = Path.MOVIE_REVIEW + "/#",
                type = "vnd.android.cursor.item/moviereview",
                whereColumn = MovieSummaryColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.MOVIE_REVIEW, String.valueOf(id));
        }

    }


    public static final class MovieOrderHelper {
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
         *
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

            if (sortType.equals(RestApiContract.SORT_KEY_FAVORITE_DESC) ||
                    sortType.equals(RestApiContract.SORT_KEY_FAVORITE_ASC)) {
                return FAVOURITE_SEARCH;
            }

            return 0;
        }

    }
}

