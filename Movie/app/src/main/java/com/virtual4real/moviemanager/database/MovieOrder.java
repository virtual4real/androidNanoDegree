package com.virtual4real.moviemanager.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

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

@Table(databaseName = MovieDatabase.NAME)
public class MovieOrder extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    long dateUpdated;

    @Column
    @ForeignKey(
            references = {@ForeignKeyReference(columnName = "movie_id",
                    columnType = Long.class,
                    foreignColumnName = "id")},
            saveForeignKeyModel = false)
    MovieSummary movieSummary;

    @Column
    @ForeignKey(
            references = {@ForeignKeyReference(columnName = "sync_operation_id",
                    columnType = Long.class,
                    foreignColumnName = "id")},
            saveForeignKeyModel = false)
    SyncOperation syncOperation;


    @Column
    int sortType;

    @Column
    int page;

    @Column
    int position;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(long dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public MovieSummary getMovieSummary() {
        return movieSummary;
    }

    public void setMovieSummary(MovieSummary movieSummary) {
        this.movieSummary = movieSummary;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SyncOperation getSyncOperation() {
        return syncOperation;
    }

    public void setSyncOperation(SyncOperation syncOperation) {
        this.syncOperation = syncOperation;
    }
}
