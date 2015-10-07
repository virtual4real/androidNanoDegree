package com.virtual4real.moviemanager;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.otto.Bus;

/**
 * Created by ioanagosman on 30/09/15.
 */
public class MovieManagerApplication extends Application {

    //private static final Bus BUS = new Bus();

    //public static Bus getInstance() {
    //return BUS;
    //}

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        //getInstance().register(this);
    }
}
