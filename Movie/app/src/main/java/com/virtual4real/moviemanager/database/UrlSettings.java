package com.virtual4real.moviemanager.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by ioanagosman on 30/09/15.
 */

/**
 * UrlSettings contains configuration information
 * provided by the rest api regarding the image path construction.
 */
@Table(databaseName = MovieDatabase.NAME)
public class UrlSettings extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    long dateUpdated;

    @Column
    String baseUrl;

    @Column
    String secureBaseUrl;

    @Column
    String backdropSizeUrl;

    @Column
    String logoSizeUrl;

    @Column
    String posterSizeUrl;

    @Column
    String profileSizeUrl;

    @Column
    String stillSizeUrl;

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

    public String getBackdropSizeUrl() {
        return backdropSizeUrl;
    }

    public void setBackdropSizeUrl(String backdropSizeMinUrl) {
        this.backdropSizeUrl = backdropSizeMinUrl;
    }


    public String getLogoSizeUrl() {
        return logoSizeUrl;
    }

    public void setLogoSizeUrl(String logoSizeMinUrl) {
        this.logoSizeUrl = logoSizeMinUrl;
    }

    public String getPosterSizeUrl() {
        return posterSizeUrl;
    }

    public void setPosterSizeUrl(String posterSizeUrl) {
        this.posterSizeUrl = posterSizeUrl;
    }


    public String getProfileSizeUrl() {
        return profileSizeUrl;
    }

    public void setProfileSizeUrl(String profileSizeUrl) {
        this.profileSizeUrl = profileSizeUrl;
    }


    public String getStillSizeUrl() {
        return stillSizeUrl;
    }

    public void setStillSizeUrl(String stillSizeUrl) {
        this.stillSizeUrl = stillSizeUrl;
    }

}
