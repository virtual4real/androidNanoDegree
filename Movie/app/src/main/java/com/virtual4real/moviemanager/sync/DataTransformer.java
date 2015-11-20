package com.virtual4real.moviemanager.sync;

import android.content.ContentValues;
import android.content.Context;

import com.google.common.base.Joiner;
import com.virtual4real.moviemanager.R;
import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.database.MovieDetailColumns;
import com.virtual4real.moviemanager.database.MovieOrderColumns;
import com.virtual4real.moviemanager.database.MovieReviewColumns;
import com.virtual4real.moviemanager.database.MovieSummaryColumns;
import com.virtual4real.moviemanager.database.MovieTrailerColumns;
import com.virtual4real.moviemanager.database.SyncOperationColumns;
import com.virtual4real.moviemanager.database.UrlSettingsColumns;
import com.virtual4real.moviemanager.sync.poco.JsnGenres;
import com.virtual4real.moviemanager.sync.poco.JsnImages;
import com.virtual4real.moviemanager.sync.poco.JsnMovieDetail;
import com.virtual4real.moviemanager.sync.poco.JsnMovieSummary;
import com.virtual4real.moviemanager.sync.poco.JsnResults;
import com.virtual4real.moviemanager.sync.poco.JsnReviews;
import com.virtual4real.moviemanager.sync.poco.JsnSettings;
import com.virtual4real.moviemanager.sync.poco.JsnTrailers;
import com.virtual4real.moviemanager.sync.poco.JsnYoutube;

