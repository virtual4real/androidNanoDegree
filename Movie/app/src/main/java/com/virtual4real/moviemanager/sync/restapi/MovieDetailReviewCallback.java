package com.virtual4real.moviemanager.sync.restapi;

import android.content.ContentValues;
import android.content.Context;

import com.virtual4real.moviemanager.Utils;
import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.sync.DataTransformer;
import com.virtual4real.moviemanager.sync.SyncDataService;
import com.virtual4real.moviemanager.sync.poco.JsnReviewsResult;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ioanagosman on 24/11/15.
 */
public class MovieDetailReviewCallback implements Callback {

    private Context ctx;
    private SyncDataService service;

    public MovieDetailReviewCallback(Context ctx, SyncDataService service) {
        this.ctx = ctx;
        this.service = service;
    }

    @Override
    public void success(Object o, Response response) {
        JsnReviewsResult revs = (JsnReviewsResult) o;

        int nMovieId = Integer.parseInt(revs.getId());

        ContentValues[] movieReviews = DataTransformer.getMovieReviews(revs.getResults(), nMovieId);

        if (null != movieReviews) {
            service.deleteAndInsertMovieReviews(movieReviews, nMovieId);
        }

        int nPage = Integer.parseInt(revs.getPage());
        int nTotalPages = Integer.parseInt(revs.getTotal_pages());

        if (nPage == nTotalPages) {
            ctx.getContentResolver()
                    .notifyChange(MovieProvider.getMovieReviewUri(nMovieId), null, false);
        } else {
            service.syncReviews(nMovieId, nPage + 1, nTotalPages);
        }
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        Utils.SendNotification(ctx, "Popular Movies", "Failure to get movie reviews - " + retrofitError.getMessage());
    }
}
