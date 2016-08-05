package com.jim.pocketaccounter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.finance.RecordAccountAdapter;
import com.jim.pocketaccounter.finance.RecordCategoryAdapter;
import com.jim.pocketaccounter.finance.RecordSubCategoryAdapter;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.finance.SubCategory;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

@SuppressLint("ValidFragment")
public class RecordEditFragment extends Fragment implements OnClickListener {
    private TextView tvRecordEditDisplay;
    private ImageView ivToolbarMostRight, ivRecordEditCategory, ivRecordEditSubCategory, ivToolbaExcel;
    private Spinner spRecordEdit, spToolbar;
    private RootCategory category;
    private SubCategory subCategory;
    private FinanceRecord record;
    private Currency currency;
    private Account account;
    private Calendar date;
    private int parent;
    private int[] numericButtons = {R.id.rlZero, R.id.rlOne, R.id.rlTwo, R.id.rlThree, R.id.rlFour, R.id.rlFive, R.id.rlSix, R.id.rlSeven, R.id.rlEight, R.id.rlNine};
    private int[] operatorButtons = {R.id.rlPlusSign, R.id.rlMinusSign, R.id.rlMultipleSign, R.id.rlDivideSign};
    private boolean lastNumeric = true;
    private boolean stateError;
    private boolean lastDot;
    private boolean lastOperator;
    private DecimalFormat decimalFormat = null;
    private RelativeLayout rlCategory, rlSubCategory;
    private Animation buttonClick;

