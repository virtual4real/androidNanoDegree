package com.virtual4real.moviemanager.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by ioanagosman on 30/09/15.
 */
@Table(databaseName = MovieDatabase.NAME)
public class UrlSettings extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    int dateUpdated;

    @Column
    String baseUrl;

    @Column
    String secureBaseUrl;

    @Column
    String backdropSizeMinUrl;

    @Column
    String backdropSizeMaxUrl;

    @Column
    String logoSizeMinUrl;

    @Column
    String logoSizeMaxUrl;

    @Column
    String posterSizeMinUrl;

    @Column
    String posterSizeMaxUrl;

    @Column
    String profileSizeMinUrl;

    @Column
    String profileSizeMaxUrl;

    @Column
    String stillSizeMinUrl;

    @Column
    String stillSizeMaxUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(int dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSecureBaseUrl() {
        return secureBaseUrl;
    }

    public void setSecureBaseUrl(String secureBaseUrl) {
        this.secureBaseUrl = secureBaseUrl;
    }

    public String getBackdropSizeMinUrl() {
        return backdropSizeMinUrl;
    }

    public void setBackdropSizeMinUrl(String backdropSizeMinUrl) {
        this.backdropSizeMinUrl = backdropSizeMinUrl;
    }

    public String getBackdropSizeMaxUrl() {
        return backdropSizeMaxUrl;
    }

    public void setBackdropSizeMaxUrl(String backdropSizeMaxUrl) {
        this.backdropSizeMaxUrl = backdropSizeMaxUrl;
    }

    public String getLogoSizeMinUrl() {
        return logoSizeMinUrl;
    }

    public void setLogoSizeMinUrl(String logoSizeMinUrl) {
        this.logoSizeMinUrl = logoSizeMinUrl;
    }

    public String getLogoSizeMaxUrl() {
        return logoSizeMaxUrl;
    }

    public void setLogoSizeMaxUrl(String logoSizeMaxUrl) {
        this.logoSizeMaxUrl = logoSizeMaxUrl;
    }

    public String getPosterSizeMinUrl() {
        return posterSizeMinUrl;
    }

    public void setPosterSizeMinUrl(String posterSizeMinUrl) {
        this.posterSizeMinUrl = posterSizeMinUrl;
    }

    public String getPosterSizeMaxUrl() {
        return posterSizeMaxUrl;
    }

    public void setPosterSizeMaxUrl(String posterSizeMaxUrl) {
        this.posterSizeMaxUrl = posterSizeMaxUrl;
    }

    public String getProfileSizeMinUrl() {
        return profileSizeMinUrl;
    }

    public void setProfileSizeMinUrl(String profileSizeMinUrl) {
        this.profileSizeMinUrl = profileSizeMinUrl;
    }

    public String getProfileSizeMaxUrl() {
        return profileSizeMaxUrl;
    }

    public void setProfileSizeMaxUrl(String profileSizeMaxUrl) {
        this.profileSizeMaxUrl = profileSizeMaxUrl;
    }

    public String getStillSizeMinUrl() {
        return stillSizeMinUrl;
    }

    public void setStillSizeMinUrl(String stillSizeMinUrl) {
        this.stillSizeMinUrl = stillSizeMinUrl;
    }

    public String getStillSizeMaxUrl() {
        return stillSizeMaxUrl;
    }

    public void setStillSizeMaxUrl(String stillSizeMaxUrl) {
        this.stillSizeMaxUrl = stillSizeMaxUrl;
    }
}
