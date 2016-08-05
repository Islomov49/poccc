package com.jim.pocketaccounter.report;

import android.content.Context;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.credit.ReckingCredit;
import com.jim.pocketaccounter.debt.DebtBorrow;
import com.jim.pocketaccounter.debt.Recking;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReportByAccount {
    private Context context;
    private Calendar begin;
    private Calendar end;
    private Account account;
    private Currency currency;

    public ReportByAccount(Context context, Calendar begin, Calendar end, Account account, Currency currency) {
        this.begin = (Calendar) begin.clone();
        this.begin.set(Calendar.HOUR_OF_DAY, 0);
        this.begin.set(Calendar.MINUTE, 0);
        this.begin.set(Calendar.SECOND, 0);
        this.begin.set(Calendar.MILLISECOND, 0);
        this.end = (Calendar) end.clone();
        this.end.set(Calendar.HOUR_OF_DAY, 23);
        this.end.set(Calendar.MINUTE, 59);
        this.end.set(Calendar.SECOND, 59);
        this.end.set(Calendar.MILLISECOND, 59);
        this.account = account;
        this.currency = currency;
        this.context = context;
    }

    public ArrayList<AccountDataRow> makeAccountReport() {
        ArrayList<AccountDataRow> result = new ArrayList<>();
        //acumluate income expense data
        ArrayList<FinanceRecord> temp = PocketAccounter.financeManager.getRecords();
        ArrayList<FinanceRecord> records = new ArrayList<>();

        for (int i = 0; i < temp.size(); i++) {
            if (begin.compareTo(temp.get(i).getDate()) <= 0
                    && end.compareTo(temp.get(i).getDate()) >= 0) {
                records.add(temp.get(i));
            }
        }
        for (int i = 0; i < records.size(); i++) {
            if (account.getId().matches(records.get(i).getAccount().getId())
                    && currency.getId().matches(records.get(i).getCurrency().getId())) {
                AccountDataRow row = new AccountDataRow();
                row.setType(records.get(i).getCategory().getType());
                row.setDate(records.get(i).getDate());
                row.setCategory(records.get(i).getCategory());
                row.setSubCategory(records.get(i).getSubCategory());
                row.setCurrency(records.get(i).getCurrency());
                row.setAmount(records.get(i).getAmount());
                result.add(row);
            }
        }
        //acumulating taken amounts
        ArrayList<DebtBorrow> temp_debt_borrow = PocketAccounter.financeManager.getDebtBorrows();
        ArrayList<DebtBorrow> debt_borrow = new ArrayList<DebtBorrow>();

        for (int i = 0; i < temp_debt_borrow.size(); i++) {
            if (begin.compareTo(temp_debt_borrow.get(i).getTakenDate()) <= 0
                    && end.compareTo(temp_debt_borrow.get(i).getTakenDate()) >= 0
                    && temp_debt_borrow.get(i).isCalculate()) {
                debt_borrow.add(temp_debt_borrow.get(i));
            }
        }
        for (int i = 0; i < debt_borrow.size(); i++) {
            if (debt_borrow.get(i).getAccount().getId().matches(account.getId())
                    && currency.getId().matches(debt_borrow.get(i).getCurrency().getId())) {
                AccountDataRow row = new AccountDataRow();
                if (debt_borrow.get(i).getType() == DebtBorrow.BORROW)
                    row.setType(PocketAccounterGeneral.EXPENSE);
                else
                    row.setType(PocketAccounterGeneral.INCOME);
                row.setDate(debt_borrow.get(i).getTakenDate());
                RootCategory rootCategory = new RootCategory();
                if (row.getType() == PocketAccounterGeneral.INCOME)
                    rootCategory.setName(context.getResources().getString(R.string.debt_borrow_taken));
                else
                    rootCategory.setName(context.getResources().getString(R.string.debt_borrow_given));
                row.setCategory(rootCategory);
                row.setSubCategory(null);
                row.setCurrency(debt_borrow.get(i).getCurrency());
                row.setAmount(debt_borrow.get(i).getAmount());
                result.add(row);
            }
        }
        //Acumulate rekings
        debt_borrow.clear();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        for (int i = 0; i < temp_debt_borrow.size(); i++) {
            if (!temp_debt_borrow.get(i).isCalculate()) continue;
            for (int j = 0; j < temp_debt_borrow.get(i).getReckings().size(); j++) {
                Recking recking = temp_debt_borrow.get(i).getReckings().get(j);
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(simpleDateFormat.parse(recking.getPayDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (cal.compareTo(begin) >= 0 && cal.compareTo(end) <= 0
                        && account.getId().matches(recking.getAccountId())
                        && temp_debt_borrow.get(i).getCurrency().getId().matches(currency.getId())) {
                    debt_borrow.add(temp_debt_borrow.get(i));
                    break;
                }
            }
        }
        for (int i = 0; i < debt_borrow.size(); i++) {
            for (int j = 0; j < debt_borrow.get(i).getReckings().size(); j++) {
                Recking recking = debt_borrow.get(i).getReckings().get(j);
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(simpleDateFormat.parse(recking.getPayDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (cal.compareTo(begin) >= 0 && cal.compareTo(end) <= 0) {
                    AccountDataRow row = new AccountDataRow();
                    if (debt_borrow.get(i).getType() == DebtBorrow.BORROW)
                        row.setType(PocketAccounterGeneral.INCOME);
                    else
                        row.setType(PocketAccounterGeneral.EXPENSE);
                    row.setDate(cal);
                    RootCategory rootCategory = new RootCategory();
                    if (row.getType() == PocketAccounterGeneral.INCOME)
                        rootCategory.setName(context.getResources().getString(R.string.debt_borrow_given_recking));
                    else
                        rootCategory.setName(context.getResources().getString(R.string.debt_borrow_taken_recking));
                    row.setCategory(rootCategory);
                    row.setSubCategory(null);
                    row.setCurrency(debt_borrow.get(i).getCurrency());
                    row.setAmount(recking.getAmount());
                    result.add(row);
                }
            }
        }
        //acumulate credits data
        ArrayList<CreditDetials> temp_credit = PocketAccounter.financeManager.getCredits();
        ArrayList<CreditDetials> credit = new ArrayList<>();
        for (int i = 0; i < temp_credit.size(); i++) {
            if (!temp_credit.get(i).isKey_for_include()) continue;
            for (int j = 0; j < temp_credit.get(i).getReckings().size(); j++) {
                ReckingCredit recking = temp_credit.get(i).getReckings().get(j);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(recking.getPayDate());
                if (cal.compareTo(begin) >= 0 && cal.compareTo(end) <= 0
                        && account.getId().matches(recking.getAccountId())
                        && temp_credit.get(i).getValyute_currency().getId().matches(currency.getId())) {
                    credit.add(temp_credit.get(i));
                    break;
                }
            }
        }
        for (int i = 0; i < credit.size(); i++) {
            for (int j = 0; j < credit.get(i).getReckings().size(); j++) {
                ReckingCredit recking = credit.get(i).getReckings().get(j);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(recking.getPayDate());
                if (cal.compareTo(begin) >= 0 && cal.compareTo(end) <= 0) {
                    AccountDataRow row = new AccountDataRow();
                    row.setType(PocketAccounterGeneral.EXPENSE);
                    row.setDate(cal);
                    RootCategory rootCategory = new RootCategory();
                    rootCategory.setName(credit.get(i).getCredit_name());
                    row.setCategory(rootCategory);
                    row.setSubCategory(null);
                    row.setCurrency(credit.get(i).getValyute_currency());
                    row.setAmount(recking.getAmount());
                    result.add(row);
                }
            }
        }
        return result;
    }
}
