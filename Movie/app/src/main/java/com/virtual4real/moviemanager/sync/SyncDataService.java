package com.virtual4real.moviemanager.sync;


import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.virtual4real.moviemanager.R;
import com.virtual4real.moviemanager.Utils;
import com.virtual4real.moviemanager.data.MovieContract;
import com.virtual4real.moviemanager.database.MovieOrder$Table;
import com.virtual4real.moviemanager.database.SyncOperation;
import com.virtual4real.moviemanager.database.SyncOperation$Table;
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
        Cursor cursor = ctx.getContentResolver().query(MovieContract.SyncOperationEntry.CONTENT_URI, null, null,
                new String[]{sch.getMaxDate(), sch.getMinDate(),
                        Integer.toString(sch.getMinVotes()), Boolean.toString(sch.isIncludesAdult()),
                        Boolean.toString(sch.isIncludesVideo())}, null);

        long nResult = 0;

        if (cursor.moveToFirst()) {
            nResult = cursor.getLong(cursor.getColumnIndex(SyncOperation$Table.ID));
        }

        cursor.close();

        return nResult;

    }

    public long InsertNewSyncOperation(SearchParameters sch) {
        Uri uri = ctx.getContentResolver().insert(MovieContract.SyncOperationEntry.CONTENT_URI,
                DataTransformer.getSyncOperationContentValues(sch));

        return Long.parseLong(uri.getLastPathSegment());
    }

    private int DeleteAllMovieOrderData() {
        return ctx.getContentResolver().delete(MovieContract.SyncOperationEntry.CONTENT_URI, null, null);
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

        if (!bCallAll && null == sOrder) {
            bCallAll = true;
        } else {
            //Utils.SendNotification(ctx, "Popular Movies", "Get movies by sort order and page");
            getMoviesByPageAndSortOrder(methods, nPage, getRestApiSortOrder(sOrder), sch, true, nOperationId);
            getMoviesByPageAndSortOrder(methods, nPage + 1, getRestApiSortOrder(sOrder), sch, true, nOperationId);
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
            ctx.getContentResolver().notifyChange(MovieContract.MovieSummaryEntry.CONTENT_URI, null, false);
        }
    }

    private void getMoviesByPageAndSortOrder(IApiMethods methods, int nPage, final String sSortOrder,
                                             SearchParameters sch, final boolean notifyContentChange, final long nOperationId) {

        boolean bGetFromService = false;
        Cursor cursor =
                ctx.getContentResolver().query(MovieContract.MovieOrderEntry.CONTENT_URI, null, null,
                        new String[]{Integer.toString(nPage), sSortOrder}, null);
        if (null != cursor && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long nTime = cursor.getLong(cursor.getColumnIndex(MovieOrder$Table.DATEUPDATED));
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

        Callback callbackMovieSummary = new Callback() {
            @Override
            public void success(Object o, Response response) {
                JsnMovieSummaryResult movieResult = (JsnMovieSummaryResult) o;


                ContentValues[] movieValues =
                        DataTransformer.getMovieSummaryContentValues(
                                movieResult.getTotal_results(),
                                movieResult.getTotal_pages(),
                                movieResult.getPage(), sSortOrder,
                                movieResult.getResults(), nOperationId);


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
                Utils.SendNotification(ctx, "Popular Movies", "Failure to get movies - " + retrofitError.getMessage());
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
                Utils.SendNotification(ctx, "Popular Movies", "Failure to get settings - " + retrofitError.getMessage());
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

                //getContext().getContentResolver()
                // .notifyChange(MovieContract.MovieSummaryEntry.CONTENT_URI, null, false);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Utils.SendNotification(ctx, "Popular Movies", "Failure to get movie detail - " + retrofitError.getMessage());
            }
        };
        methods.getMovieDetail(nMovieId, API_KEY_VALUE, callbackMovieDetail);

    }


}
