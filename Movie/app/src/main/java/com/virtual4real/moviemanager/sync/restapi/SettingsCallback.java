package com.virtual4real.moviemanager.sync.restapi;

import android.content.ContentValues;
import android.content.Context;

import com.virtual4real.moviemanager.Utils;
import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.sync.DataTransformer;
import com.virtual4real.moviemanager.sync.SyncDataService;
import com.virtual4real.moviemanager.sync.poco.JsnMovieDetail;
import com.virtual4real.moviemanager.sync.poco.JsnReviews;
import com.virtual4real.moviemanager.sync.poco.JsnSettings;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ioanagosman on 24/11/15.
 */
public class SettingsCallback implements Callback {

    private Context ctx;

    public SettingsCallback(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void success(Object o, Response response) {
        JsnSettings setting = (JsnSettings) o;

        ContentValues settingsValues = DataTransformer.getSettingsContentValues(setting);

        ctx.getContentResolver().insert(
                MovieProvider.getSeetingsUri(),
                settingsValues
        );
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        Utils.SendNotification(ctx, "Popular Movies", "Failure to get settings - " + retrofitError.getMessage());
    }
}
