package com.virtual4real.moviemanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.sync.MovieManagerSyncAdapter;

public class MovieSummaryActivity extends AppCompatActivity implements MovieSummaryFragment.Callback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_summary);

        MovieManagerSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        //Log.d("CALLBACK", dateUri.toString());
        long nMovieId = MovieProvider.getMovieSummaryId(dateUri);
        MovieManagerSyncAdapter.syncImmediately(getApplicationContext(), nMovieId);
        Intent intent = new Intent(this, MovieDetailActivity.class).setData(dateUri);
        startActivity(intent);
    }



}
