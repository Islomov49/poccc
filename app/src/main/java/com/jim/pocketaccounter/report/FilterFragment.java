package com.jim.pocketaccounter.report;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.jim.pocketaccounter.R;

import java.util.Calendar;

/**
 * Created by user on 6/14/2016.
 */

public class FilterFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final int YEAR_MONTH = 0;
    private static final int YEAR = 1;
    private static final int START_END_TIME = 2;

    private Spinner yilFilter;
    private Spinner monthFilter;

    private Spinner yearFilter;

    private EditText startTimeFilter;
    private EditText endTimeFilter;

    private Spinner spinner;

    private ImageView saveBt;
    private Calendar yearDate = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_statistic_layout, container, false);
        yearFilter = (Spinner) view.findViewById(R.id.etFilterStatisticYear);

        yilFilter = (Spinner) view.findViewById(R.id.spFilterStatisticYearMont);
        monthFilter = (Spinner) view.findViewById(R.id.spFilterStatisticMonth);

        startTimeFilter = (EditText) view.findViewById(R.id.etFilterStatisticStartTime);
        endTimeFilter = (EditText) view.findViewById(R.id.etFilterStatisticEndTime);

        spinner = (Spinner) view.findViewById(R.id.spFilerStatistic);
        saveBt = (ImageView) view.findViewById(R.id.ivFilterStatisticSave);

        view.findViewById(R.id.ivFilterStatisticCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             // ----- Cancel ------
            }
        });
        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             // ----- Save -------

            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, new String[] {"oy yil", "yil", "between date"});

        String[] year = new String[51];
        for (int i = 2000; i < 2051; i++) {
            year[i] = ""+i;
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, year);

        ArrayAdapter<String> yearMonthAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.months));

        yearFilter.setAdapter(yearAdapter);
        yilFilter.setAdapter(yearAdapter);
        monthFilter.setAdapter(yearMonthAdapter);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //--------- item selection Events ---------
        switch (position) {
            case YEAR_MONTH: {
                yilFilter.setVisibility(View.VISIBLE);
                monthFilter.setVisibility(View.VISIBLE);

                yearFilter.setVisibility(View.INVISIBLE);
                startTimeFilter.setVisibility(View.INVISIBLE);
                endTimeFilter.setVisibility(View.INVISIBLE);

                break;
            }
            case YEAR: {
                yearFilter.setVisibility(View.VISIBLE);

                yilFilter.setVisibility(View.INVISIBLE);
                monthFilter.setVisibility(View.INVISIBLE);
                startTimeFilter.setVisibility(View.INVISIBLE);
                endTimeFilter.setVisibility(View.INVISIBLE);

                break;
            }
            case START_END_TIME: {
                startTimeFilter.setVisibility(View.VISIBLE);
                endTimeFilter.setVisibility(View.VISIBLE);

                yilFilter.setVisibility(View.INVISIBLE);
                monthFilter.setVisibility(View.INVISIBLE);
                yearFilter.setVisibility(View.INVISIBLE);

                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
