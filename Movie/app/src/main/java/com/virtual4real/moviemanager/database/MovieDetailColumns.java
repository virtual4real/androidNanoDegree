package com.virtual4real.moviemanager.database;

/**
 * Created by ioanagosman on 30/09/15.
 */

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;


public interface MovieDetailColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String ID = "id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String DATE_UPDATED = "dateUpdated";

    @DataType(DataType.Type.INTEGER)
    @References(table = MovieDatabase.Tables.MOVIE_SUMMARY, column = MovieSummaryColumns.MOVIE_ID)
    String MOVIE_ID = "movieId";

    @DataType(DataType.Type.INTEGER)
    String DATE_RELEASE = "releaseDate";

    @DataType(DataType.Type.TEXT)
    String GENRES = "genres";

    @DataType(DataType.Type.TEXT)
    String HOMEPAGE = "homepage";

    @DataType(DataType.Type.TEXT)
    String STATUS = "status";

    @DataType(DataType.Type.TEXT)
    String TAG_LINE = "tagLine";

    @DataType(DataType.Type.INTEGER)
    String RUNTIME = "runtime";

}
