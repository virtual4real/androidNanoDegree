package com.virtual4real.moviemanager.sync.restapi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.virtual4real.moviemanager.Utils;
import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.database.MovieOrderColumns;
import com.virtual4real.moviemanager.database.MovieSummaryColumns;
import com.virtual4real.moviemanager.sync.DataTransformer;
import com.virtual4real.moviemanager.sync.SyncDataService;
import com.virtual4real.moviemanager.sync.poco.JsnMovieDetail;
import com.virtual4real.moviemanager.sync.poco.JsnMovieSummaryResult;
import com.virtual4real.moviemanager.sync.poco.JsnReviews;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ioanagosman on 24/11/15.
 */
public class MovieSummaryCallback implements Callback {

    private Context ctx;
    private SyncDataService service;
    private String sSortOrder;
    private long nOperationId;
    private boolean notifyContentChange;

    public MovieSummaryCallback(Context ctx, SyncDataService service, String sSortOrder,
                                long nOperationId, boolean notifyContentChange) {
        this.ctx = ctx;
        this.service = service;
        this.sSortOrder = sSortOrder;
        this.nOperationId = nOperationId;
        this.notifyContentChange = notifyContentChange;
    }

    @Override
    public void success(Object o, Response response) {
        JsnMovieSummaryResult movieResult = (JsnMovieSummaryResult) o;

        int nSortType = MovieProvider.MovieOrderHelper.GetSortTypeInt(sSortOrder);
        int nTotalResults = Integer.parseInt(movieResult.getTotal_results());
        int nTotalPages = Integer.parseInt(movieResult.getTotal_pages());
        int nPage = Integer.parseInt(movieResult.getPage());

        int nDeleted = ctx.getContentResolver().delete(MovieProvider.getMovieOrderUri(),
                MovieOrderColumns.PAGE + " = ? AND " + MovieOrderColumns.SORT_TYPE + " = ?",
                new String[]{Integer.toString(nPage), Integer.toString(nSortType)});


        ContentValues[] movieValues =
                DataTransformer.getMovieSummaryContentValues(ctx, movieResult.getResults());

        for (int i = 0; i < movieValues.length; i++) {
            String nMovieId = (String) movieValues[i].get(MovieSummaryColumns.MOVIE_ID);
            long nMovieSummaryId = 0;

            Cursor crs = ctx.getContentResolver().query(MovieProvider.getMovieSummaryUri(),
                    new String[]{MovieSummaryColumns.ID},
                    MovieSummaryColumns.MOVIE_ID + " = ?",
                    new String[]{nMovieId}, null, null);

            if (null != crs && 0 != crs.getCount()) {
                crs.moveToFirst();

                nMovieSummaryId = crs.getLong(crs.getColumnIndex(MovieSummaryColumns.ID));

                crs.close();

                ctx.getContentResolver().update(MovieProvider.getMovieSummaryUri(), movieValues[i],
                        MovieSummaryColumns.MOVIE_ID + " = ?",
                        new String[]{nMovieId});
            } else {
                Uri uriNewMovieProvider =
                        ctx.getContentResolver().insert(MovieProvider.getMovieSummaryUri(), movieValues[i]);
                nMovieSummaryId = MovieProvider.getMovieSummaryId(uriNewMovieProvider);
            }

            ContentValues cvMovieOrder =
                    DataTransformer.getMovieOrderContentValues(nPage, nSortType, nOperationId, nMovieSummaryId, i);
            ctx.getContentResolver().insert(MovieProvider.getMovieOrderUri(), cvMovieOrder);

        }

        if (notifyContentChange) {
            ctx.getContentResolver().notifyChange(MovieProvider.getMovieSummaryUri(), null, false);
        }
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        Utils.SendNotification(ctx, "Popular Movies", "Failure to get movies - " + retrofitError.getMessage());
    }
}
