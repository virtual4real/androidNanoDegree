package com.virtual4real.moviemanager.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.virtual4real.moviemanager.R;
import com.virtual4real.moviemanager.Utils;
import com.virtual4real.moviemanager.database.UrlSettingsColumns;


/**
 * Created by ioanagosman on 28/09/15.
 */
public class MovieManagerSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieManagerSyncAdapter.class.getSimpleName();

    public static final String SYNC_SORT_ORDER = "sort_order";
    public static final String SYNC_PAGE = "page";
    public static final String SYNC_MOVIE_ID = "movie_id";

    // Interval at which to sync in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private static final int MOVIE_MANAGER_NOTIFICATION_ID = 3005;


    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[]{
            UrlSettingsColumns.BASE_URL,
            UrlSettingsColumns.SECURE_BASE_URL
    };


    private static final int INDEX_BASE_URL = 0;
    private static final int INDEX_BASE_SECURE_URL = 1;

    public MovieManagerSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        try {
            SyncDataService sync = new SyncDataService(getContext());

            String sOrder = extras.getString(SYNC_SORT_ORDER);
            int nPage = extras.getInt(SYNC_PAGE);
            long nMovieId = extras.getLong(SYNC_MOVIE_ID);

            if (nPage == 0) {
                nPage = 1;
            }

            if (0 == nMovieId) {
                SearchParameters sch = new SearchParameters(sOrder, nPage,
                        Utils.getMinDate(getContext()), Utils.getMaxDate(getContext()),
                        Utils.getMinVotes(getContext()), Utils.getIncludeAdult(getContext()),
                        Utils.getIncludeVideo(getContext()));
                sync.syncMovieSummary(account, authority, provider, syncResult, sch);
            } else {
                sync.syncMovie(nMovieId);
            }


        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        }

    }







    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            //TODO: the periodic sync deletes the favorites
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context, String sOrder, int nPage) {

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString(SYNC_SORT_ORDER, sOrder);
        bundle.putInt(SYNC_PAGE, nPage);

        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static void syncImmediately(Context context, long nMovieId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putLong(SYNC_MOVIE_ID, nMovieId);

        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));


        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieManagerSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        //syncImmediately(context, "", 1);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
