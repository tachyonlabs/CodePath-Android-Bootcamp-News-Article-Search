package com.tachyonlabs.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.tachyonlabs.nytimessearch.R;
import com.tachyonlabs.nytimessearch.fragments.DatePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String beginDate;
    String beginOrEndDate;
    String endDate;
    String sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        EditText etBeginDate = (EditText) findViewById(R.id.etBeginDate);
        EditText etEndDate = (EditText) findViewById(R.id.etEndDate);
        beginDate = getIntent().getStringExtra("beginDate");
        endDate = getIntent().getStringExtra("endDate");
        etBeginDate.setText(beginDate);
        etEndDate.setText(endDate);
        sortOrder = getIntent().getStringExtra("sortOrder");
        if (sortOrder.equals("newest")) {
            RadioButton newest = (RadioButton) findViewById(R.id.rdbNewest);
            newest.setChecked(true);
        } else {
            RadioButton oldest = (RadioButton) findViewById(R.id.rdbOldest);
            oldest.setChecked(true);
        }
    }

    // attach to an onclick handler to show the date picker
    public void showTimePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        beginOrEndDate = v.getTag().toString();
        EditText et;
        if (beginOrEndDate.equals("beginDate")) {
            et = (EditText) findViewById(R.id.etBeginDate);
        } else {
            et = (EditText) findViewById(R.id.etEndDate);
        }
        args.putString("date", et.getText().toString());
        args.putString("beginOrEnd", beginOrEndDate);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        EditText et;
        String dateString;
        String today;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        today = sdf.format(c.getTime());
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if (beginOrEndDate.equals("beginDate")) {
            et = (EditText) findViewById(R.id.etBeginDate);
        } else {
            et = (EditText) findViewById(R.id.etEndDate);
        }

        dateString = sdf.format(c.getTime());
        if (dateString.equals("February 1, 1900")) {
            et.setText("All dates");
        } else if (dateString.equals(today)) {
            et.setText("Today");
        } else {
            et.setText(dateString);
        }
    }

    public void saveSettings(View view) {
        Intent data = new Intent();
        RadioButton newest = (RadioButton) findViewById(R.id.rdbNewest);
        // send the edited settings back to the main activity
        if (newest.isChecked()) {
            data.putExtra("sortOrder", "newest");
        } else {
            data.putExtra("sortOrder", "oldest");
        }
        EditText etBeginDate = (EditText) findViewById(R.id.etBeginDate);
        EditText etEndDate = (EditText) findViewById(R.id.etEndDate);
        beginDate = etBeginDate.getText().toString();
        endDate = etEndDate.getText().toString();
        data.putExtra("beginDate", beginDate);
        data.putExtra("endDate", endDate);
        setResult(RESULT_OK, data); // set result code and bundle data for response
        this.finish();
    }
}
