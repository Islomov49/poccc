package com.jim.pocketaccounter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout.LayoutParams;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.CurrencyCost;
import com.jim.pocketaccounter.finance.CurrencyExchangeAdapter;
import com.jim.pocketaccounter.finance.FinanceManager;
import com.jim.pocketaccounter.helper.PockerTag;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressLint("ValidFragment")
public class CurrencyEditFragment extends Fragment implements OnClickListener, OnItemClickListener {
	private ImageView ivExCurrencyAdd, ivExCurrencyDelete;
	private ListView lvCurrencyEditExchange;
	private Currency currency;
	private CheckBox chbCurrencyEditMainCurrency;
	private Calendar day = Calendar.getInstance();
	private int mode = PocketAccounterGeneral.NORMAL_MODE;
	private boolean[] selected;
	public CurrencyEditFragment(Currency currency) {
		this.currency = currency;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.currency_edit, container, false);
		ivExCurrencyAdd = (ImageView) rootView.findViewById(R.id.ivExCurrencyAdd);
		ivExCurrencyAdd.setOnClickListener(this);
		ivExCurrencyDelete = (ImageView) rootView.findViewById(R.id.ivExCurrencyDelete);
		ivExCurrencyDelete.setOnClickListener(this);
		lvCurrencyEditExchange = (ListView) rootView.findViewById(R.id.lvCurrencyEditExchange);
		lvCurrencyEditExchange.setOnItemClickListener(this);
		chbCurrencyEditMainCurrency = (CheckBox) rootView.findViewById(R.id.chbCurrencyEditMainCurrency);
		chbCurrencyEditMainCurrency.setChecked(currency.getMain());
		((PocketAccounter)getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((PocketAccounter)getContext()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_button);
		PocketAccounter.toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((PocketAccounter)getContext()).replaceFragment(new CurrencyFragment(), PockerTag.CURRENCY);
			}
		});
		PocketAccounter.toolbar.setTitle(currency.getName()+", "+currency.getAbbr());
		PocketAccounter.toolbar.setSubtitle(R.string.edit);
		((ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight)).setImageResource(R.drawable.check_sign);
		((ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight)).setVisibility(View.VISIBLE);
		((ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel)).setVisibility(View.GONE);
		((Spinner) PocketAccounter.toolbar.findViewById(R.id.spToolbar)).setVisibility(View.GONE);
		((ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight)).setOnClickListener(this);
		refreshExchangeList();
		return rootView;
	}

	private void refreshExchangeList() {
		CurrencyExchangeAdapter adapter = new CurrencyExchangeAdapter(getActivity(), currency.getCosts(), selected, mode, currency.getAbbr());
		lvCurrencyEditExchange.setAdapter(adapter);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mode == PocketAccounterGeneral.NORMAL_MODE)
			exchangeEditDialog(currency.getCosts().get(position));
		else {
			if (view != null) {
				CheckBox chbCurrencyExchangeListItem = (CheckBox) view.findViewById(R.id.chbCurrencyExchangeListItem);
				chbCurrencyExchangeListItem.setChecked(!chbCurrencyExchangeListItem.isChecked());
			}
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.ivExCurrencyAdd:
				exchangeEditDialog(null);
				break;
			case R.id.ivExCurrencyDelete:
				if (mode == PocketAccounterGeneral.NORMAL_MODE) {
					selected = new boolean[currency.getCosts().size()];
					mode = PocketAccounterGeneral.EDIT_MODE;
					ivExCurrencyDelete.setImageDrawable(null);
					ivExCurrencyDelete.setImageResource(R.drawable.ic_cat_trash);
				}
				else {
					mode = PocketAccounterGeneral.NORMAL_MODE;
					ivExCurrencyDelete.setImageDrawable(null);
					ivExCurrencyDelete.setImageResource(R.drawable.subcat_delete);
					deleteCosts();
					selected = null;
				}
				refreshExchangeList();
				break;
			case R.id.ivToolbarMostRight:
				InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				if (chbCurrencyEditMainCurrency.isChecked()) {
					if (!currency.getMain()) {
						double cost = 0.0;
						Calendar cal = Calendar.getInstance();
						for (int i=0; i<currency.getCosts().size(); i++) {
							if (i == 0)
								cal = (Calendar) currency.getCosts().get(i).getDay().clone();
							if (cal.compareTo(currency.getCosts().get(i).getDay()) <= 0) {
								cal = (Calendar) currency.getCosts().get(i).getDay().clone();
								cost = currency.getCosts().get(i).getCost();
							}
						}
						for (int i = 0; i< PocketAccounter.financeManager.getCurrencies().size(); i++) {
							PocketAccounter.financeManager.getCurrencies().get(i).setMain(false);
							for (int j = 0; j< PocketAccounter.financeManager.getCurrencies().get(i).getCosts().size(); j++)
								PocketAccounter.financeManager.getCurrencies().get(i).getCosts().get(j).setCost(PocketAccounter.financeManager.getCurrencies().get(i).getCosts().get(j).getCost()/cost);
						}
						for (int i = 0; i < PocketAccounter.financeManager.getCurrencies().size(); i++) {
							if (currency.getId().matches(PocketAccounter.financeManager.getCurrencies().get(i).getId())) {
								PocketAccounter.financeManager.getCurrencies().get(i).setMain(true);
								break;
							}
						}
					}
					PocketAccounter.financeManager.saveCurrencies();
					PocketAccounter.financeManager.saveRecords();
					PocketAccounter.financeManager.setRecords(PocketAccounter.financeManager.loadRecords());
				}
				((PocketAccounter)getActivity()).replaceFragment(new CurrencyFragment(), PockerTag.CURRENCY);
				break;
		}
	}
	private void exchangeEditDialog(final CurrencyCost currCost) {
		final Dialog dialog=new Dialog(getActivity());
		View dialogView = getActivity().getLayoutInflater().inflate(R.layout.exchange_edit, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView);
		final TextView tvExchangeEditDate = (TextView) dialogView.findViewById(R.id.tvExchangeEditDate);
		tvExchangeEditDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final Dialog dialog=new Dialog(getActivity());
				View dialogView = getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(dialogView);
				final DatePicker dp = (DatePicker) dialogView.findViewById(R.id.dp);
				ImageView ivDatePickOk = (ImageView) dialogView.findViewById(R.id.ivDatePickOk);
				ivDatePickOk.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
						day.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
						tvExchangeEditDate.setText(format.format(day.getTime()));
						dialog.dismiss();
					}
				});
				ImageView ivDatePickCancel = (ImageView) dialogView.findViewById(R.id.ivDatePickCancel);
				ivDatePickCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
		final EditText etExchange = (EditText) dialogView.findViewById(R.id.etExchange);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator('.');
		final DecimalFormat decFormat = new DecimalFormat("0.00", otherSymbols);
		etExchange.setText(decFormat.format(0.0));
		double cost = 1.0;
		if (currCost != null) {
			tvExchangeEditDate.setText(dateFormat.format(currCost.getDay().getTime()));
			day = (Calendar) currCost.getDay().clone();
			cost = currCost.getCost();
		}
		tvExchangeEditDate.setText(dateFormat.format(day.getTime()));
		etExchange.setText(decFormat.format(cost));
		ImageView ivCurrencyEditDialogOk = (ImageView) dialogView.findViewById(R.id.ivCurrencyEditDialogOk);
		ImageView ivCurrencyEditDialogCancel = (ImageView) dialogView.findViewById(R.id.ivCurrencyEditDialogCancel);
		ivCurrencyEditDialogOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (etExchange.getText().toString().matches("") || Double.parseDouble(etExchange.getText().toString()) == 0) {
					etExchange.setError(getString(R.string.incorrect_value));
					return;
				}
				if (currCost != null) {
					currCost.setCost(Double.parseDouble(etExchange.getText().toString()));
					currCost.setDay(day);
				}
				else {
					boolean dayFound = false;
					int position = 0;
					for (int i=0; i<currency.getCosts().size(); i++) {
						Calendar cal = currency.getCosts().get(i).getDay();
						if (cal.get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
								cal.get(Calendar.MONTH) == day.get(Calendar.MONTH) &&
								cal.get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH)) {
							dayFound = true;
							position = i;
							break;
						}
					}
					if (dayFound)
						currency.getCosts().get(position).setCost(Double.parseDouble(etExchange.getText().toString()));
					else {
						CurrencyCost newCurrencyCost = new CurrencyCost();
						newCurrencyCost.setCost(Double.parseDouble(etExchange.getText().toString()));
						newCurrencyCost.setDay(day);
						currency.getCosts().add(newCurrencyCost);
					}

				}
				refreshExchangeList();
				dialog.dismiss();
			}
		});
		ivCurrencyEditDialogCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		dialog.getWindow().setLayout(6*width/7, LayoutParams.WRAP_CONTENT);
		dialog.show();
	}
	private void deleteCosts() {
		boolean allSelected = true;
		for (int i=0; i<selected.length; i++) {
			if (!selected[i]) {
				allSelected = false;
				break;
			}
		}
		if (allSelected) {
			for (int i=0; i<currency.getCosts().size(); i++) {
				if (i != currency.getCosts().size()-1) {
					currency.getCosts().remove(i);
					i--;
				}
			}
			Toast.makeText(getActivity(), getResources().getString(R.string.costs_selected_all_warning), Toast.LENGTH_SHORT).show();
		} else {
			for (int i=0; i<selected.length; i++) {
				if (selected[i])
					currency.getCosts().set(i, null);
			}
			for (int i=0; i<currency.getCosts().size(); i++) {
				if (currency.getCosts().get(i) == null) {
					currency.getCosts().remove(i);
					i--;
				}
			}
		}
		refreshExchangeList();
	}
}