package com.virtual4real.moviemanager;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.virtual4real.moviemanager.data.MovieContract;
import com.virtual4real.moviemanager.database.MovieSummary$Table;
import com.virtual4real.moviemanager.database.UrlSettings$Table;
import com.virtual4real.moviemanager.sync.MovieManagerSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieSummaryActivityFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    public MovieSummaryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_summary, container, false);

        // Calling the RecyclerView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        if (null != mRecyclerView) {
            mRecyclerView.setHasFixedSize(true);

            // The number of Columns
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(mLayoutManager);

            Cursor cursor =
                    getContext().getContentResolver()
                            .query(MovieContract.MovieSummaryEntry.CONTENT_URI, null, null, null, null);

            mAdapter = new MovieGridAdapter(getContext(), cursor);
            mRecyclerView.setAdapter(mAdapter);
        }


//        Cursor cursor =
//                getContext().getContentResolver().query(MovieContract.SettingEntry.CONTENT_URI, null, null, null, null);
//        int nCount = cursor.getCount();
//
//        if (cursor.moveToFirst()){
//            do{
//                String data = cursor.getString(cursor.getColumnIndex(UrlSettings$Table.BASEURL));
//                data = null;
//            }while(cursor.moveToNext());
//        }
//        cursor.close();


        return rootView;
    }


}
