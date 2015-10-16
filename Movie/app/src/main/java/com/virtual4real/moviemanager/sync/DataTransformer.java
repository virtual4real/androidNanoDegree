package com.virtual4real.moviemanager.sync;

import android.content.ContentValues;

import com.virtual4real.moviemanager.data.MovieContract;
import com.virtual4real.moviemanager.database.MovieSummary$Table;
import com.virtual4real.moviemanager.database.UrlSettings$Table;
import com.virtual4real.moviemanager.sync.poco.JsnImages;
import com.virtual4real.moviemanager.sync.poco.JsnMovieSummary;
import com.virtual4real.moviemanager.sync.poco.JsnSettings;

/**
 * Created by ioanagosman on 16/10/15.
 */
public class DataTransformer {


    public static ContentValues[] getMovieSummaryContentValues(String totalResults, String totalPages,
                                                               String currentPage, String sortType, JsnMovieSummary[] movies) {
        ContentValues[] values = new ContentValues[movies.length + 1];

        values[0] = getMovieSummaryInfoContentValues(totalResults, totalPages, currentPage, sortType);

        for (int i = 0; i < movies.length; i++) {
            values[i + 1] = getMovieSummaryContentValue(movies[i], i);

        }

        return values;
    }

    public static ContentValues getMovieSummaryInfoContentValues(String totalResults,
                                                                 String totalPages, String currentPage, String sortType) {
        ContentValues values = new ContentValues();

        values.put(MovieContract.MovieSummaryEntry.PAGINATION_TOTAL_RESULTS, totalResults);
        values.put(MovieContract.MovieSummaryEntry.PAGINATION_TOTAL_PAGES, totalPages);
        values.put(MovieContract.MovieSummaryEntry.PAGINATION_CURRENT_PAGE, currentPage);
        values.put(MovieContract.MovieSummaryEntry.SORT_TYPE, MovieContract.MovieSummaryEntry.GetSortTypeInt(sortType));

        return values;
    }


    public static ContentValues getMovieSummaryContentValue(JsnMovieSummary movie, int i) {
        ContentValues value = new ContentValues();

        value.put(MovieSummary$Table.MOVIEID, movie.getId());
        value.put(MovieSummary$Table.ORIGINALTITLE, movie.getOriginal_title());
        value.put(MovieSummary$Table.TITLE, movie.getTitle());
        value.put(MovieSummary$Table.POPULARITY, movie.getPopularity());
        value.put(MovieSummary$Table.POSTERPATH, movie.getPoster_path());
        value.put(MovieSummary$Table.RELEASEDATE, movie.getRelease_date());
        value.put(MovieSummary$Table.VOTEAVERAGE, movie.getVote_average());
        value.put(MovieSummary$Table.VOTECOUNT, movie.getVote_count());
        value.put(MovieSummary$Table.BACKDROPPATH, movie.getBackdrop_path());
        value.put(MovieSummary$Table.OVERVIEW, movie.getOverview());
        value.put(MovieContract.MovieSummaryEntry.MOVIE_SUMMARY_POSITION, i);

        return value;
    }

    public static ContentValues getSettingsContentValues(JsnSettings setting) {
        ContentValues settingsValues = new ContentValues();

        JsnImages img = setting.getImages();

        settingsValues.put(UrlSettings$Table.BASEURL, img.getBase_url());
        settingsValues.put(UrlSettings$Table.SECUREBASEURL, img.getSecure_base_url());

        settingsValues.put(UrlSettings$Table.LOGOSIZEURL, getValues(img.getLogo_sizes()));
        settingsValues.put(UrlSettings$Table.BACKDROPSIZEURL, getValues(img.getBackdrop_sizes()));
        settingsValues.put(UrlSettings$Table.POSTERSIZEURL, getValues(img.getPoster_sizes()));
        settingsValues.put(UrlSettings$Table.PROFILESIZEURL, getValues(img.getProfile_sizes()));
        settingsValues.put(UrlSettings$Table.STILLSIZEURL, getValues(img.getStill_sizes()));


        return settingsValues;
    }

    public static String getValues(String[] vstr) {
        StringBuilder builder = new StringBuilder();
        for (String s : vstr) {
            if (s.equals("original")) continue;
            builder.append(s);
            builder.append(";");
        }
        return builder.toString();
    }

}
