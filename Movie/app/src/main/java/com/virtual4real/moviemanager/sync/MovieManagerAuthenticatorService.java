package com.virtual4real.moviemanager.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by ioanagosman on 28/09/15.
 */
public class MovieManagerAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private MovieManagerAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new MovieManagerAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
