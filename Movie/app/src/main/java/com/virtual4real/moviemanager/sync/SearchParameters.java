package com.virtual4real.moviemanager.sync;

/**
 * Created by ioanagosman on 16/10/15.
 */
public class SearchParameters {
    String order;
    int page;

    String minDate;
    String maxDate;
    int minVotes;
    boolean includesAdult;
    boolean includesVideo;


    public SearchParameters(String order, int page, String minDate,
                            String maxDate, int minVotes, boolean includesAdult, boolean includesVideo) {
        this.order = order;
        this.page = page;
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.minVotes = minVotes;
        this.includesAdult = includesAdult;
        this.includesVideo = includesVideo;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getMinDate() {
        return minDate;
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public String getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    public int getMinVotes() {
        return minVotes;
    }

    public void setMinVotes(int minVotes) {
        this.minVotes = minVotes;
    }

    public boolean isIncludesAdult() {
        return includesAdult;
    }

    public void setIncludesAdult(boolean includesAdult) {
        this.includesAdult = includesAdult;
    }

    public boolean isIncludesVideo() {
        return includesVideo;
    }

    public void setIncludesVideo(boolean includesVideo) {
        this.includesVideo = includesVideo;
    }
}
