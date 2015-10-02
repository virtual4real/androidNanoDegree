package com.virtual4real.moviemanager.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by ioanagosman on 30/09/15.
 */
@Database(name = MovieDatabase.NAME, version = MovieDatabase.VERSION)
public class MovieDatabase {

    public static final String NAME = "MoviesDB";
    public static final int VERSION = 1;
}
