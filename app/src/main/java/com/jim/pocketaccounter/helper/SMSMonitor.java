package com.jim.pocketaccounter.helper;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.SmsMessage;
import android.util.Log;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.FinanceManager;
import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.finance.SubCategory;
import com.jim.pocketaccounter.widget.WidgetKeys;
import com.jim.pocketaccounter.widget.WidgetProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class SMSMonitor extends BroadcastReceiver {
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() == null) return;
        FinanceManager manager = new FinanceManager(context);
        ArrayList<SmsParseObject> objects = manager.getSmsObjects();
        if (intent != null && intent.getAction() != null &&
                ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");

            SmsMessage[] messages = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = intent.getExtras().getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i], format);
                }
                else {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
                }
                for (int j=0; j<objects.size(); j++) {
                    if (messages[i].getDisplayOriginatingAddress().contains(objects.get(j).getNumber())) {
                        String messageBody = messages[i].getMessageBody();
                        String amount = "";
                        RootCategory category = null;
                        Account account;
                        Currency currency ;
                        if (objects.get(j).getType() == PocketAccounterGeneral.SMS_ONLY_INCOME) {
                            String sms_income_id = context.getResources().getString(R.string.sms_parse_income_id);
                            boolean catFound = false;
                            for (int k=0; k<manager.getCategories().size(); k++) {
                                if (sms_income_id.matches(manager.getCategories().get(k).getId())) {
                                    category = manager.getCategories().get(k);
                                    catFound = true;
                                    break;
                                }
                            }
                            if (!catFound) {
                                category = new RootCategory();
                                category.setSubCategories(new ArrayList<SubCategory>());
                                category.setName(context.getResources().getString(R.string.sms_parse_income));
                                category.setType(PocketAccounterGeneral.INCOME);
                                category.setId(sms_income_id);
                                category.setIcon("icons_20");
                                manager.getCategories().add(category);
                                manager.saveCategories();
                            }

                        }
                        if (objects.get(j).getType() == PocketAccounterGeneral.SMS_ONLY_EXPENSE) {
                            String sms_expense_id = context.getResources().getString(R.string.sms_parse_expense_id);
                            boolean catFound = false;
                            for (int k=0; k<manager.getCategories().size(); k++) {
                                if (sms_expense_id.matches(manager.getCategories().get(k).getId())) {
                                    category = manager.getCategories().get(k);
                                    catFound = true;
                                    break;
                                }
                            }
                            if (!catFound) {
                                category = new RootCategory();
                                category.setSubCategories(new ArrayList<SubCategory>());
                                category.setName(context.getResources().getString(R.string.sms_parse_expense));
                                category.setType(PocketAccounterGeneral.EXPENSE);
                                category.setId(sms_expense_id);
                                category.setIcon("icons_21");
                                manager.getCategories().add(category);
                                manager.saveCategories();
                            }
                        }
                        if (objects.get(j).getType() == PocketAccounterGeneral.SMS_BOTH) {
                            boolean catFound = false;
                            String[] incomes = objects.get(j).getIncomeWords().split(",");
                            for (int k=0; k < incomes.length; k++) {
                                if (messageBody.contains(incomes[k])) {
                                    catFound = true;
                                    String sms_income_id = context.getResources().getString(R.string.sms_parse_income_id);
                                    boolean incFound = false;
                                    for (int l=0; l<manager.getCategories().size(); l++) {
                                        if (sms_income_id.matches(manager.getCategories().get(l).getId())) {
                                            category = manager.getCategories().get(l);
                                            incFound = true;
                                            break;
                                        }
                                    }
                                    if (!incFound) {
                                        category = new RootCategory();
                                        category.setSubCategories(new ArrayList<SubCategory>());
                                        category.setName(context.getResources().getString(R.string.sms_parse_income));
                                        category.setType(PocketAccounterGeneral.INCOME);
                                        category.setId(sms_income_id);
                                        category.setIcon("icons_20");
                                        manager.getCategories().add(category);
                                        manager.saveCategories();
                                    }
                                    break;
                                }
                            }
                            if (!catFound) {
                                String[] expenses = objects.get(j).getExpenseWords().split(",");
                                for (int k=0; k < expenses.length; k++) {
                                    if (messageBody.contains(expenses[k])) {
                                        catFound = true;
                                        String sms_expense_id = context.getResources().getString(R.string.sms_parse_expense_id);
                                        boolean expFound = false;
                                        for (int l=0; l<manager.getCategories().size(); l++) {
                                            if (sms_expense_id.matches(manager.getCategories().get(l).getId())) {
                                                category = manager.getCategories().get(l);
                                                expFound = true;
                                                break;
                                            }
                                        }
                                        if (!expFound) {
                                            category = new RootCategory();
                                            category.setSubCategories(new ArrayList<SubCategory>());
                                            category.setName(context.getResources().getString(R.string.sms_parse_expense));
                                            category.setType(PocketAccounterGeneral.EXPENSE);
                                            category.setId(sms_expense_id);
                                            category.setIcon("icons_21");
                                            manager.getCategories().add(category);
                                            manager.saveCategories();
                                        }
                                    }
                                }
                            }
                            if (!catFound) return;
                        }
                        boolean amountFound = false;
                        String[] amounts = objects.get(j).getAmountWords().split(",");
                        for (int k=0; k<amounts.length; k++) {
                            if (messageBody.contains(amounts[k])) {
                                amountFound = true;
                                int pos = messageBody.lastIndexOf(amounts[k]);
                                while(!isNumber(messageBody.charAt(pos))) {
                                    pos++;
                                }

                                while (isNumber(messageBody.charAt(pos))) {
                                    amount += messageBody.charAt(pos);
                                    pos++;
                                }
                            }
                            if (!amountFound) return;
                        }
                        currency = objects.get(i).getCurrency();
                        account = objects.get(i).getAccount();
                        double sum = parseDouble(amount);
                        FinanceRecord record = new FinanceRecord();
                        record.setDate(Calendar.getInstance());
                        record.setCategory(category);
                        record.setSubCategory(null);
                        record.setCurrency(currency);
                        record.setAccount(account);
                        record.setRecordId("record_"+UUID.randomUUID().toString());
                        record.setAmount(sum);
                        if (PocketAccounter.financeManager != null) {
                            PocketAccounter.financeManager.getRecords().add(record);
                            PocketAccounter.financeManager.saveRecords();
                        } else
                        {
                            manager.getRecords().add(record);
                            manager.saveRecords();
                        }
                        //TODO
                        SharedPreferences sPref;
                        sPref = context.getSharedPreferences("infoFirst", MODE_PRIVATE);
                        int t=sPref.getInt(WidgetKeys.SPREF_WIDGET_ID,-1);
                        if(t>=0){
                            if(AppWidgetManager.INVALID_APPWIDGET_ID!=t)
                                WidgetProvider.updateWidget(context, AppWidgetManager.getInstance(context),
                                        t);
                        }
                    }
                }
            }
        }
        abortBroadcast();
    }
    private double parseDouble(String amount) {
        if (amount.indexOf('.') == -1 && amount.indexOf(',') == -1) return Double.parseDouble(amount);
        boolean dot = false;
        String result = "";
        for (int i=0; i<amount.length(); i++) {
            if ((amount.charAt(i) == '.' || amount.charAt(i) == ',') && !dot) {
                result = result + amount.charAt(i);
                dot = true;
                continue;
            }
            if (amount.charAt(i) != '.' && amount.charAt(i) != ',')
                result = result + amount.charAt(i);
            if (dot && (amount.charAt(i) == '.' || amount.charAt(i) == ','))
                break;
        }
        if (result.indexOf(',') != -1)
            result = result.substring(0, result.indexOf(',')) + "." + result.substring(result.indexOf(',')+1, result.length());
        return Double.parseDouble(result);
    }
    private boolean isNumber(char ch) {
        char[] numbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', ','};
        for (int i=0; i<numbers.length; i++) {
            if (numbers[i] == ch) {
                return true;
            }
        }
        return false;
    }
}
