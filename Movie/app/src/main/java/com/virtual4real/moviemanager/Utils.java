package com.virtual4real.moviemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.virtual4real.moviemanager.sync.restapi.RestApiContract;

/**
 * Created by ioanagosman on 02/10/15.
 */
public class Utils {

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
        //TODO: construct the image base url with the root url from where the services are called --- unify
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
        //TODO: construct the image base url with the root url from where the services are called --- unify
        return prefs.getString(context.getString(R.string.backdrop_base_key), RestApiContract.IMAGE_BASE_URL_DEFAULT);
    }

}
