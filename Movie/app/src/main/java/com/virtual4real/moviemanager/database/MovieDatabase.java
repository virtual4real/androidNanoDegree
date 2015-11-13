package com.virtual4real.moviemanager.database;


/**
 * Created by ioanagosman on 30/09/15.
 */

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.ExecOnCreate;
import net.simonvt.schematic.annotation.Table;

/**
 * Defines the database name and version and it is used by Schematic
 */
@Database(version = MovieDatabase.VERSION, packageName = "com.virtual4real.moviemanager.provider")
public class MovieDatabase {

    private MovieDatabase() {
    }

    public static final int VERSION = 1;

    public static class Tables {

        @Table(MovieDetailColumns.class)
        public static final String MOVIE_DETAIL = "moviedetail";
        @Table(MovieSummaryColumns.class)
        public static final String MOVIE_SUMMARY = "moviesummary";
        @Table(MovieOrderColumns.class)
        public static final String MOVIE_ORDER = "movieorder";
        @Table(SyncOperationColumns.class)
        public static final String SYNC_OPERATION = "syncoperation";
        @Table(UrlSettingsColumns.class)
        public static final String URL_SETTINGS = "urlsettings";
    }

    /*
        @OnCreate public static void onCreate(Context context, SQLiteDatabase db) {
        }

        @OnUpgrade public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                                int newVersion) {
        }

        @OnConfigure public static void onConfigure(SQLiteDatabase db) {
        }
    */
    @ExecOnCreate
    public static final String EXEC_ON_CREATE = "PRAGMA foreign_keys = ON; ";
}


