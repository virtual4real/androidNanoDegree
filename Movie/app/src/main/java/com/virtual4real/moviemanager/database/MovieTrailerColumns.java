package com.virtual4real.moviemanager.database;


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by ioanagosman on 30/09/15.
 */


public interface MovieTrailerColumns {
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

    @DataType(DataType.Type.TEXT)
    @NotNull
    String NAME = "name";

    @DataType(DataType.Type.TEXT)
    String SIZE = "size";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String SOURCE = "source";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String TYPE = "type";


}
