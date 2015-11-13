package com.virtual4real.moviemanager.database;

/**
 * Created by ioanagosman on 01/10/15.
 */

/**
 * Helper class to work with the database
 */
public class DbService {
//
//    /**
//     * In the UrlSettings table should be only one entry at all times
//     *
//     * @return
//     */
//    public UrlSettings GetFirstUrlSetting() {
//        UrlSettings setting = new Select().from(UrlSettings.class).querySingle();
//        return setting;
//    }
//
//    /**
//     * Get all UrlSettings
//     * @return
//     */
//    public Cursor GetUrlSetting() {
//        return new Select().from(UrlSettings.class).query();
//    }
//
//    /**
//     * Get all SyncOperations based on parameters
//     *
//     * @param sMinDate the min date column
//     * @param sMaxDate max date column
//     * @param nVotes   number of votes column
//     * @param bAdult   is adult column
//     * @param bVideo   is video column
//     * @return all SyncOperations that satisfy the criterias
//     */
//    public Cursor GetOperations(String sMinDate, String sMaxDate, int nVotes, boolean bAdult, boolean bVideo) {
//        return new Select()
//                .from(SyncOperation.class)
//                .where(Condition.column(SyncOperation$Table.STARTDATE).eq(sMinDate))
//                .and(Condition.column(SyncOperation$Table.ENDDATE).eq(sMaxDate))
//                .and(Condition.column(SyncOperation$Table.NOOFVOTES).eq(nVotes))
//                .and(Condition.column(SyncOperation$Table.ISADULT).eq(bAdult))
//                .and(Condition.column(SyncOperation$Table.ISVIDEO).eq(bVideo))
//                .query();
//    }
//
//    /**
//     * Get a sync operation by its id
//     *
//     * @param nOperationid operation id
//     * @return the operation with the required id or null
//     */
//    public SyncOperation GetSyncOperationById(long nOperationid) {
//        return new Select()
//                .from(SyncOperation.class)
//                .where(Condition.column(SyncOperation$Table.ID).eq(nOperationid))
//                .querySingle();
//    }
//
//    /**
//     * Get all movie summaries for the specified sort order and page
//     * @param nSort integer defining the sort type
//     * @param nPage the requested page
//     * @return all movie summaries that satisfy the criterias
//     */
//    public Cursor GetMovieSummaries(int nSort, int nPage) {
//        return new Select()
//                .from(MovieSummary.class)
//                .join(MovieOrder.class, Join.JoinType.INNER)
//                .on(Condition.column(ColumnAlias.columnWithTable(MovieSummary$Table.TABLE_NAME, MovieSummary$Table.ID))
//                        .is(MovieOrder$Table.MOVIESUMMARY_MOVIE_ID))
//                .where(Condition.column(MovieOrder$Table.SORTTYPE).eq(nSort)/*,
//                        Condition.column(MovieOrder$Table.PAGE).eq(nPage)*/)
//                .orderBy(true, MovieOrder$Table.POSITION).query();
//    }
//
//
//    public long InsertUrlSetting(UrlSettings url) {
//        if (0 == url.getId()) {
//            url.insert();
//        } else {
//            url.save();
//        }
//        return url.getId();
//    }
//
//    public long InsertSyncOperation(SyncOperation ops) {
//        if (0 == ops.getId()) {
//            ops.insert();
//        } else {
//            ops.save();
//        }
//
//        return ops.getId();
//    }
//
//    public void DeleteUrlSettings(long nId) {
//        Delete.table(UrlSettings.class, Condition.column(UrlSettings$Table.ID).isNot(nId));
//    }
//
//    public void DeleteSyncOperation(long nId) {
//        Delete.table(SyncOperation.class, Condition.column(SyncOperation$Table.ID).is(nId));
//    }
//
//    public void DeleteAllSyncOperationAndOrders() {
//        Delete.table(MovieOrder.class);
//        Delete.table(SyncOperation.class);
//    }
//
//    public void DeleteAllUrlSettings() {
//        Delete.table(UrlSettings.class);
//    }
//
//    public void DeleteMovieOrder(long nPage, int sortType) {
//        Delete.table(MovieOrder.class,
//                Condition.column(MovieOrder$Table.PAGE).is(nPage),
//                Condition.column(MovieOrder$Table.SORTTYPE).is(sortType));
//    }
//
//    public void DeleteMovieSummary(long nId) {
//        MovieSummary movieSummary = GetMovieSummaryByMovieId(nId);
//
//        if (null == movieSummary) {
//            return;
//        }
//
//        Delete.table(MovieOrder.class,
//                Condition.column(MovieOrder$Table.MOVIESUMMARY_MOVIE_ID).is(movieSummary.getId()));
//
//        Delete.table(MovieSummary.class,
//                Condition.column(MovieSummary$Table.ID).is(nId));
//    }
//
//    public void DeleteAllMovieSummary() {
//        Delete.table(MovieOrder.class);
//        Delete.table(MovieSummary.class);
//    }
//
//    public MovieSummary GetMovieSummaryByMovieId(long movieId) {
//        MovieSummary movie = new Select().from(MovieSummary.class)
//                .where(Condition.column(MovieSummary$Table.MOVIEID).is(movieId)).querySingle();
//        return movie;
//    }
//
//    public Cursor GetMovieSummaryCursorByMovieId(long movieId) {
//        return new Select().from(MovieSummary.class)
//                .where(Condition.column(MovieSummary$Table.MOVIEID).is(movieId)).query();
//    }
//
//    public long InsertMovieSummary(MovieSummary movieSummary) {
//        if (0 == movieSummary.getId()) {
//            movieSummary.insert();
//        } else {
//            movieSummary.save();
//        }
//        return movieSummary.id;
//    }
//
//    public long InsertMovieOrder(MovieOrder order, MovieSummary movieSummary) {
//        order.setMovieSummary(movieSummary);
//        order.save();
//        return order.id;
//    }
//

}
