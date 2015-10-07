package com.virtual4real.moviemanager.database;

import android.database.Cursor;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.List;

/**
 * Created by ioanagosman on 01/10/15.
 */
public class DbService {

    public UrlSettings GetFirstUrlSetting() {
        UrlSettings setting = new Select().from(UrlSettings.class).querySingle();
        return setting;
    }

    public Cursor GetUrlSetting() {
        return new Select().from(UrlSettings.class).query();
    }

    public Cursor GetMovieSummaries(int nSort, int nPage) {
        return new Select()
                .from(MovieSummary.class)
                .join(MovieOrder.class, Join.JoinType.INNER)
                .on(Condition.column(ColumnAlias.columnWithTable(MovieSummary$Table.TABLE_NAME, MovieSummary$Table.ID))
                        .is(MovieOrder$Table.MOVIESUMMARY_MOVIE_ID))
                .where(Condition.column(MovieOrder$Table.SORTTYPE).eq(nSort)/*,
                        Condition.column(MovieOrder$Table.PAGE).eq(nPage)*/)
                .orderBy(true, MovieOrder$Table.POSITION).query();
    }


    public long InsertUrlSetting(UrlSettings url) {
        if (0 == url.getId()) {
            url.insert();
        } else {
            url.save();
        }
        return url.id;
    }

    public void DeleteUrlSettings(long nId) {
        Delete.table(UrlSettings.class, Condition.column(UrlSettings$Table.ID).isNot(nId));
    }

    public void DeleteAllUrlSettings() {
        Delete.table(UrlSettings.class);
    }

    public void DeleteMovieOrder(long nPage, int sortType) {
        Delete.table(MovieOrder.class,
                Condition.column(MovieOrder$Table.PAGE).is(nPage),
                Condition.column(MovieOrder$Table.SORTTYPE).is(sortType));
    }

    public void DeleteMovieSummary(long nId) {
        MovieSummary movieSummary = GetMovieSummaryByMovieId(nId);

        if (null == movieSummary) {
            return;
        }

        Delete.table(MovieOrder.class,
                Condition.column(MovieOrder$Table.MOVIESUMMARY_MOVIE_ID).is(movieSummary.getId()));

        Delete.table(MovieSummary.class,
                Condition.column(MovieSummary$Table.ID).is(nId));
    }

    public void DeleteAllMovieSummary() {
        Delete.table(MovieOrder.class);
        Delete.table(MovieSummary.class);
    }

    public MovieSummary GetMovieSummaryByMovieId(long movieId) {
        MovieSummary movie = new Select().from(MovieSummary.class)
                .where(Condition.column(MovieSummary$Table.MOVIEID).is(movieId)).querySingle();
        return movie;
    }

    public Cursor GetMovieSummaryCursorByMovieId(long movieId) {
        return new Select().from(MovieSummary.class)
                .where(Condition.column(MovieSummary$Table.MOVIEID).is(movieId)).query();
    }

    public long InsertMovieSummary(MovieSummary movieSummary) {
        if (0 == movieSummary.getId()) {
            movieSummary.insert();
        } else {
            movieSummary.save();
        }
        return movieSummary.id;
    }

    public long InsertMovieOrder(MovieOrder order, MovieSummary movieSummary) {
        order.setMovieSummary(movieSummary);
        order.save();
        return order.id;
    }


}
