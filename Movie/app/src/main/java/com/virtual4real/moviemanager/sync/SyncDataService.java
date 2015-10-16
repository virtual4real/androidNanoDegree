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
import com.virtual4real.moviemanager.data.MovieContract;
import com.virtual4real.moviemanager.database.UrlSettings$Table;
import com.virtual4real.moviemanager.sync.poco.JsnMovieDetail;
import com.virtual4real.moviemanager.sync.poco.JsnMovieSummaryResult;
import com.virtual4real.moviemanager.sync.poco.JsnSettings;
import com.virtual4real.moviemanager.sync.restapi.IApiMethods;
import com.virtual4real.moviemanager.sync.restapi.RestApiContract;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ioanagosman on 16/10/15.
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


    public void syncMovieSummary(Account account, String authority, ContentProviderClient provider,
                                 SyncResult syncResult, SearchParameters sch) {

        boolean bCallAll = false;

        String sOrder = sch.getOrder();
        int nPage = sch.getPage();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();
        IApiMethods methods = restAdapter.create(IApiMethods.class);

        if (null == sOrder) {
            bCallAll = true;
        } else {
            getMoviesByPageAndSortOrder(methods, nPage, getRestApiSortOrder(sOrder), sch, true);
            getMoviesByPageAndSortOrder(methods, nPage + 1, getRestApiSortOrder(sOrder), sch, true);
        }

        if (bCallAll) {
            getSettings(methods);

            getAllMovies(methods, nPage, sch, true);
            getAllMovies(methods, nPage + 1, sch, true);
        }

    }


    private void getAllMovies(IApiMethods methods, int nPage, SearchParameters sch, final boolean notifyContentChange) {
        getMoviesByPageAndSortOrder(methods, nPage, RestApiContract.SORT_KEY_POPULARITY_ASC, sch, false);
        getMoviesByPageAndSortOrder(methods, nPage, RestApiContract.SORT_KEY_POPULARITY_DESC, sch, false);
        getMoviesByPageAndSortOrder(methods, nPage, RestApiContract.SORT_KEY_VOTE_ASC, sch, false);
        getMoviesByPageAndSortOrder(methods, nPage, RestApiContract.SORT_KEY_VOTE_DESC, sch, false);
        getMoviesByPageAndSortOrder(methods, nPage, RestApiContract.SORT_KEY_RELEASE_ASC, sch, false);
        getMoviesByPageAndSortOrder(methods, nPage, RestApiContract.SORT_KEY_RELEASE_DESC, sch, false);

        if (notifyContentChange) {
            ctx.getContentResolver().notifyChange(MovieContract.MovieSummaryEntry.CONTENT_URI, null, false);
        }
    }

    private void getMoviesByPageAndSortOrder(IApiMethods methods, int nPage, final String sSortOrder,
                                             SearchParameters sch, final boolean notifyContentChange) {
        Callback callbackMovieSummary = new Callback() {
            @Override
            public void success(Object o, Response response) {
                JsnMovieSummaryResult movieResult = (JsnMovieSummaryResult) o;


                ContentValues[] movieValues =
                        DataTransformer.getMovieSummaryContentValues(
                                movieResult.getTotal_results(),
                                movieResult.getTotal_pages(),
                                movieResult.getPage(), sSortOrder,
                                movieResult.getResults());


                int nInserted = ctx.getContentResolver().bulkInsert(
                        MovieContract.MovieSummaryEntry.CONTENT_URI,
                        movieValues
                );

                if (notifyContentChange) {
                    ctx.getContentResolver().notifyChange(MovieContract.MovieSummaryEntry.CONTENT_URI, null, false);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        };
        methods.getMovieSummary(API_KEY_VALUE, sSortOrder, nPage, sch.getMinDate(), sch.getMaxDate(),
                sch.getMinVotes(), sch.isIncludesVideo(), sch.isIncludesAdult(), callbackMovieSummary);

    }


    private String getTypeOfOrder(String sInterface, String sOrder, String asc, String desc) {
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
                .query(MovieContract.SettingEntry.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() != 1) {
            ctx.getContentResolver().delete(MovieContract.SettingEntry.CONTENT_URI, null, null);
            bExecute = true;
        } else {
            cursor.moveToFirst();

            long nDateUpdate = cursor.getLong(cursor.getColumnIndex(UrlSettings$Table.DATEUPDATED));
            bExecute = (System.currentTimeMillis() - nDateUpdate >= DAY_IN_MILLIS);

            cursor.close();
        }

        if (!bExecute) {
            return;
        }

        Callback callbackSettings = new Callback() {
            @Override
            public void success(Object o, Response response) {
                JsnSettings setting = (JsnSettings) o;

                ContentValues settingsValues = DataTransformer.getSettingsContentValues(setting);


                Uri insertedUri = ctx.getContentResolver().insert(
                        MovieContract.SettingEntry.CONTENT_URI,
                        settingsValues
                );
                long settingId = ContentUris.parseId(insertedUri);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        };
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


    public void syncMovie(Account account, String authority, ContentProviderClient provider,
                          SyncResult syncResult, long nMovieId) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();
        IApiMethods methods = restAdapter.create(IApiMethods.class);

        Callback callbackMovieDetail = new Callback() {
            @Override
            public void success(Object o, Response response) {
                JsnMovieDetail movieDetailResult = (JsnMovieDetail) o;


                //ContentValues[] movieValues =
                //        getMovieSummaryContentValues(movieDetailResult);

                //int nInserted = getContext().getContentResolver().bulkInsert(
                //        MovieContract.MovieDetailEntry.CONTENT_URI,
                //        movieValues
                //);


                //getContext().getContentResolver().notifyChange(MovieContract.MovieSummaryEntry.CONTENT_URI, null, false);

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        };
        methods.getMovieDetail(nMovieId, API_KEY_VALUE, callbackMovieDetail);

    }


}
