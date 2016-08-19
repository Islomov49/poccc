package com.jim.pocketaccounter;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.credit.ReckingCredit;
import com.jim.pocketaccounter.debt.DebtBorrow;
import com.jim.pocketaccounter.debt.Recking;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.AccountManagementObject;
import com.jim.pocketaccounter.finance.CurrencyAmount;
import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.finance.SubCategory;
import com.jim.pocketaccounter.finance.TransferAccountAdapter;
import com.jim.pocketaccounter.helper.FloatingActionButton;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;
import com.jim.pocketaccounter.photocalc.PhotoDetails;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

@SuppressLint("ValidFragment")
public class AccountManagementFragment extends Fragment {
    private RecyclerView rvAccountsManagement;
    private Calendar date;
    private FloatingActionButton fabAccManAdd;
    private Account first, second;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account_management_layout, container, false);
        fabAccManAdd = (FloatingActionButton) rootView.findViewById(R.id.fabAccManAdd);
              Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_compare_arrows_white_24dp);
        Bitmap add = Bitmap.createScaledBitmap(temp, (int)getResources().getDimension(R.dimen.twentyfour_dp), (int)getResources().getDimension(R.dimen.twentyfour_dp), true);
        fabAccManAdd.setImageBitmap(add);
        fabAccManAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTransferDialog();
            }
        });
        rvAccountsManagement = (RecyclerView) rootView.findViewById(R.id.rvAccountsManagement);
        rvAccountsManagement.setLayoutManager(new LinearLayoutManager(getContext()));
        PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel).setVisibility(View.GONE);
        PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight).setVisibility(View.GONE);
        ((PocketAccounter)getContext()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_button);
        PocketAccounter.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PocketAccounter) getContext()).initialize(date);
                ((PocketAccounter) getContext()).getSupportFragmentManager().popBackStack();
            }
        });
        PocketAccounter.toolbar.findViewById(R.id.spToolbar).setVisibility(View.GONE);
        PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight).setVisibility(View.GONE);
        PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel).setVisibility(View.GONE);
        PocketAccounter.toolbar.setTitle(getResources().getString(R.string.accounts));
        refreshList(date);
        return rootView;
    }
    private void openTransferDialog() {
        if(PocketAccounter.financeManager.getAccounts().size()<2){
            Toast.makeText(getActivity(), R.string.sys_not_found_accanount,Toast.LENGTH_SHORT).show();
            return;
        }
        final Dialog dialog=new Dialog(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.account_transfer_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        final EditText etAccountEditName = (EditText) dialogView.findViewById(R.id.etAccountEditName);
        final Spinner spTransferFirst = (Spinner) dialogView.findViewById(R.id.spTransferFirst);
        final TransferAccountAdapter firstAdapter = new TransferAccountAdapter(getContext(), PocketAccounter.financeManager.getAccounts());
        final Spinner spAccManDialog = (Spinner) dialogView.findViewById(R.id.spAccManDialog);
        String[] currs = new String[PocketAccounter.financeManager.getCurrencies().size()];
        for (int i=0; i<PocketAccounter.financeManager.getCurrencies().size(); i++) {
            currs[i] = PocketAccounter.financeManager.getCurrencies().get(i).getAbbr();
        }
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, currs);
        spAccManDialog.setAdapter(currencyAdapter);
        final Spinner spTransferSecond = (Spinner) dialogView.findViewById(R.id.spTransferSecond);
        spTransferFirst.setAdapter(firstAdapter);
        final ArrayList<Account> temp = new ArrayList<Account>();
        spTransferFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long pos) {

                first = PocketAccounter.financeManager.getAccounts().get(i);
                Log.d("sss", first.getName());
                temp.clear();
                for (int j=0; j<PocketAccounter.financeManager.getAccounts().size(); j++) {
                    if (!PocketAccounter.financeManager.getAccounts().get(j).getId().matches(first.getId()))
                        temp.add(PocketAccounter.financeManager.getAccounts().get(j));
                }
                TransferAccountAdapter secondAdapter = new TransferAccountAdapter(getContext(), temp);
                spTransferSecond.setAdapter(secondAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spTransferSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                second = temp.get(i);
                Log.d("sss", second.getName());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ImageView ivYes = (ImageView) dialogView.findViewById(R.id.ivAccountManSave);
        ivYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etAccountEditName.getText().toString().matches("")) {
                    etAccountEditName.setError(getResources().getString(R.string.enter_amount));
                    return;
                }

                Log.d("sss", first.getName()+" "+second.getName());
                ////
                if (first != null && first.isLimited()) {
                    double limit =  first.getLimitSum();
                    double accounted = PocketAccounterGeneral.getCost(date, first.getStartMoneyCurrency(), first.getLimitCurrency(), first.getAmount());

                    for (int i = 0; i < PocketAccounter.financeManager.getRecords().size(); i++) {

                        FinanceRecord tempac=PocketAccounter.financeManager.getRecords().get(i);
                        if (tempac.getAccount().getId().matches(first.getId()) ) {
                            if (tempac.getCategory().getType() == PocketAccounterGeneral.INCOME)
                                accounted = accounted + PocketAccounterGeneral.getCost(tempac.getDate(),tempac.getCurrency(),first.getLimitCurrency(),tempac.getAmount());
                            else
                                accounted = accounted - PocketAccounterGeneral.getCost(tempac.getDate(),tempac.getCurrency(),first.getLimitCurrency(),tempac.getAmount());
                        }
                    }
                    for (DebtBorrow debtBorrow : PocketAccounter.financeManager.getDebtBorrows()) {
                        if (debtBorrow.isCalculate()) {
                            if (debtBorrow.getAccount().getId().matches(first.getId())) {
                                if (debtBorrow.getType() == DebtBorrow.BORROW) {
                                    accounted = accounted - PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(),first.getLimitCurrency(), debtBorrow.getAmount());
                                } else {
                                    accounted = accounted + PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(),first.getLimitCurrency(), debtBorrow.getAmount());
                                }
                                for (Recking recking : debtBorrow.getReckings()) {
                                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                    Calendar cal = Calendar.getInstance();
                                    try {
                                        cal.setTime(format.parse(recking.getPayDate()));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (debtBorrow.getType() == DebtBorrow.DEBT) {
                                        accounted = accounted - PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(),first.getLimitCurrency(), recking.getAmount());
                                    } else {
                                        accounted = accounted + PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(),first.getLimitCurrency(), recking.getAmount());
                                    }
                                }
                            } else {
                                for (Recking recking : debtBorrow.getReckings()) {
                                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                    Calendar cal = Calendar.getInstance();
                                    if (recking.getAccountId().matches(first.getId())) {
                                        try {
                                            cal.setTime(format.parse(recking.getPayDate()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if (debtBorrow.getType() == DebtBorrow.BORROW) {
                                            accounted = accounted + PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(),first.getLimitCurrency(), recking.getAmount());
                                        } else {
                                            accounted = accounted - PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(),first.getLimitCurrency(), recking.getAmount());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //TODO editda tekwir ozini hisoblamaslini
                    for (CreditDetials creditDetials : PocketAccounter.financeManager.getCredits()) {
                        if (creditDetials.isKey_for_include()) {
                            for (ReckingCredit reckingCredit : creditDetials.getReckings()) {
                                if (reckingCredit.getAccountId().matches(first.getId())) {
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTimeInMillis(reckingCredit.getPayDate());
                                    accounted = accounted - PocketAccounterGeneral.getCost(cal, creditDetials.getValyute_currency(),first.getLimitCurrency(), reckingCredit.getAmount());
                                }
                            }
                        }
                    }
                    accounted = accounted - PocketAccounterGeneral.getCost(date, PocketAccounter.financeManager.getCurrencies().get(spAccManDialog.getSelectedItemPosition()), first.getLimitCurrency() ,Double.parseDouble(etAccountEditName.getText().toString()));
                    if (-limit > accounted) {
                        Toast.makeText(getContext(), R.string.limit_exceed, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //
                }

                boolean toCategoryFound = false;
                boolean fromCategoryFound = false;
                String toTransferId = getResources().getString(R.string.to_transfer_id);
                String fromTransferId = getResources().getString(R.string.from_transfer_id);
                for (int i=0; i<PocketAccounter.financeManager.getCategories().size(); i++) {
                    if (toCategoryFound && fromCategoryFound) break;
                    if (PocketAccounter.financeManager.getCategories().get(i).getId().matches(toTransferId) && !toCategoryFound) {
                        toCategoryFound = true;
                        continue;
                    }
                    if (PocketAccounter.financeManager.getCategories().get(i).getId().matches(fromTransferId) && !fromCategoryFound) {
                        fromCategoryFound = true;
                        continue;
                    }
                }
                if (!toCategoryFound) {
                    RootCategory category = new RootCategory();
                    category.setType(PocketAccounterGeneral.INCOME);
                    category.setName(getResources().getString(R.string.to_transfer));
                    category.setId(getResources().getString(R.string.to_transfer_id));
                    category.setSubCategories(new ArrayList<SubCategory>());
                    category.setIcon(getResources().getString(R.string.transfer_icon));
                    PocketAccounter.financeManager.getCategories().add(category);
                    PocketAccounter.financeManager.saveCategories();
                }
                if (!fromCategoryFound) {
                    RootCategory category = new RootCategory();
                    category.setType(PocketAccounterGeneral.EXPENSE);
                    category.setName(getResources().getString(R.string.from_transfer));
                    category.setId(getResources().getString(R.string.from_transfer_id));
                    category.setIcon(getResources().getString(R.string.transfer_icon));
                    category.setSubCategories(new ArrayList<SubCategory>());
                    PocketAccounter.financeManager.getCategories().add(category);
                    PocketAccounter.financeManager.saveCategories();
                }
                FinanceRecord expRecord = new FinanceRecord();
                expRecord.setAccount(first);
                expRecord.setDate(date);
                expRecord.setCurrency(PocketAccounter.financeManager.getCurrencies().get(spAccManDialog.getSelectedItemPosition()));
                expRecord.setRecordId(UUID.randomUUID().toString());
                expRecord.setAllTickets(new ArrayList<PhotoDetails>());
                expRecord.setComment("");
                expRecord.setAmount(Double.parseDouble(etAccountEditName.getText().toString()));
                for (int i=0; i<PocketAccounter.financeManager.getCategories().size(); i++) {
                    if (PocketAccounter.financeManager.getCategories().get(i).getId().matches(getResources().getString(R.string.from_transfer_id))) {
                        expRecord.setCategory(PocketAccounter.financeManager.getCategories().get(i));
                        break;
                    }
                }
                expRecord.setSubCategory(null);
                PocketAccounter.financeManager.getRecords().add(expRecord);
                FinanceRecord incRecord = new FinanceRecord();
                incRecord.setAccount(second);
                incRecord.setDate(date);
                incRecord.setCurrency(PocketAccounter.financeManager.getCurrencies().get(spAccManDialog.getSelectedItemPosition()));
                incRecord.setRecordId(UUID.randomUUID().toString());
                incRecord.setAllTickets(new ArrayList<PhotoDetails>());
                incRecord.setComment("");
                incRecord.setAmount(Double.parseDouble(etAccountEditName.getText().toString()));
                for (int i=0; i<PocketAccounter.financeManager.getCategories().size(); i++) {
                    if (PocketAccounter.financeManager.getCategories().get(i).getId().matches(getResources().getString(R.string.to_transfer_id))) {
                        incRecord.setCategory(PocketAccounter.financeManager.getCategories().get(i));
                        break;
                    }
                }
                incRecord.setSubCategory(null);
                PocketAccounter.financeManager.getRecords().add(incRecord);
                refreshList(date);
                PocketAccounter.financeManager.saveRecords();
                dialog.dismiss();
            }
        });
        ImageView ivClose = (ImageView) dialogView.findViewById(R.id.ivAccountManClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public AccountManagementFragment(Calendar date) {
        this.date = (Calendar) date.clone();
    }
    private void refreshList(Calendar date) {
        date = (Calendar) this.date.clone();
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 59);
        ArrayList<FinanceRecord> records = new ArrayList<>();

        for (int i = 0; i < PocketAccounter.financeManager.getRecords().size(); i++) {
            if (PocketAccounter.financeManager.getRecords().get(i).getDate().compareTo(date) <= 0)
                records.add(PocketAccounter.financeManager.getRecords().get(i));
        }
        ArrayList<DebtBorrow> temp = new ArrayList<>();
        for (int i = 0; i < PocketAccounter.financeManager.getDebtBorrows().size(); i++) {
            if (PocketAccounter.financeManager.getDebtBorrows().get(i).isCalculate()) {
                temp.add(PocketAccounter.financeManager.getDebtBorrows().get(i));
            }
        }
        ArrayList<DebtBorrow> debtBorrows = new ArrayList<>();
        for (int i=0; i<temp.size(); i++) {
            boolean include = false;
            include = (temp.get(i).getTakenDate().compareTo(date) <= 0);
            if (include)
                debtBorrows.add(temp.get(i));
        }
        ArrayList<CreditDetials> temp_credits = new ArrayList<>();
        for (int i = 0; i < PocketAccounter.financeManager.getCredits().size(); i++) {
            if (PocketAccounter.financeManager.getCredits().get(i).isKey_for_include()) {
                temp_credits.add(PocketAccounter.financeManager.getCredits().get(i));
            }
        }
        ArrayList<CreditDetials> credits = new ArrayList<>();
        for (int i=0; i < temp_credits.size(); i++) {
            for (int j=0; j<temp_credits.get(i).getReckings().size(); j++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(temp_credits.get(i).getReckings().get(j).getPayDate());
                if (calendar.compareTo(date) <= 0) {
                    credits.add(temp_credits.get(i));
                    break;
                }
            }
        }
        ArrayList<AccountManagementObject> accountManagementObjects = new ArrayList<>();
        for (int i=0; i<records.size(); i++) {
            boolean accFound = false;
            int pos = 0;
            for (int j=0; j<accountManagementObjects.size(); j++) {
                if (accountManagementObjects.get(j).getAccount().getId().matches(records.get(i).getAccount().getId())) {
                    accFound = true;
                    pos = j;
                    break;
                }
            }
            if (accFound) {
                boolean currFound = false;
                int curPos = 0;
                for (int j=0; j<accountManagementObjects.get(pos).getCurAmounts().size(); j++) {
                    if (accountManagementObjects.get(pos).getCurAmounts().get(j).getCurrency().getId().matches(records.get(i).getCurrency().getId())) {
                        currFound = true;
                        curPos = j;
                        break;

                    }
                }
                if (currFound) {
                    double amount = 0.0;
                    if (records.get(i).getCategory().getType() == PocketAccounterGeneral.INCOME)
                        amount = accountManagementObjects.get(pos).getCurAmounts().get(curPos).getAmount()+records.get(i).getAmount();
                    else
                        amount = accountManagementObjects.get(pos).getCurAmounts().get(curPos).getAmount()-records.get(i).getAmount();
                    accountManagementObjects.get(pos).getCurAmounts().get(curPos).setAmount(amount);
                }
                else {
                    CurrencyAmount currencyAmount = new CurrencyAmount();
                    currencyAmount.setCurrency(records.get(i).getCurrency());
                    if (records.get(i).getCategory().getType() == PocketAccounterGeneral.INCOME)
                        currencyAmount.setAmount(records.get(i).getAmount());
                    else
                        currencyAmount.setAmount(-records.get(i).getAmount());
                    accountManagementObjects.get(pos).getCurAmounts().add(currencyAmount);
                }
            }
            else {
                AccountManagementObject accountManagementObject = new AccountManagementObject();
                accountManagementObject.setAccount(records.get(i).getAccount());
                CurrencyAmount currencyAmount = new CurrencyAmount();
                currencyAmount.setCurrency(records.get(i).getCurrency());
                if (records.get(i).getCategory().getType() == PocketAccounterGeneral.INCOME)
                    currencyAmount.setAmount(records.get(i).getAmount());
                else
                    currencyAmount.setAmount(-records.get(i).getAmount());
                ArrayList<CurrencyAmount> currencyAmounts = new ArrayList<>();
                currencyAmounts.add(currencyAmount);
                accountManagementObject.setCurAmounts(currencyAmounts);
                accountManagementObjects.add(accountManagementObject);
            }
        }
        for (int i=0; i<debtBorrows.size(); i++) {
            boolean accFound = false;
            int pos = 0;
            for (int j=0; j<accountManagementObjects.size(); j++) {
                if (accountManagementObjects.get(j).getAccount().getId().matches(debtBorrows.get(i).getAccount().getId())) {
                    accFound = true;
                    pos = j;
                    break;

                }
            }
            if (accFound) {
                boolean currFound = false;
                int curPos = 0;
                for (int j=0; j<accountManagementObjects.get(pos).getCurAmounts().size(); j++) {
                    if (accountManagementObjects.get(pos).getCurAmounts().get(j).getCurrency().getId().matches(debtBorrows.get(i).getCurrency().getId())) {
                        currFound = true;
                        curPos = j;
                        break;

                    }
                }
                if (currFound) {
                    double amount = 0.0;
                    if (debtBorrows.get(i).getType() == DebtBorrow.BORROW)
                        amount = accountManagementObjects.get(pos).getCurAmounts().get(curPos).getAmount() - debtBorrows.get(i).getAmount();
                    else
                        amount = accountManagementObjects.get(pos).getCurAmounts().get(curPos).getAmount() + debtBorrows.get(i).getAmount();
                    accountManagementObjects.get(pos).getCurAmounts().get(curPos).setAmount(amount);
                }
                else {
                    CurrencyAmount currencyAmount = new CurrencyAmount();
                    currencyAmount.setCurrency(debtBorrows.get(i).getCurrency());
                    if (debtBorrows.get(i).getType() == DebtBorrow.BORROW)
                        currencyAmount.setAmount(-debtBorrows.get(i).getAmount());
                    else
                        currencyAmount.setAmount(debtBorrows.get(i).getAmount());
                    accountManagementObjects.get(pos).getCurAmounts().add(currencyAmount);
                }
            }
            else {
                AccountManagementObject accountManagementObject = new AccountManagementObject();
                accountManagementObject.setAccount(debtBorrows.get(i).getAccount());
                CurrencyAmount currencyAmount = new CurrencyAmount();
                currencyAmount.setCurrency(debtBorrows.get(i).getCurrency());
                if (debtBorrows.get(i).getType() == DebtBorrow.BORROW)
                    currencyAmount.setAmount(-debtBorrows.get(i).getAmount());
                else
                    currencyAmount.setAmount(debtBorrows.get(i).getAmount());
                ArrayList<CurrencyAmount> currencyAmounts = new ArrayList<>();
                currencyAmounts.add(currencyAmount);
                accountManagementObject.setCurAmounts(currencyAmounts);
                accountManagementObjects.add(accountManagementObject);
            }
            for (int j=0; j<debtBorrows.get(i).getReckings().size(); j++) {
                Recking recking = debtBorrows.get(i).getReckings().get(j);
                boolean accountFound = false;
                int accountPos = 0;
                for (int k=0; k < accountManagementObjects.size(); k++) {
                    if (accountManagementObjects.get(k).getAccount().getId().matches(recking.getAccountId())) {
                        accountFound = true;
                        accountPos = k;
                        break;
                    }
                }
                if (accountFound) {
                    boolean currFound = false;
                    int curPos = 0;
                    for (int k=0; k<accountManagementObjects.get(accountPos).getCurAmounts().size(); k++) {
                        if (accountManagementObjects.get(accountPos).getCurAmounts().get(k).getCurrency().getId().matches(debtBorrows.get(i).getCurrency().getId())) {
                            currFound = true;
                            curPos = k;
                            break;
                        }
                    }
                    if (currFound) {
                        double amount = 0.0;
                        if (debtBorrows.get(i).getType() == DebtBorrow.BORROW)
                            amount = accountManagementObjects.get(accountPos).getCurAmounts().get(curPos).getAmount() + recking.getAmount();
                        else
                            amount = accountManagementObjects.get(accountPos).getCurAmounts().get(curPos).getAmount() - recking.getAmount();
                        accountManagementObjects.get(accountPos).getCurAmounts().get(curPos).setAmount(amount);
                    }
                    else {
                        CurrencyAmount currencyAmount = new CurrencyAmount();
                        currencyAmount.setCurrency(debtBorrows.get(i).getCurrency());
                        if (debtBorrows.get(i).getType() == DebtBorrow.BORROW)
                            currencyAmount.setAmount(recking.getAmount());
                        else
                            currencyAmount.setAmount(-recking.getAmount());
                        accountManagementObjects.get(accountPos).getCurAmounts().add(currencyAmount);
                    }
                }
                else {
                    AccountManagementObject accountManagementObject = new AccountManagementObject();
                    Account account = null;
                    for (int k = 0; k<PocketAccounter.financeManager.getAccounts().size(); k++) {
                        if (recking.getAccountId().matches(PocketAccounter.financeManager.getAccounts().get(k).getId())) {
                            account = PocketAccounter.financeManager.getAccounts().get(k);
                            break;
                        }
                    }
                    accountManagementObject.setAccount(account);
                    CurrencyAmount currencyAmount = new CurrencyAmount();
                    currencyAmount.setCurrency(debtBorrows.get(i).getCurrency());
                    if (debtBorrows.get(i).getType() == DebtBorrow.BORROW)
                        currencyAmount.setAmount(recking.getAmount());
                    else
                        currencyAmount.setAmount(-recking.getAmount());
                    ArrayList<CurrencyAmount> currencyAmounts = new ArrayList<>();
                    currencyAmounts.add(currencyAmount);
                    accountManagementObject.setCurAmounts(currencyAmounts);
                    accountManagementObjects.add(accountManagementObject);
                }
            }
        }
        for (int i=0; i<credits.size(); i++) {
            for (int j=0; j<credits.get(i).getReckings().size(); j++) {
                ReckingCredit recking = credits.get(i).getReckings().get(j);
                boolean accFound = false;
                int pos = 0;
                for (int k=0; k<accountManagementObjects.size(); k++) {
                    if (accountManagementObjects.get(k).getAccount().getId().matches(recking.getAccountId())) {
                        accFound = true;
                        pos = k;
                        break;

                    }
                }
                if (accFound) {
                    boolean currFound = false;
                    int curPos = 0;
                    for (int k=0; k<accountManagementObjects.get(pos).getCurAmounts().size(); k++) {
                        if (accountManagementObjects.get(pos).getCurAmounts().get(k).getCurrency().getId().matches(credits.get(i).getValyute_currency().getId())) {
                            currFound = true;
                            curPos = k;
                            break;

                        }
                    }
                    if (currFound) {
                        double amount = accountManagementObjects.get(pos).getCurAmounts().get(curPos).getAmount()-recking.getAmount();
                        accountManagementObjects.get(pos).getCurAmounts().get(curPos).setAmount(amount);
                    }
                    else {
                        CurrencyAmount currencyAmount = new CurrencyAmount();
                        currencyAmount.setCurrency(credits.get(i).getValyute_currency());
                        currencyAmount.setAmount(-recking.getAmount());
                        accountManagementObjects.get(pos).getCurAmounts().add(currencyAmount);
                    }
                }
                else {
                    AccountManagementObject accountManagementObject = new AccountManagementObject();
                    for (int k=0; k<PocketAccounter.financeManager.getAccounts().size(); k++) {
                        if (PocketAccounter.financeManager.getAccounts().get(k).getId().matches(recking.getAccountId())) {
                            accountManagementObject.setAccount(PocketAccounter.financeManager.getAccounts().get(k));
                            break;
                        }
                    }
                    CurrencyAmount currencyAmount = new CurrencyAmount();
                    currencyAmount.setCurrency(credits.get(i).getValyute_currency());
                    currencyAmount.setAmount(-recking.getAmount());
                    ArrayList<CurrencyAmount> currencyAmounts = new ArrayList<>();
                    currencyAmounts.add(currencyAmount);
                    accountManagementObject.setCurAmounts(currencyAmounts);
                    accountManagementObjects.add(accountManagementObject);

                }

            }
        }
        for (int i=0; i<PocketAccounter.financeManager.getAccounts().size(); i++) {
            boolean accFount = false;
            int pos = 0;
            for (int j=0; j<accountManagementObjects.size(); j++) {
                if (accountManagementObjects.get(j).getAccount().getId().matches(PocketAccounter.financeManager.getAccounts().get(i).getId())) {
                    accFount = true;
                    pos = j;
                    break;
                }
            }
            if (accFount) {
                boolean currFound = false;
                int curPos = 0;
                for (int k=0; k<accountManagementObjects.get(pos).getCurAmounts().size(); k++) {
                    if (accountManagementObjects.get(pos).getCurAmounts().get(k).getCurrency().getId().matches(PocketAccounter.financeManager.getAccounts().get(i).getStartMoneyCurrency().getId())) {
                        currFound = true;
                        curPos = k;
                        break;

                    }
                }
                if (currFound) {
                    double amount = accountManagementObjects.get(pos).getCurAmounts().get(curPos).getAmount()-PocketAccounter.financeManager.getAccounts().get(i).getAmount();
                    accountManagementObjects.get(pos).getCurAmounts().get(curPos).setAmount(amount);
                }
                else {
                    CurrencyAmount currencyAmount = new CurrencyAmount();
                    currencyAmount.setCurrency(PocketAccounter.financeManager.getAccounts().get(i).getStartMoneyCurrency());
                    currencyAmount.setAmount(PocketAccounter.financeManager.getAccounts().get(i).getAmount());
                    accountManagementObjects.get(pos).getCurAmounts().add(currencyAmount);
                }
            }
            else {
                AccountManagementObject accountManagementObject = new AccountManagementObject();
                accountManagementObject.setAccount(PocketAccounter.financeManager.getAccounts().get(i));
                CurrencyAmount currencyAmount = new CurrencyAmount();
                currencyAmount.setCurrency(PocketAccounter.financeManager.getAccounts().get(i).getStartMoneyCurrency());
                currencyAmount.setAmount(PocketAccounter.financeManager.getAccounts().get(i).getAmount());
                ArrayList<CurrencyAmount> currencyAmounts = new ArrayList<>();
                currencyAmounts.add(currencyAmount);
                accountManagementObject.setCurAmounts(currencyAmounts);
                accountManagementObjects.add(accountManagementObject);
            }
        }
        MyAdapter myAdapter = new MyAdapter(accountManagementObjects);
        rvAccountsManagement.setAdapter(myAdapter);
    }
    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<AccountManagementObject> accountManagementObjects;

        public MyAdapter(ArrayList<AccountManagementObject> accountManagementObjects) {
            this.accountManagementObjects = accountManagementObjects;
        }
        public int getItemCount() {
            return accountManagementObjects.size();
        }
        public void onBindViewHolder(final ViewHolder view, final int position) {
            view.ivAccontManagement.setImageResource(accountManagementObjects.get(position).getAccount().getIcon());
            view.tvAccManAccName.setText(accountManagementObjects.get(position).getAccount().getName());
            TextView tv = new TextView(getContext());
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, (int)getContext().getResources().getDimension(R.dimen.ten_dp), 0, (int)getContext().getResources().getDimension(R.dimen.ten_dp));
            tv.setTextSize(getResources().getDimension(R.dimen.six_dp));
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.toolbar_text_color));
            tv.setGravity(Gravity.RIGHT);
            tv.setLayoutParams(lp);
            DecimalFormat format = new DecimalFormat("0.00##");
            String text = "";
            for (int i=0; i<accountManagementObjects.get(position).getCurAmounts().size(); i++) {
                 text = text + accountManagementObjects.get(position).getCurAmounts().get(i).getCurrency().getName()+": "+
                        format.format(accountManagementObjects.get(position).getCurAmounts().get(i).getAmount())+
                        accountManagementObjects.get(position).getCurAmounts().get(i).getCurrency().getAbbr()+"\n";
            }
            tv.setText(text);
            view.llAccManContainer.addView(tv);
        }
        public ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_management_list_item, parent, false);
            return new ViewHolder(view);
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAccManAccName;
        LinearLayout llAccManContainer;
        ImageView ivAccontManagement;
        public ViewHolder(View view) {
            super(view);
            tvAccManAccName = (TextView) view.findViewById(R.id.tvAccManAccName);
            llAccManContainer = (LinearLayout) view.findViewById(R.id.llAccManContainer);
            ivAccontManagement = (ImageView) view.findViewById(R.id.ivAccontManagement);
        }
    }
}
