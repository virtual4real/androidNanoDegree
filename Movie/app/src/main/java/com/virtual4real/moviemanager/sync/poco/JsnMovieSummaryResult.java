package com.virtual4real.moviemanager.sync.poco;

/**
 * Created by ioanagosman on 01/10/15.
 */

/**
 * JsonObject returned by the movie db rest api
 */
public class JsnMovieSummaryResult {
    private JsnMovieSummary[] results;

    private String page;

    private String total_pages;

    private String total_results;

    public JsnMovieSummary[] getResults() {
        return results;
    }

    public void setResults(JsnMovieSummary[] results) {
        this.results = results;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(String total_pages) {
        this.total_pages = total_pages;
    }

    public String getTotal_results() {
        return total_results;
    }

    public void setTotal_results(String total_results) {
        this.total_results = total_results;
    }

    @Override
    public String toString() {
        return "ClassPojo [results = " + results + ", page = " + page + ", total_pages = " + total_pages + ", total_results = " + total_results + "]";
    }
}
