package barqsoft.footballscores;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.ArrayList;

import barqsoft.footballscores.service.ServiceContract;

/**
 * Created by ioanagosman on 21/01/16.
 */

public class SummaryWidgetConfigure extends Activity {
    boolean[] vLeagues = new boolean[5];

    static final String TAG = "SummaryWidgetConfigure";
    static final int PREMIERE_LEAGUE = 0;
    static final int SERIE_A = 1;
    static final int BUNDESLIGA_1 = 2;
    static final int BUNDESLIGA_2 = 3;
    static final int PRIMERA_DIVISION = 4;


    private static final String PREFS_NAME
            = "io.appium.android.apis.appwidget.ExampleAppWidgetProvider";
    private static final String PREF_PREFIX_KEY = "prefix_";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public SummaryWidgetConfigure() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Set the view layout resource to use.
        setContentView(R.layout.summary_configuration);

        // Bind the action for the save button.
        findViewById(R.id.save_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = SummaryWidgetConfigure.this;

            // When the button is clicked, save the string in our prefs and return that they
            // clicked OK.
            String sLeague = getLeagueByRadioButton();
            String titlePrefix = getTitleByRadioButton(); //mAppWidgetPrefix.getText().toString();
            saveTitlePref(context, mAppWidgetId, titlePrefix);

            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            SummaryWidgetProvider.updateAppWidget(context, appWidgetManager,
                    mAppWidgetId, titlePrefix, sLeague);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    private String getLeagueByRadioButton() {
        String sResult = "";

        if(vLeagues[PREMIERE_LEAGUE]){ sResult = ServiceContract.PREMIER_LEAGUE; }
        if(vLeagues[SERIE_A]){ sResult = ServiceContract.SERIE_A; }
        if(vLeagues[BUNDESLIGA_1]){ sResult = ServiceContract.BUNDESLIGA1; }
        if(vLeagues[BUNDESLIGA_2]){ sResult = ServiceContract.BUNDESLIGA2; }
        if(vLeagues[PRIMERA_DIVISION]){ sResult = ServiceContract.PRIMERA_DIVISION; }


        return sResult;
    }

    private String getTitleByRadioButton() {
        String sResult = "";

        if(vLeagues[PREMIERE_LEAGUE]){ sResult = getResources().getString(R.string.radio_premire_league_title); }
        if(vLeagues[SERIE_A]){ sResult = getResources().getString(R.string.radio_serie_a_title); }
        if(vLeagues[BUNDESLIGA_1]){ sResult = getResources().getString(R.string.radio_bundesliga_1_title); }
        if(vLeagues[BUNDESLIGA_2]){ sResult = getResources().getString(R.string.radio_bundesliga_2_title); }
        if(vLeagues[PRIMERA_DIVISION]){ sResult = getResources().getString(R.string.radio_primera_division_title); }


        return sResult;
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.commit();
    }

    /*
    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String prefix = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (prefix != null) {
            return prefix;
        } else {
            return "appwidget_prefix_default"; //context.getString(R.string.appwidget_prefix_default);
        }
    }
   */


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_premiere:
                vLeagues[PREMIERE_LEAGUE] = checked;
                    break;
            case R.id.radio_primera:
                vLeagues[PRIMERA_DIVISION] = checked;
                    break;
            case R.id.radio_bundesliga_1:
                vLeagues[BUNDESLIGA_1] = checked;
                break;
            case R.id.radio_bundesliga_2:
                vLeagues[BUNDESLIGA_2] = checked;
                break;
            case R.id.radio_serie_a:
                vLeagues[SERIE_A] = checked;
                break;
            default:
                break;
        }
    }


    static void deleteTitlePref(Context context, int appWidgetId) {
    }

    static void loadAllTitlePrefs(Context context, ArrayList<Integer> appWidgetIds,
                                  ArrayList<String> texts) {
    }
}
