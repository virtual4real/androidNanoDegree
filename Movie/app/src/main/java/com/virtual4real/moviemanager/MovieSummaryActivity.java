package com.virtual4real.moviemanager;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.virtual4real.moviemanager.data.MovieContract;
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //MovieManagerSyncAdapter.syncImmediately(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        Log.d("CALLBACK", dateUri.toString());
        long nMovieId = MovieContract.MovieSummaryEntry.getMovieSummaryId(dateUri);
        MovieManagerSyncAdapter.syncImmediately(getApplicationContext(), nMovieId);
        Intent intent = new Intent(this, MovieDetailActivity.class).setData(dateUri);
        startActivity(intent);
    }



}
