package com.virtual4real.moviemanager.sync;

import android.content.ContentValues;

import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.database.MovieOrderColumns;
import com.virtual4real.moviemanager.database.MovieSummaryColumns;
import com.virtual4real.moviemanager.database.SyncOperationColumns;
import com.virtual4real.moviemanager.database.UrlSettingsColumns;
import com.virtual4real.moviemanager.sync.poco.JsnImages;
import com.virtual4real.moviemanager.sync.poco.JsnMovieSummary;
import com.virtual4real.moviemanager.sync.poco.JsnSettings;

/**
 * Created by ioanagosman on 16/10/15.
 */

/**
 * Utility class to transform data between formats like
 * poco based on json objects to content values
 * content values to objects corresponding to tables in the database
 */
public class DataTransformer {


    public static ContentValues[] getMovieSummaryContentValues(JsnMovieSummary[] movies) {
        ContentValues[] values = new ContentValues[movies.length];

        for (int i = 0; i < movies.length; i++) {
            values[i] = getMovieSummaryContentValue(movies[i], i);

        }

        return values;
    }

//    public static ContentValues getMovieSummaryInfoContentValues(String totalResults,
//                                                                 String totalPages, String currentPage,
//                                                                 String sortType, long nOperationId) {
//        ContentValues values = new ContentValues();
//
//        values.put(MovieProvider.MovieOrderHelper.PAGINATION_TOTAL_RESULTS, totalResults);
//        values.put(MovieProvider.MovieOrderHelper.PAGINATION_TOTAL_PAGES, totalPages);
//        values.put(MovieProvider.MovieOrderHelper.PAGINATION_CURRENT_PAGE, currentPage);
//        values.put(MovieProvider.MovieOrderHelper.SORT_TYPE, MovieProvider.MovieOrderHelper.GetSortTypeInt(sortType));
//        values.put(MovieProvider.MovieOrderHelper.OPERATION_ID, nOperationId);
//
//        return values;
//    }

    public static ContentValues getMovieOrderContentValues(int currentPage, int nSortType, long nOperationId,
                                                           long nMovieSummaryId, int nPosition) {
        if (3 == nSortType) {
            String str = "bla bla";
            str = null;
        }

        ContentValues values = new ContentValues();

        values.put(MovieOrderColumns.MOVIE_SUMMARY_ID, nMovieSummaryId);
        values.put(MovieOrderColumns.PAGE, currentPage);
        values.put(MovieOrderColumns.POSITION, nPosition);
        values.put(MovieOrderColumns.SORT_TYPE, nSortType);
        values.put(MovieOrderColumns.SYNC_OPERATION_ID, nOperationId);
        values.put(MovieOrderColumns.DATE_UPDATED, MovieProvider.normalizeDate(System.currentTimeMillis()));


        return values;
    }

    public static ContentValues getMovieSummaryContentValue(JsnMovieSummary movie, int i) {
        ContentValues value = new ContentValues();

        String str = movie.getRelease_date();
        if (null != str && 0 != str.length() && 4 < str.length()) {
            str = str.substring(0, 4);
        }

        value.put(MovieSummaryColumns.MOVIE_ID, movie.getId());
        value.put(MovieSummaryColumns.ORIGINAL_TITLE, movie.getOriginal_title());
        value.put(MovieSummaryColumns.TITLE, movie.getTitle());
        value.put(MovieSummaryColumns.POPULARITY, movie.getPopularity());
        value.put(MovieSummaryColumns.POSTER_PATH, movie.getPoster_path());
        value.put(MovieSummaryColumns.DATE_RELEASE, movie.getRelease_date());
        value.put(MovieSummaryColumns.VOTE_AVERAGE, movie.getVote_average());
        value.put(MovieSummaryColumns.VOTE_COUNT, movie.getVote_count());
        value.put(MovieSummaryColumns.BACKDROP_PATH, movie.getBackdrop_path());
        value.put(MovieSummaryColumns.OVERVIEW, movie.getOverview());
        //value.put(MovieProvider.MovieOrderHelper.MOVIE_SUMMARY_POSITION, i);
        value.put(MovieSummaryColumns.DATE_UPDATED, MovieProvider.normalizeDate(System.currentTimeMillis()));
        value.put(MovieSummaryColumns.YEAR_OF_RELEASE, str);

        return value;
    }

    public static ContentValues getSettingsContentValues(JsnSettings setting) {
        ContentValues settingsValues = new ContentValues();

        JsnImages img = setting.getImages();

        settingsValues.put(UrlSettingsColumns.BASE_URL, img.getBase_url());
        settingsValues.put(UrlSettingsColumns.SECURE_BASE_URL, img.getSecure_base_url());

        settingsValues.put(UrlSettingsColumns.LOGO_SIZE_URL, getValues(img.getLogo_sizes()));
        settingsValues.put(UrlSettingsColumns.BACKDROP_SIZE_URL, getValues(img.getBackdrop_sizes()));
        settingsValues.put(UrlSettingsColumns.POSTER_SIZE_URL, getValues(img.getPoster_sizes()));
        settingsValues.put(UrlSettingsColumns.PROFILE_SIZE_URL, getValues(img.getProfile_sizes()));
        settingsValues.put(UrlSettingsColumns.STILL_SIZE_URL, getValues(img.getStill_sizes()));
        settingsValues.put(UrlSettingsColumns.DATE_UPDATED, MovieProvider.normalizeDate(System.currentTimeMillis()));

        return settingsValues;
    }