import java.text.SimpleDateFormat;
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


    public static ContentValues[] getMovieSummaryContentValues(Context ctx, JsnMovieSummary[] movies) {
        ContentValues[] values = new ContentValues[movies.length];

        for (int i = 0; i < movies.length; i++) {
            values[i] = getMovieSummaryContentValue(movies[i], i, ctx);

        }

        return values;
    }


    public static ContentValues getMovieOrderContentValues(int currentPage, int nSortType, long nOperationId,
                                                           long nMovieSummaryId, int nPosition) {


        ContentValues values = new ContentValues();

        values.put(MovieOrderColumns.MOVIE_SUMMARY_ID, nMovieSummaryId);
        values.put(MovieOrderColumns.PAGE, currentPage);
        values.put(MovieOrderColumns.POSITION, nPosition);
        values.put(MovieOrderColumns.SORT_TYPE, nSortType);
        values.put(MovieOrderColumns.SYNC_OPERATION_ID, nOperationId);
        values.put(MovieOrderColumns.DATE_UPDATED, MovieProvider.normalizeDate(System.currentTimeMillis()));


        return values;
    }

    public static ContentValues getMovieSummaryContentValue(JsnMovieSummary movie, int i, Context ctx) {
        ContentValues value = new ContentValues();

        String str2 = "";
        String str = movie.getRelease_date();
        if (null != str && 0 != str.length() && 4 < str.length()) {
            str2 = str.substring(0, 4);
        }

        long milliseconds = 0;

        try {
            SimpleDateFormat f = new SimpleDateFormat(ctx.getResources().getString(R.string.date_time_format)); // "yyyy-MM-dd");
            Date d = f.parse(str);
            milliseconds = d.getTime();
        } catch (Exception exex) {

        }

        value.put(MovieSummaryColumns.MOVIE_ID, movie.getId());
        value.put(MovieSummaryColumns.ORIGINAL_TITLE, movie.getOriginal_title());
        value.put(MovieSummaryColumns.TITLE, movie.getTitle());
        value.put(MovieSummaryColumns.POPULARITY, movie.getPopularity());
        value.put(MovieSummaryColumns.POSTER_PATH, movie.getPoster_path());
        value.put(MovieSummaryColumns.DATE_RELEASE, milliseconds); // movie.getRelease_date());
        value.put(MovieSummaryColumns.VOTE_AVERAGE, movie.getVote_average());
        value.put(MovieSummaryColumns.VOTE_COUNT, movie.getVote_count());
        value.put(MovieSummaryColumns.BACKDROP_PATH, movie.getBackdrop_path());
        value.put(MovieSummaryColumns.OVERVIEW, movie.getOverview());
        //value.put(MovieProvider.MovieOrderHelper.MOVIE_SUMMARY_POSITION, i);
        value.put(MovieSummaryColumns.DATE_UPDATED, MovieProvider.normalizeDate(System.currentTimeMillis()));
        value.put(MovieSummaryColumns.YEAR_OF_RELEASE, str2);
        value.put(MovieSummaryColumns.IS_FAVORITE, 0);

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

    public static ContentValues getMovieDetailsContentValues(JsnMovieDetail movieDetailResult) {
        ContentValues detailValues = new ContentValues();

        detailValues.put(MovieDetailColumns.DATE_RELEASE, movieDetailResult.getRelease_date());
        detailValues.put(MovieDetailColumns.DATE_UPDATED, MovieProvider.normalizeDate(System.currentTimeMillis()));
        detailValues.put(MovieDetailColumns.GENRES, getGenres(movieDetailResult.getGenres()));
        detailValues.put(MovieDetailColumns.HOMEPAGE, movieDetailResult.getHomepage());
        detailValues.put(MovieDetailColumns.MOVIE_ID, movieDetailResult.getId());
        detailValues.put(MovieDetailColumns.RUNTIME, movieDetailResult.getRuntime());
        detailValues.put(MovieDetailColumns.STATUS, movieDetailResult.getStatus());
        detailValues.put(MovieDetailColumns.TAG_LINE, movieDetailResult.getTagline());

        return detailValues;
    }

    private static String getGenres(JsnGenres[] genres) {
        String[] vGenres = new String[genres.length];

        for (int i = 0; i < genres.length; i++) {
            vGenres[i] = genres[i].getName();
        }

        return Joiner.on(",").skipNulls().join(vGenres);
    }

    public static ContentValues[] getMovieTrailers(JsnMovieDetail movieDetailResult) {
        JsnTrailers trailers = movieDetailResult.getTrailers();
        if (null == trailers || 0 == trailers.getYoutube().length) {
            return null;
        }

        ContentValues[] trailerValues = new ContentValues[trailers.getYoutube().length];

        for (int i = 0; i < trailers.getYoutube().length; i++) {
            JsnYoutube yt = trailers.getYoutube()[i];
            trailerValues[i] = new ContentValues();

            trailerValues[i].put(MovieTrailerColumns.DATE_UPDATED, MovieProvider.normalizeDate(System.currentTimeMillis()));
            trailerValues[i].put(MovieTrailerColumns.MOVIE_ID, movieDetailResult.getId());
            trailerValues[i].put(MovieTrailerColumns.NAME, yt.getName());
            trailerValues[i].put(MovieTrailerColumns.SIZE, yt.getSize());
            trailerValues[i].put(MovieTrailerColumns.SOURCE, yt.getSource());
            trailerValues[i].put(MovieTrailerColumns.TYPE, yt.getType());
        }

        return trailerValues;
    }

    public static ContentValues[] getMovieReviews(JsnMovieDetail movieDetailResult) {
        JsnReviews reviews = movieDetailResult.getReviews();
        if (null == reviews || 0 == Integer.parseInt(reviews.getTotal_results())) {
            return null;
        }

        JsnResults[] results = reviews.getResults();

        ContentValues[] reviewValues = new ContentValues[results.length];

        for (int i = 0; i < results.length; i++) {
            JsnResults res = results[i];
            reviewValues[i] = new ContentValues();

            reviewValues[i].put(MovieReviewColumns.DATE_UPDATED, MovieProvider.normalizeDate(System.currentTimeMillis()));
            reviewValues[i].put(MovieReviewColumns.MOVIE_ID, movieDetailResult.getId());
            reviewValues[i].put(MovieReviewColumns.AUTHOR, res.getAuthor());
            reviewValues[i].put(MovieReviewColumns.CONTENT, res.getContent());
            reviewValues[i].put(MovieReviewColumns.REVIEW_ID, res.getId());
            reviewValues[i].put(MovieReviewColumns.URL, res.getUrl());
        }

        return reviewValues;
    }


}
