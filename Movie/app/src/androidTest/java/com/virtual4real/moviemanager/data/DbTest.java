package com.virtual4real.moviemanager.data;

import android.test.AndroidTestCase;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.virtual4real.moviemanager.database.DbService;
import com.virtual4real.moviemanager.database.UrlSettings;

/**
 * Created by ioanagosman on 01/10/15.
 */
public class DbTest extends AndroidTestCase {
/*
    public void setUp() {
        FlowManager.init(mContext.getApplicationContext());
    }

    public void testInsertUrlSetting() {
        DbService service = new DbService();

        UrlSettings url = createUrlSetting(200, "baseUrl", "secureBaseUrl",
                "backdropMin", "backdropMax", "logoMin", "logoMax",
                "posterMin", "posterMax", "profileMin", "profileMax", "stillMin", "stillMax");


        service.InsertUrlSetting(url);
        service.DeleteUrlSettings(url.getId());

        url = service.GetFirstUrlSetting();
        verifyUrlSetting(url, 200, "baseUrl", "secureBaseUrl",
                "backdropMin", "backdropMax", "logoMin", "logoMax",
                "posterMin", "posterMax", "profileMin", "profileMax", "stillMin", "stillMax");

    }

    private UrlSettings createUrlSetting(int dateUpdated, String baseUrl, String secureBaseUrl,
                                         String backdropMin, String backdropMax, String logoMin, String logoMax,
                                         String posterMin, String posterMax, String profileMin, String profileMax,
                                         String stillMin, String stillMax) {

        UrlSettings url = new UrlSettings();
        url.setDateUpdated(dateUpdated);
        url.setBaseUrl(baseUrl);
        url.setSecureBaseUrl(secureBaseUrl);
        return url;
    }

    private void verifyUrlSetting(UrlSettings url, int dateUpdated, String baseUrl, String secureBaseUrl,
                                  String backdropMin, String backdropMax, String logoMin, String logoMax,
                                  String posterMin, String posterMax, String profileMin, String profileMax,
                                  String stillMin, String stillMax) {

        assertEquals(dateUpdated, url.getDateUpdated());
        assertEquals(baseUrl, url.getBaseUrl());
        assertEquals(secureBaseUrl, url.getSecureBaseUrl());
        //assertEquals(profileMin, url.getProfileSizeMinUrl());
        //assertEquals(profileMax, url.getProfileSizeMaxUrl());
        //assertEquals(stillMin, url.getStillSizeMinUrl());
        //assertEquals(stillMax, url.getStillSizeMaxUrl());
    }

    */
}
