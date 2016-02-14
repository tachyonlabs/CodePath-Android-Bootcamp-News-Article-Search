package com.tachyonlabs.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tachyonlabs.nytimessearch.R;
import com.tachyonlabs.nytimessearch.fragments.AlertDialogFragment;
import com.tachyonlabs.nytimessearch.fragments.DatePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String beginDate;
    String beginOrEndDate;
    String endDate;
    String sortOrder;
    boolean allNewsDeskValues;
    boolean newsDeskArtsSelected;
    boolean newsDeskFashionAndStyleSelected;
    boolean newsDeskSportsSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        EditText etBeginDate = (EditText) findViewById(R.id.etBeginDate);
        EditText etEndDate = (EditText) findViewById(R.id.etEndDate);
        beginDate = getIntent().getStringExtra("beginDate");
        endDate = getIntent().getStringExtra("endDate");
        etBeginDate.setText(yyyymmddToLongDate(beginDate));
        etEndDate.setText(yyyymmddToLongDate(endDate));
        sortOrder = getIntent().getStringExtra("sortOrder");
        if (sortOrder.equals("newest")) {
            RadioButton newest = (RadioButton) findViewById(R.id.rdbNewest);
            newest.setChecked(true);
        } else {
            RadioButton oldest = (RadioButton) findViewById(R.id.rdbOldest);
            oldest.setChecked(true);
        }
        CheckBox chkArts = (CheckBox) findViewById(R.id.chkArts);
        CheckBox chkFashionAndStyle = (CheckBox) findViewById(R.id.chkFashionAndStyle);
        CheckBox chkSports = (CheckBox) findViewById(R.id.chkSports);
        allNewsDeskValues = getIntent().getBooleanExtra("allNewsDeskValues", true);
        if (allNewsDeskValues) {
            RadioButton rdbAll = (RadioButton) findViewById(R.id.rdbAll);
            rdbAll.setChecked(true);
            chkArts.setVisibility(View.INVISIBLE);
            chkFashionAndStyle.setVisibility(View.INVISIBLE);
            chkSports.setVisibility(View.INVISIBLE);
        } else {
            RadioButton rdbSelect = (RadioButton) findViewById(R.id.rdbSelect);
            rdbSelect.setChecked(true);
            chkArts.setVisibility(View.VISIBLE);
            chkFashionAndStyle.setVisibility(View.VISIBLE);
            chkSports.setVisibility(View.VISIBLE);
        }
        newsDeskArtsSelected = getIntent().getBooleanExtra("newsDeskArtsSelected", false);
        if (newsDeskArtsSelected) {
            chkArts.setChecked(true);
        } else {
            chkArts.setChecked(false);
        }
        newsDeskFashionAndStyleSelected = getIntent().getBooleanExtra("newsDeskFashionAndStyleSelected", false);
        if (newsDeskFashionAndStyleSelected) {
            chkFashionAndStyle.setChecked(true);
        } else {
            chkFashionAndStyle.setChecked(false);
        }
        newsDeskSportsSelected = getIntent().getBooleanExtra("newsDeskSportsSelected", false);
        if (newsDeskSportsSelected) {
            chkSports.setChecked(true);
        } else {
            chkSports.setChecked(false);
        }

        RadioGroup rgNewsDesk = (RadioGroup) findViewById(R.id.rdgNewsDesk);
        rgNewsDesk.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // make the news desk checkboxes appear and disappear depending on whether
                // they click the "All depts." or "Select depts." radio buttons
                CheckBox chkArts = (CheckBox) findViewById(R.id.chkArts);
                CheckBox chkFashionAndStyle = (CheckBox) findViewById(R.id.chkFashionAndStyle);
                CheckBox chkSports = (CheckBox) findViewById(R.id.chkSports);
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                if (radioButton.getText().toString().equals(getResources().getString(R.string.all_values))) {
                    chkArts.setVisibility(View.INVISIBLE);
                    chkFashionAndStyle.setVisibility(View.INVISIBLE);
                    chkSports.setVisibility(View.INVISIBLE);
                } else {
                    chkArts.setVisibility(View.VISIBLE);
                    chkFashionAndStyle.setVisibility(View.VISIBLE);
                    chkSports.setVisibility(View.VISIBLE);
                }
            }
        });
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
        if (dateString.equals(today)) {
            et.setText(getResources().getString(R.string.today));
        } else {
            et.setText(dateString);
        }
    }

    public void saveSettings(View view) {
        RadioButton rdbAll = (RadioButton) findViewById(R.id.rdbAll);
        CheckBox chkArts = (CheckBox) findViewById(R.id.chkArts);
        CheckBox chkFashionAndStyle = (CheckBox) findViewById(R.id.chkFashionAndStyle);
        CheckBox chkSports = (CheckBox) findViewById(R.id.chkSports);

        if (!rdbAll.isChecked() && !chkArts.isChecked() && !chkFashionAndStyle.isChecked() && !chkSports.isChecked()) {
            // if they selected "Select depts." but didn't actually select any departments
            FragmentManager fm = getSupportFragmentManager();
            AlertDialogFragment alertDialog = AlertDialogFragment.newInstance("News Article Search Settings", "If you select 'Select Depts.' instead of 'All depts.', you also need to select which departments you want to search.");
            alertDialog.show(fm, "fragment_alert");
        } else {
            // send the edited settings back to the main activity
            Intent data = new Intent();
            RadioButton newest = (RadioButton) findViewById(R.id.rdbNewest);
            if (newest.isChecked()) {
                data.putExtra("sortOrder", "newest");
            } else {
                data.putExtra("sortOrder", "oldest");
            }
            if (rdbAll.isChecked()) {
                data.putExtra("allNewsDeskValues", true);
            } else {
                data.putExtra("allNewsDeskValues", false);
            }
            if (chkArts.isChecked()) {
                data.putExtra("newsDeskArtsSelected", true);
            } else {
                data.putExtra("newsDeskArtsSelected", false);
            }
            if (chkFashionAndStyle.isChecked()) {
                data.putExtra("newsDeskFashionAndStyleSelected", true);
            } else {
                data.putExtra("newsDeskFashionAndStyleSelected", false);
            }
            if (chkSports.isChecked()) {
                data.putExtra("newsDeskSportsSelected", true);
            } else {
                data.putExtra("newsDeskSportsSelected", false);
            }
            EditText etBeginDate = (EditText) findViewById(R.id.etBeginDate);
            EditText etEndDate = (EditText) findViewById(R.id.etEndDate);
            beginDate = etBeginDate.getText().toString();
            endDate = etEndDate.getText().toString();
            data.putExtra("beginDate", longDateToYYYYMMDD(beginDate));
            data.putExtra("endDate", longDateToYYYYMMDD(endDate));
            setResult(RESULT_OK, data); // set result code and bundle data for response
            this.finish();
        }
    }

    public String longDateToYYYYMMDD(String longDate) {
        Date date;
        if (longDate.equals(getResources().getString(R.string.today))) {
            return longDate;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
            try {
                date = sdf.parse(longDate);
            } catch (Exception e) {
                e.printStackTrace();
                Calendar c = Calendar.getInstance();
                date = c.getTime();
            }
            sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(date);
        }
    }

    public String yyyymmddToLongDate(String yyyymmddDate) {
        Date date;
        if (yyyymmddDate.equals("Today") || yyyymmddDate.equals("All Dates")) {
            return yyyymmddDate;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            try {
                date = sdf.parse(yyyymmddDate);
            } catch (Exception e) {
                e.printStackTrace();
                Calendar c = Calendar.getInstance();
                date = c.getTime();
            }
            sdf = new SimpleDateFormat("MMMM d, yyyy");
            return sdf.format(date);
        }
    }
}
