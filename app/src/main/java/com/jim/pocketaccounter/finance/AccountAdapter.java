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

import com.jim.pocketaccounter.AccountFragment;
import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.util.ArrayList;

@SuppressLint("ViewHolder")
public class AccountAdapter extends BaseAdapter {
	private ArrayList<Account> result;
	private LayoutInflater inflater;
	private int mode;
	private boolean[] selected;
	public AccountAdapter(Context context, ArrayList<Account> result, boolean[] selected, int mode) {
	    this.result = result;
	    this.mode = mode;
	    this.selected = selected;
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
		View view = inflater.inflate(R.layout.account_list_item, parent, false);
		ImageView ivAccountListIcon = (ImageView) view.findViewById(R.id.ivAccountListIcon);
		ivAccountListIcon.setImageResource(result.get(position).getIcon());
		TextView tvAccountListName = (TextView) view.findViewById(R.id.tvAccountListName);
		tvAccountListName.setText(result.get(position).getName());
		CheckBox chbAccountListItem = (CheckBox) view.findViewById(R.id.chbAccountListItem);
		chbAccountListItem.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				selected[position] = isChecked;
			}
		});
		if (mode == PocketAccounterGeneral.EDIT_MODE) {
			chbAccountListItem.setVisibility(View.VISIBLE);
			chbAccountListItem.setChecked(selected[position]);
		}
		else 
			chbAccountListItem.setVisibility(View.GONE);
		return view;
	}
}
