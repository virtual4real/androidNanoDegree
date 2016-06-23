package com.udacity.gradle.builditbigger;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;
import android.text.TextUtils;
import android.util.Pair;

import java.util.concurrent.CountDownLatch;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    CountDownLatch signal = null;
    String mJoke;
    Exception mError;


    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        signal = new CountDownLatch(1);
    }

    @Override
    protected void tearDown() throws Exception {
        signal.countDown();
    }


    public void testJokesEndpoint() throws InterruptedException {
        //new JokeEndpointAsyncTask().execute(new Pair<Context, String>(this, Integer.toString(nPos)));

        JokeEndpointAsyncTask task = new JokeEndpointAsyncTask();
        task.setListener(new JokeEndpointAsyncTask.JokesGetTaskListener() {
            @Override
            public void onComplete(String sJoke, Exception e) {

                     mJoke = sJoke;
                     mError = e;
                     signal.countDown();

            }
        }).execute(new Pair<Context, String>(super.getApplication(), Integer.toString(2)));
        signal.await();

        assertNull(mError);
        assertFalse(TextUtils.isEmpty(mJoke));

    }
}