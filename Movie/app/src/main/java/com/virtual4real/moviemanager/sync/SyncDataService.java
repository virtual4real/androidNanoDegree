package com.virtual4real.moviemanager.sync;


import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;

import com.virtual4real.moviemanager.R;
import com.virtual4real.moviemanager.Utils;
import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.database.MovieDetailColumns;
import com.virtual4real.moviemanager.database.MovieOrderColumns;
import com.virtual4real.moviemanager.database.MovieSummaryColumns;
import com.virtual4real.moviemanager.database.SyncOperationColumns;
import com.virtual4real.moviemanager.database.UrlSettingsColumns;
import com.virtual4real.moviemanager.sync.poco.JsnMovieDetail;
import com.virtual4real.moviemanager.sync.poco.JsnMovieSummaryResult;
import com.virtual4real.moviemanager.sync.poco.JsnReviews;
import com.virtual4real.moviemanager.sync.poco.JsnReviewsResult;
import com.virtual4real.moviemanager.sync.poco.JsnSettings;
import com.virtual4real.moviemanager.sync.restapi.IApiMethods;
import com.virtual4real.moviemanager.sync.restapi.MovieDetailCallback;
import com.virtual4real.moviemanager.sync.restapi.MovieDetailReviewCallback;
import com.virtual4real.moviemanager.sync.restapi.MovieSummaryCallback;
import com.virtual4real.moviemanager.sync.restapi.RestApiContract;
import com.virtual4real.moviemanager.sync.restapi.SettingsCallback;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ioanagosman on 16/10/15.
 */

/**
 * The class contains the logic for synchronization with the rest api.
 * Its methods are called from the MovieManagerSyncAdapter
 */
public class SyncDataService {
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;


    private String API_KEY_VALUE;
    private String API_URL;
    private Context ctx = null;

    private RestAdapter restAdapter;

    public SyncDataService(Context ctx) {
        this.ctx = ctx;
        API_KEY_VALUE = ctx.getString(R.string.api_key);
        API_URL = ctx.getString(R.string.api_url);

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();
        IApiMethods methods = restAdapter.create(IApiMethods.class);
    }


    public long GetOperationIdForParameters(SearchParameters sch) {
        Integer nAdult = sch.isIncludesAdult() ? 1 : 0;
        Integer nVideo = sch.isIncludesVideo() ? 1 : 0;

        Cursor cursor = ctx.getContentResolver().query(MovieProvider.getOperationUri(),
                new String[]{SyncOperationColumns.ID},
                SyncOperationColumns.DATE_START + " = ? AND " + SyncOperationColumns.DATE_END + " = ? AND " +
                        SyncOperationColumns.NO_OF_VOTES + " = ? AND " +
                        SyncOperationColumns.IS_ADULT + " = ? AND " + SyncOperationColumns.IS_VIDEO + " = ?",
                new String[]{sch.getMinDate(), sch.getMaxDate(),
                        Integer.toString(sch.getMinVotes()),
                        nAdult.toString(), nVideo.toString()}, null);

        long nResult = 0;

        if (cursor.moveToFirst()) {
            nResult = cursor.getLong(cursor.getColumnIndex(SyncOperationColumns.ID));
        }

        cursor.close();

        return nResult;

    }

    public long InsertNewSyncOperation(SearchParameters sch) {
        Uri uri = ctx.getContentResolver().insert(MovieProvider.getOperationUri(),
                DataTransformer.getSyncOperationContentValues(sch));

        return Long.parseLong(uri.getLastPathSegment());
    }

    private int DeleteAllMovieOrderData() {
        int nCount = ctx.getContentResolver().delete(MovieProvider.getOperationUri(), null, null);
        nCount = ctx.getContentResolver().delete(MovieProvider.getMovieOrderUri(), null, null);

        return nCount;
    }


