package com.jim.pocketaccounter;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;
import com.jim.pocketaccounter.report.BarReportView;
import com.jim.pocketaccounter.report.IncomeExpanseDataRow;
import com.jim.pocketaccounter.report.IncomeExpanseDayDetails;
import com.jim.pocketaccounter.report.ReportByIncomeExpanseDialogAdapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReportByIncomeExpanseBarFragment extends Fragment implements OnChartValueSelectedListener {
    private LinearLayout llReportBarMain;
    private BarReportView reportView;
    private Calendar begin, end;
    private Dialog dialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.report_bar, container, false);
        llReportBarMain = (LinearLayout) rootView.findViewById(R.id.llReportBarMain);
        init();
        reportView = new BarReportView(getContext(), begin, end);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        reportView.setLayoutParams(lp);
        reportView.getBarChart().setOnChartValueSelectedListener(this);
        llReportBarMain.addView(reportView);
        return rootView;
    }
    public void invalidate(Calendar begin, Calendar end) {
        this.begin = (Calendar) begin.clone();
        this.end = (Calendar) end.clone();
        reportView.setBeginTime(this.begin);
        reportView.setEndTime(this.end);
        reportView.makeReport();
        reportView.drawReport();
        llReportBarMain.invalidate();
    }
    private void init() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String setting = sharedPreferences.getString("report_filter", "0");
        begin = Calendar.getInstance();
        end = Calendar.getInstance();
        switch (setting) {
            case "0":
                begin.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case "1":
                begin.add(Calendar.DAY_OF_MONTH, -2);
                break;
            case "2":
                begin.add(Calendar.DAY_OF_MONTH, -6);
                break;
        }
        begin.set(Calendar.HOUR_OF_DAY, 0);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);
        begin.set(Calendar.MILLISECOND, 0);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 59);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (dialog != null) return;
        ArrayList<IncomeExpanseDayDetails> result = new ArrayList<>();
        ArrayList<IncomeExpanseDataRow> rows = reportView.getDatas();
        IncomeExpanseDataRow row = rows.get(e.getXIndex());
        if (row.getTotalExpanse() == 0 && row.getTotalIncome() == 0) return;
        switch (dataSetIndex) {
            case 0:
                for (int i=0; i<row.getDetails().size(); i++) {
                    if (row.getDetails().get(i).getCategory().getType() == PocketAccounterGeneral.INCOME)
                        result.add(row.getDetails().get(i));
                }
                openDialog(row.getDate(), result, row.getTotalIncome(), dataSetIndex);
                break;
            case 1:
                for (int i=0; i<row.getDetails().size(); i++) {
                    if (row.getDetails().get(i).getCategory().getType() == PocketAccounterGeneral.EXPENSE)
                        result.add(row.getDetails().get(i));
                }
                openDialog(row.getDate(), result, row.getTotalExpanse(), dataSetIndex);
                //expanse
                break;
            case 2:
                openDialog(row.getDate(), row.getDetails(), row.getTotalProfit(), dataSetIndex);
                //income
                break;
        }
    }

    @Override
    public void onNothingSelected() {

    }
    private void openDialog(Calendar date, ArrayList<IncomeExpanseDayDetails> dataRow, double amount, int type) {
        dialog=new Dialog(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.report_by_income_expanse_bar_info, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        TextView tvReportByIncomeExpanseDate = (TextView) dialogView.findViewById(R.id.tvReportByIncomeExpanseDate);
        SimpleDateFormat format = new SimpleDateFormat("dd LLL, yyyy");
        tvReportByIncomeExpanseDate.setText(format.format(date.getTime()));
        ListView lvReportByIncomeExpanseInfo = (ListView) dialogView.findViewById(R.id.lvReportByIncomeExpanseInfo);
        ImageView ivReportByCategoryClose = (ImageView) dialogView.findViewById(R.id.ivReportByCategoryClose);
        ivReportByCategoryClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog = null;
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ReportByIncomeExpanseBarFragment.this.dialog = null;
            }
        });
        ReportByIncomeExpanseDialogAdapter adapter = new ReportByIncomeExpanseDialogAdapter(getContext(), dataRow);
        lvReportByIncomeExpanseInfo.setAdapter(adapter);
        DecimalFormat decimalFormat = new DecimalFormat("0.00##");
        String title = "";
        int color = 0;
        switch (type) {
            case 0:
                title = getResources().getString(R.string.report_income_expanse_total_income);
                color = ContextCompat.getColor(getContext(), R.color.green_just);
                break;
            case 1:
                title = getResources().getString(R.string.report_income_expanse_total_expanse);
                color = ContextCompat.getColor(getContext(), R.color.red);
                break;
            case 2:
                title = getResources().getString(R.string.report_income_expanse_total_profit);
                color = ContextCompat.getColor(getContext(), R.color.profit_color);
                break;
        }
        TextView tvReportIncomeExpanseTitle = (TextView) dialogView.findViewById(R.id.tvReportIncomeExpanseTitle);
        tvReportIncomeExpanseTitle.setText(title);
        TextView tvReportByIncomeExpanseBar = (TextView) dialogView.findViewById(R.id.tvReportByIncomeExpanseBar);
        tvReportByIncomeExpanseBar.setTextColor(color);
        tvReportByIncomeExpanseBar.setText(decimalFormat.format(amount)+PocketAccounter.financeManager.getMainCurrency().getAbbr());
        dialog.show();
    }
}
