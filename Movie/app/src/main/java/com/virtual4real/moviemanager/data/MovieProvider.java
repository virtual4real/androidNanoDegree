package com.virtual4real.moviemanager.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.format.Time;

import com.virtual4real.moviemanager.database.MovieDatabase;
import com.virtual4real.moviemanager.database.MovieOrderColumns;
import com.virtual4real.moviemanager.database.MovieSummaryColumns;
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

    public static final String AUTHORITY = "com.virtual4real.moviemanager.app";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static String[] getMovieSummaryProjection() {
        String[] cols = new String[]{
                MovieSummaryColumns.ID, MovieSummaryColumns.BACKDROP_PATH, MovieSummaryColumns.DATE_RELEASE,
                MovieSummaryColumns.DATE_UPDATED, MovieSummaryColumns.MOVIE_ID, MovieSummaryColumns.ORIGINAL_TITLE,
                MovieSummaryColumns.OVERVIEW, MovieSummaryColumns.POPULARITY, MovieSummaryColumns.POSTER_PATH,
                MovieSummaryColumns.TITLE, MovieSummaryColumns.VOTE_AVERAGE, MovieSummaryColumns.VOTE_COUNT,
                MovieSummaryColumns.YEAR_OF_RELEASE};

        return getColsWithTable(cols, MovieDatabase.Tables.MOVIE_SUMMARY);
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

        String BY_ORDER_PAGE = "byorderandpage";
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

            return 0;
        }

    }
}

