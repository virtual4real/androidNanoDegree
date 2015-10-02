package com.virtual4real.moviemanager.sync.restapi;

import com.virtual4real.moviemanager.sync.poco.JsnMovieSummaryResult;
import com.virtual4real.moviemanager.sync.poco.JsnSettings;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ioanagosman on 28/09/15.
 */
public interface IApiMethods {

    // http://api.themoviedb.org/3/configuration?api_key=a631aec64cbc0a254235d4f45341a957
    @GET("/configuration")
    void getSettings(@Query("api_key") String key, Callback<JsnSettings> cb);

    //http://api.themoviedb.org/3/discover/movie?api_key=a631aec64cbc0a254235d4f45341a957&sort_by=popularity.desc&page=2
    @GET("/discover/movie")
    void getMovieSummary(@Query("api_key") String key, @Query("sort_by") String sortby,
                         @Query("page") int page, Callback<JsnMovieSummaryResult> cb);
}
