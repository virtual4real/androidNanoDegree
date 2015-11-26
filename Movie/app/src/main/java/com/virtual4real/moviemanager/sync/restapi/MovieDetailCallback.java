package com.virtual4real.moviemanager.sync.restapi;

import android.content.ContentValues;
import android.content.Context;

import com.virtual4real.moviemanager.Utils;
import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.sync.DataTransformer;
import com.virtual4real.moviemanager.sync.SyncDataService;
import com.virtual4real.moviemanager.sync.poco.JsnMovieDetail;
import com.virtual4real.moviemanager.sync.poco.JsnReviews;
import com.virtual4real.moviemanager.sync.poco.JsnReviewsResult;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ioanagosman on 24/11/15.
 */
public class MovieDetailCallback implements Callback {

    private Context ctx;
    private SyncDataService service;

    public MovieDetailCallback(Context ctx, SyncDataService service) {
        this.ctx = ctx;
        this.service = service;
    }

    @Override
    public void success(Object o, Response response) {
        boolean bRefreshReviews = true;
        JsnMovieDetail movieDetailResult = (JsnMovieDetail) o;

        int nMovieId = Integer.parseInt(movieDetailResult.getId());

        ContentValues movieDetailValues = DataTransformer.getMovieDetailsContentValues(movieDetailResult);
        ContentValues[] movieTrailers = DataTransformer.getMovieTrailers(movieDetailResult);
        ContentValues[] movieReviews = DataTransformer.getMovieReviews(movieDetailResult);

        JsnReviews revs = movieDetailResult.getReviews();
        if (null != revs) {
            int nTotalPages = Integer.parseInt(revs.getTotal_pages());
            if (1 < nTotalPages) {
                bRefreshReviews = false;
                service.syncReviews(nMovieId, 2, nTotalPages);
            }

        }

        if (null != movieDetailValues) {
            service.insertOrUpdateMovieDetails(movieDetailValues, nMovieId);
        }

        if (null != movieTrailers) {
            service.deleteAndInsertMovieTrailers(movieTrailers, nMovieId);
        }

        if (null != movieReviews) {
            service.deleteAndInsertMovieReviews(movieReviews, nMovieId);
        }

        ctx.getContentResolver()
                .notifyChange(MovieProvider.getMovieDetailUri(nMovieId), null, false);
        ctx.getContentResolver()
                .notifyChange(MovieProvider.getMovieTrailerUri(nMovieId), null, false);

        if (bRefreshReviews) {
            ctx.getContentResolver()
                    .notifyChange(MovieProvider.getMovieReviewUri(nMovieId), null, false);
        }


    }

    @Override
    public void failure(RetrofitError retrofitError) {
        Utils.SendNotification(ctx, "Popular Movies", "Failure to get movie detail - " + retrofitError.getMessage());
    }
}