//public class MovieProvider extends ContentProvider {
//
//    // The URI Matcher used by this content provider.
//    private static final UriMatcher sUriMatcher = buildUriMatcher();
//    //private WeatherDbHelper mOpenHelper;
//
//    static final int SETTINGS = 100;
//    static final int MOVIE_SUMMARY = 101;
//    static final int MOVIE_DETAIL_ID = 102;
//    static final int OPERATIONS = 103;
//
//
//    static UriMatcher buildUriMatcher() {
//        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
//        final String authority = MovieContract.CONTENT_AUTHORITY;
//
//        matcher.addURI(authority, MovieContract.PATH_SETTINGS, SETTINGS);
//        matcher.addURI(authority, MovieContract.PATH_OPERATIONS, OPERATIONS);
//        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIE_SUMMARY);
//        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/*", MOVIE_DETAIL_ID);
//
//        return matcher;
//    }
//
//
//    @Override
//    public boolean onCreate() {
//        return true;
//    }
//
//    @Override
//    public String getType(Uri uri) {
//        final int match = sUriMatcher.match(uri);
//
//        switch (match) {
//            case SETTINGS:
//                return MovieContract.SettingEntry.CONTENT_ITEM_TYPE;
//            case MOVIE_SUMMARY:
//                return MovieContract.MovieSummaryEntry.CONTENT_TYPE;
//            case MOVIE_DETAIL_ID:
//                return MovieContract.MovieSummaryEntry.CONTENT_ITEM_TYPE;
//
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//    }
//
//    @Override
//    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
//                        String sortOrder) {
//        Cursor retCursor = null;
//        final int match = sUriMatcher.match(uri);
//
//        switch (match) {
//            case SETTINGS: {
//                DbService service = new DbService();
//                retCursor = service.GetUrlSetting();
//                break;
//            }
//            case OPERATIONS: {
//                DbService service = new DbService();
//
//                //TODO: put these strings in resources
//                String sMaxDate = getParameter(selectionArgs, 0, getContext().getString(R.string.default_max_date));
//                String sMinDate = getParameter(selectionArgs, 1, getContext().getString(R.string.default_min_date));
//                int nVotes = Integer.parseInt(getParameter(selectionArgs, 2, getContext().getString(R.string.default_min_votes)));
//                boolean bAdult = Boolean.parseBoolean(getParameter(selectionArgs, 3, getContext().getString(R.string.default_adult)));
//                boolean bVideo = Boolean.parseBoolean(getParameter(selectionArgs, 4, getContext().getString(R.string.default_video)));
//
//                retCursor = service.GetOperations(sMinDate, sMaxDate, nVotes, bAdult, bVideo);
//                break;
//            }
//            case MOVIE_SUMMARY: {
//                DbService service = new DbService();
//
//                int nOrder = MovieContract.MovieOrderEntry.GetSortTypeIntFromInterface(selectionArgs[1]);
//                int nPage = Integer.parseInt(selectionArgs[0]);
//                //TODO: other paramters from selectionArgs
//                retCursor = service.GetMovieSummaries(nOrder, nPage);
//                break;
//            }
//
//            case MOVIE_DETAIL_ID: {
//                DbService service = new DbService();
//                retCursor = service.GetMovieSummaryCursorByMovieId(Long.parseLong(selection));
//                break;
//            }
//        }
//
//        if (null != retCursor) {
//            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
//        }
//        return retCursor;
//    }
//
//    /**
//     * Helper method to get the value at a position in an array or a default value
//     *
//     * @param args          the array in which to search for a value
//     * @param nPos          the position where the value should be found
//     * @param sDefaultValue if the value was not found at the specified position, return the default value
//     * @return
//     */
//    private String getParameter(String[] args, int nPos, String sDefaultValue) {
//        if (null == args || args.length < nPos + 1) {
//            return sDefaultValue;
//        }
//
//        return args[nPos];
//    }
//
//
//
//    @Override
//    public Uri insert(Uri uri, ContentValues values) {
//        final int match = sUriMatcher.match(uri);
//        Uri returnUri = null;
//
//        switch (match) {
//            case SETTINGS: {
//                DbService service = new DbService();
//                UrlSettings url = DataTransformer.getUrlSetting(values);
//                long id = service.InsertUrlSetting(url);
//                returnUri = MovieContract.SettingEntry.buildSettingUri(id);
//                break;
//            }
//            case OPERATIONS: {
//                DbService service = new DbService();
//                SyncOperation ops = DataTransformer.getSyncOperation(values);
//                long id = service.InsertSyncOperation(ops);
//                returnUri = MovieContract.SyncOperationEntry.buildSettingUri(id);
//                break;
//            }
//            case MOVIE_SUMMARY: {
//                DbService service = new DbService();
//                MovieSummary movie = DataTransformer.getMovieSummary(service, values);
//                movie.save();
//                returnUri = MovieContract.MovieSummaryEntry.buildMovieSummaryUri(movie.getMovieId());
//                break;
//            }
//            default:
//                break;
//
//
//        }
//
//        getContext().getContentResolver().notifyChange(uri, null);
//        return returnUri;
//    }
//
//    @Override
//    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        final int match = sUriMatcher.match(uri);
//        int nCount = 0;
//
//        String[] vIds = (null == selection ? null : selection.split(Pattern.quote(";")));
//
//        switch (match) {
//            case SETTINGS: {
//                DbService service = new DbService();
//                if (null == vIds) {
//                    service.DeleteAllUrlSettings();
//                } else {
//                    for (int i = 0; i < vIds.length; i++) {
//                        service.DeleteUrlSettings(Long.parseLong(vIds[i]));
//                        nCount++;
//                    }
//                }
//                break;
//            }
//            case OPERATIONS: {
//                DbService service = new DbService();
//                service.DeleteAllSyncOperationAndOrders();
//                break;
//            }
//            case MOVIE_SUMMARY: {
//                DbService service = new DbService();
//                if (null == vIds) {
//                    service.DeleteAllMovieSummary();
//                } else {
//                    for (int i = 0; i < vIds.length; i++) {
//                        service.DeleteMovieSummary(Long.parseLong(vIds[i]));
//                        nCount++;
//                    }
//                }
//                break;
//            }
//            default:
//                return 0;
//
//
//        }
//
//        getContext().getContentResolver().notifyChange(uri, null);
//        return nCount;
//    }
//
//
//    @Override
//    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        final int match = sUriMatcher.match(uri);
//
//        switch (match) {
//            case SETTINGS: {
//                DbService service = new DbService();
//                UrlSettings url = DataTransformer.getUrlSetting(values);
//                service.InsertUrlSetting(url);
//                break;
//            }
//            case MOVIE_SUMMARY: {
//                DbService service = new DbService();
//                MovieSummary movie = DataTransformer.getMovieSummary(service, values);
//                service.InsertMovieSummary(movie);
//                break;
//            }
//            default:
//                return 0;
//
//
//        }
//
//        getContext().getContentResolver().notifyChange(uri, null);
//        return 1;
//    }
//
//    @Override
//    public int bulkInsert(Uri uri, ContentValues[] values) {
//        int nInsert = 0;
//
//        final int match = sUriMatcher.match(uri);
//        switch (match) {
//            case MOVIE_SUMMARY:
//                try {
//                    //TODO: store total pages and total results (maybe in app settings)
//                    int page = values[0].getAsInteger(MovieContract.MovieOrderEntry.PAGINATION_CURRENT_PAGE);
//                    int totalPages = values[0].getAsInteger(MovieContract.MovieOrderEntry.PAGINATION_TOTAL_PAGES);
//                    int totalResults = values[0].getAsInteger(MovieContract.MovieOrderEntry.PAGINATION_TOTAL_RESULTS);
//                    int sortType = values[0].getAsInteger(MovieContract.MovieOrderEntry.SORT_TYPE);
//                    long operationid = values[0].getAsLong(MovieContract.MovieOrderEntry.OPERATION_ID);
//
//                    DbService service = new DbService();
//
//                    SyncOperation sync = service.GetSyncOperationById(operationid);
//
//                    service.DeleteMovieOrder(page, sortType);
//
//                    for (int i = 1; i < values.length; i++) {
//                        MovieSummary movieSummary = DataTransformer.getMovieSummary(service, values[i]);
//                        service.InsertMovieSummary(movieSummary);
//
//                        MovieOrder movieOrder = new MovieOrder();
//                        movieOrder.setDateUpdated(MovieContract.normalizeDate(System.currentTimeMillis()));
//                        movieOrder.setMovieSummary(movieSummary);
//                        movieOrder.setPage(page);
//                        movieOrder.setPosition(values[i].getAsInteger(MovieContract.MovieOrderEntry.MOVIE_SUMMARY_POSITION));
//                        movieOrder.setSortType(sortType);
//                        movieOrder.setSyncOperation(sync);
//
//                        service.InsertMovieOrder(movieOrder, movieSummary);
//                        nInsert++;
//                    }
//
//                    getContext().getContentResolver().notifyChange(uri, null);
//
//                } catch (Exception e) {
//                    return 0;
//                }
//
//                break;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//        return nInsert;
//    }
//
//
//
//    // You do not need to call this method. This is a method specifically to assist the testing
//    // framework in running smoothly. You can read more at:
//    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
//    @Override
//    @TargetApi(11)
//    public void shutdown() {
//        //mOpenHelper.close();
//        super.shutdown();
//    }
//}
