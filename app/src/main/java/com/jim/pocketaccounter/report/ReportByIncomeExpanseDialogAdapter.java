package com.jim.pocketaccounter.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

@SuppressLint("ViewHolder")
public class ReportByIncomeExpanseDialogAdapter extends BaseAdapter {
	private ArrayList<IncomeExpanseDayDetails> result;
	private LayoutInflater inflater;
	public ReportByIncomeExpanseDialogAdapter(Context context, ArrayList<IncomeExpanseDayDetails> result) {
	    this.result = result;
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
		View view = inflater.inflate(R.layout.report_by_category_list_item, parent, false);
		TextView tvReportByCategoryListSubCatName = (TextView) view.findViewById(R.id.tvReportByCategoryListSubCatName);
		String text = "- "+result.get(position).getCategory().getName();
		if(result.get(position).getSubCategory() != null)
			text = text + ", " + result.get(position).getSubCategory().getName();
		tvReportByCategoryListSubCatName.setText(text);
		TextView tvReportByCategoryListSubCatAmount = (TextView) view.findViewById(R.id.tvReportByCategoryListSubCatAmount);
		DecimalFormat format = new DecimalFormat("0.00##");
		tvReportByCategoryListSubCatAmount.setText(format.format(result.get(position).getAmount())+ result.get(position).getCurrency().getAbbr());
		return view;
	}
}
