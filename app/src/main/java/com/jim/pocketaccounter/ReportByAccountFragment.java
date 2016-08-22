package com.jim.pocketaccounter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.FinanceManager;
import com.jim.pocketaccounter.report.AccountDataRow;
import com.jim.pocketaccounter.report.FilterDialog;
import com.jim.pocketaccounter.report.FilterSelectable;
import com.jim.pocketaccounter.report.ReportByAccount;
import com.jim.pocketaccounter.report.TableView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ReportByAccountFragment extends Fragment implements View.OnClickListener {
    private ImageView ivToolbarMostRight, ivToolbarExcel;
    private Spinner spToolbar;
    private String[] titles;
    private int pos_account = 0, pos_currency = 0;
    private Calendar begin, end;
    private SimpleDateFormat simpleDateFormat;
    private DecimalFormat decimalFormat;
    private Account account;
    private Currency currency;
    private TableView tbReportByAccount;
    private FinanceManager financeManager;
    private FilterDialog filterDialog;
    private ReportByAccount reportByAccount;
    private ArrayList<AccountDataRow> sortReportByAccount;
    private LinearLayout linLayReportByAccountInfo;
    private TextView tvReportByAccountNoDatas;
    private TextView tvReportbyAccountTotalIncome, tvReportbyAccountTotalExpanse, tvReportbyAccountTotalProfit, tvReportbyAccountAverageProfit;
    private final int PERMISSION_READ_STORAGE = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.report_by_account, container, false);

        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(PocketAccounter.keyboardVisible){
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);}
            }
        },100);

        rootView.setBackgroundColor(Color.WHITE);
        linLayReportByAccountInfo = (LinearLayout) rootView.findViewById(R.id.linLayReportByAccountInfo);
        tvReportbyAccountTotalIncome = (TextView) rootView.findViewById(R.id.tvReportbyAccountTotalIncome);
        tvReportbyAccountTotalExpanse = (TextView) rootView.findViewById(R.id.tvReportbyAccountTotalExpanse);
        tvReportbyAccountTotalProfit = (TextView) rootView.findViewById(R.id.tvReportbyAccountTotalProfit);
        tvReportbyAccountAverageProfit = (TextView) rootView.findViewById(R.id.tvReportbyAccountAverageProfit);
        ivToolbarExcel = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel);
        ivToolbarExcel.setImageDrawable(null);
        ivToolbarExcel.setImageResource(R.drawable.ic_excel);
        ivToolbarExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(((PocketAccounter) getContext()),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Permission to access the SD-CARD is required for this app to Download PDF.")
                                .setTitle("Permission required");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat.requestPermissions((PocketAccounter) getContext(),
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_READ_STORAGE);
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {
                        ActivityCompat.requestPermissions((PocketAccounter) getContext(),
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_READ_STORAGE);
                    }
                } else {
                    saveExcel();
                }

            }
        });
        ivToolbarMostRight = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
        ivToolbarMostRight.setVisibility(View.VISIBLE);
        ivToolbarMostRight.setImageResource(R.drawable.ic_filter);
        ivToolbarMostRight.setOnClickListener(this);
        PocketAccounter.toolbar.setTitle("");
        PocketAccounter.toolbar.setSubtitle("");
        filterDialog = new FilterDialog(getContext());
        begin = (Calendar) Calendar.getInstance().clone();
        end = (Calendar) Calendar.getInstance().clone();
        spToolbar = (Spinner) PocketAccounter.toolbar.findViewById(R.id.spToolbar);
        spToolbar.setVisibility(View.VISIBLE);
        final ArrayList<String> result = new ArrayList<>();
        financeManager = PocketAccounter.financeManager;
        for (int i = 0; i < financeManager.getAccounts().size(); i++) {
            for (int j = 0; j < financeManager.getCurrencies().size(); j++) {
                result.add(financeManager.getAccounts().get(i).getName() + ", " + financeManager.getCurrencies().get(j).getAbbr());
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(), R.layout.spiner_gravity_right2, result);

        spToolbar.setAdapter(arrayAdapter);
        tbReportByAccount = (TableView) rootView.findViewById(R.id.tbReportByAccount);
        tvReportByAccountNoDatas = (TextView) rootView.findViewById(R.id.tvReportByAccountNoDatas);



        spToolbar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos_account = (int) (position / financeManager.getCurrencies().size());
                pos_currency = position % financeManager.getCurrencies().size();
                account = PocketAccounter.financeManager.getAccounts().get(pos_account);
                currency = PocketAccounter.financeManager.getCurrencies().get(pos_currency);

                begin.set(Calendar.DAY_OF_YEAR, end.get(Calendar.DAY_OF_YEAR) - 2);
                begin.set(Calendar.HOUR_OF_DAY, 0);
                begin.set(Calendar.MINUTE, 0);
                begin.set(Calendar.SECOND, 0);
                begin.set(Calendar.MILLISECOND, 0);
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("report_shp",account.getName()+", "+currency.getAbbr()).apply();
                onCreateReportbyAccount(getContext(), begin, end, account, currency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        titles = rootView.getResources().getStringArray(R.array.report_by_account_titles);
        String tekwir=PreferenceManager.getDefaultSharedPreferences(getContext()).getString("report_shp","");
        if(!tekwir.matches("")){
            int position=0;
            for (String temp:result){
                if (temp.equals(tekwir)){
                    try {
                        spToolbar.setSelection(position);
                    }
                    catch (Exception o){
                        o.printStackTrace();
                    }
                    break;
                }
                position++;
            }
        }
        tbReportByAccount.setTitle(titles, true);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivToolbarMostRight:
                filterDialog.show();
                filterDialog.setOnDateSelectedListener(new FilterSelectable() {
                    @Override
                    public void onDateSelected(Calendar begin, Calendar end) {
                        onCreateReportbyAccount(getContext(), begin, end, account, currency);
                    }
                });
                break;
            case R.id.ivToolbarExcel:
                exportToExcelFile();
                break;
        }
    }

    void onCreateReportbyAccount(Context context, Calendar begin, Calendar end, Account account, Currency currency) {
        reportByAccount = new ReportByAccount(context, begin, end, account, currency);

        sortReportByAccount = reportByAccount.makeAccountReport();
        Calendar current_begin = (Calendar) Calendar.getInstance().clone(), current_end = (Calendar) Calendar.getInstance().clone();
        Collections.sort(sortReportByAccount, new MyComparator());
        long countOfDays = 0;
        if (sortReportByAccount != null && sortReportByAccount.size() >= 2) {
            current_begin = (Calendar) sortReportByAccount.get(0).getDate().clone();
            current_end = (Calendar) sortReportByAccount.get(sortReportByAccount.size() - 1).getDate().clone();
            while (current_begin.getTime().compareTo(current_end.getTime()) <= 0) {
                countOfDays++;
                current_begin.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else countOfDays = 1;
        if (sortReportByAccount.isEmpty()) {
            tbReportByAccount.setVisibility(View.GONE);
            linLayReportByAccountInfo.setVisibility(View.GONE);
            ivToolbarExcel.setVisibility(View.GONE);
            tvReportByAccountNoDatas.setVisibility(View.VISIBLE);
            return;
        } else {
            tbReportByAccount.setVisibility(View.VISIBLE);
            linLayReportByAccountInfo.setVisibility(View.VISIBLE);
            ivToolbarExcel.setVisibility(View.VISIBLE);
            tvReportByAccountNoDatas.setVisibility(View.GONE);
        }
        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        decimalFormat = new DecimalFormat("0.00##");
        double totalIncome = 0.0, totalExpanse = 0.0, totalProfit = 0.0, averageProfit = 0.0;
        String abbr = "";
        for (int i = 0; i < sortReportByAccount.size(); i++) {
            abbr = sortReportByAccount.get(i).getCurrency().getAbbr();
            if (sortReportByAccount.get(i).getType() == 0)
                totalIncome = totalIncome + sortReportByAccount.get(i).getAmount();
            else
                totalExpanse = totalExpanse + sortReportByAccount.get(i).getAmount();
        }
        tbReportByAccount.setDatas(sortReportByAccount);
        totalProfit = totalIncome - totalExpanse;
        averageProfit = totalProfit / countOfDays;
        tvReportbyAccountTotalIncome.setText(getResources().getString(R.string.report_income_expanse_total_income) + decimalFormat.format(totalIncome) + abbr);
        tvReportbyAccountTotalExpanse.setText(getResources().getString(R.string.report_income_expanse_total_expanse) + decimalFormat.format(totalExpanse) + abbr);
        tvReportbyAccountTotalProfit.setText(getResources().getString(R.string.report_income_expanse_total_profit) + decimalFormat.format(totalProfit) + abbr);
        tvReportbyAccountAverageProfit.setText(getResources().getString(R.string.report_income_expanse_aver_profit) + decimalFormat.format(averageProfit) + abbr);
    }

    class MyComparator implements Comparator<AccountDataRow> {
        @Override
        public int compare(AccountDataRow o1, AccountDataRow o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }

    private void saveExcel() {
        File direct = new File(Environment.getExternalStorageDirectory() + "/Pocket Accounter");
        if (!direct.exists()) {
            if (direct.mkdir()) {
                exportToExcelFile();
            }
        } else {
            exportToExcelFile();
        }
    }

    public void exportToExcelFile() {
        final Dialog dialog = new Dialog(getContext());
        View dialogView = ((PocketAccounter) getContext()).getLayoutInflater().inflate(R.layout.warning_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        TextView tvWarningText = (TextView) dialogView.findViewById(R.id.tvWarningText);
        tvWarningText.setText(R.string.save_excel);
        Button ok = (Button) dialogView.findViewById(R.id.btnWarningYes);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format = new SimpleDateFormat("dd_MM_yyyy");
                File sd = Environment.getExternalStorageDirectory();
                String fname = sd.getAbsolutePath() + "/" +
                        "Pocket Accounter/" +
                        "ra_" + format.format(Calendar.getInstance().getTime());
                File temp = new File(fname + ".xlsx");
                while (temp.exists()) {
                    fname = fname + "_copy";
                    temp = new File(fname);
                }
                fname = fname + ".xlsx";
                try {
                    File exlFile = new File(fname);
                    WritableWorkbook writableWorkbook = Workbook.createWorkbook(exlFile);
                    WritableSheet writableSheet = writableWorkbook.createSheet(getContext().getResources().getString(R.string.app_name), 0);
                    String[] labels = getResources().getStringArray(R.array.report_by_account_titles);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    for (int i = 0; i < labels.length; i++) {
                        Label label = new Label(i, 0, labels[i]);
                        writableSheet.addCell(label);
                    }
                    for (int i = 0; i < sortReportByAccount.size(); i++) {
                        Number type = new Number(0, i + 1, sortReportByAccount.get(i).getType());
                        Label date = new Label(1, i + 1, dateFormat.format(sortReportByAccount.get(i).getDate().getTime()));
                        Number amount = new Number(2, i + 1, sortReportByAccount.get(i).getAmount());
                        Label category = new Label(3, i + 1, sortReportByAccount.get(i).getCategory().getName());
                        writableSheet.addCell(type);
                        writableSheet.addCell(date);
                        writableSheet.addCell(amount);
                        writableSheet.addCell(category);
                    }
                    writableWorkbook.write();
                    writableWorkbook.close();
                    Toast.makeText(getContext(), fname + ": saved...", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        Button cancel = (Button) dialogView.findViewById(R.id.btnWarningNo);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        spToolbar.setVisibility(View.VISIBLE);
        ivToolbarExcel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}