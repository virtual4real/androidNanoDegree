package com.virtual4real.moviemanager;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by ioanagosman on 30/09/15.
 */
public class MovieManagerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }
}
