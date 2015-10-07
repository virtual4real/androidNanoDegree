package com.virtual4real.moviemanager.sync.restapi;

/**
 * Created by ioanagosman on 02/10/15.
 */
public class RestApiContract {
    public static final String CONFIGURATION_PATH = "/configuration";
    public static final String MOVIE_LIST_PATH = "/discover/movie";
    public static final String MOVIE_DETAIL = "/movie/{id}";
    public static final String MOVIE_DETAIL_ID = "id";


    public static final String API_KEY = "api_key";
    public static final String SORT_KEY = "sort_by";
    public static final String PAGE_KEY = "page";

    public static final String SORT_KEY_POPULARITY_ASC = "popularity.asc";
    public static final String SORT_KEY_POPULARITY_DESC = "popularity.desc";
    public static final String SORT_KEY_VOTE_ASC = "vote_average.asc";
    public static final String SORT_KEY_VOTE_DESC = "vote_average.desc";
    public static final String SORT_KEY_RELEASE_ASC = "release_date.asc";
    public static final String SORT_KEY_RELEASE_DESC = "release_date.desc";

    //TODO: get base url from where is taken for other services calls -- unify
    public static final String IMAGE_BASE_URL_DEFAULT = "http://image.tmdb.org/t/p/w300";

}
