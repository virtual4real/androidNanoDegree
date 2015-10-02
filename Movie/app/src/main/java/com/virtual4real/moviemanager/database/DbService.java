package com.virtual4real.moviemanager.database;

import android.database.Cursor;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

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

    public Cursor GetMovieSummaries() {
        return new Select().from(MovieSummary.class).query();
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

    public void DeleteMovieOrder(long nPage, int sortType) {
        Delete.table(MovieOrder.class,
                Condition.column(MovieOrder$Table.PAGE).is(nPage),
                Condition.column(MovieOrder$Table.SORTTYPE).is(sortType));
    }

    public MovieSummary GetMovieSummaryByMovieId(long movieId) {
        MovieSummary movie = new Select().from(MovieSummary.class)
                .where(Condition.column(MovieSummary$Table.MOVIEID).is(movieId)).querySingle();
        return movie;
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
