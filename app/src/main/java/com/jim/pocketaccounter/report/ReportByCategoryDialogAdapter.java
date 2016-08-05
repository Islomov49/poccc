package com.jim.pocketaccounter.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.DecimalFormat;
import java.util.ArrayList;

@SuppressLint("ViewHolder")
public class ReportByCategoryDialogAdapter extends BaseAdapter {
	private ArrayList<SubCategoryWitAmount> result;
	private LayoutInflater inflater;
	public ReportByCategoryDialogAdapter(Context context, ArrayList<SubCategoryWitAmount> result) {
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
		tvReportByCategoryListSubCatName.setText("- "+result.get(position).getSubCategory().getName());
		TextView tvReportByCategoryListSubCatAmount = (TextView) view.findViewById(R.id.tvReportByCategoryListSubCatAmount);
		DecimalFormat format = new DecimalFormat("0.00##");
		tvReportByCategoryListSubCatAmount.setText(format.format(result.get(position).getAmount())+ PocketAccounter.financeManager.getMainCurrency().getAbbr());
		return view;
	}
}