    public static String getValues(String[] vstr) {
        StringBuilder builder = new StringBuilder();
        for (String s : vstr) {
            if (s.equals("original")) continue;
            builder.append(s);
            builder.append(";");
        }
        return builder.toString();
    }

//    public static UrlSettings getUrlSetting(ContentValues values) {
//        UrlSettings url = new UrlSettings();
//
//        url.setDateUpdated(MovieContract.normalizeDate(System.currentTimeMillis()));
//
//        url.setBaseUrl(values.getAsString(UrlSettings$Table.BASEURL));
//        url.setSecureBaseUrl(values.getAsString(UrlSettings$Table.SECUREBASEURL));
//
//        url.setLogoSizeUrl(values.getAsString(UrlSettings$Table.LOGOSIZEURL));
//        url.setBackdropSizeUrl(values.getAsString(UrlSettings$Table.BACKDROPSIZEURL));
//        url.setPosterSizeUrl(values.getAsString(UrlSettings$Table.POSTERSIZEURL));
//        url.setProfileSizeUrl(values.getAsString(UrlSettings$Table.PROFILESIZEURL));
//        url.setStillSizeUrl(values.getAsString(UrlSettings$Table.STILLSIZEURL));
//
//        return url;
//    }
//
//    public static SyncOperation getSyncOperation(ContentValues values) {
//        SyncOperation ops = new SyncOperation();
//
//        ops.setDateUpdated(MovieContract.normalizeDate(System.currentTimeMillis()));
//
//        ops.setStartDate(values.getAsString(SyncOperation$Table.STARTDATE));
//        ops.setEndDate(values.getAsString(SyncOperation$Table.ENDDATE));
//        ops.setNoOfVotes(values.getAsInteger(SyncOperation$Table.NOOFVOTES));
//        ops.setIsAdult(values.getAsBoolean(SyncOperation$Table.ISADULT));
//        ops.setIsVideo(values.getAsBoolean(SyncOperation$Table.ISVIDEO));
//
//        return ops;
//    }

    public static ContentValues getSyncOperationContentValues(SearchParameters sch) {
        ContentValues settingsValues = new ContentValues();

        settingsValues.put(SyncOperationColumns.DATE_START, sch.getMinDate());
        settingsValues.put(SyncOperationColumns.DATE_END, sch.getMaxDate());
        settingsValues.put(SyncOperationColumns.NO_OF_VOTES, sch.getMinVotes());
        settingsValues.put(SyncOperationColumns.IS_ADULT, sch.isIncludesAdult());
        settingsValues.put(SyncOperationColumns.IS_VIDEO, sch.isIncludesVideo());
        settingsValues.put(SyncOperationColumns.DATE_UPDATED, MovieProvider.normalizeDate(System.currentTimeMillis()));

        return settingsValues;
    }

//    public static MovieSummary getMovieSummary(DbService service, ContentValues values) {
//        long movieId = values.getAsLong(MovieSummary$Table.MOVIEID);
//        MovieSummary movieSummary = service.GetMovieSummaryByMovieId(movieId);
//
//        if (null == movieSummary) {
//            movieSummary = new MovieSummary();
//            movieSummary.setDateUpdated(System.currentTimeMillis());
//        }
//
//        movieSummary.setMovieId(values.getAsInteger(MovieSummary$Table.MOVIEID));
//        movieSummary.setOriginalTitle(values.getAsString(MovieSummary$Table.ORIGINALTITLE));
//        movieSummary.setTitle(values.getAsString(MovieSummary$Table.TITLE));
//        movieSummary.setPosterPath(values.getAsString(MovieSummary$Table.POSTERPATH));
//        movieSummary.setPopularity(values.getAsDouble(MovieSummary$Table.POPULARITY));
//        movieSummary.setVoteAverage(values.getAsDouble(MovieSummary$Table.VOTEAVERAGE));
//        movieSummary.setVoteCount(values.getAsInteger(MovieSummary$Table.VOTECOUNT));
//        movieSummary.setBackdropPath(values.getAsString(MovieSummary$Table.BACKDROPPATH));
//        movieSummary.setOverview(values.getAsString(MovieSummary$Table.OVERVIEW));
//
//        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            String sDate = values.getAsString(MovieSummary$Table.RELEASEDATE);
//            Date dt = null;
//            if (null != sDate) {
//                dt = f.parse(sDate);
//
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(dt);
//
//                movieSummary.setYearOfRelease(cal.get(Calendar.YEAR));
//            }
//            movieSummary.setReleaseDate(null == dt ? MovieContract.normalizeDate(System.currentTimeMillis()) :
//                    MovieContract.normalizeDate(dt.getTime()));
//        } catch (ParseException exex) {
//            movieSummary.setReleaseDate(MovieContract.normalizeDate(System.currentTimeMillis()));
//        }
//
//        return movieSummary;
//    }
}
