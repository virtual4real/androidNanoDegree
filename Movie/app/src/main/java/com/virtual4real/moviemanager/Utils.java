package com.virtual4real.moviemanager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.squareup.picasso.Picasso;
import com.virtual4real.moviemanager.sync.restapi.RestApiContract;

/**
 * Created by ioanagosman on 02/10/15.
 */
public class Utils {

    private static int MOVIE_SUMMARY_NOTIFICATION_ID = 3005;

    public static String getOrderAndSortFromPreferences(Context context) {
        String sOrderSort = null;

        String sOrder = getPreferredOrder(context);
        String sSort = getPreferredSort(context);

        sOrderSort = sOrder + "." + sSort;

        return sOrderSort;
    }

    public static void setPreferredSort(Context context, String sValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.sort_key), sValue);
        editor.commit();
    }

    public static String getPreferredSort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.sort_key), context.getString(R.string.sort_desc));
    }

    public static void setPreferredOrder(Context context, String sValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.order_key), sValue);
        editor.commit();
    }

    public static String getPreferredOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.order_key), context.getString(R.string.order_popular));
    }


    public static void setBaseImageUrl(Context context, String sValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.image_base_key), sValue);
        editor.commit();
    }

    public static String getBaseImageUrl(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.image_base_key), RestApiContract.IMAGE_BASE_URL_DEFAULT);
    }

    public static void setBackdropImageUrl(Context context, String sValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.backdrop_base_key), sValue);
        editor.commit();
    }

    public static String getBackdropImageUrl(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.backdrop_base_key), RestApiContract.IMAGE_BASE_URL_DEFAULT);
    }

    public static String getMinDate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sDate = prefs.getString(context.getString(R.string.search_start_key), context.getString(R.string.default_min_date));

        //error in the rest api if date is 29 of february
        if (sDate.endsWith("0229")) {
            sDate = sDate.substring(0, sDate.length() - 1) + 8;
        }

        return sDate;
    }

    public static String getMaxDate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sDate = prefs.getString(context.getString(R.string.search_end_key), context.getString(R.string.default_max_date));

        //error in the rest api if date is 29 of february
        if (sDate.endsWith("0229")) {
            sDate = sDate.substring(0, sDate.length() - 1) + 8;
        }

        return sDate;

    }

    public static int getMinVotes(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString(context.getString(R.string.search_min_vote_key), context.getString(R.string.default_min_votes)));
    }


    public static boolean getIncludeAdult(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.search_adult_key),
                Boolean.parseBoolean(context.getString(R.string.default_adult)));
    }

    public static boolean getIncludeVideo(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.search_video_key),
                Boolean.parseBoolean(context.getString(R.string.default_video)));
    }

    public static boolean isConnected(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void SendNotification(Context ctx, String title, String text) {
        // NotificationCompatBuilder is a very convenient way to build backward-compatible
        // notifications.  Just throw in some data.
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setColor(ContextCompat.getColor(ctx, R.color.primary))
                        .setSmallIcon(R.drawable.moviemanager)
                        .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.moviemanager))
                        .setContentTitle(title)
                        .setContentText(text);

        // Make something interesting happen when the user clicks on the notification.
        // In this case, opening the app is sufficient.
        Intent resultIntent = new Intent(ctx, MovieSummaryActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
        mNotificationManager.notify(MOVIE_SUMMARY_NOTIFICATION_ID, mBuilder.build());
    }


}
