package com.virtual4real.moviemanager.sync.poco;

/**
 * Created by ioanagosman on 13/11/15.
 */


public class JsnCountries {
    private String certification;

    private String primary;

    private String release_date;

    private String iso_3166_1;

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getIso_3166_1() {
        return iso_3166_1;
    }

    public void setIso_3166_1(String iso_3166_1) {
        this.iso_3166_1 = iso_3166_1;
    }

    @Override
    public String toString() {
        return "ClassPojo [certification = " + certification + ", primary = " + primary + ", release_date = " + release_date + ", iso_3166_1 = " + iso_3166_1 + "]";
    }
}


