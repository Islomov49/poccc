package com.jim.pocketaccounter.helper.record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.DecimalFormat;
import java.util.ArrayList;

@SuppressLint("ViewHolder")
public class LongPressAdapter extends BaseAdapter {
	private ArrayList<FinanceRecord> result;
	private LayoutInflater inflater;
	private Context context;
	public LongPressAdapter(Context context, ArrayList<FinanceRecord> result) {
	    this.result = result;
		this.context = context;
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
		View view = inflater.inflate(R.layout.record_edit_list_item, parent, false);
		ImageView ivRecordEditCatIcon = (ImageView) view.findViewById(R.id.ivRecordEditCatIcon);
		TextView tvRecordEditCatName = (TextView) view.findViewById(R.id.tvRecordEditCatName);
		String catName = result.get(position).getCategory().getName();
		String icon = "";
		if (result.get(position).getSubCategory() != null) {
			catName = result.get(position).getSubCategory().getName();

			icon = result.get(position).getSubCategory().getIcon();
		}
		else {
			catName = result.get(position).getCategory().getName();
			icon = result.get(position).getCategory().getIcon();
		}
		int resId = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
		ivRecordEditCatIcon.setImageResource(resId);
		tvRecordEditCatName.setText(catName);
		int color = 0;
		String amount = "";
		if (result.get(position).getCategory().getType() == PocketAccounterGeneral.INCOME) {
			color = ContextCompat.getColor(context, R.color.green_just);
			amount = amount + "+";
		}
		else {
			color = ContextCompat.getColor(context, R.color.red);
			amount = amount + "-";
		}
		DecimalFormat format = new DecimalFormat("0.00##");
		TextView tvRecordEditCatAmount = (TextView) view.findViewById(R.id.tvRecordEditCatAmount);
		tvRecordEditCatAmount.setTextColor(color);
		amount = amount + format.format(result.get(position).getAmount()) + result.get(position).getCurrency().getAbbr();
		tvRecordEditCatAmount.setText(amount);
		return view;
	}
}
