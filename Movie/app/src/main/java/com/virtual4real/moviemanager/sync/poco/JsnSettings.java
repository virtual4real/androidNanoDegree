package com.virtual4real.moviemanager.sync.poco;

/**
 * Created by ioanagosman on 28/09/15.
 */

/**
 * JsonObject returned by the movie db rest api
 */
public class JsnSettings {
    private String[] change_keys;

    private JsnImages images;

    public String[] getChange_keys() {
        return change_keys;
    }

    public void setChange_keys(String[] change_keys) {
        this.change_keys = change_keys;
    }

    public JsnImages getImages() {
        return images;
    }

    public void setImages(JsnImages images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "ClassPojo [change_keys = " + change_keys + ", images = " + images + "]";
    }
}
