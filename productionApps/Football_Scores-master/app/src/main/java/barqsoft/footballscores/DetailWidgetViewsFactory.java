package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * Created by ioanagosman on 23/01/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    public String title = null;
    public String league = null;
    public String packageName = null;
    private Cursor data = null;
    private ContentResolver contentResolver = null;

    public DetailWidgetViewsFactory(String stitle, String sleague, String spackageName, ContentResolver res){
        title = stitle;
        league = sleague;
        packageName = spackageName;
        contentResolver = res;
    }

    @Override
    public void onCreate() {
        // Nothing to do
    }


    @Override
    public void onDataSetChanged() {

        if (data != null) {
            data.close();
        }
        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();
        data = contentResolver.query(
                //bLeague ? DatabaseContract.scores_table.buildScoreWithDateAndLeague() :
                DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, SummaryWidgetIntentService.getDate(1, league), null);
        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
            data = null;
        }
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {


        if (position == AdapterView.INVALID_POSITION ||
                data == null || !data.moveToPosition(position)) {
            return null;
        }
        RemoteViews views = new RemoteViews(packageName, R.layout.detail_widget_item);

        views.setTextViewText(R.id.home_name, data.getString(scoresAdapter.COL_HOME));
        views.setTextViewText(R.id.away_name, data.getString(scoresAdapter.COL_AWAY));
        views.setTextViewText(R.id.data_textview, data.getString(scoresAdapter.COL_MATCHTIME));

        int nScore = data.getInt(scoresAdapter.COL_HOME_GOALS);
        views.setTextViewText(R.id.home_score, (-1 == nScore ? "" : String.valueOf(nScore)));

        nScore = data.getInt(scoresAdapter.COL_AWAY_GOALS);
        views.setTextViewText(R.id.away_score, (-1 == nScore ? "" : String.valueOf(nScore)));
        //views.setTextViewText(R.id.match_id, data.getString(scoresAdapter.COL_ID));



                /*
                mHolder.home_name.setText(cursor.getString(COL_HOME));
                mHolder.away_name.setText(cursor.getString(COL_AWAY));
                mHolder.date.setText(cursor.getString(COL_MATCHTIME));
                mHolder.score.setText(Utilies.getScores(cursor.getInt(COL_HOME_GOALS),cursor.getInt(COL_AWAY_GOALS)));
                mHolder.match_id = cursor.getDouble(COL_ID);
                mHolder.home_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                        cursor.getString(COL_HOME)));
                mHolder.away_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                        cursor.getString(COL_AWAY)
                ));
                */
                /*
                int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
                int weatherArtResourceId = Utility.getIconResourceForWeatherCondition(weatherId);


                String description = data.getString(INDEX_WEATHER_DESC);
                long dateInMillis = data.getLong(INDEX_WEATHER_DATE);
                String formattedDate = Utility.getFriendlyDayString(
                        DetailWidgetRemoteViewsService.this, dateInMillis, false);
                double maxTemp = data.getDouble(INDEX_WEATHER_MAX_TEMP);
                double minTemp = data.getDouble(INDEX_WEATHER_MIN_TEMP);
                String formattedMaxTemperature =
                        Utility.formatTemperature(DetailWidgetRemoteViewsService.this, maxTemp);
                String formattedMinTemperature =
                        Utility.formatTemperature(DetailWidgetRemoteViewsService.this, minTemp);
                if (weatherArtImage != null) {
                    views.setImageViewBitmap(R.id.widget_icon, weatherArtImage);
                } else {
                    views.setImageViewResource(R.id.widget_icon, weatherArtResourceId);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, description);
                }
                views.setTextViewText(R.id.widget_date, formattedDate);
                views.setTextViewText(R.id.widget_description, description);
                views.setTextViewText(R.id.widget_high_temperature, formattedMaxTemperature);
                views.setTextViewText(R.id.widget_low_temperature, formattedMinTemperature);

                final Intent fillInIntent = new Intent();
                String locationSetting =
                        Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        locationSetting,
                        dateInMillis);
                fillInIntent.setData(weatherUri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                */
        return views;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        //views.setContentDescription(R.id.widget_icon, description);
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(packageName, R.layout.detail_widget_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (data.moveToPosition(position))
            return data.getLong(scoresAdapter.COL_ID);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
