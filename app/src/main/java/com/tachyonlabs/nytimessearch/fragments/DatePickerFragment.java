package com.tachyonlabs.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import com.tachyonlabs.nytimessearch.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends android.support.v4.app.DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year;
        int month;
        int day;
        Calendar cal;
        Date date;
        Bundle args = this.getArguments();
        String dateString = args.getString("date");

        if (dateString.equals(getResources().getString(R.string.today))) {
            cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d, yyyy");
            try {
                date = sdf.parse(dateString);
            } catch (Exception e) {
                e.printStackTrace();
                cal = Calendar.getInstance();
                date = cal.getTime();
            }
            cal = Calendar.getInstance();
            cal.setTime(date);
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        }

        // Activity needs to implement this interface
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);

    }
}