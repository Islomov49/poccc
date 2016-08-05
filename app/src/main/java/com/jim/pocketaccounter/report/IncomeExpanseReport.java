package com.jim.pocketaccounter.report;

import android.content.Context;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.credit.ReckingCredit;
import com.jim.pocketaccounter.debt.DebtBorrow;
import com.jim.pocketaccounter.debt.Recking;
import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class IncomeExpanseReport {
    private Context context;
    private Calendar begin, end;
    public IncomeExpanseReport(Context context, Calendar begin, Calendar end) {
        this.begin = (Calendar) begin.clone();
        this.end = (Calendar) end.clone();
        this.context = context;
    }
    public ArrayList<IncomeExpanseDataRow> makeReport() {
        ArrayList<IncomeExpanseDataRow> result = new ArrayList<IncomeExpanseDataRow>();
        Calendar recordBegin = (Calendar) begin.clone();
        Calendar recordEnd = (Calendar) end.clone();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        //finance records
        ArrayList<FinanceRecord> tempRecords = PocketAccounter.financeManager.getRecords();
        ArrayList<FinanceRecord> records = new ArrayList<FinanceRecord>();
        for (int i=0; i<tempRecords.size(); i++) {
            if (tempRecords.get(i).getDate().compareTo(begin) >= 0 &&
                    tempRecords.get(i).getDate().compareTo(end) <= 0)
                records.add(tempRecords.get(i));
        }
        //debt borrows main
        ArrayList<DebtBorrow> tempDebtBorrows = PocketAccounter.financeManager.getDebtBorrows();
        ArrayList<DebtBorrow> debtBorrowMain = new ArrayList<DebtBorrow>();
        for (int i=0; i<tempDebtBorrows.size(); i++) {
            if (tempDebtBorrows.get(i).getTakenDate().compareTo(begin)>=0 &&
                tempDebtBorrows.get(i).getTakenDate().compareTo(end) <= 0 &&
                tempDebtBorrows.get(i).isCalculate())
                debtBorrowMain.add(tempDebtBorrows.get(i));
        }
        //debt borrows recking
        ArrayList<DebtBorrow> debtBorrowRecking = new ArrayList<DebtBorrow>();
        for (int i=0; i<tempDebtBorrows.size(); i++) {
            if (!tempDebtBorrows.get(i).isCalculate()) continue;
            for (int j=0; j<tempDebtBorrows.get(i).getReckings().size(); j++) {
                Recking recking = tempDebtBorrows.get(i).getReckings().get(j);
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(format.parse(recking.getPayDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (cal.compareTo(begin) >= 0 && cal.compareTo(end) <= 0) {
                    debtBorrowRecking.add(tempDebtBorrows.get(i));
                    break;
                }
            }
        }
        //credits
        ArrayList<CreditDetials> temp = PocketAccounter.financeManager.getCredits();
        ArrayList<CreditDetials> credits = new ArrayList<CreditDetials>();
        for (int i=0; i<temp.size(); i++) {
            if (!temp.get(i).isKey_for_include()) continue;
            for (int j=0; j<temp.get(i).getReckings().size(); j++) {
                ReckingCredit recking = temp.get(i).getReckings().get(j);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(recking.getPayDate());
                if (cal.compareTo(begin) >= 0 && cal.compareTo(end) <= 0) {
                    credits.add(temp.get(i));
                    break;
                }
            }
        }
        while(recordBegin.compareTo(recordEnd)<=0) {
            IncomeExpanseDataRow row = new IncomeExpanseDataRow(recordBegin);
            ArrayList<IncomeExpanseDayDetails> details = new ArrayList<IncomeExpanseDayDetails>();
            //accumulate records
            Calendar b = (Calendar)recordBegin.clone();
            b.set(Calendar.HOUR_OF_DAY, 0);
            b.set(Calendar.MINUTE, 0);
            b.set(Calendar.SECOND, 0);
            b.set(Calendar.MILLISECOND, 0);
            Calendar e = (Calendar)recordBegin.clone();
            e.set(Calendar.HOUR_OF_DAY, 23);
            e.set(Calendar.MINUTE, 59);
            e.set(Calendar.SECOND, 59);
            e.set(Calendar.MILLISECOND, 59);
            for (int i=0; i<records.size(); i++) {
                if (records.get(i).getDate().compareTo(b) >= 0 &&
                        records.get(i).getDate().compareTo(e) <= 0) {
                    IncomeExpanseDayDetails detail = new IncomeExpanseDayDetails();
                    detail.setCategory(records.get(i).getCategory());
                    detail.setSubCategory(records.get(i).getSubCategory());
                    detail.setAmount(records.get(i).getAmount());
                    detail.setCurrency(records.get(i).getCurrency());
                    details.add(detail);
                }
            }
            //accumulate debtborrow mains
            for (int i=0; i<debtBorrowMain.size(); i++) {
                if (debtBorrowMain.get(i).getTakenDate().compareTo(b) >= 0 &&
                        debtBorrowMain.get(i).getTakenDate().compareTo(e) <= 0) {
                    IncomeExpanseDayDetails detail = new IncomeExpanseDayDetails();
                    RootCategory category = new RootCategory();
                    if (debtBorrowMain.get(i).getType() == DebtBorrow.BORROW) {
                        category.setName(context.getResources().getString(R.string.borrow_statistics));
                        category.setType(PocketAccounterGeneral.EXPENSE);
                    }
                    else {
                        category.setName(context.getResources().getString(R.string.debt_statistics));
                        category.setType(PocketAccounterGeneral.INCOME);
                    }
                    detail.setCategory(category);
                    detail.setSubCategory(null);
                    detail.setAmount(debtBorrowMain.get(i).getAmount());
                    detail.setCurrency(debtBorrowMain.get(i).getCurrency());
                    details.add(detail);
                }
            }

            //accumulate debtborrow reckings
            for (int i=0; i<debtBorrowRecking.size(); i++) {
                for (int j=0; j<debtBorrowRecking.get(i).getReckings().size(); j++) {
                    Recking recking = debtBorrowRecking.get(i).getReckings().get(j);
                    Calendar cal = Calendar.getInstance();
                    try {
                        cal.setTime(format.parse(recking.getPayDate()));
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    if (cal.compareTo(b) >= 0 && cal.compareTo(e) <= 0) {
                        IncomeExpanseDayDetails detail = new IncomeExpanseDayDetails();
                        RootCategory category = new RootCategory();
                        if (debtBorrowMain.get(i).getType() == DebtBorrow.BORROW) {
                            category.setName(context.getResources().getString(R.string.borrow_recking_statistics));
                            category.setType(PocketAccounterGeneral.INCOME);
                        }
                        else {
                            category.setName(context.getResources().getString(R.string.debt_recking_statistics));
                            category.setType(PocketAccounterGeneral.EXPENSE);
                        }
                        detail.setCategory(category);
                        detail.setSubCategory(null);
                        detail.setAmount(recking.getAmount());
                        detail.setCurrency(debtBorrowRecking.get(i).getCurrency());
                        details.add(detail);
                    }
                }
            }
            //accumulate credits
            for (int i=0; i<credits.size(); i++) {
                for (int j=0; j<credits.get(i).getReckings().size(); j++) {
                    ReckingCredit recking = credits.get(i).getReckings().get(j);
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(recking.getPayDate());
                    if (cal.compareTo(b) >= 0 && cal.compareTo(e) <= 0) {
                        IncomeExpanseDayDetails detail = new IncomeExpanseDayDetails();
                        RootCategory category = new RootCategory();
                        category.setName(credits.get(i).getCredit_name());
                        category.setType(PocketAccounterGeneral.EXPENSE);
                        detail.setCategory(category);
                        detail.setSubCategory(null);
                        detail.setAmount(recking.getAmount());
                        detail.setCurrency(credits.get(i).getValyute_currency());
                        details.add(detail);
                    }
                }
            }
            row.setDetails(details);
            row.calculate();
            result.add(row);
            recordBegin.add(Calendar.DAY_OF_MONTH, 1);
        }
        return result;
    }
}