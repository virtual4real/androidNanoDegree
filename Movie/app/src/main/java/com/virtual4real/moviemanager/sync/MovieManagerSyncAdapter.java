package com.virtual4real.moviemanager.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.virtual4real.moviemanager.R;
import com.virtual4real.moviemanager.data.MovieContract;
import com.virtual4real.moviemanager.database.MovieSummary;
import com.virtual4real.moviemanager.database.MovieSummary$Table;
import com.virtual4real.moviemanager.database.UrlSettings;
import com.virtual4real.moviemanager.database.UrlSettings$Table;
import com.virtual4real.moviemanager.sync.poco.JsnImages;
import com.virtual4real.moviemanager.sync.poco.JsnMovieDetail;
import com.virtual4real.moviemanager.sync.poco.JsnMovieSummary;
import com.virtual4real.moviemanager.sync.poco.JsnMovieSummaryResult;
import com.virtual4real.moviemanager.sync.poco.JsnSettings;
import com.virtual4real.moviemanager.sync.restapi.IApiMethods;
import com.virtual4real.moviemanager.sync.restapi.RestApiContract;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ioanagosman on 28/09/15.
 */
public class MovieManagerSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieManagerSyncAdapter.class.getSimpleName();

    public static final String SYNC_SORT_ORDER = "sort_order";
    public static final String SYNC_PAGE = "page";
    public static final String SYNC_MOVIE_ID = "movie_id";

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int MOVIE_MANAGER_NOTIFICATION_ID = 3005;


    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[]{
            UrlSettings$Table.BASEURL,
            UrlSettings$Table.SECUREBASEURL
    };

    // these indices must match the projection
    private static final int INDEX_BASE_URL = 0;
    private static final int INDEX_BASE_SECURE_URL = 1;

    public MovieManagerSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        try {
            final String API_KEY_VALUE = getContext().getString(R.string.api_key);
            final String API_URL = getContext().getString(R.string.api_url);

            String sOrder = extras.getString(SYNC_SORT_ORDER);
            int nPage = extras.getInt(SYNC_PAGE);
            long nMovieId = extras.getLong(SYNC_MOVIE_ID);

            if (0 == nMovieId) {
                syncMovieSummary(account, authority, provider, syncResult, API_KEY_VALUE, API_URL, sOrder, nPage);
            } else {
                syncMovie(account, authority, provider, syncResult, API_KEY_VALUE, API_URL, nMovieId);
            }


        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        }
        return;
    }

    private void syncMovie(Account account, String authority, ContentProviderClient provider,
                           SyncResult syncResult, String API_KEY_VALUE, String API_URL, long nMovieId) {

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

    private void syncMovieSummary(Account account, String authority, ContentProviderClient provider,
                                  SyncResult syncResult, String API_KEY_VALUE, String API_URL, String sOrder, int nPage) {

        boolean bCallAll = false;

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();
        IApiMethods methods = restAdapter.create(IApiMethods.class);

        if (null == sOrder) {
            bCallAll = true;
        } else {
            getMoviesByPageAndSortOrder(methods, API_KEY_VALUE, nPage, getRestApiSortOrder(sOrder), false);
        }

        if (bCallAll) {
            getSettings(methods, API_KEY_VALUE);

            getMoviesByPageAndSortOrder(methods, API_KEY_VALUE, nPage, RestApiContract.SORT_KEY_POPULARITY_ASC, false);
            getMoviesByPageAndSortOrder(methods, API_KEY_VALUE, nPage, RestApiContract.SORT_KEY_POPULARITY_DESC, false);
            getMoviesByPageAndSortOrder(methods, API_KEY_VALUE, nPage, RestApiContract.SORT_KEY_VOTE_ASC, false);
            getMoviesByPageAndSortOrder(methods, API_KEY_VALUE, nPage, RestApiContract.SORT_KEY_VOTE_DESC, false);
            getMoviesByPageAndSortOrder(methods, API_KEY_VALUE, nPage, RestApiContract.SORT_KEY_RELEASE_ASC, false);
            getMoviesByPageAndSortOrder(methods, API_KEY_VALUE, nPage, RestApiContract.SORT_KEY_RELEASE_DESC, false);
        }

    }

    private String getRestApiSortOrder(String sIntefaceString) {
        String sResult = getTypeOfOrder(sIntefaceString, getContext().getString(R.string.order_popular));
        if (null != sResult) {
            return sResult;
        }

        sResult = getTypeOfOrder(sIntefaceString, getContext().getString(R.string.order_rated));
        if (null != sResult) {
            return sResult;
        }

        sResult = getTypeOfOrder(sIntefaceString, getContext().getString(R.string.order_release));
        if (null != sResult) {
            return sResult;
        }

        return null;
    }

    private String getTypeOfOrder(String sInterface, String sOrder) {
        if (sInterface.startsWith(sOrder)) {
            return getAscOrDesc(sInterface, RestApiContract.SORT_KEY_POPULARITY_ASC, RestApiContract.SORT_KEY_POPULARITY_DESC);
        }

        return null;
    }

    private String getAscOrDesc(String sInterface, String sAsc, String sDesc) {
        if (null == sInterface) return null;

        return sInterface.endsWith(getContext().getString(R.string.sort_asc)) ? sAsc : sDesc;
    }

    private void getMoviesByPageAndSortOrder(IApiMethods methods, String sApiKey, int nPage, final String sSortOrder, final boolean notifyContentChange) {
        Callback callbackMovieSummary = new Callback() {
            @Override
            public void success(Object o, Response response) {
                JsnMovieSummaryResult movieResult = (JsnMovieSummaryResult) o;


                ContentValues[] movieValues =
                        getMovieSummaryContentValues(
                                movieResult.getTotal_results(),
                                movieResult.getTotal_pages(),
                                movieResult.getPage(), sSortOrder,
                                movieResult.getResults());


                int nInserted = getContext().getContentResolver().bulkInsert(
                        MovieContract.MovieSummaryEntry.CONTENT_URI,
                        movieValues
                );

                if (notifyContentChange) {
                    getContext().getContentResolver().notifyChange(MovieContract.MovieSummaryEntry.CONTENT_URI, null, false);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        };
        methods.getMovieSummary(sApiKey, sSortOrder, 1, callbackMovieSummary);

    }

    private void getSettings(IApiMethods methods, String sApiKey) {
        boolean bExecute = false;

        Cursor cursor = getContext().getContentResolver()
                .query(MovieContract.SettingEntry.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() != 1) {
            getContext().getContentResolver().delete(MovieContract.SettingEntry.CONTENT_URI, null, null);
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

                ContentValues settingsValues = getSettingsContentValues(setting);


                Uri insertedUri = getContext().getContentResolver().insert(
                        MovieContract.SettingEntry.CONTENT_URI,
                        settingsValues
                );
                long settingId = ContentUris.parseId(insertedUri);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        };
        methods.getSettings(sApiKey, callbackSettings);

    }

    private ContentValues[] getMovieSummaryContentValues(String totalResults, String totalPages,
                                                         String currentPage, String sortType, JsnMovieSummary[] movies) {
        ContentValues[] values = new ContentValues[movies.length + 1];

        values[0] = getMovieSummaryInfoContentValues(totalResults, totalPages, currentPage, sortType);

        for (int i = 0; i < movies.length; i++) {
            values[i + 1] = getMovieSummaryContentValue(movies[i], i);

        }

        return values;
    }

    private ContentValues getMovieSummaryInfoContentValues(String totalResults,
                                                           String totalPages, String currentPage, String sortType) {
        ContentValues values = new ContentValues();

        values.put(MovieContract.MovieSummaryEntry.PAGINATION_TOTAL_RESULTS, totalResults);
        values.put(MovieContract.MovieSummaryEntry.PAGINATION_TOTAL_PAGES, totalPages);
        values.put(MovieContract.MovieSummaryEntry.PAGINATION_CURRENT_PAGE, currentPage);
        values.put(MovieContract.MovieSummaryEntry.SORT_TYPE, MovieContract.MovieSummaryEntry.GetSortTypeInt(sortType));

        return values;
    }


    private ContentValues getMovieSummaryContentValue(JsnMovieSummary movie, int i) {
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
        value.put(MovieContract.MovieSummaryEntry.MOVIE_SUMMARY_POSITION, i);

        return value;
    }

    private ContentValues getSettingsContentValues(JsnSettings setting) {
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

    private String getValues(String[] vstr) {
        StringBuilder builder = new StringBuilder();
        for (String s : vstr) {
            if (s.equals("original")) continue;
            builder.append(s);
            builder.append(";");
        }
        return builder.toString();
    }


    private void notifyMovies() {
        Log.d(LOG_TAG, "notify movies");
        Context context = getContext();

        //checking the last update and notify if it' the first of the day
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        //boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
        //        Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        //if ( displayNotifications ) {

        //String lastNotificationKey = context.getString(R.string.pref_last_notification);
        //long lastSync = prefs.getLong(lastNotificationKey, 0);

//            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
//                // Last sync was more than 1 day ago, let's send a notification with the weather.
//                String locationQuery = Utility.getPreferredLocation(context);
//
//                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationQuery, System.currentTimeMillis());
//
//                // we'll query our contentProvider, as always
//                Cursor cursor = context.getContentResolver().query(weatherUri, NOTIFY_WEATHER_PROJECTION, null, null, null);
//
//                if (cursor.moveToFirst()) {
//                    int weatherId = cursor.getInt(INDEX_WEATHER_ID);
//                    double high = cursor.getDouble(INDEX_MAX_TEMP);
//                    double low = cursor.getDouble(INDEX_MIN_TEMP);
//                    String desc = cursor.getString(INDEX_SHORT_DESC);
//
//                    int iconId = Utility.getIconResourceForWeatherCondition(weatherId);
//                    Resources resources = context.getResources();
//                    Bitmap largeIcon = BitmapFactory.decodeResource(resources,
//                            Utility.getArtResourceForWeatherCondition(weatherId));
//                    String title = context.getString(R.string.app_name);
//
//                    // Define the text of the forecast.
//                    String contentText = String.format(context.getString(R.string.format_notification),
//                            desc,
//                            Utility.formatTemperature(context, high),
//                            Utility.formatTemperature(context, low));
//
//                    // NotificationCompatBuilder is a very convenient way to build backward-compatible
//                    // notifications.  Just throw in some data.
//                    NotificationCompat.Builder mBuilder =
//                            new NotificationCompat.Builder(getContext())
//                                    .setColor(resources.getColor(R.color.sunshine_light_blue))
//                                    .setSmallIcon(iconId)
//                                    .setLargeIcon(largeIcon)
//                                    .setContentTitle(title)
//                                    .setContentText(contentText);
//
//                    // Make something interesting happen when the user clicks on the notification.
//                    // In this case, opening the app is sufficient.
//                    Intent resultIntent = new Intent(context, MainActivity.class);
//
//                    // The stack builder object will contain an artificial back stack for the
//                    // started Activity.
//                    // This ensures that navigating backward from the Activity leads out of
//                    // your application to the Home screen.
//                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//                    stackBuilder.addNextIntent(resultIntent);
//                    PendingIntent resultPendingIntent =
//                            stackBuilder.getPendingIntent(
//                                    0,
//                                    PendingIntent.FLAG_UPDATE_CURRENT
//                            );
//                    mBuilder.setContentIntent(resultPendingIntent);
//
//                    NotificationManager mNotificationManager =
//                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
//                    mNotificationManager.notify(WEATHER_NOTIFICATION_ID, mBuilder.build());
//
//                    //refreshing last sync
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
//                    editor.commit();
//                }
//                cursor.close();
//            }
        //}
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
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
        //ContentResolver.setIsSyncable(getSyncAccount(context), context.getString(R.string.content_authority), 1);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static void syncImmediately(Context context, long nMovieId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putLong(SYNC_MOVIE_ID, nMovieId);
        //ContentResolver.setIsSyncable(getSyncAccount(context), context.getString(R.string.content_authority), 1);
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

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context, "", 1);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
