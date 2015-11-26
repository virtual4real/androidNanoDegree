package com.virtual4real.moviemanager.sync.restapi;

/**
 * Created by ioanagosman on 02/10/15.
 */

/**
 * contract defining the movie db rest api path and paramters
 */
public class RestApiContract {
    public static final String CONFIGURATION_PATH = "/configuration";
    public static final String MOVIE_LIST_PATH = "/discover/movie";
    public static final String MOVIE_DETAIL = "/movie/{id}";
    public static final String MOVIE_DETAIL_REVIEWS = "/movie/{id}/reviews";
    public static final String MOVIE_DETAIL_ID = "id";


    public static final String API_KEY = "api_key";
    public static final String APPEND_TO_RESPONSE = "append_to_response";
    public static final String SORT_KEY = "sort_by";
    public static final String PAGE_KEY = "page";
    public static final String RELEASE_DATE_START_KEY = "release_date.gte";
    public static final String RELEASE_DATE_END_KEY = "release_date.lte";
    public static final String VOTE_COUNT_MIN_KEY = "vote_count.gte";
    public static final String INCLUDE_VIDEO_KEY = "include_video";
    public static final String INCLUDE_ADULT_KEY = "include_adult";

    public static final String SORT_KEY_POPULARITY_ASC = "popularity.asc";
    public static final String SORT_KEY_POPULARITY_DESC = "popularity.desc";
    public static final String SORT_KEY_VOTE_ASC = "vote_average.asc";
    public static final String SORT_KEY_VOTE_DESC = "vote_average.desc";
    public static final String SORT_KEY_RELEASE_ASC = "release_date.asc";
    public static final String SORT_KEY_RELEASE_DESC = "release_date.desc";

    public static final String SORT_KEY_FAVORITE_DESC = "favorite.desc";
    public static final String SORT_KEY_FAVORITE_ASC = "favorite.asc";

    public static final String APPEND_TO_RESPONSE_DETAIL = "reviews,trailers";

    public static final String IMAGE_BASE_URL_DEFAULT = "http://image.tmdb.org/t/p/w300";

}
