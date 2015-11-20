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

/**
 * Interface used by Retrofit to call the movie db rest api
 */
public interface IApiMethods {

    @GET(RestApiContract.CONFIGURATION_PATH)
    void getSettings(@Query(RestApiContract.API_KEY) String key, Callback<JsnSettings> cb);

    @GET(RestApiContract.MOVIE_LIST_PATH)
    void getMovieSummary(@Query(RestApiContract.API_KEY) String key, @Query(RestApiContract.SORT_KEY) String sortby,
                         @Query(RestApiContract.PAGE_KEY) int page,
                         @Query(RestApiContract.RELEASE_DATE_START_KEY) String sReleaseStart,
                         @Query(RestApiContract.RELEASE_DATE_END_KEY) String sReleaseEnd,
                         @Query(RestApiContract.VOTE_COUNT_MIN_KEY) int nMinVote,
                         @Query(RestApiContract.INCLUDE_VIDEO_KEY) boolean bIncludeVideo,
                         @Query(RestApiContract.INCLUDE_ADULT_KEY) boolean bIncludeAdult,
                         Callback<JsnMovieSummaryResult> cb);

    @GET(RestApiContract.MOVIE_DETAIL)
    void getMovieDetail(@Path(RestApiContract.MOVIE_DETAIL_ID) long movieId,
                        @Query(RestApiContract.API_KEY) String key,
                        @Query(RestApiContract.APPEND_TO_RESPONSE) String appendTo,
                        Callback<JsnMovieDetail> cb);
}
