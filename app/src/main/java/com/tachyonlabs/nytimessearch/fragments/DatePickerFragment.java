package com.tachyonlabs.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends android.support.v4.app.DialogFragment {
    int year;
    int month;
    int day;
    Calendar cal;
    Date date;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        String dateString = args.getString("date");

        switch (dateString) {
            case "All dates":
                // NYT search supposedly goes back to 1851, but this datepicker only goes back to 1900
                year = 1900;
                month = 1;
                day = 1;
                break;
            case "Today":
                cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                break;
            default:
                SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d, yyyy");
                try {
                    date = sdf.parse(dateString);
                    Log.d("DATE", date.toString());
                } catch (Exception e) {

                }
                cal = Calendar.getInstance();
                cal.setTime(date);
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                break;
        }

        // Activity needs to implement this interface
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);

    }
}