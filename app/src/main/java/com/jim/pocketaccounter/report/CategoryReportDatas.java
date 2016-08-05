package com.jim.pocketaccounter.report;

import android.content.Context;
import android.util.Log;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.debt.DebtBorrow;
import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.finance.SubCategory;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CategoryReportDatas {
    private Calendar begin, end;
    private ArrayList<FinanceRecord> periodDatas = new ArrayList<FinanceRecord>();
    private ArrayList<DebtBorrow> debtBorrows;
    private Context context;
    public CategoryReportDatas(Context context, Calendar begin, Calendar end) {
        this.context = context;
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
        for (int i=0; i< PocketAccounter.financeManager.getRecords().size(); i++) {
            if (begin.compareTo(PocketAccounter.financeManager.getRecords().get(i).getDate())<=0 &&
                    end.compareTo(PocketAccounter.financeManager.getRecords().get(i).getDate())>=0)
                periodDatas.add(PocketAccounter.financeManager.getRecords().get(i));
        }
    }
    public CategoryReportDatas(Context context) {
        this.context = context;
        for (int i=0; i< PocketAccounter.financeManager.getRecords().size(); i++)
            periodDatas.add(PocketAccounter.financeManager.getRecords().get(i));
    }
    private ArrayList<CategoryDataRow> makeWholeReport() {
        ArrayList<CategoryDataRow> result  = new ArrayList<CategoryDataRow>();
        //income expanses begin
        for (int i=0; i<periodDatas.size(); i++) {
            boolean categoryFound = false;
            int foundCategoryPosition = 0;
            for (int j=0; j<result.size(); j++) {
                if (result.get(j).getCategory().getId().matches(periodDatas.get(i).getCategory().getId())) {
                    categoryFound = true;
                    foundCategoryPosition = j;
                    break;
                }
            }
            if (categoryFound) {
                CategoryDataRow foundCategory = result.get(foundCategoryPosition);
                if (periodDatas.get(i).getSubCategory() == null) {
                    boolean nullSubcatFound = false;
                    int nullSubcatPosition = 0;
                    for (int j = 0; j < foundCategory.getSubCats().size(); j++) {
                        if (foundCategory.getSubCats().get(j).getSubCategory().getId().matches(context.getResources().getString(R.string.no_category))) {
                            nullSubcatPosition = j;
                            nullSubcatFound = true;
                            break;
                        }
                    }
                    if (nullSubcatFound)
                        foundCategory.getSubCats().get(nullSubcatPosition).setAmount(foundCategory.getSubCats().get(nullSubcatPosition).getAmount()+PocketAccounterGeneral.getCost(periodDatas.get(i)));
                    else {
                        SubCategoryWitAmount newSubCategoryWithAmount = new SubCategoryWitAmount();
                        SubCategory noSubCategory = new SubCategory();
                        noSubCategory.setId(context.getResources().getString(R.string.no_category));
                        newSubCategoryWithAmount.setSubCategory(noSubCategory);
                        newSubCategoryWithAmount.setAmount(PocketAccounterGeneral.getCost(periodDatas.get(i)));
                        foundCategory.getSubCats().add(newSubCategoryWithAmount);
                    }
                }
                else {
                    boolean subcatFound = false;
                    int foundSubcatPosition = 0;
                    for (int j=0; j<foundCategory.getSubCats().size(); j++) {
                        if (foundCategory.getSubCats().get(j).getSubCategory().getId().matches(periodDatas.get(i).getSubCategory().getId())) {
                            subcatFound = true;
                            foundSubcatPosition = j;
                            break;
                        }
                    }
                    if (subcatFound) {
                        foundCategory.getSubCats().get(foundSubcatPosition).setAmount(foundCategory.getSubCats().get(foundSubcatPosition).getAmount()+PocketAccounterGeneral.getCost(periodDatas.get(i)));
                    }
                    else {
                        SubCategoryWitAmount newSubCategoryWithAmount = new SubCategoryWitAmount();
                        newSubCategoryWithAmount.setSubCategory(periodDatas.get(i).getSubCategory());
                        newSubCategoryWithAmount.setAmount(PocketAccounterGeneral.getCost(periodDatas.get(i)));
                        foundCategory.getSubCats().add(newSubCategoryWithAmount);
                    }
                }
                double amount = 0.0;
                for (int j=0; j<foundCategory.getSubCats().size(); j++)
                    amount = amount + foundCategory.getSubCats().get(j).getAmount();
                foundCategory.setTotalAmount(amount);
            }
            else {
                CategoryDataRow newCategoryDataRow = new CategoryDataRow();
                newCategoryDataRow.setCategory(periodDatas.get(i).getCategory());
                newCategoryDataRow.setTotalAmount(PocketAccounterGeneral.getCost(periodDatas.get(i)));
                SubCategoryWitAmount newSubCategoryWithAmount = new SubCategoryWitAmount();
                if (periodDatas.get(i).getSubCategory() == null) {
                    SubCategory noSubCategory = new SubCategory();
                    noSubCategory.setId(context.getResources().getString(R.string.no_category));
                    newSubCategoryWithAmount.setSubCategory(noSubCategory);
                    newSubCategoryWithAmount.setAmount(PocketAccounterGeneral.getCost(periodDatas.get(i)));
                }
                else {
                    newSubCategoryWithAmount.setSubCategory(periodDatas.get(i).getSubCategory());
                    newSubCategoryWithAmount.setAmount(PocketAccounterGeneral.getCost(periodDatas.get(i)));
                }
                newCategoryDataRow.getSubCats().add(newSubCategoryWithAmount);
                newCategoryDataRow.setTotalAmount(PocketAccounterGeneral.getCost(periodDatas.get(i)));
                result.add(newCategoryDataRow);
            }
        }
        //end income expanses
        //credit begin
        double creditTotalPaid = 0.0;
        ArrayList<CreditDetials> credits = new ArrayList<CreditDetials>();
        for (int i=0; i<PocketAccounter.financeManager.getCredits().size(); i++) {
            if (PocketAccounter.financeManager.getCredits().get(i).isKey_for_include())
                credits.add(PocketAccounter.financeManager.getCredits().get(i));
        }
        for (int i=0; i<credits.size(); i++) {
            for (int j=0; j<credits.get(i).getReckings().size(); j++) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(credits.get(i).getReckings().get(j).getPayDate());
                if (cal.compareTo(begin)>=0 && cal.compareTo(end)<=0)
                    creditTotalPaid = creditTotalPaid + PocketAccounterGeneral.getCost(cal, credits.get(i).getValyute_currency(), credits.get(i).getReckings().get(j).getAmount());
            }
            if (creditTotalPaid != 0) {
                CategoryDataRow creditDataRow = new CategoryDataRow();
                RootCategory creditCategory = new RootCategory();
                creditCategory.setType(PocketAccounterGeneral.EXPENSE);
                creditCategory.setName(credits.get(i).getCredit_name());
                creditDataRow.setCategory(creditCategory);
                creditDataRow.setTotalAmount(creditTotalPaid);
                result.add(creditDataRow);
            }
            creditTotalPaid = 0.0;
        }
        //credit end
        //debt borrows begin
        ArrayList<DebtBorrow> debtBorrows = new ArrayList<DebtBorrow>();
        for (int i=0; i<PocketAccounter.financeManager.getDebtBorrows().size(); i++) {
            if (!PocketAccounter.financeManager.getDebtBorrows().get(i).isCalculate()) continue;
            if (begin.compareTo(PocketAccounter.financeManager.getDebtBorrows().get(i).getTakenDate())<=0 &&
                    end.compareTo(PocketAccounter.financeManager.getDebtBorrows().get(i).getTakenDate())>=0) {
                debtBorrows.add(PocketAccounter.financeManager.getDebtBorrows().get(i));
            }
        }
        for (int i=0; i<debtBorrows.size(); i++) {
            RootCategory category = new RootCategory();
            if (debtBorrows.get(i).getType() == DebtBorrow.BORROW) {
                category.setType(PocketAccounterGeneral.EXPENSE);
                category.setName(context.getResources().getString(R.string.borrow_statistics));
            } else {
                category.setType(PocketAccounterGeneral.INCOME);
                category.setName(context.getResources().getString(R.string.debt_statistics));
            }
            CategoryDataRow categoryDataRow = new CategoryDataRow();
            categoryDataRow.setTotalAmount(PocketAccounterGeneral.getCost(debtBorrows.get(i).getTakenDate(), debtBorrows.get(i).getCurrency(), debtBorrows.get(i).getAmount()));
            categoryDataRow.setCategory(category);
            result.add(categoryDataRow);
        }
        debtBorrows.clear();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        for (int i=0; i<PocketAccounter.financeManager.getDebtBorrows().size(); i++) {
            if (!PocketAccounter.financeManager.getDebtBorrows().get(i).isCalculate()) continue;
            for (int j=0; j<PocketAccounter.financeManager.getDebtBorrows().get(i).getReckings().size(); j++) {
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(format.parse(PocketAccounter.financeManager.getDebtBorrows().get(i).getReckings().get(j).getPayDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (begin.compareTo(cal)<=0 && end.compareTo(cal)>=0) {
                    debtBorrows.add(PocketAccounter.financeManager.getDebtBorrows().get(i));
                    break;
                }
            }
        }
        for (int i=0; i<debtBorrows.size(); i++) {
            RootCategory category = new RootCategory();
            double totalAmount = 0.0;
            for (int j=0; j<debtBorrows.get(i).getReckings().size(); j++) {
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(format.parse(debtBorrows.get(i).getReckings().get(j).getPayDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (begin.compareTo(cal)<=0 && end.compareTo(cal) >= 0) {
                    totalAmount = totalAmount + PocketAccounterGeneral.getCost(cal, debtBorrows.get(i).getCurrency(), debtBorrows.get(i).getReckings().get(j).getAmount());
                }
            }
            if (debtBorrows.get(i).getType() == DebtBorrow.BORROW) {
                category.setName(context.getResources().getString(R.string.borrow_recking_statistics));
                category.setType(PocketAccounterGeneral.INCOME);
            }
            else {
                category.setName(context.getResources().getString(R.string.debt_recking_statistics));
                category.setType(PocketAccounterGeneral.EXPENSE);
            }
            CategoryDataRow categoryDataRow = new CategoryDataRow();
            categoryDataRow.setCategory(category);
            categoryDataRow.setTotalAmount(totalAmount);
            result.add(categoryDataRow);
        }
        //debt borrows end
        return result;
    }
    public ArrayList<CategoryDataRow> makeExpanseReport() {
        ArrayList<CategoryDataRow> result = new ArrayList<>();
        ArrayList<CategoryDataRow> temp = makeWholeReport();
        for (int i=0; i<temp.size(); i++) {
            Log.d("sss", ""+temp.get(i).getCategory().getName());
            if (temp.get(i).getCategory().getType() == PocketAccounterGeneral.EXPENSE)
                result.add(temp.get(i));
        }
        return result;
    }
    public ArrayList<CategoryDataRow> makeIncomeReport() {
        ArrayList<CategoryDataRow> result = new ArrayList<>();
        ArrayList<CategoryDataRow> temp = makeWholeReport();
        for (int i=0; i<temp.size(); i++) {
            if (temp.get(i).getCategory().getType() == PocketAccounterGeneral.INCOME)
                result.add(temp.get(i));
        }
        return result;
    }
}