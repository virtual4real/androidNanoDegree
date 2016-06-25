package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.virtual4real.builditbiggershowjoke.ShowJokeActivity;

import java.util.concurrent.ThreadLocalRandom;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view){
        int nPos = ThreadLocalRandom.current().nextInt(1, 20);

        JokeEndpointAsyncTask task = new JokeEndpointAsyncTask();
        task.setListener(new JokeEndpointAsyncTask.JokesGetTaskListener() {
            @Override
            public void onComplete(String sJoke, Exception e) {
                Intent myIntent = new Intent(MainActivity.this, ShowJokeActivity.class);
                myIntent.putExtra("joke", sJoke); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        }).execute(new Pair<Context, String>(super.getApplication(), Integer.toString(2)));

        //new JokeEndpointAsyncTask().execute(new Pair<Context, String>(this, Integer.toString(nPos)));
        //Toast.makeText(this, "derp", Toast.LENGTH_SHORT).show();

    }


}
