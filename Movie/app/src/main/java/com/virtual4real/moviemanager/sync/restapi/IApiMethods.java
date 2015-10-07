package com.virtual4real.moviemanager.sync.restapi;

import com.virtual4real.moviemanager.sync.poco.JsnMovieDetail;
import com.virtual4real.moviemanager.sync.poco.JsnMovieSummaryResult;
import com.virtual4real.moviemanager.sync.poco.JsnSettings;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by ioanagosman on 28/09/15.
 */
public interface IApiMethods {

    @GET(RestApiContract.CONFIGURATION_PATH)
    void getSettings(@Query(RestApiContract.API_KEY) String key, Callback<JsnSettings> cb);

    @GET(RestApiContract.MOVIE_LIST_PATH)
    void getMovieSummary(@Query(RestApiContract.API_KEY) String key, @Query(RestApiContract.SORT_KEY) String sortby,
                         @Query(RestApiContract.PAGE_KEY) int page, Callback<JsnMovieSummaryResult> cb);

    @GET(RestApiContract.MOVIE_DETAIL)
    void getMovieDetail(@Path(RestApiContract.MOVIE_DETAIL_ID) long movieId,
                        @Query(RestApiContract.API_KEY) String key, Callback<JsnMovieDetail> cb);
}
