package com.jim.pocketaccounter.finance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.R;

import java.util.ArrayList;

@SuppressLint("ViewHolder")
public class TransferAccountAdapter extends BaseAdapter {
	private ArrayList<Account> result;
	private LayoutInflater inflater;
	public TransferAccountAdapter(Context context, ArrayList<Account> result) {
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
		View view = inflater.inflate(R.layout.transfer_account_item, parent, false);
		ImageView ivTransferItem = (ImageView) view.findViewById(R.id.ivTransferItem);
		ivTransferItem.setImageResource(result.get(position).getIcon());
		TextView tvTransferItem = (TextView) view.findViewById(R.id.tvTransferItem);
		tvTransferItem.setText(result.get(position).getName());
		return view;
	}
}
