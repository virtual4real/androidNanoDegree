package com.virtual4real.moviemanager;

import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.virtual4real.moviemanager.data.MovieContract;
import com.virtual4real.moviemanager.database.MovieSummary$Table;
import com.virtual4real.moviemanager.database.UrlSettings;
import com.virtual4real.moviemanager.database.UrlSettings$Table;
import com.virtual4real.moviemanager.sync.MovieManagerSyncAdapter;
import com.virtual4real.moviemanager.sync.restapi.RestApiContract;

import java.util.regex.Pattern;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieSummaryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    private ContentObserver mObserver;

    private static final int MOVIE_SUMMARY_LOADER = 0;

    public MovieSummaryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(boolean selfChange) {
                //TODO: setup local variables... maybe
                Log.d("ONCHANGE", "ONCHANGE_______________");
            }
        };
        getActivity().getContentResolver()
                .registerContentObserver(MovieContract.MovieSummaryEntry.CONTENT_URI, true, mObserver);
    }

    @Override
    public void onDestroy() {
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_summary_fragment, menu);

        MenuItem sortItem = menu.findItem(R.id.sortId);
        sortItem.setActionView(R.layout.asc_desc_layout);

        final ToggleButton switchAB = (ToggleButton) sortItem.getActionView().findViewById(R.id.sortAD);
        switchAB.setChecked(Utils.getPreferredSort(getContext()).equals(getContext().getString(R.string.sort_asc)));

        final LoaderManager.LoaderCallbacks<Cursor> frag = this;

        switchAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Utils.setPreferredSort(getContext(), getContext().getString(R.string.sort_asc));
                    MovieManagerSyncAdapter.syncImmediately(getContext(), Utils.getOrderAndSortFromPreferences(getContext()), 1);
                    getLoaderManager().restartLoader(MOVIE_SUMMARY_LOADER, null, frag);
                } else {
                    Utils.setPreferredSort(getContext(), getContext().getString(R.string.sort_desc));
                    MovieManagerSyncAdapter.syncImmediately(getContext(), Utils.getOrderAndSortFromPreferences(getContext()), 1);
                    getLoaderManager().restartLoader(MOVIE_SUMMARY_LOADER, null, frag);
                }
            }
        });


        final MenuItem orderItem = menu.findItem(R.id.OrderId);
        String sOrder = Utils.getPreferredOrder(getContext());
        if (sOrder.equals(getContext().getString(R.string.order_popular))) {
            orderItem.setIcon(R.drawable.popularity);
        } else {
            if (sOrder.equals(getContext().getString(R.string.order_rated))) {
                orderItem.setIcon(R.drawable.voting);
            } else {
                if (sOrder.equals(getContext().getString(R.string.order_release))) {
                    orderItem.setIcon(R.drawable.release);
                } else {
                    orderItem.setIcon(R.drawable.favourite);
                }
            }
        }

        SubMenu subMenu = orderItem.getSubMenu();
        if (null != subMenu) {
            setSubMenuListener(0, R.drawable.popularity, R.string.order_popular, orderItem, subMenu);
            setSubMenuListener(1, R.drawable.voting, R.string.order_rated, orderItem, subMenu);
            setSubMenuListener(2, R.drawable.favourite, R.string.order_favorite, orderItem, subMenu);
            setSubMenuListener(3, R.drawable.release, R.string.order_release, orderItem, subMenu);
        }
    }

    private void setSubMenuListener(int nIndex, final int nIconResourceId, final int nOrderResourceId,
                                    final MenuItem itemToChange, SubMenu subMenu) {
        MenuItem item = subMenu.getItem(nIndex);
        final LoaderManager.LoaderCallbacks<Cursor> frag = this;
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                itemToChange.setIcon(nIconResourceId);
                Utils.setPreferredOrder(getContext(), getContext().getString(nOrderResourceId));
                MovieManagerSyncAdapter.syncImmediately(getContext(), Utils.getOrderAndSortFromPreferences(getContext()), 1);
                getLoaderManager().restartLoader(MOVIE_SUMMARY_LOADER, null, frag);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
////        if (id == R.id.action_refresh) {
////            updateWeather();
////            return true;
////        }
//        if (id == R.id.action_map) {
//
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = Utils.getOrderAndSortFromPreferences(getContext());
        //TODO: set the page in the selection string of the query call in the content provider

        Log.d("CURSOR____", sortOrder);

        return new CursorLoader(getActivity(), MovieContract.MovieSummaryEntry.CONTENT_URI, null, null, null, sortOrder);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_SUMMARY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_summary, container, false);

        // Calling the RecyclerView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        if (null != mRecyclerView) {
            mRecyclerView.setHasFixedSize(true);

            mLayoutManager = new GridLayoutManager(getActivity(), getNoColumnsBasedOnScreenSize());
            mRecyclerView.setLayoutManager(mLayoutManager);

            setImageBaseUrl();
            getLoaderManager().restartLoader(MOVIE_SUMMARY_LOADER, null, this);

        }

        return rootView;
    }

    private int getNoColumnsBasedOnScreenSize() {

        DisplayMetrics dMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
        float density = dMetrics.density;
        int w = Math.round(dMetrics.widthPixels / density);

        //TODO: modify when the detail fragment is in the same screen as summary fragment
        return (w < 450 ? 2 : 3);
    }

    private void setImageBaseUrl() {
        try {
            String strUrl = null;

            Cursor cursor = getContext().getContentResolver()
                    .query(MovieContract.SettingEntry.CONTENT_URI, null, null, null, null);
            if (0 == cursor.getCount()) {
                Utils.setBaseImageUrl(getContext(), RestApiContract.IMAGE_BASE_URL_DEFAULT);
            }

            if (cursor.moveToFirst()) {
                String strBaseUrl = cursor.getString(cursor.getColumnIndex(UrlSettings$Table.BASEURL));
                strUrl = getPathBySize(cursor, UrlSettings$Table.LOGOSIZEURL, strBaseUrl);
                if (null != strUrl) {
                    Utils.setBaseImageUrl(getContext(), strUrl);
                }

                strUrl = getPathBySize(cursor, UrlSettings$Table.BACKDROPSIZEURL, strBaseUrl);
                if (null != strUrl) {
                    Utils.setBackdropImageUrl(getContext(), strUrl);
                }
            }

            cursor.close();
        } catch (Exception ex) {

        }
    }

    private String getPathBySize(Cursor cursor, String columnName, String strBaseUrl) {
        String strSizes = cursor.getString(cursor.getColumnIndex(columnName));
        if (null != strSizes) {
            String[] vSizes = strSizes.split(Pattern.quote(";"));
            int nLimit = getLimitByDensity();
            strSizes = getMaxToLimit(nLimit, vSizes);
            return strBaseUrl + strSizes;
        }

        return null;
    }

    private String getMaxToLimit(int nLimit, String[] vSizes) {
        int nMax = 0;
        String sMax = null;
        for (int i = 0; i < vSizes.length; i++) {
            sMax = vSizes[i].substring(1, vSizes[i].length() - 1);
            nMax = Integer.parseInt(sMax);
            if (nMax <= nLimit) {
                sMax = vSizes[i];
            }
        }

        return sMax;
    }

    private int getLimitByDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int density = metrics.densityDpi;

        int nLimit = 0;
        switch (density) {
            case DisplayMetrics.DENSITY_HIGH:
                nLimit = 240;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                nLimit = 160;
                break;
            case DisplayMetrics.DENSITY_LOW:
                nLimit = 120;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                nLimit = 300;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                nLimit = 400;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                nLimit = 600;
                break;
            default:
                nLimit = 300;
                break;
        }

        return nLimit;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //TODO: checkout the position in the model application
//        mForecastAdapter.swapCursor(data);
//        if (mPosition != ListView.INVALID_POSITION) {
//            // If we don't need to restart the loader, and there's a desired position to restore
//            // to, do so now.
//            mListView.smoothScrollToPosition(mPosition);
//        }

        mAdapter = new MovieGridAdapter(getContext(), data, (Callback) getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //mForecastAdapter.swapCursor(null);
        mAdapter = new MovieGridAdapter(getContext(), null, (Callback) getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }


//    private void setInternalCursor(){
//        String sortOrder = Utils.getOrderAndSortFromPreferences(getContext());
//        //TODO: set the page in the selection string of the query call in the content provider
//        Cursor cursor =
//                getContext().getContentResolver()
//                        .query(MovieContract.MovieSummaryEntry.CONTENT_URI, null, null, null, sortOrder);
//
//        if(null != cursor){
//            Log.d("CURSOR___", Integer.toString(cursor.getCount()));
//        }
//
//
//        mAdapter = new MovieGridAdapter(getContext(), cursor, (Callback)getActivity());
//        mRecyclerView.setAdapter(mAdapter);
//    }

    public interface Callback {
        public void onItemSelected(Uri dateUri);
    }
}
