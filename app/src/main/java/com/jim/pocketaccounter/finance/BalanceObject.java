package com.jim.pocketaccounter.finance;

import java.util.Calendar;

/**
 * Created by DEV on 17.08.2016.
 */

public class BalanceObject {
    private int type;
    private double sum;
    private Currency currency;

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = (Calendar)calendar.clone();
    }

    private Calendar calendar;
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public double getSum() {
        return sum;
    }
    public void setSum(double sum) {
        this.sum = sum;
    }
    public Currency getCurrency() {
        return currency;
    }
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
