package com.jim.pocketaccounter.report;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jim.pocketaccounter.PocketAccounter;

import java.text.DecimalFormat;

public class DecFormat implements ValueFormatter {
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        DecimalFormat format = new DecimalFormat("0.00##");
        String text = format.format(entry.getVal()) + PocketAccounter.financeManager.getMainCurrency().getAbbr();
        return text;
    }
}