    public void syncMovieSummary(Account account, String authority, ContentProviderClient provider,
                                 SyncResult syncResult, SearchParameters sch) {

        boolean bCallAll = false;

        String sOrder = sch.getOrder();
        int nPage = sch.getPage();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();
        IApiMethods methods = restAdapter.create(IApiMethods.class);

        long nOperationId = GetOperationIdForParameters(sch);
        if (0 == nOperationId) {
            DeleteAllMovieOrderData();
            bCallAll = true;
            nOperationId = InsertNewSyncOperation(sch);
        }

        if (!bCallAll || null == sOrder) {
            bCallAll = true;
        } else {
            //Utils.SendNotification(ctx, "Popular Movies", "Get movies by sort order and page");
            getMoviesByPageAndSortOrder(methods, nPage, getRestApiSortOrder(sOrder), sch, true, nOperationId);
            getMoviesByPageAndSortOrder(methods, nPage + 1, getRestApiSortOrder(sOrder), sch, true, nOperationId);
            bCallAll = false;
        }

        if (bCallAll) {
            //Utils.SendNotification(ctx, "Popular Movies", "Get movies all movies");
            getSettings(methods);

            getAllMovies(methods, nPage, sch, true, nOperationId);
            getAllMovies(methods, nPage + 1, sch, true, nOperationId);
        }

    }


    private void getAllMovies(IApiMethods methods, int nPage, SearchParameters sch, final boolean notifyContentChange, long nOperationId) {
        getMoviesByPageAndSortOrder(methods, nPage, RestApiContract.SORT_KEY_POPULARITY_ASC, sch, false, nOperationId);
        getMoviesByPageAndSortOrder(methods, nPage, RestApiContract.SORT_KEY_POPULARITY_DESC, sch, false, nOperationId);
        getMoviesByPageAndSortOrder(methods, nPage, RestApiContract.SORT_KEY_VOTE_ASC, sch, false, nOperationId);
        getMoviesByPageAndSortOrder(methods, nPage, RestApiContract.SORT_KEY_VOTE_DESC, sch, false, nOperationId);
        getMoviesByPageAndSortOrder(methods, nPage, RestApiContract.SORT_KEY_RELEASE_ASC, sch, false, nOperationId);
        getMoviesByPageAndSortOrder(methods, nPage, RestApiContract.SORT_KEY_RELEASE_DESC, sch, false, nOperationId);


        if (notifyContentChange) {
            ctx.getContentResolver().notifyChange(MovieProvider.getMovieSummaryUri(), null, false);
        }
    }

    private void getMoviesByPageAndSortOrder(IApiMethods methods, int nPage, final String sSortOrder,
                                             SearchParameters sch, final boolean notifyContentChange, final long nOperationId) {

        boolean bGetFromService = false;
        int nSortType = MovieProvider.MovieOrderHelper.GetSortTypeInt(sSortOrder);
        Cursor cursor =
                ctx.getContentResolver().query(MovieProvider.getMovieOrderUri(),
                        new String[]{MovieOrderColumns.DATE_UPDATED},
                        MovieOrderColumns.PAGE + " = ? AND " + MovieOrderColumns.SORT_TYPE + " = ? ",
                        new String[]{Integer.toString(nPage), Integer.toString(nSortType)}, null);

        if (null != cursor && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long nTime = cursor.getLong(cursor.getColumnIndex(MovieOrderColumns.DATE_UPDATED));
                if (System.currentTimeMillis() - nTime >= DAY_IN_MILLIS) {
                    bGetFromService = true;
                    break;
                }
            }

            cursor.close();
        } else {
            bGetFromService = true;
        }

        if (!bGetFromService) {
            return;
        }

        MovieSummaryCallback callbackMovieSummary =
                new MovieSummaryCallback(ctx, this, sSortOrder, nOperationId, notifyContentChange);

