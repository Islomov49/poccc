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
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.util.ArrayList;

@SuppressLint("ViewHolder")
public class CategoryAdapterForDialog extends BaseAdapter {
	private ArrayList<RootCategory> result;
	private LayoutInflater inflater;
	private Context context;
	public CategoryAdapterForDialog(Context context, ArrayList<RootCategory> result) {
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
		View view = inflater.inflate(R.layout.category_list_item, parent, false);
		ImageView ivCategoryListIcon = (ImageView) view.findViewById(R.id.ivAccountListIcon);
		int resId = context.getResources().getIdentifier(result.get(position).getIcon(), "drawable", context.getPackageName());
		ivCategoryListIcon.setImageResource(resId);
		TextView tvCategoryListName = (TextView) view.findViewById(R.id.tvAccountListName);
		tvCategoryListName.setText(result.get(position).getName());
		return view;
	}
}