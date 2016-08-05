package com.jim.pocketaccounter.report;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.report.FilterSelectable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
public class FilterDialog extends Dialog implements AdapterView.OnItemSelectedListener {
    private static final int START_CURRENT_MONTH = 0;
    private static final int START_3_DAY = 1;
    private static final int START_7_DAY = 2;
    private static final int YEAR_MONTH = 3;
    private static final int YEAR = 4;
    private static final int START_END_TIME = 5;
    private Calendar beginDate;
    private Calendar endDate;
    private Spinner yilFilter;
    private Spinner monthFilter;
    private Spinner yearFilter;
    private EditText startTimeFilter;
    private EditText endTimeFilter;
    private Spinner spinner;
    private ImageView saveBt;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private FilterSelectable filterSelectable = null;
    public FilterDialog(Context context) {
        super(context);
    }
    public FilterDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
    protected FilterDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    DatePickerDialog.OnDateSetListener getBeginListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            beginDate.set(arg1, arg2, arg3);
            if (beginDate.compareTo(endDate) >= 0) {
                beginDate = (Calendar)endDate.clone();
            }
            startTimeFilter.setText(dateFormat.format(beginDate.getTime()));
        }
    };
    DatePickerDialog.OnDateSetListener getEndListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            endDate.set(arg1, arg2, arg3);
            if (beginDate.compareTo(endDate) >= 0) {
                endDate = (Calendar) beginDate.clone();
            }
            endTimeFilter.setText(dateFormat.format(endDate.getTime()));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filter_statistic_layout);
        beginDate = Calendar.getInstance();
        beginDate.set(Calendar.HOUR_OF_DAY, 0);
        beginDate.set(Calendar.MINUTE, 0);
        beginDate.set(Calendar.SECOND, 0);
        beginDate.set(Calendar.MILLISECOND, 0);
        endDate = Calendar.getInstance();
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.MILLISECOND, 59);
        yearFilter = (Spinner) findViewById(R.id.etFilterStatisticYear);
        yilFilter = (Spinner) findViewById(R.id.spFilterStatisticYearMont);
        monthFilter = (Spinner) findViewById(R.id.spFilterStatisticMonth);
        startTimeFilter = (EditText) findViewById(R.id.etFilterStatisticStartTime);
        endTimeFilter = (EditText) findViewById(R.id.etFilterStatisticEndTime);
        spinner = (Spinner) findViewById(R.id.spFilerStatistic);
        saveBt = (ImageView) findViewById(R.id.ivFilterStatisticSave);
        findViewById(R.id.ivFilterStatisticCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (spinner.getSelectedItemPosition() == 0) {
                    beginDate = (Calendar) Calendar.getInstance().clone();
                    endDate = (Calendar) Calendar.getInstance().clone();
                    beginDate.set(Calendar.DAY_OF_MONTH, 1);
                    beginDate.set(Calendar.HOUR_OF_DAY, 0);
                    beginDate.set(Calendar.MINUTE, 0);
                    beginDate.set(Calendar.SECOND, 0);
                    beginDate.set(Calendar.MILLISECOND, 0);
                    endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                    endDate.set(Calendar.HOUR_OF_DAY, 23);
                    endDate.set(Calendar.MINUTE, 59);
                    endDate.set(Calendar.SECOND, 59);
                    endDate.set(Calendar.MILLISECOND, 59);
                }
                filterSelectable.onDateSelected(beginDate, endDate);
                dismiss();
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(), R.layout.spiner_gravity_right,
                getContext().getResources().getStringArray(R.array.filter_titles_dialog));
        String[] year = new String[51];
        for (int i = 0; i < 51; i++) {
            year[i] = (i + 2000) + "";
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(
                getContext(), R.layout.spiner_gravity_right, year);
        String[] months = getContext().getResources().getStringArray(R.array.months);
        ArrayAdapter<String> yearMonthAdapter = new ArrayAdapter<String>(
                getContext(), R.layout.spiner_gravity_right, months);
        yearFilter.setAdapter(yearAdapter);
        yilFilter.setAdapter(yearAdapter);
        monthFilter.setAdapter(yearMonthAdapter);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);
        for (int i = 0; i < year.length; i++) {
            if (year[i].matches("" + Calendar.getInstance().get(Calendar.YEAR))) {
                yearFilter.setSelection(i);
                yilFilter.setSelection(i);
            }
        }

        for (int i = 0; i < months.length; i++) {
            if (i == Calendar.getInstance().get(Calendar.MONTH)) {
                monthFilter.setSelection(i);
            }
        }
        spinner.setOnItemSelectedListener(this);
        yearFilter.setOnItemSelectedListener(this);
        yilFilter.setOnItemSelectedListener(this);
        monthFilter.setOnItemSelectedListener(this);
        startTimeFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar calendar = Calendar.getInstance();
                    Dialog mDialog = new DatePickerDialog(getContext(),
                            getBeginListener, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar
                            .get(Calendar.DAY_OF_MONTH));
                    mDialog.show();
                    return true;
                }
                return false;
            }
        });
        endTimeFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar calendar = Calendar.getInstance();
                    Dialog mDialog = new DatePickerDialog(getContext(),
                            getEndListener, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar
                            .get(Calendar.DAY_OF_MONTH));
                    mDialog.show();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spFilerStatistic:
                mainSpinner(position);
                break;
            case R.id.spFilterStatisticMonth:
                if (monthFilter.getVisibility() != View.VISIBLE) return;
                beginDate.set(Calendar.MONTH, position);
                beginDate.set(Calendar.DAY_OF_MONTH, 1);
                endDate = (Calendar) beginDate.clone();
                endDate.set(Calendar.DAY_OF_MONTH, beginDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                Log.d("sss", format.format(beginDate.getTime())+":"+format.format(endDate.getTime())+"month");
                break;
            case R.id.spFilterStatisticYearMont:
                if (yilFilter.getVisibility() != View.VISIBLE) return;
                beginDate.set(Calendar.YEAR, Integer.parseInt(yilFilter.getSelectedItem().toString()));
                endDate.set(Calendar.YEAR, beginDate.get(Calendar.YEAR));
                endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                SimpleDateFormat formatdate = new SimpleDateFormat("dd.MM.yyyy");
                Log.d("sss", formatdate.format(beginDate.getTime())+":"+formatdate.format(endDate.getTime())+"year");
                break;
            case R.id.etFilterStatisticYear:
                if (yearFilter.getVisibility() != View.VISIBLE) return;
                beginDate.set(Calendar.YEAR, Integer.parseInt(yearFilter.getSelectedItem().toString()));
                beginDate.set(Calendar.MONTH, Calendar.JANUARY);
                beginDate.set(Calendar.DAY_OF_MONTH, 1);
                endDate = (Calendar) beginDate.clone();
                endDate.set(Calendar.MONTH, Calendar.DECEMBER);
                endDate.set(Calendar.DAY_OF_MONTH, 31);
                break;
        }
    }

    private void mainSpinner(int position) {
        switch (position) {
            case START_CURRENT_MONTH: {
                findViewById(R.id.yi_oy).setVisibility(View.GONE);
                findViewById(R.id.yil_edit).setVisibility(View.GONE);
                findViewById(R.id.interval_edit).setVisibility(View.GONE);

                beginDate.set(Calendar.DAY_OF_MONTH, 1);
                beginDate.set(Calendar.HOUR_OF_DAY, 0);
                beginDate.set(Calendar.MINUTE, 0);
                beginDate.set(Calendar.SECOND, 0);
                beginDate.set(Calendar.MILLISECOND, 0);
                endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 59);
                break;
            }
            case START_3_DAY: {
                findViewById(R.id.yi_oy).setVisibility(View.GONE);
                findViewById(R.id.yil_edit).setVisibility(View.GONE);
                findViewById(R.id.interval_edit).setVisibility(View.GONE);

                beginDate = (Calendar) Calendar.getInstance().clone();
                endDate = (Calendar) Calendar.getInstance().clone();
                beginDate.set(Calendar.DAY_OF_YEAR, endDate.get(Calendar.DAY_OF_YEAR) - 2);
                beginDate.set(Calendar.HOUR_OF_DAY, 0);
                beginDate.set(Calendar.MINUTE, 0);
                beginDate.set(Calendar.SECOND, 0);
                beginDate.set(Calendar.MILLISECOND, 0);
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 59);
                break;
            }
            case START_7_DAY: {
                findViewById(R.id.yi_oy).setVisibility(View.GONE);
                findViewById(R.id.yil_edit).setVisibility(View.GONE);
                findViewById(R.id.interval_edit).setVisibility(View.GONE);
                beginDate = (Calendar) Calendar.getInstance().clone();
                endDate = (Calendar) Calendar.getInstance().clone();
                beginDate.set(Calendar.DAY_OF_YEAR, endDate.get(Calendar.DAY_OF_YEAR) - 6);
                beginDate.set(Calendar.HOUR_OF_DAY, 0);
                beginDate.set(Calendar.MINUTE, 0);
                beginDate.set(Calendar.SECOND, 0);
                beginDate.set(Calendar.MILLISECOND, 0);
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 59);
                break;
            }
            case YEAR_MONTH: {
                findViewById(R.id.yi_oy).setVisibility(View.VISIBLE);
                findViewById(R.id.yil_edit).setVisibility(View.GONE);
                findViewById(R.id.interval_edit).setVisibility(View.GONE);

                beginDate.set(Calendar.MONTH, monthFilter.getSelectedItemPosition());
                beginDate.set(Calendar.DAY_OF_MONTH, 1);
                endDate = (Calendar) beginDate.clone();
                endDate.set(Calendar.DAY_OF_MONTH, beginDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            }
            case YEAR: {
                findViewById(R.id.yi_oy).setVisibility(View.GONE);
                findViewById(R.id.yil_edit).setVisibility(View.VISIBLE);
                findViewById(R.id.interval_edit).setVisibility(View.GONE);


                beginDate.set(Calendar.YEAR, Integer.parseInt(yearFilter.getSelectedItem().toString()));
                beginDate.set(Calendar.MONTH, Calendar.JANUARY);
                beginDate.set(Calendar.DAY_OF_MONTH, 1);
                endDate = (Calendar) beginDate.clone();
                endDate.set(Calendar.MONTH, Calendar.DECEMBER);
                endDate.set(Calendar.DAY_OF_MONTH, 31);
                break;
            }
            case START_END_TIME: {
                findViewById(R.id.yi_oy).setVisibility(View.GONE);
                findViewById(R.id.yil_edit).setVisibility(View.GONE);
                findViewById(R.id.interval_edit).setVisibility(View.VISIBLE);

                startTimeFilter.setText(dateFormat.format(beginDate.getTime()));
                endTimeFilter.setText(dateFormat.format(endDate.getTime()));
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void setOnDateSelectedListener(FilterSelectable filterSelectable) {
        this.filterSelectable = filterSelectable;
    }
}
