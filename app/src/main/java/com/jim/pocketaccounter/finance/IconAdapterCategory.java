package com.jim.pocketaccounter.finance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jim.pocketaccounter.R;

public class IconAdapterCategory extends BaseAdapter {
	private String[] result;
	private LayoutInflater inflater;
	private String selectedItem;
	private Context context;
	public IconAdapterCategory(Context context, String[] result, String selectedItem) {
		this.selectedItem = selectedItem;
		this.context = context;
	    this.result = result;
	    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  }
	@Override
	public int getCount() {
		return result.length;
	}
	@Override
	public Object getItem(int position) {
		return result[position];
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.select_icon_item, parent, false);
		if (!result[position].matches(selectedItem))
			view.setBackgroundResource(R.drawable.unselected_icon);
		else 
			view.setBackgroundResource(R.drawable.selected_icon);
		ImageView ivSelectIcon = (ImageView) view.findViewById(R.id.ivSelectIcon);
		int resId = context.getResources().getIdentifier(result[position], "drawable", context.getPackageName());
		ivSelectIcon.setImageResource(resId);
		return view;
	}
}
