package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.example.virtual4real.builditbigger.jokes.v4rJokesApi.V4rJokesApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import android.widget.Toast;

import java.io.IOException;

/**
 * Created by ioanagosman on 18/06/16.
 */
public class JokeEndpointAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
    private static V4rJokesApi myApiService = null;
    private Context context;

    private JokesGetTaskListener mListener = null;
    private Exception mError = null;

    public JokeEndpointAsyncTask setListener(JokesGetTaskListener listener) {
        this.mListener = listener;
        return this;
    }


    @Override
    protected String doInBackground(Pair<Context, String>... params) {
        if(myApiService == null) {  // Only do this once
            V4rJokesApi.Builder builder = new V4rJokesApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

        context = params[0].first;
        String name = params[0].second;

        try {
            return myApiService.getAJoke(name).execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (this.mListener != null)
            this.mListener.onComplete(result, mError);

    }


    @Override
    protected void onCancelled() {
        if (this.mListener != null) {
            mError = new InterruptedException("AsyncTask cancelled");
            this.mListener.onComplete(null, mError);
        }
    }

    public static interface JokesGetTaskListener {
        public void onComplete(String sJoke, Exception e);
    }
}
