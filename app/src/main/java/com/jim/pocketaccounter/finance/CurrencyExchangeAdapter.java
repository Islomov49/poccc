package com.jim.pocketaccounter.finance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.jim.pocketaccounter.CurrencyEditFragment;
import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@SuppressLint("ViewHolder")
public class CurrencyExchangeAdapter extends BaseAdapter {
	private ArrayList<CurrencyCost> result;
	private LayoutInflater inflater;
	private int mode;
	private boolean[] selected;
	private String abbr;
	public CurrencyExchangeAdapter(Context context, ArrayList<CurrencyCost> result, boolean[] selected, int mode, String abbr) {
	    this.result = result;
	    this.mode = mode;
	    this.selected = selected;
		this.abbr = abbr;
	    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  }
	@Override
	public int getCount() {
		return result.size();
	}
	@Override
	public Object getItem(int position) {
		return result.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.curr_exchange_list_item, parent, false);
		DecimalFormat decFormat = new DecimalFormat("0.00");
		TextView tvCurrencyExchangeListItem = (TextView) view.findViewById(R.id.tvCurrencyExchangeListItem);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		String text = dateFormat.format(result.get(position).getDay().getTime()) 
					  + "    1"+ PocketAccounter.financeManager.getMainCurrency().getAbbr()
					  + "=" + decFormat.format(result.get(position).getCost())+abbr;
		tvCurrencyExchangeListItem.setText(text);
		CheckBox chbCurrencyExchangeListItem = (CheckBox) view.findViewById(R.id.chbCurrencyExchangeListItem);
		chbCurrencyExchangeListItem.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				selected[position] = isChecked;
			}
		});
		if (mode == PocketAccounterGeneral.EDIT_MODE) {
			chbCurrencyExchangeListItem.setVisibility(View.VISIBLE);
			chbCurrencyExchangeListItem.setChecked(selected[position]);
		}
		else 
			chbCurrencyExchangeListItem.setVisibility(View.GONE);
		return view;
	}
}
