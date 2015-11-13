package com.virtual4real.moviemanager.database;


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by ioanagosman on 30/09/15.
 */

/**
 * MovieOrder table contains a reference to a MovieSummary and the information about the
 * order of this MovieSummary in one of the possible sort types.
 * MovieOrder also contains a reference to the SyncOperation that corresponds to the data.
 * The SyncOperation contains the parameters (min date, max date, min vote, adult, video)
 * used to query data from the rest api.
 */


public interface MovieOrderColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String ID = "id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String DATE_UPDATED = "dateUpdated";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String SORT_TYPE = "sorttype";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String PAGE = "page";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String POSITION = "position";


    @DataType(DataType.Type.INTEGER)
    @References(table = MovieDatabase.Tables.MOVIE_SUMMARY, column = MovieSummaryColumns.ID)
    String MOVIE_SUMMARY_ID = "moviesummaryid";

    @DataType(DataType.Type.INTEGER)
    @References(table = MovieDatabase.Tables.SYNC_OPERATION, column = SyncOperationColumns.ID)
    String SYNC_OPERATION_ID = "syncoperationid";


}
