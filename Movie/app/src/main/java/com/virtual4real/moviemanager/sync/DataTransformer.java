package com.virtual4real.moviemanager.sync;

import android.content.ContentValues;

import com.virtual4real.moviemanager.data.MovieContract;
import com.virtual4real.moviemanager.database.DbService;
import com.virtual4real.moviemanager.database.MovieSummary;
import com.virtual4real.moviemanager.database.MovieSummary$Table;
import com.virtual4real.moviemanager.database.SyncOperation;
import com.virtual4real.moviemanager.database.SyncOperation$Table;
import com.virtual4real.moviemanager.database.UrlSettings;
import com.virtual4real.moviemanager.database.UrlSettings$Table;
import com.virtual4real.moviemanager.sync.poco.JsnImages;
import com.virtual4real.moviemanager.sync.poco.JsnMovieSummary;
import com.virtual4real.moviemanager.sync.poco.JsnSettings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ioanagosman on 16/10/15.
 */

/**
 * Utility class to transform data between formats like
 * poco based on json objects to content values
 * content values to objects corresponding to tables in the database
 */
public class DataTransformer {


    public static ContentValues[] getMovieSummaryContentValues(String totalResults, String totalPages,
                                                               String currentPage, String sortType,
                                                               JsnMovieSummary[] movies, long nOperationId) {
        ContentValues[] values = new ContentValues[movies.length + 1];

        values[0] = getMovieSummaryInfoContentValues(totalResults, totalPages, currentPage, sortType, nOperationId);

        for (int i = 0; i < movies.length; i++) {
            values[i + 1] = getMovieSummaryContentValue(movies[i], i);

        }

        return values;
    }

    public static ContentValues getMovieSummaryInfoContentValues(String totalResults,
                                                                 String totalPages, String currentPage,
                                                                 String sortType, long nOperationId) {
        ContentValues values = new ContentValues();

        values.put(MovieContract.MovieOrderEntry.PAGINATION_TOTAL_RESULTS, totalResults);
        values.put(MovieContract.MovieOrderEntry.PAGINATION_TOTAL_PAGES, totalPages);
        values.put(MovieContract.MovieOrderEntry.PAGINATION_CURRENT_PAGE, currentPage);
        values.put(MovieContract.MovieOrderEntry.SORT_TYPE, MovieContract.MovieOrderEntry.GetSortTypeInt(sortType));
        values.put(MovieContract.MovieOrderEntry.OPERATION_ID, nOperationId);

        return values;
    }


    public static ContentValues getMovieSummaryContentValue(JsnMovieSummary movie, int i) {
        ContentValues value = new ContentValues();

        value.put(MovieSummary$Table.MOVIEID, movie.getId());
        value.put(MovieSummary$Table.ORIGINALTITLE, movie.getOriginal_title());
        value.put(MovieSummary$Table.TITLE, movie.getTitle());
        value.put(MovieSummary$Table.POPULARITY, movie.getPopularity());
        value.put(MovieSummary$Table.POSTERPATH, movie.getPoster_path());
        value.put(MovieSummary$Table.RELEASEDATE, movie.getRelease_date());
        value.put(MovieSummary$Table.VOTEAVERAGE, movie.getVote_average());
        value.put(MovieSummary$Table.VOTECOUNT, movie.getVote_count());
        value.put(MovieSummary$Table.BACKDROPPATH, movie.getBackdrop_path());
        value.put(MovieSummary$Table.OVERVIEW, movie.getOverview());
        value.put(MovieContract.MovieOrderEntry.MOVIE_SUMMARY_POSITION, i);

        return value;
    }

    public static ContentValues getSettingsContentValues(JsnSettings setting) {
        ContentValues settingsValues = new ContentValues();

        JsnImages img = setting.getImages();

        settingsValues.put(UrlSettings$Table.BASEURL, img.getBase_url());
        settingsValues.put(UrlSettings$Table.SECUREBASEURL, img.getSecure_base_url());

        settingsValues.put(UrlSettings$Table.LOGOSIZEURL, getValues(img.getLogo_sizes()));
        settingsValues.put(UrlSettings$Table.BACKDROPSIZEURL, getValues(img.getBackdrop_sizes()));
        settingsValues.put(UrlSettings$Table.POSTERSIZEURL, getValues(img.getPoster_sizes()));
        settingsValues.put(UrlSettings$Table.PROFILESIZEURL, getValues(img.getProfile_sizes()));
        settingsValues.put(UrlSettings$Table.STILLSIZEURL, getValues(img.getStill_sizes()));


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

    public static UrlSettings getUrlSetting(ContentValues values) {
        UrlSettings url = new UrlSettings();

        url.setDateUpdated(MovieContract.normalizeDate(System.currentTimeMillis()));

        url.setBaseUrl(values.getAsString(UrlSettings$Table.BASEURL));
        url.setSecureBaseUrl(values.getAsString(UrlSettings$Table.SECUREBASEURL));

        url.setLogoSizeUrl(values.getAsString(UrlSettings$Table.LOGOSIZEURL));
        url.setBackdropSizeUrl(values.getAsString(UrlSettings$Table.BACKDROPSIZEURL));
        url.setPosterSizeUrl(values.getAsString(UrlSettings$Table.POSTERSIZEURL));
        url.setProfileSizeUrl(values.getAsString(UrlSettings$Table.PROFILESIZEURL));
        url.setStillSizeUrl(values.getAsString(UrlSettings$Table.STILLSIZEURL));

        return url;
    }

    public static SyncOperation getSyncOperation(ContentValues values) {
        SyncOperation ops = new SyncOperation();

        ops.setDateUpdated(MovieContract.normalizeDate(System.currentTimeMillis()));

        ops.setStartDate(values.getAsString(SyncOperation$Table.STARTDATE));
        ops.setEndDate(values.getAsString(SyncOperation$Table.ENDDATE));
        ops.setNoOfVotes(values.getAsInteger(SyncOperation$Table.NOOFVOTES));
        ops.setIsAdult(values.getAsBoolean(SyncOperation$Table.ISADULT));
        ops.setIsVideo(values.getAsBoolean(SyncOperation$Table.ISVIDEO));

        return ops;
    }

    public static ContentValues getSyncOperationContentValues(SearchParameters sch) {
        ContentValues settingsValues = new ContentValues();

        settingsValues.put(SyncOperation$Table.STARTDATE, sch.getMinDate());
        settingsValues.put(SyncOperation$Table.ENDDATE, sch.getMaxDate());
        settingsValues.put(SyncOperation$Table.NOOFVOTES, sch.getMinVotes());
        settingsValues.put(SyncOperation$Table.ISADULT, sch.isIncludesAdult());
        settingsValues.put(SyncOperation$Table.ISVIDEO, sch.isIncludesVideo());

        return settingsValues;
    }

    public static MovieSummary getMovieSummary(DbService service, ContentValues values) {
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
}