        methods.getMovieSummary(API_KEY_VALUE, sSortOrder, nPage, sch.getMinDate(), sch.getMaxDate(),
                sch.getMinVotes(), sch.isIncludesVideo(), sch.isIncludesAdult(), callbackMovieSummary);

    }


    private String getTypeOfOrder(String sInterface, String sOrder, String asc, String desc) {
        if (null == sInterface) {
            return null;
        }

        if (sInterface.startsWith(sOrder)) {
            return getAscOrDesc(sInterface, asc, desc);
        }

        return null;
    }

    private String getAscOrDesc(String sInterface, String sAsc, String sDesc) {
        if (null == sInterface) return null;

        return sInterface.endsWith(ctx.getString(R.string.sort_asc)) ? sAsc : sDesc;
    }

    private void getSettings(IApiMethods methods) {
        boolean bExecute = false;

        Cursor cursor = ctx.getContentResolver()
                .query(MovieProvider.getSeetingsUri(), null, null, null, null);
        if (cursor.getCount() != 1) {
            ctx.getContentResolver().delete(MovieProvider.getSeetingsUri(), null, null);
            bExecute = true;
        } else {
            cursor.moveToFirst();

            long nDateUpdate = cursor.getLong(cursor.getColumnIndex(UrlSettingsColumns.DATE_UPDATED));
            bExecute = (System.currentTimeMillis() - nDateUpdate >= DAY_IN_MILLIS);

            cursor.close();
        }

        if (!bExecute) {
            return;
        }

        SettingsCallback callbackSettings = new SettingsCallback(ctx);
        methods.getSettings(API_KEY_VALUE, callbackSettings);

    }


    private String getRestApiSortOrder(String sIntefaceString) {
        String sResult = getTypeOfOrder(sIntefaceString, ctx.getString(R.string.order_popular),
                RestApiContract.SORT_KEY_POPULARITY_ASC, RestApiContract.SORT_KEY_POPULARITY_DESC);
        if (null != sResult) {
            return sResult;
        }

        sResult = getTypeOfOrder(sIntefaceString, ctx.getString(R.string.order_rated),
                RestApiContract.SORT_KEY_VOTE_ASC, RestApiContract.SORT_KEY_VOTE_DESC);
        if (null != sResult) {
            return sResult;
        }

        sResult = getTypeOfOrder(sIntefaceString, ctx.getString(R.string.order_release),
                RestApiContract.SORT_KEY_RELEASE_ASC, RestApiContract.SORT_KEY_RELEASE_DESC);
        if (null != sResult) {
            return sResult;
        }

        return null;
    }


    public void syncReviews(long nMovieId, int nPage, int nTotalPages) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();
        IApiMethods methods = restAdapter.create(IApiMethods.class);

        MovieDetailReviewCallback callbackMovieReviews = new MovieDetailReviewCallback(ctx, this);
        methods.getMovieReviews(nMovieId, API_KEY_VALUE, nPage, callbackMovieReviews);
    }

    public void syncMovie(long nMovieId) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();
        IApiMethods methods = restAdapter.create(IApiMethods.class);

        MovieDetailCallback callbackMovieDetail = new MovieDetailCallback(ctx, this);
        methods.getMovieDetail(nMovieId, API_KEY_VALUE, RestApiContract.APPEND_TO_RESPONSE_DETAIL, callbackMovieDetail);

    }

    public void deleteAndInsertMovieReviews(ContentValues[] movieReviews, int nMovieId) {
        ctx.getContentResolver().delete(MovieProvider.getMovieReviewUri(nMovieId),
                MovieDetailColumns.MOVIE_ID + " = ?", new String[]{String.valueOf(nMovieId)});

        ctx.getContentResolver().bulkInsert(MovieProvider.getMovieReviewUri(nMovieId), movieReviews);
    }

    public void deleteAndInsertMovieTrailers(ContentValues[] movieTrailers, int nMovieId) {
        ctx.getContentResolver().delete(MovieProvider.getMovieTrailerUri(nMovieId),
                MovieDetailColumns.MOVIE_ID + " = ?", new String[]{String.valueOf(nMovieId)});

        ctx.getContentResolver().bulkInsert(MovieProvider.getMovieTrailerUri(nMovieId), movieTrailers);
    }

    public void insertOrUpdateMovieDetails(ContentValues movieDetailValues, int nMovieId) {
        Cursor cursor =
                ctx.getContentResolver().query(MovieProvider.getMovieDetailUri(nMovieId), null, null, null, null);

        if (0 == cursor.getCount()) {
            cursor.close();
            ctx.getContentResolver().insert(MovieProvider.getMovieDetailUri(nMovieId), movieDetailValues);
        } else {
            cursor.close();
            ctx.getContentResolver().update(MovieProvider.getMovieDetailUri(nMovieId), movieDetailValues,
                    MovieDetailColumns.MOVIE_ID + " = ?", new String[]{String.valueOf(nMovieId)});
        }



    }


}
