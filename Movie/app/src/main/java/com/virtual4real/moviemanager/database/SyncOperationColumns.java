package com.virtual4real.moviemanager.database;


/**
 * Created by ioanagosman on 12/10/15.
 */

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;


public interface SyncOperationColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String ID = "id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String DATE_UPDATED = "dateUpdated";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String DATE_START = "dateStart";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String DATE_END = "dateEnd";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String NO_OF_VOTES = "noOfVotes";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String IS_ADULT = "isAdult";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String IS_VIDEO = "isVideo";


}
