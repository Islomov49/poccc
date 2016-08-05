package com.jim.pocketaccounter;

import android.app.Dialog;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.CurrencyChooseAdapter;
import com.jim.pocketaccounter.finance.CurrencyCost;
import com.jim.pocketaccounter.helper.PockerTag;

import java.util.ArrayList;
import java.util.Calendar;

public class CurrencyChooseFragment extends Fragment {
    private GridView gvCurrencyChoose;
    private ArrayList<Currency> currencies;
    private boolean[] chbs;
    private ImageView ivToolbarMostRight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currency_choose_fragment, container, false);
        PocketAccounter.toolbar.setTitle(getResources().getString(R.string.choose_currencies));
        PocketAccounter.toolbar.setSubtitle("");
        ((PocketAccounter) getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(!PocketAccounter.financeManager.getCurrencies().isEmpty());
        PocketAccounter.toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PocketAccounter) getContext()).replaceFragment(new CurrencyFragment());
            }
        });
        ivToolbarMostRight = ((ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight));
        ivToolbarMostRight.setVisibility(View.VISIBLE);
        ivToolbarMostRight.setImageResource(R.drawable.check_sign);
        ((ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel)).setVisibility(View.GONE);
        ((Spinner) PocketAccounter.toolbar.findViewById(R.id.spToolbar)).setVisibility(View.GONE);
        gvCurrencyChoose = (GridView) view.findViewById(R.id.gvCurrencyChoose);
        final String[] baseCurrencies = getResources().getStringArray(R.array.base_currencies);
        final String[] baseAbbrs = getResources().getStringArray(R.array.base_abbrs);
        final String[] currIds = getResources().getStringArray(R.array.currency_ids);
        String[] costs = getResources().getStringArray(R.array.currency_costs);
        chbs = new boolean[baseCurrencies.length];
        for (int i = 0; i < currIds.length; i++) {
            boolean found = false;
            for (int j = 0; j < PocketAccounter.financeManager.getCurrencies().size(); j++) {
                if (currIds[i].matches(PocketAccounter.financeManager.getCurrencies().get(j).getId())) {
                    found = true;
                    break;
                }
            }
            chbs[i] = found;
        }
        currencies = new ArrayList<Currency>();
        for (int i = 0; i < baseCurrencies.length; i++) {
            Currency currency = new Currency(baseCurrencies[i]);
            currency.setAbbr(baseAbbrs[i]);
            currency.setName(baseCurrencies[i]);
            currency.setId(currIds[i]);
            CurrencyCost cost = new CurrencyCost();
            cost.setCost(Double.parseDouble(costs[i]));
            cost.setDay(Calendar.getInstance());
            ArrayList<CurrencyCost> tempCost = new ArrayList<CurrencyCost>();
            tempCost.add(cost);
            currency.setCosts(tempCost);
            currencies.add(currency);
        }
        CurrencyChooseAdapter adapter = new CurrencyChooseAdapter(getActivity(), currencies, chbs);
        gvCurrencyChoose.setAdapter(adapter);
        ivToolbarMostRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = false;
                for (int i = 0; i < chbs.length; i++) {
                    if (chbs[i]) {
                        checked = true;
                        break;
                    }
                }
                if (!checked) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.curr_not_choosen), Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isCurrencyListChanged = false;
                for (int i = 0; i < PocketAccounter.financeManager.getCurrencies().size(); i++) {
                    if (isCurrencyListChanged) break;
                    for (int j = 0; j < currIds.length; j++) {
                        if (PocketAccounter.financeManager.getCurrencies().get(i).getId().matches(currIds[j]) && !chbs[j]) {
                            isCurrencyListChanged = true;
                            break;
                        }
                    }
                }
                if (isCurrencyListChanged) {
                    final Dialog dialog = new Dialog(getActivity());
                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.warning_dialog, null);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(dialogView);
                    TextView tvWarningText = (TextView) dialogView.findViewById(R.id.tvWarningText);
                    tvWarningText.setText(getResources().getString(R.string.currency_exchange_warning));
                    Button btnYes = (Button) dialogView.findViewById(R.id.btnWarningYes);
                    Button btnNo = (Button) dialogView.findViewById(R.id.btnWarningNo);
                    btnYes.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<Currency> temp = new ArrayList<Currency>();
                            for (int i = 0; i < currencies.size(); i++) {
                                if (chbs[i])
                                    temp.add(currencies.get(i));
                            }
                            for (int i = 0; i < temp.size(); i++) {
                                for (int j = 0; j < PocketAccounter.financeManager.getCurrencies().size(); j++) {
                                    if (temp.get(i).getId().matches(PocketAccounter.financeManager.getCurrencies().get(j).getId())) {
                                        temp.get(i).setCosts(PocketAccounter.financeManager.getCurrencies().get(j).getCosts());
                                    }
                                }
                            }
                            boolean foundMainCurrency = false;
                            int pos = 0;
                            for (int i = 0; i < temp.size(); i++) {
                                for (int j = 0; j < PocketAccounter.financeManager.getCurrencies().size(); j++) {
                                    if (foundMainCurrency) break;
                                    if (temp.get(i).getId().matches(PocketAccounter.financeManager.getCurrencies().get(j).getId()) &&
                                            PocketAccounter.financeManager.getCurrencies().get(j).getMain()) {
                                        pos = i;
                                        foundMainCurrency = true;
                                        break;
                                    }
                                }
                            }
                            if (foundMainCurrency)
                                temp.get(pos).setMain(true);
                            else {
                                temp.get(0).setMain(true);

                            }
                            for (int i = 0; i < temp.size(); i++) {
                                for (int j = 0; j < PocketAccounter.financeManager.getCurrencies().size(); j++) {
                                    if (temp.get(i).getId().matches(PocketAccounter.financeManager.getCurrencies().get(j).getId())) {
                                        temp.get(i).setCosts(PocketAccounter.financeManager.getCurrencies().get(j).getCosts());
                                        break;
                                    }
                                }
                            }
                            boolean isFound = false;
                            for (int i = 0; i < PocketAccounter.financeManager.getRecords().size(); i++) {
                                for (int j = 0; j < temp.size(); j++) {
                                    if (PocketAccounter.financeManager.getRecords().get(i).getCurrency().getId().matches(temp.get(j).getId())) {
                                        isFound = true;
                                        break;
                                    }
                                }
                                if (!isFound) {
                                    PocketAccounter.financeManager.getRecords().remove(i);
                                    i--;
                                }
                                isFound = false;
                            }
                            for (int i = 0; i < PocketAccounter.financeManager.getCredits().size(); i++) {
                                if (!PocketAccounter.financeManager.getCredits().get(i).isKey_for_include())
                                    continue;
                                for (int j = 0; j < temp.size(); j++) {
                                    if (PocketAccounter.financeManager.getCredits().get(i).getValyute_currency().getId().matches(temp.get(j).getId())) {
                                        isFound = true;
                                        break;
                                    }
                                }
                                if (!isFound) {
                                    PocketAccounter.financeManager.getCredits().remove(i);
                                    i--;
                                }
                                isFound = false;
                            }
                            for (int i = 0; i < PocketAccounter.financeManager.getDebtBorrows().size(); i++) {
                                if (!PocketAccounter.financeManager.getDebtBorrows().get(i).isCalculate())
                                    continue;
                                for (int j = 0; j < temp.size(); j++) {
                                    if (PocketAccounter.financeManager.getDebtBorrows().get(i).getCurrency().getId().matches(temp.get(j).getId())) {
                                        isFound = true;
                                        break;
                                    }
                                }
                                if (!isFound) {
                                    PocketAccounter.financeManager.getDebtBorrows().remove(i);
                                    i--;
                                }
                                isFound = false;
                            }
                            for (int i = 0; i < PocketAccounter.financeManager.getSmsObjects().size(); i++) {
                                for (int j = 0; j < temp.size(); j++) {
                                    if (PocketAccounter.financeManager.getSmsObjects().get(i).getCurrency().getId().matches(temp.get(j).getId())) {
                                        isFound = true;
                                        break;
                                    }
                                }
                                if (!isFound) {
                                    PocketAccounter.financeManager.getSmsObjects().remove(i);
                                    i--;
                                }
                                isFound = false;
                            }
                            PocketAccounter.financeManager.setCurrencies(temp);
                            PocketAccounter.financeManager.saveCurrencies();
                            PocketAccounter.financeManager.saveRecords();
                            PocketAccounter.financeManager.setRecords(PocketAccounter.financeManager.loadRecords());
                            PocketAccounter.financeManager.saveSmsObjects();
                            ((PocketAccounter) getActivity()).replaceFragment(new CurrencyFragment(), PockerTag.CURRENCY);
                            dialog.dismiss();
                        }
                    });
                    btnNo.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            return;
                        }
                    });
                    dialog.show();
                } else {
                    ArrayList<Currency> temp = new ArrayList<Currency>();
                    for (int i = 0; i < currencies.size(); i++) {
                        if (chbs[i])
                            temp.add(currencies.get(i));
                    }
                    boolean foundMainCurrency = false;
                    int pos = 0;
                    for (int i = 0; i < temp.size(); i++) {
                        if (foundMainCurrency) break;
                        for (int j = 0; j < PocketAccounter.financeManager.getCurrencies().size(); j++) {
                            if (temp.get(i).getId().matches(PocketAccounter.financeManager.getCurrencies().get(j).getId()) && PocketAccounter.financeManager.getCurrencies().get(j).getMain()) {
                                pos = i;
                                foundMainCurrency = true;
                                break;
                            }
                        }
                    }
                    if (foundMainCurrency)
                        temp.get(pos).setMain(true);
                    else
                        temp.get(0).setMain(true);
                    for (int i = 0; i < temp.size(); i++) {
                        for (int j = 0; j < PocketAccounter.financeManager.getCurrencies().size(); j++) {
                            if (temp.get(i).getId().matches(PocketAccounter.financeManager.getCurrencies().get(j).getId())) {
                                temp.get(i).setCosts(PocketAccounter.financeManager.getCurrencies().get(j).getCosts());
                                break;
                            }
                        }
                    }
                    PocketAccounter.financeManager.setCurrencies(temp);
                    PocketAccounter.financeManager.saveCurrencies();
                    PocketAccounter.financeManager.saveRecords();
                    PocketAccounter.financeManager.setRecords(PocketAccounter.financeManager.loadRecords());
                    ((PocketAccounter) getActivity()).replaceFragment(new CurrencyFragment(), PockerTag.CURRENCY);
                }
            }
        });
        return view;
    }
}