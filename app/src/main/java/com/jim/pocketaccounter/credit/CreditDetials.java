package com.jim.pocketaccounter.credit;

import java.util.ArrayList;
import java.util.Calendar;

public class CreditDetials {
    private String credit_name;
    private int icon_ID;
    private Calendar take_time;
    private double procent;
    private long procent_interval;
    private long period_time;
    private long period_time_tip;
    private long myCredit_id;
    private double value_of_credit;
    private double value_of_credit_with_procent;
    private com.jim.pocketaccounter.finance.Currency valyute_currency;
    private ArrayList<ReckingCredit> reckings;
    private boolean key_for_include;
    private boolean key_for_archive;
    private String info = "";

    public CreditDetials getObj(){
        CreditDetials backGA=new CreditDetials(getIcon_ID(),getCredit_name(),getTake_time(),getProcent(),getProcent_interval(),getPeriod_time(),getPeriod_time_tip(),
                isKey_for_include(),getValue_of_credit(),getValyute_currency(),getValue_of_credit_with_procent(),getMyCredit_id(),getReckings());

      return  backGA;
    };

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public CreditDetials(){

    }
    public CreditDetials(int icon_ID, String credit_name, Calendar take_time,
                         double procent, long procent_interval, long period_time,long period_time_tip,boolean key_for_include,
                         double value_of_credit, com.jim.pocketaccounter.finance.Currency valyute_currency,
                         double value_of_credit_with_procent, long myCredit_id,ArrayList<ReckingCredit> reckingCredits) {
        this.icon_ID=icon_ID;
        this.credit_name = credit_name;
        this.take_time = take_time;
        this.procent = procent;
        this.procent_interval = procent_interval;
        this.period_time = period_time;
        this.value_of_credit = value_of_credit;
        this.valyute_currency = valyute_currency;
        this.value_of_credit_with_procent=value_of_credit_with_procent;
        this.period_time_tip=period_time_tip;
        this.myCredit_id=myCredit_id;
        this.key_for_include=key_for_include;
        key_for_archive=false;
        this.reckings = reckingCredits;
    }
    public CreditDetials(int icon_ID, String credit_name, Calendar take_time,
                         double procent, long procent_interval, long period_time,long period_time_tip,boolean key_for_include,
                         double value_of_credit, com.jim.pocketaccounter.finance.Currency valyute_currency,
                         double value_of_credit_with_procent, long myCredit_id) {
        this.icon_ID=icon_ID;
        this.credit_name = credit_name;
        this.take_time = take_time;
        this.procent = procent;
        this.procent_interval = procent_interval;
        this.period_time = period_time;
        this.value_of_credit = value_of_credit;
        this.valyute_currency = valyute_currency;
        this.value_of_credit_with_procent=value_of_credit_with_procent;
        this.period_time_tip=period_time_tip;
        this.myCredit_id=myCredit_id;
        this.key_for_include=key_for_include;
        key_for_archive=false;
        reckings = new ArrayList<>();
    }

    public boolean isKey_for_archive() {
        return key_for_archive;
    }
    public void setKey_for_archive(boolean key_for_archive) {
        this.key_for_archive = key_for_archive;
    }
    public ArrayList<ReckingCredit> getReckings() {
        return reckings;
    }
    public void setReckings(ArrayList<ReckingCredit> reckings) {
        this.reckings = reckings;
    }
    public long getPeriod_time_tip() {
        return period_time_tip;
    }
    public void setPeriod_time_tip(long period_time_tip) {
        this.period_time_tip = period_time_tip;
    }
    public boolean isKey_for_include() {
        return key_for_include;
    }
    public void setKey_for_include(boolean key_for_include) {
        this.key_for_include = key_for_include;
    }
    public long getMyCredit_id() {
        return myCredit_id;
    }
    public void setMyCredit_id(long myCredit_id) {
        this.myCredit_id = myCredit_id;
    }
    public int getIcon_ID() {
        return icon_ID;
    }
    public void setIcon_ID(int icon_ID) {
        this.icon_ID = icon_ID;
    }
    public double getValue_of_credit_with_procent() {
        return value_of_credit_with_procent;
    }
    public void setValue_of_credit_with_procent(double value_of_credit_with_procent) {
        this.value_of_credit_with_procent = value_of_credit_with_procent;
    }
    public String getCredit_name() {
        return credit_name;
    }
    public void setCredit_name(String credit_name) {
        this.credit_name = credit_name;
    }
    public Calendar getTake_time() {
        return take_time;
    }
    public void setTake_time(Calendar take_time) {
        this.take_time = (Calendar)take_time.clone();
    }
    public double getProcent() {
        return procent;
    }
    public void setProcent(double procent) {
        this.procent = procent;
    }
    public long getProcent_interval() {
        return procent_interval;
    }
    public void setProcent_interval(long procent_interval) {
        this.procent_interval = procent_interval;
    }
    public long getPeriod_time() {
        return period_time;
    }
    public void setPeriod_time(long period_time) {
        this.period_time = period_time;
    }
    public double getValue_of_credit() {
        return value_of_credit;
    }
    public void setValue_of_credit(double value_of_credit) {
        this.value_of_credit = value_of_credit;
    }
    public com.jim.pocketaccounter.finance.Currency getValyute_currency() {
        return valyute_currency;
    }
    public void setValyute_currency(com.jim.pocketaccounter.finance.Currency valyute_currency) {
        this.valyute_currency = valyute_currency;
    }
}
