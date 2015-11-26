package com.virtual4real.moviemanager;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.virtual4real.moviemanager.data.MovieProvider;
import com.virtual4real.moviemanager.database.MovieOrderColumns;
import com.virtual4real.moviemanager.database.MovieSummaryColumns;
import com.virtual4real.moviemanager.database.UrlSettingsColumns;
import com.virtual4real.moviemanager.sync.MovieManagerSyncAdapter;
import com.virtual4real.moviemanager.sync.SearchParameters;
import com.virtual4real.moviemanager.sync.SyncDataService;
import com.virtual4real.moviemanager.sync.restapi.RestApiContract;

import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieSummaryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private int mTotalWidth = 0;

    private int mScrollFirstItem = 0;
    private float mScrollTopOffset = 0;

    public interface CallbackSummary {
        void onItemSelected(Uri dateUri, int nIndex);

        void onItemSummaryFavoriteChanged(Uri dateUri);
    }

    class SummaryImageSize {
        public int Width;
        public int NoColumns;
        public int NoSpaces;
        public int Height;
        public int TextHeight;

        public SummaryImageSize() {

        }
    }

    @Bind(R.id.summary_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    SummaryImageSize summ;

    public ToggleButton switchAB;
    private boolean bTwoPane = false;

    private ContentObserver mObserver;

    private static final int MOVIE_SUMMARY_LOADER = 0;
    public static final String TOTAL_WIDTH = "total_width";
    public static final String TWO_PANE = "two_pane";

    public MovieSummaryFragment() {

        mTotalWidth = 0;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(boolean selfChange) {
                //TODO: maybe needed for pagination ???
                //Log.d("ONCHANGE", "ONCHANGE_______________");
            }
        };

        getActivity().getContentResolver()
                .registerContentObserver(MovieProvider.getMovieSummaryUri(), true, mObserver);
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
                Utils.setPreferredSort(getContext(),
                        getContext().getString(isChecked ? R.string.sort_asc : R.string.sort_desc));
                MovieManagerSyncAdapter.syncImmediately(getContext(), Utils.getOrderAndSortFromPreferences(getContext()), 1);
                getLoaderManager().restartLoader(MOVIE_SUMMARY_LOADER, null, frag);

                if (bTwoPane) {
                    ((MovieSummaryActivity) getActivity()).onItemSelected(null, 0);
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
                    switchAB.setVisibility(View.INVISIBLE);
                }
            }
        }

        SubMenu subMenu = orderItem.getSubMenu();
        if (null != subMenu) {
            setSubMenuListener(0, R.drawable.popularity, R.string.order_popular, orderItem, subMenu, switchAB);
            setSubMenuListener(1, R.drawable.voting, R.string.order_rated, orderItem, subMenu, switchAB);
            setSubMenuListener(2, R.drawable.favourite, R.string.order_favorite, orderItem, subMenu, switchAB);
            setSubMenuListener(3, R.drawable.release, R.string.order_release, orderItem, subMenu, switchAB);
        }
    }

    private void setSubMenuListener(int nIndex, final int nIconResourceId, final int nOrderResourceId,
                                    final MenuItem itemToChange, SubMenu subMenu, final ToggleButton switchAB) {
        MenuItem item = subMenu.getItem(nIndex);
        final LoaderManager.LoaderCallbacks<Cursor> frag = this;
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                itemToChange.setIcon(nIconResourceId);

                if (null != switchAB) {
                    switchAB.setVisibility((nIconResourceId == R.drawable.favourite ? View.INVISIBLE : View.VISIBLE));
                }

                Utils.setPreferredOrder(getContext(), getContext().getString(nOrderResourceId));
                if (nIconResourceId != R.drawable.favourite) {
                    MovieManagerSyncAdapter.syncImmediately(getContext(), Utils.getOrderAndSortFromPreferences(getContext()), 1);
                }

                getLoaderManager().restartLoader(MOVIE_SUMMARY_LOADER, null, frag);

                if (bTwoPane) {
                    ((MovieSummaryActivity) getActivity()).onItemSelected(null, 0);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = Utils.getOrderAndSortFromPreferences(getContext());
        //TODO: set the page in the selection string of the query call in the content provider

        //verify if there is data for the parameters
        //and if it is not call sync imediatelly
        SyncDataService sync = new SyncDataService(getContext());
        if (0 == sync.GetOperationIdForParameters(new SearchParameters(sortOrder, 1,
                Utils.getMinDate(getContext()), Utils.getMaxDate(getContext()),
                Utils.getMinVotes(getContext()), Utils.getIncludeAdult(getContext()), Utils.getIncludeVideo(getContext())))) {
            MovieManagerSyncAdapter.syncImmediately(getContext(), sortOrder, 1);
        }

        int nSortType = MovieProvider.MovieOrderHelper.GetSortTypeInt(sortOrder);


        return nSortType == MovieProvider.FAVOURITE_SEARCH ?
                new CursorLoader(getActivity(), MovieProvider.buildMovieSummaryUriForFavorites(1),
                        MovieProvider.getMovieSummaryProjection(),
                        null, null, MovieSummaryColumns.TITLE) :

                new CursorLoader(getActivity(), MovieProvider.buildMovieSummaryUri(1, nSortType),
                        MovieProvider.getMovieSummaryProjection(),
                        null, null,
                        MovieOrderColumns.PAGE + ", " + MovieOrderColumns.POSITION);

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

        Bundle arguments = getArguments();
        if (null != arguments) {
            mTotalWidth = arguments.getInt(MovieSummaryFragment.TOTAL_WIDTH);
            bTwoPane = arguments.getBoolean(MovieSummaryFragment.TWO_PANE);
        }

        ButterKnife.bind(this, rootView);
        // Calling the RecyclerView
        if (null != mRecyclerView) {
            mRecyclerView.setHasFixedSize(true);

            summ = getColumnMeasurementsBasedOnScreenSize();
            mLayoutManager = new GridLayoutManager(getActivity(), summ.NoColumns);
            mRecyclerView.setLayoutManager(mLayoutManager);

            setImageBaseUrl();
            getLoaderManager().restartLoader(MOVIE_SUMMARY_LOADER, null, this);

        }

        if (!bTwoPane) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        }

        return rootView;
    }

    private SummaryImageSize getColumnMeasurementsBasedOnScreenSize() {

        SummaryImageSize summ = new SummaryImageSize();

        int nMinWidth = (int) getResources().getDimension(R.dimen.card_view_width_min);
        float nRatio = Float.parseFloat(getResources().getString(R.string.card_view_image_ratio));
        int nSpace = (int) getResources().getDimension(R.dimen.card_view_space);
        int nTextHeight = (int) getResources().getDimension(R.dimen.card_view_rating_height);
        //(int) getResources().getDimension(R.dimen.card_view_text_height);

        int nColumns = 1;

        while (nMinWidth * nColumns < mTotalWidth) {
            nColumns++;
        }

        float nWidth = (mTotalWidth - (nColumns + 1) * nSpace) / nColumns;

        summ.NoColumns = nColumns;

        summ.Width = Math.round(nWidth);
        summ.Height = Math.round(nWidth * nRatio);
        summ.TextHeight = Math.round(nTextHeight);
        summ.NoSpaces = Math.round(nSpace);

        return summ;
    }

    private void setImageBaseUrl() {
        try {
            String strUrl = null;

            Cursor cursor = getContext().getContentResolver()
                    .query(MovieProvider.getSeetingsUri(),
                            new String[]{UrlSettingsColumns.BASE_URL, UrlSettingsColumns.LOGO_SIZE_URL,
                                    UrlSettingsColumns.BACKDROP_SIZE_URL},
                            null, null, null);
            if (0 == cursor.getCount()) {
                Utils.setBaseImageUrl(getContext(), RestApiContract.IMAGE_BASE_URL_DEFAULT);
            }

            if (cursor.moveToFirst()) {
                String strBaseUrl = cursor.getString(cursor.getColumnIndex(UrlSettingsColumns.BASE_URL));
                strUrl = getPathBySize(cursor, UrlSettingsColumns.LOGO_SIZE_URL, strBaseUrl);
                if (null != strUrl) {
                    Utils.setBaseImageUrl(getContext(), strUrl);
                }

                strUrl = getPathBySize(cursor, UrlSettingsColumns.BACKDROP_SIZE_URL, strBaseUrl);
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
        mAdapter = new MovieGridAdapter(getContext(), data, this, (CallbackSummary) getActivity(),
                summ.NoSpaces, summ.Width, summ.Height, summ.TextHeight);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        manager.scrollToPositionWithOffset(mScrollFirstItem, (int) mScrollTopOffset);

        mScrollFirstItem = 0;
        mScrollTopOffset = 0;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter = new MovieGridAdapter(getContext(), null, this, (CallbackSummary) getActivity(),
                summ.NoSpaces, summ.Width, summ.Height, summ.TextHeight);

        if (null != mRecyclerView) {
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void restartLoaderOnlyForFavorites() {
        String sortOrder = Utils.getOrderAndSortFromPreferences(getContext());
        int nSortType = MovieProvider.MovieOrderHelper.GetSortTypeInt(sortOrder);

        if (MovieProvider.FAVOURITE_SEARCH == nSortType) {
            SaveScrollPosition();
            getLoaderManager().restartLoader(MOVIE_SUMMARY_LOADER, null, this);
        }

    }

    public void RefreshAdapter(int nPosition, int nNewFavorite) {
        if (null != mAdapter && 0 <= nPosition) {
            //mAdapter.notifyItemChanged(nPosition);
            SaveScrollPosition();
            getLoaderManager().restartLoader(MOVIE_SUMMARY_LOADER, null, this);


        }
    }


    private void SaveScrollPosition() {
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mScrollFirstItem = manager.findFirstVisibleItemPosition();
        View firstItemView = manager.findViewByPosition(mScrollFirstItem);
        mScrollTopOffset = firstItemView.getTop();
    }


}
