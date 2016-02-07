package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ioanagosman on 17/01/16.
 */
public class SummaryWidgetIntentService extends IntentService {

    public SummaryWidgetIntentService() {
        super("SummaryWidgetIntentService");
    }

    public static String[] getDate(int i, String sLeague){
        boolean bLeague = !(null == sLeague || 0 == sLeague.length());

        String[] vFrag = new String[(bLeague ? 2 : 1)];

        Date fragmentdate = new Date(System.currentTimeMillis()+(i*86400000));
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        vFrag[0] = mformat.format(fragmentdate);
        if(bLeague) {
            vFrag[1] = sLeague;
        }
        return vFrag;
    }

    private int getMatchesNo(int i, String sLeague){
        boolean bLeague = !(null == sLeague || 0 == sLeague.length());
        int nResult = 0;
        Cursor data = getContentResolver().query(
                bLeague ? DatabaseContract.scores_table.buildScoreWithDateAndLeague() :
                        DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, getDate(i, sLeague), null);

        if (data != null) {
            nResult = data.getCount();
            data.close();
        }

        return nResult;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int nToday = 0;
        int nTomorrow = 0;

        SharedPreferences prefs = getSharedPreferences("WidgetPrefs", getBaseContext().MODE_PRIVATE);


        // Retrieve all of the Summary widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                SummaryWidgetProvider.class));


        // Perform this loop procedure for each Summary widget
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.summary_appwidget;

            String sLeague = prefs.getString("WIDGET_" + Integer.toString(appWidgetId), "");
            String sTitle = prefs.getString("WIDGET_TITLE_" + Integer.toString(appWidgetId), "");
            nToday = getMatchesNo(0, sLeague);
            nTomorrow = getMatchesNo(1, sLeague);

            RemoteViews views = new RemoteViews(getPackageName(), layoutId);
            if(null != sTitle && 0 < sTitle.length()) {
                views.setTextViewText(R.id.summary_widget_title, sTitle);
            }
            views.setTextViewText(R.id.no_matches_today, Integer.toString(nToday));
            views.setTextViewText(R.id.no_matches_tomorrow, Integer.toString(nTomorrow));

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.summary_widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_margin/*.widget_today_default_width*/);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_margin/*.widget_today_default_width*/);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        //views.setContentDescription(R.id.widget_icon, description);
    }
}
