package com.jim.pocketaccounter.widget;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.FinanceManager;
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

public class CalcActivity extends AppCompatActivity implements View.OnClickListener{
    
        private TextView tvRecordEditDisplay;
        private ImageView ivRecordEditCategory, ivRecordEditSubCategory;
        private Spinner spRecordEdit, spToolbar;
        private SubCategory subCategory;
        private FinanceManager financeManager;
        private Currency currency;
        private Account account;
        private int[] numericButtons = {R.id.rlZero, R.id.rlOne, R.id.rlTwo, R.id.rlThree, R.id.rlFour, R.id.rlFive, R.id.rlSix, R.id.rlSeven, R.id.rlEight, R.id.rlNine};
        private int[] operatorButtons = {R.id.rlPlusSign, R.id.rlMinusSign, R.id.rlMultipleSign, R.id.rlDivideSign};
        private boolean lastNumeric = true;
        private boolean stateError;
        private boolean lastDot;
        private boolean lastOperator;
        private DecimalFormat decimalFormat = null;
        private RelativeLayout rlCategory, rlSubCategory, imOKBut;
        private Animation buttonClick;
        private LinearLayout llRoot;
        private RootCategory category;
        private int WIDGET_ID;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_calc);
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
            otherSymbols.setDecimalSeparator('.');
            otherSymbols.setGroupingSeparator('.');
            decimalFormat = new DecimalFormat("0.00##", otherSymbols);
            financeManager=new FinanceManager(this);
            buttonClick = AnimationUtils.loadAnimation(this, R.anim.button_click);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            toolbar.setTitle("");
            toolbar.setSubtitle("");
            imOKBut = (RelativeLayout) findViewById(R.id.imOKBut);
            imOKBut.setOnClickListener(this);
            spRecordEdit = (Spinner) findViewById(R.id.spRecordEdit);
            spToolbar = (Spinner) toolbar.findViewById(R.id.spToolbar);
            spToolbar.setVisibility(View.VISIBLE);
            llRoot = (LinearLayout) findViewById(R.id.llRoot);
            RecordAccountAdapter accountAdapter = new RecordAccountAdapter(CalcActivity.this, financeManager.getAccounts());
            spToolbar.setAdapter(accountAdapter);
            spToolbar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    account = financeManager.getAccounts().get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            final String[] currencies = new String[financeManager.getCurrencies().size()];
            for (int i = 0; i < financeManager.getCurrencies().size(); i++)
                currencies[i] = financeManager.getCurrencies().get(i).getAbbr();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CalcActivity.this, R.layout.spinner_single_item, currencies);
            spRecordEdit.setAdapter(adapter);
            for (int i = 0; i < financeManager.getCurrencies().size(); i++) {
                if (financeManager.getCurrencies().get(i).getId().matches(financeManager.getMainCurrency().getId())) {
                    spRecordEdit.setSelection(i);
                    break;
                }
            }
            spRecordEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currency = financeManager.getCurrencies().get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            ivRecordEditCategory = (ImageView) findViewById(R.id.ivRecordEditCategory);
            ivRecordEditSubCategory = (ImageView) findViewById(R.id.ivRecordEditSubCategory);
            tvRecordEditDisplay = (TextView) findViewById(R.id.tvRecordEditDisplay);
            rlCategory = (RelativeLayout) findViewById(R.id.rlCategory);
            rlCategory.setOnClickListener(this);
            rlSubCategory = (RelativeLayout) findViewById(R.id.rlSubcategory);
            rlSubCategory.setOnClickListener(this);
            setNumericOnClickListener(llRoot);
            setOperatorOnClickListener(llRoot);
            category = new RootCategory();
            String catId = getIntent().getStringExtra(WidgetKeys.KEY_FOR_INTENT_ID);
            WIDGET_ID = getIntent().getIntExtra(WidgetKeys.ACTION_WIDGET_RECEIVER_CHANGE_DIAGRAM_ID,  AppWidgetManager.INVALID_APPWIDGET_ID);
            for (int i=0; i<financeManager.getCategories().size(); i++) {
                if (financeManager.getCategories().get(i).getId().matches(catId)) {
                    category = financeManager.getCategories().get(i);
                    break;
                }
            }
            if (category != null) {
                ivRecordEditSubCategory.setImageResource(R.drawable.category_not_selected);
                int resId = getResources().getIdentifier(category.getIcon(), "drawable", getPackageName());
                ivRecordEditCategory.setImageResource(resId);
            }
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
            view.findViewById(R.id.rlBackspaceSign).setOnClickListener(new View.OnClickListener() {
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
                    final Dialog dialog = new Dialog(this);
                    View dialogView = getLayoutInflater().inflate(R.layout.category_choose_list, null);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(dialogView);
                    ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
                    String expanse = getResources().getString(R.string.expanse);
                    String income = getResources().getString(R.string.income);
                    String[] items = new String[2];
                    items[0] = expanse;
                    items[1] = income;
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(CalcActivity.this, android.R.layout.simple_list_item_1, items);
                    lvCategoryChoose.setAdapter(adapter);
                    lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ArrayList<RootCategory> categories = new ArrayList<RootCategory>();
                            if (position == 0) {
                                for (int i = 0; i < financeManager.getCategories().size(); i++) {
                                    if (financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.EXPENSE)
                                        categories.add(financeManager.getCategories().get(i));
                                }
                            } else {
                                for (int i = 0; i < financeManager.getCategories().size(); i++) {
                                    if (financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.INCOME)
                                        categories.add(financeManager.getCategories().get(i));
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
                case R.id.imOKBut:
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
                FinanceRecord newRecord = new FinanceRecord();
                newRecord.setCategory(category);
                newRecord.setSubCategory(subCategory);
                newRecord.setDate(Calendar.getInstance());
                newRecord.setAccount(account);
                newRecord.setCurrency(currency);
                newRecord.setAmount(Double.parseDouble(tvRecordEditDisplay.getText().toString()));
                newRecord.setRecordId("record_" + UUID.randomUUID().toString());
                financeManager.getRecords().add(newRecord);
                financeManager.saveRecords();
                if(AppWidgetManager.INVALID_APPWIDGET_ID!=WIDGET_ID)
                    WidgetProvider.updateWidget(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),
                            WIDGET_ID);

                finish();
            }
        }

        private void openCategoryDialog(final ArrayList<RootCategory> categories) {
            final Dialog dialog = new Dialog(this);
            View dialogView = getLayoutInflater().inflate(R.layout.category_choose_list, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView);
            ListView lvCategoryChoose = (ListView) dialogView.findViewById(R.id.lvCategoryChoose);
            RecordCategoryAdapter adapter = new RecordCategoryAdapter(CalcActivity.this, categories);
            lvCategoryChoose.setAdapter(adapter);
            lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int resId = getResources().getIdentifier(categories.get(position).getIcon(), "drawable", getPackageName());
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
            final Dialog dialog = new Dialog(this);
            View dialogView = getLayoutInflater().inflate(R.layout.category_choose_list, null);
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
            RecordSubCategoryAdapter adapter = new RecordSubCategoryAdapter(CalcActivity.this, subCategories);
            lvCategoryChoose.setAdapter(adapter);
            lvCategoryChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (subCategories.get(position).getId().matches(getResources().getString(R.string.no_category)))
                        subCategory = null;
                    else
                        subCategory = subCategories.get(position);
                    int resId = getResources().getIdentifier(subCategories.get(position).getIcon(), "drawable", getPackageName());
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
