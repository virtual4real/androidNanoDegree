package com.virtual4real.moviemanager.database;


import android.support.annotation.Nullable;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by ioanagosman on 30/09/15.
 */
public interface MovieSummaryColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String ID = "id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String DATE_UPDATED = "dateUpdated";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String MOVIE_ID = "movieId";

    @DataType(DataType.Type.INTEGER)
    @Nullable
    String DATE_RELEASE = "dateRelease";

    @DataType(DataType.Type.INTEGER)
    @Nullable
    String YEAR_OF_RELEASE = "yearOfRelease";

    @DataType(DataType.Type.REAL)
    @Nullable
    String POPULARITY = "popularity";

    @DataType(DataType.Type.REAL)
    @Nullable
    String VOTE_AVERAGE = "voteAverage";

    @DataType(DataType.Type.INTEGER)
    @Nullable
    String VOTE_COUNT = "voteCount";

    @DataType(DataType.Type.TEXT)
    @Nullable
    String ORIGINAL_TITLE = "originalTitle";

    @DataType(DataType.Type.TEXT)
    @Nullable
    String OVERVIEW = "overview";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String TITLE = "title";

    @DataType(DataType.Type.TEXT)
    @Nullable
    String POSTER_PATH = "posterPath";

    @DataType(DataType.Type.TEXT)
    @Nullable
    String BACKDROP_PATH = "backdropPath";


    @DataType(DataType.Type.INTEGER)
    @NotNull
    String IS_FAVORITE = "isFavorite";
}
