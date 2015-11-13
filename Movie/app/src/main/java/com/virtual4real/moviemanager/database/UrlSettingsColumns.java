package com.virtual4real.moviemanager.database;


/**
 * Created by ioanagosman on 30/09/15.
 */

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * UrlSettings contains configuration information
 * provided by the rest api regarding the image path construction.
 */
public interface UrlSettingsColumns {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String ID = "id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String DATE_UPDATED = "dateUpdated";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String BASE_URL = "baseUrl";

    @DataType(DataType.Type.TEXT)
    String SECURE_BASE_URL = "secureBaseUrl";

    @DataType(DataType.Type.TEXT)
    String BACKDROP_SIZE_URL = "backdropSizeUrl";

    @DataType(DataType.Type.TEXT)
    String LOGO_SIZE_URL = "logoSizeUrl";

    @DataType(DataType.Type.TEXT)
    String POSTER_SIZE_URL = "poster_size_url";

    @DataType(DataType.Type.TEXT)
    String PROFILE_SIZE_URL = "profileSizeUrl";

    @DataType(DataType.Type.TEXT)
    String STILL_SIZE_URL = "stillSizeUrl";


}