    @SuppressLint("ValidFragment")
    public RecordEditFragment(RootCategory category, Calendar date, FinanceRecord record, int parent) {
        this.parent = parent;
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("0.00##", otherSymbols);
        if (category != null) {
            for (int i = 0; i < PocketAccounter.financeManager.getCategories().size(); i++) {
                if (category.getId().matches(PocketAccounter.financeManager.getCategories().get(i).getId()))
                    this.category = PocketAccounter.financeManager.getCategories().get(i);
            }
            this.subCategory = null;
        } else {
            this.category = record.getCategory();
            this.subCategory = record.getSubCategory();
        }
        this.date = (Calendar) date.clone();
        this.record = record;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record_edit, container, false);
        buttonClick = AnimationUtils.loadAnimation(getContext(), R.anim.button_click);
        ((PocketAccounter) getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((PocketAccounter) getContext()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_button);
        PocketAccounter.toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (parent == PocketAccounterGeneral.MAIN) {
                    ((PocketAccounter) getContext()).initialize(date);
                    ((PocketAccounter) getContext()).getSupportFragmentManager().popBackStack();
                } else {
                    ((PocketAccounter) getContext()).getSupportFragmentManager().popBackStack();
                    ((PocketAccounter) getContext()).replaceFragment(new RecordDetailFragment(date));
                }
            }
        });
        PocketAccounter.toolbar.setTitle("");
        PocketAccounter.toolbar.setSubtitle("");
        spRecordEdit = (Spinner) rootView.findViewById(R.id.spRecordEdit);
        spToolbar = (Spinner) PocketAccounter.toolbar.findViewById(R.id.spToolbar);
        spToolbar.setVisibility(View.VISIBLE);
        RecordAccountAdapter accountAdapter = new RecordAccountAdapter(getContext(), PocketAccounter.financeManager.getAccounts());
        spToolbar.setAdapter(accountAdapter);
        spToolbar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                account = PocketAccounter.financeManager.getAccounts().get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        final String[] currencies = new String[PocketAccounter.financeManager.getCurrencies().size()];
        for (int i = 0; i < PocketAccounter.financeManager.getCurrencies().size(); i++)
            currencies[i] = PocketAccounter.financeManager.getCurrencies().get(i).getAbbr();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_single_item, currencies);
        spRecordEdit.setAdapter(adapter);
        for (int i = 0; i < PocketAccounter.financeManager.getCurrencies().size(); i++) {
            if (PocketAccounter.financeManager.getCurrencies().get(i).getId().matches(PocketAccounter.financeManager.getMainCurrency().getId())) {
                spRecordEdit.setSelection(i);
                break;
            }
        }
        spRecordEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currency = PocketAccounter.financeManager.getCurrencies().get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ivToolbarMostRight = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
        ivToolbarMostRight.setVisibility(View.VISIBLE);
        ivToolbarMostRight.setImageResource(R.drawable.check_sign);
        ivToolbarMostRight.setOnClickListener(this);
        ivToolbaExcel = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel);
        ivToolbaExcel.setVisibility(View.GONE);
        ivRecordEditCategory = (ImageView) rootView.findViewById(R.id.ivRecordEditCategory);
        ivRecordEditSubCategory = (ImageView) rootView.findViewById(R.id.ivRecordEditSubCategory);
        tvRecordEditDisplay = (TextView) rootView.findViewById(R.id.tvRecordEditDisplay);
        rlCategory = (RelativeLayout) rootView.findViewById(R.id.rlCategory);
        rlCategory.setOnClickListener(this);
        rlSubCategory = (RelativeLayout) rootView.findViewById(R.id.rlSubcategory);
        rlSubCategory.setOnClickListener(this);
        setNumericOnClickListener(rootView);
        setOperatorOnClickListener(rootView);
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("0.00", otherSymbols);
        if (category != null) {
            ivRecordEditSubCategory.setImageResource(R.drawable.category_not_selected);
            int resId = getResources().getIdentifier(category.getIcon(), "drawable", getContext().getPackageName());

            ivRecordEditCategory.setImageResource(resId);
        }
        if (record != null) {
            int resId = getResources().getIdentifier(record.getCategory().getIcon(), "drawable", getContext().getPackageName());
            ivRecordEditCategory.setImageResource(resId);
            if (record.getSubCategory() != null) {
                resId = getResources().getIdentifier(record.getSubCategory().getIcon(), "drawable", getContext().getPackageName());
                ivRecordEditSubCategory.setImageResource(resId);
            }
            else
                ivRecordEditSubCategory.setImageResource(R.drawable.category_not_selected);
            tvRecordEditDisplay.setText(decimalFormat.format(record.getAmount()));
            for (int i = 0; i < PocketAccounter.financeManager.getCurrencies().size(); i++) {
                if (PocketAccounter.financeManager.getCurrencies().get(i).getId().matches(record.getCurrency().getId())) {
                    spRecordEdit.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < PocketAccounter.financeManager.getAccounts().size(); i++) {
                if (PocketAccounter.financeManager.getAccounts().get(i).getId().matches(record.getAccount().getId())) {
                    spToolbar.setSelection(i);
                    break;
                }
            }
        }
        return rootView;
    }

    private void setNumericOnClickListener(View view) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                if (tvRecordEditDisplay.getText().toString().length() >= 14) return;
                String text = "";
                switch (v.getId()) {
                    case R.id.rlZero:
                        text = "0";
                        break;
                    case R.id.rlOne:
                        text = "1";
                        break;
                    case R.id.rlTwo:
                        text = "2";
                        break;
                    case R.id.rlThree:
                        text = "3";
                        break;
                    case R.id.rlFour:
                        text = "4";
                        break;
                    case R.id.rlFive:
                        text = "5";
                        break;
                    case R.id.rlSix:
                        text = "6";
                        break;
                    case R.id.rlSeven:
                        text = "7";
                        break;
                    case R.id.rlEight:
                        text = "8";
                        break;
                    case R.id.rlNine:
                        text = "9";
                        break;
                }
                if (stateError) {
                    tvRecordEditDisplay.setText(text);
                    stateError = false;
                } else {
                    String displayText = tvRecordEditDisplay.getText().toString();
                    if (displayText.matches("") || displayText.matches("0"))
                        tvRecordEditDisplay.setText(text);
                    else
                        tvRecordEditDisplay.append(text);
                }
                lastNumeric = true;
                lastOperator = false;
                lastDot = false;
            }
        };
        for (int id:numericButtons)
            view.findViewById(id).setOnClickListener(listener);
    }

    private void setOperatorOnClickListener(View view) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                if (tvRecordEditDisplay.getText().toString().length() >= 14) return;
                String text = "";
                switch (v.getId()) {
                    case R.id.rlPlusSign:
                        text = "+";
                        break;
                    case R.id.rlMinusSign:
                        text = "-";
                        break;
                    case R.id.rlDivideSign:
                        text = "/";
                        break;
                    case R.id.rlMultipleSign:
                        text = "*";
                        break;
                }
                if (lastNumeric && !stateError) {
                    tvRecordEditDisplay.append(text);
                    lastNumeric = false;
                    lastDot = false;
                    lastOperator = true;
                }
                if (lastOperator) {
                    String dispText = tvRecordEditDisplay.getText().toString();
                    dispText = dispText.substring(0, dispText.length() - 1) + text;
                    tvRecordEditDisplay.setText(dispText);
                }
            }
        };
        for (int id : operatorButtons)
            view.findViewById(id).setOnClickListener(listener);
        view.findViewById(R.id.rlDot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                if (tvRecordEditDisplay.getText().toString().length() >= 14) return;
                if (lastNumeric && !stateError && !lastDot && !lastOperator) {
                    tvRecordEditDisplay.append(".");
                    lastNumeric = false;
                    lastDot = true;
                }
            }
        });
        view.findViewById(R.id.rlBackspaceSign).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                String dispText = tvRecordEditDisplay.getText().toString();
                char lastChar = dispText.charAt(dispText.length() - 1);
                char[] opers = {'+', '-', '*', '/'};
                for (int i = 0; i < opers.length; i++) {
                    if (opers[i] == lastChar) {
                        lastOperator = false;
                        lastNumeric = true;
                    }
                }
                if (lastChar == '.') {
                    lastDot = false;
                    lastNumeric = true;
                }
                if (tvRecordEditDisplay.getText().toString().length() == 1)
                    tvRecordEditDisplay.setText("0");
                else {
                    dispText = dispText.substring(0, dispText.length() - 1);
                    tvRecordEditDisplay.setText(dispText);
                }
            }
        });
        view.findViewById(R.id.rlCancelSign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                tvRecordEditDisplay.setText("0");
                lastNumeric = false;
                stateError = false;
                lastDot = false;
                lastOperator = false;
            }
        });
        view.findViewById(R.id.rlEqualSign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                onEqual();
            }
        });
    }

    private void onEqual() {
        if (lastNumeric && !stateError) {
            String txt = tvRecordEditDisplay.getText().toString();
            Expression expression = new ExpressionBuilder(txt).build();
            try {
                double result = expression.evaluate();
                tvRecordEditDisplay.setText(decimalFormat.format(result));
            } catch (ArithmeticException ex) {
                tvRecordEditDisplay.setText(getResources().getString(R.string.error));
                stateError = true;
                lastNumeric = false;
            }
        }
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(buttonClick);
        switch (view.getId()) {
            case R.id.rlCategory:
                final Dialog dialog = new Dialog(getActivity());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.category_choose_list, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(dialogView);
                ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
                String expanse = getResources().getString(R.string.expanse);
                String income = getResources().getString(R.string.income);
                String[] items = new String[2];
                items[0] = expanse;
                items[1] = income;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
                lvCategoryChoose.setAdapter(adapter);
                lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ArrayList<RootCategory> categories = new ArrayList<RootCategory>();
                        if (position == 0) {
                            for (int i = 0; i < PocketAccounter.financeManager.getCategories().size(); i++) {
                                if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.EXPENSE)
                                    categories.add(PocketAccounter.financeManager.getCategories().get(i));
                            }
                        } else {
                            for (int i = 0; i < PocketAccounter.financeManager.getCategories().size(); i++) {
                                if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.INCOME)
                                    categories.add(PocketAccounter.financeManager.getCategories().get(i));
                            }
                        }
                        dialog.dismiss();
                        openCategoryDialog(categories);
                    }
                });
                dialog.show();
                break;
            case R.id.rlSubcategory:
                openSubCategoryDialog();
                break;
            case R.id.ivToolbarMostRight:
                if (lastDot || lastOperator) {
                    return;
                }
                createNewRecord();

                break;
        }
    }

    private void createNewRecord() {
        onEqual();
        String value = tvRecordEditDisplay.getText().toString();
        if (value.length() > 14)
            value = value.substring(0, 14);
        if (Double.parseDouble(value) != 0) {
            if (record != null) {
                record.setCategory(category);
                record.setSubCategory(subCategory);
                record.setDate(date);
                record.setAccount(account);
                record.setCurrency(currency);
                record.setAmount(Double.parseDouble(tvRecordEditDisplay.getText().toString()));
            } else {
                FinanceRecord newRecord = new FinanceRecord();
                newRecord.setCategory(category);
                newRecord.setSubCategory(subCategory);
                newRecord.setDate(date);
                newRecord.setAccount(account);
                newRecord.setCurrency(currency);
                newRecord.setAmount(Double.parseDouble(tvRecordEditDisplay.getText().toString()));
                newRecord.setRecordId("record_" + UUID.randomUUID().toString());
                PocketAccounter.financeManager.getRecords().add(newRecord);
            }
        }
        if (parent != PocketAccounterGeneral.MAIN) {
            if (((PocketAccounter) getContext()).getSupportFragmentManager().getBackStackEntryCount() != 0) {
                FragmentManager fm = ((PocketAccounter) getContext()).getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); i++)
                    fm.popBackStack();
                ((PocketAccounter) getContext()).replaceFragment(new RecordDetailFragment(date));
            }
        }
        else {
            ((PocketAccounter) getContext()).initialize(date);
            ((PocketAccounter) getContext()).getSupportFragmentManager().popBackStack();
        }
    }

    private void openCategoryDialog(final ArrayList<RootCategory> categories) {
        final Dialog dialog = new Dialog(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.category_choose_list, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
        RecordCategoryAdapter adapter = new RecordCategoryAdapter(getContext(), categories);
        lvCategoryChoose.setAdapter(adapter);
        lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int resId = getResources().getIdentifier(categories.get(position).getIcon(), "drawable", getContext().getPackageName());
                ivRecordEditCategory.setImageResource(resId);
                ivRecordEditSubCategory.setImageResource(R.drawable.category_not_selected);
                category = categories.get(position);
                dialog.dismiss();
            }
        });
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        dialog.getWindow().setLayout(8 * width / 9, ActionBarOverlayLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    private void openSubCategoryDialog() {
        final Dialog dialog = new Dialog(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.category_choose_list, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
        final ArrayList<SubCategory> subCategories = new ArrayList<SubCategory>();
        SubCategory noSubCategory = new SubCategory();
        noSubCategory.setIcon("category_not_selected");
        noSubCategory.setName(getResources().getString(R.string.no_category_name));
        noSubCategory.setId(getResources().getString(R.string.no_category));
        subCategories.add(noSubCategory);
        for (int i = 0; i < category.getSubCategories().size(); i++)
            subCategories.add(category.getSubCategories().get(i));
        RecordSubCategoryAdapter adapter = new RecordSubCategoryAdapter(getContext(), subCategories);
        lvCategoryChoose.setAdapter(adapter);
        lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (subCategories.get(position).getId().matches(getResources().getString(R.string.no_category)))
                    subCategory = null;
                else
                    subCategory = subCategories.get(position);
                int resId = getResources().getIdentifier(subCategories.get(position).getIcon(), "drawable", getContext().getPackageName());
                ivRecordEditSubCategory.setImageResource(resId);
                dialog.dismiss();
            }
        });
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        dialog.getWindow().setLayout(8 * width / 9, ActionBarOverlayLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }
}