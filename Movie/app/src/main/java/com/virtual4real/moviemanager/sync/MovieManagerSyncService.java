package com.virtual4real.moviemanager.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by ioanagosman on 28/09/15.
 */
public class MovieManagerSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MovieManagerSyncAdapter sMovieManagerSyncAdapter = null;

    @Override
    public void onCreate() {
        //Log.d("MovieManagerSyncService", "onCreate - MovieManagerSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMovieManagerSyncAdapter == null) {
                sMovieManagerSyncAdapter = new MovieManagerSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMovieManagerSyncAdapter.getSyncAdapterBinder();
    }
}
