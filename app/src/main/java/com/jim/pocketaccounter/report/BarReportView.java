package com.jim.pocketaccounter.report;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class BarReportView extends LinearLayout {
    private HorizontalBarChart barChart;
    private ArrayList<IncomeExpanseDataRow> datas;
    private Calendar begin, end;

    public BarReportView(Context context, Calendar begin, Calendar end) {
        super(context);
        this.begin = (Calendar)begin.clone();
        this.end = (Calendar)end.clone();
        barChart = new HorizontalBarChart(context);
        barChart.setDescription("");
        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);
        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setYOffset(6f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(30f);
        barChart.getAxisRight().setEnabled(false);
        IncomeExpanseReport report = new IncomeExpanseReport(getContext(), begin, end);
        datas = report.makeReport();
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        barChart.setLayoutParams(lp);
        addView(barChart);
        drawReport();
    }
    public void setBeginTime(Calendar begin) {
        this.begin = (Calendar) begin.clone();
    }
    public void setEndTime(Calendar end) {
        this.end = (Calendar) end.clone();
    }
    public void makeReport() {
        IncomeExpanseReport report = new IncomeExpanseReport(getContext(), begin, end);
        datas = report.makeReport();
    }
    public BarChart getBarChart() {return barChart;}
    public BarReportView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public BarReportView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BarReportView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public ArrayList<IncomeExpanseDataRow> getDatas() {return datas;}
    public void drawReport() {
        ArrayList<String> xVals = new ArrayList<String>();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        for (int i=0; i<datas.size(); i++) {
            xVals.add(format.format(datas.get(i).getDate().getTime()));
        }
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();
        for (int i = 0; i < datas.size(); i++) {
            yVals1.add(new BarEntry((float) datas.get(i).getTotalIncome(), i));
            yVals2.add(new BarEntry((float) datas.get(i).getTotalExpanse(), i));
            yVals3.add(new BarEntry((float) datas.get(i).getTotalProfit(), i));
        }
        BarDataSet set1, set2, set3;
        set1 = new BarDataSet(yVals1, getResources().getString(R.string.income));
        set1.setColor(ContextCompat.getColor(getContext(), R.color.bar_income));
        set2 = new BarDataSet(yVals2, getResources().getString(R.string.expanse));
        set2.setColor(ContextCompat.getColor(getContext(), R.color.bar_expanse));
        set3 = new BarDataSet(yVals3, getResources().getString(R.string.profit));
        set3.setColor(ContextCompat.getColor(getContext(), R.color.bar_profit));
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);
        BarData data = new BarData(xVals, dataSets);
        data.setValueFormatter(new DecFormat());
        data.setGroupSpace(80f);
        barChart.setData(data);
        barChart.invalidate();
    }
}