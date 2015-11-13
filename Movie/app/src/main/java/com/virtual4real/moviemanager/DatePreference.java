package com.virtual4real.moviemanager;
/**
 * Created by ioanagosman on 11/10/15.
 * <p/>
 * used as startup the ideea from
 * http://stackoverflow.com/questions/5533078/timepicker-in-preferencescreen
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePreference extends DialogPreference {
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private DatePicker picker = null;

    public static int getDay(String time) {
        return Integer.valueOf(time.substring(6, 8));
    }

    public static int getMonth(String time) {
        return Integer.valueOf(time.substring(4, 6));
    }

    public static int getYear(String time) {
        return Integer.valueOf(time.substring(0, 4));
    }

    public DatePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker = new DatePicker(getContext());

        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        picker.updateDate(year, month, day);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            day = picker.getDayOfMonth();
            month = picker.getMonth();
            year = picker.getYear();


            String time = getStorageString();


            if (callChangeListener(time)) {
                persistString(time);
            }

            setSummary(getSummaryDate());
        }
    }

    private String getStorageString() {
        String s = String.valueOf(month);
        String d = String.valueOf(day);
        String time = String.valueOf(year) + (1 == s.length() ? "0" + s : s) +
                (1 == d.length() ? "0" + d : d);

        return time;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time = getPersistedString((null == defaultValue ? getDefaultValue() : String.valueOf(defaultValue)));

        year = getYear(time);
        month = getMonth(time);
        day = getDay(time);

        this.setSummary(getSummaryDate());
    }

    public String getSummaryDate() {
        final Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(c.getTime());
    }

    private String getDefaultValue() {
        final Calendar c = Calendar.getInstance();

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        return String.valueOf(year) + (month > 9 ? "" : "0") + String.valueOf(month) +
                (day > 9 ? "" : "0") + String.valueOf(day);

    }
}
