package com.jim.pocketaccounter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jim.pocketaccounter.helper.FloatingActionButton;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;
import com.jim.pocketaccounter.helper.SmsParseObject;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public class SMSParseFragment extends Fragment {
	private FloatingActionButton fabSmsParse;
	private RecyclerView rvSmsParseList;
	private boolean[] selected;
	private int mode = PocketAccounterGeneral.NORMAL_MODE;
	private ImageView ivToolbarMostRight;
	private final int PERMISSION_REQUEST_CONTACT = 5;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.sms_parse_layout, container, false);
		rootView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(PocketAccounter.keyboardVisible){
					InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);}
			}
		},100);

		((ImageView)PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel)).setVisibility(View.GONE);
		ivToolbarMostRight = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
		ivToolbarMostRight.setImageResource(R.drawable.pencil);
		ivToolbarMostRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setMode(mode);
			}
		});
		rvSmsParseList = (RecyclerView) rootView.findViewById(R.id.rvSmsParseList);
		rvSmsParseList.setLayoutManager(new LinearLayoutManager(getContext()));
		fabSmsParse = (FloatingActionButton)  rootView.findViewById(R.id.fabSmsParse);
		PocketAccounter.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PocketAccounter.drawer.openLeftSide();
			}
		});
		((PocketAccounter)getContext()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
		PocketAccounter.toolbar.setTitle(R.string.sms_parse);
		PocketAccounter.toolbar.setSubtitle("");
		PocketAccounter.toolbar.findViewById(R.id.spToolbar).setVisibility(View.GONE);
		Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_white_24dp);
		int size = (int) getResources().getDimension(R.dimen.thirty_dp);
		Bitmap add = Bitmap.createScaledBitmap(temp, size, size, false);
		fabSmsParse.setImageBitmap(add);
		fabSmsParse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				((PocketAccounter)getContext()).replaceFragment(new SMSParseEditFragment(null));
			}
		});
		refreshList();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
						android.Manifest.permission.RECEIVE_SMS)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle("Contacts access needed");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setMessage("please confirm Contacts access");//TODO put real question
					builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@TargetApi(Build.VERSION_CODES.M)
						@Override
						public void onDismiss(DialogInterface dialog) {
							requestPermissions(
									new String[]
											{android.Manifest.permission.RECEIVE_SMS}
									, PERMISSION_REQUEST_CONTACT);
						}
					});
					builder.show();
				} else {
					ActivityCompat.requestPermissions(getActivity(),
							new String[]{android.Manifest.permission.RECEIVE_SMS},
							PERMISSION_REQUEST_CONTACT);
				}
			}
		}

		return rootView;
	}
	private void refreshList() {
		MyAdapter adapter = new MyAdapter(PocketAccounter.financeManager.getSmsObjects());
		rvSmsParseList.setAdapter(adapter);
	}
	private void setMode(int mode) {
		if (mode == PocketAccounterGeneral.NORMAL_MODE) {
			ivToolbarMostRight.setImageResource(R.drawable.trash);
			this.mode = PocketAccounterGeneral.EDIT_MODE;
			selected = new boolean[PocketAccounter.financeManager.getSmsObjects().size()];
			RecyclerView.Adapter adapter = rvSmsParseList.getAdapter();
			for (int i=0; i<adapter.getItemCount(); i++)
				adapter.notifyItemChanged(i);
		}
		else {
			ivToolbarMostRight.setImageResource(R.drawable.pencil);
			this.mode = PocketAccounterGeneral.NORMAL_MODE;
			RecyclerView.Adapter adapter = rvSmsParseList.getAdapter();
			for (int i=0; i<adapter.getItemCount(); i++)
				adapter.notifyItemChanged(i);
			deleteSelected();
			selected = null;
		}

	}
	private void deleteSelected() {
		for (int i=0; i<selected.length; i++) {
			if (selected[i])
				PocketAccounter.financeManager.getSmsObjects().set(i, null);
		}
		for (int i=0; i<PocketAccounter.financeManager.getSmsObjects().size(); i++) {
			if (PocketAccounter.financeManager.getSmsObjects().get(i) == null) {
				PocketAccounter.financeManager.getSmsObjects().remove(i);
				i--;
			}
		}
		RecyclerView.Adapter adapter = rvSmsParseList.getAdapter();
		for (int i=0; i<adapter.getItemCount(); i++)
			adapter.notifyItemRemoved(i);
		PocketAccounter.financeManager.saveSmsObjects();
	}
	private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
		private ArrayList<SmsParseObject> objects;
		public MyAdapter(ArrayList<SmsParseObject> objects) {
			this.objects = objects;
		}
		public int getItemCount() {
			return objects.size();
		}
		public void onBindViewHolder(final ViewHolder view, final int position) {
			view.tvSmsParseItemNumber.setText(objects.get(position).getNumber());
			view.AccoountName.setText(objects.get(position).getAccount().getName());

			String text = "";
			if (objects.get(position).getType() == PocketAccounterGeneral.SMS_ONLY_EXPENSE)
				text = getResources().getString(R.string.only_expense)+"\n\n";
			else if (objects.get(position).getType() == PocketAccounterGeneral.SMS_ONLY_INCOME)
				text = getResources().getString(R.string.only_income)+"\n\n";
			else {
				text = getResources().getString(R.string.income_keywords)+" "+objects.get(position).getIncomeWords()+"\n\n"+
						getResources().getString(R.string.expense_keywords)+" "+objects.get(position).getExpenseWords()+"\n\n";
			}
			text = text + getResources().getString(R.string.amount_keywords)+" "+objects.get(position).getAmountWords()+"\n\n";
			text+= getResources().getString(R.string.currency)+": "+objects.get(position).getCurrency().getAbbr();
			view.tvSmsParsingItemInfo.setText(text);
			if (mode == PocketAccounterGeneral.NORMAL_MODE){

				view.forGONE.setVisibility(View.GONE);
				view.chbSmsObjectItem.setVisibility(View.GONE);
			}
			else {
				view.forGONE.setVisibility(View.VISIBLE);
				view.chbSmsObjectItem.setVisibility(View.VISIBLE);
				view.chbSmsObjectItem.setChecked(selected[position]);
				view.chbSmsObjectItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						selected[position] = isChecked;
					}
				});
			}
			view.rootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mode == PocketAccounterGeneral.NORMAL_MODE) {
						((PocketAccounter)getContext()).replaceFragment(new SMSParseEditFragment(objects.get(position)));
					}
					else {
						view.chbSmsObjectItem.setChecked(!view.chbSmsObjectItem.isChecked());
					}
				}
			});
		}
		public ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_object_list_item, parent, false);
			return new ViewHolder(view);
		}
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public CheckBox chbSmsObjectItem;
		public TextView tvSmsParseItemNumber;
		public TextView tvSmsParsingItemInfo;
		public TextView AccoountName;
		public View rootView;
		public LinearLayout forGONE;
		public ViewHolder(View view) {
			super(view);
			chbSmsObjectItem = (CheckBox) view.findViewById(R.id.chbSmsObjectItem);
			tvSmsParseItemNumber = (TextView) view.findViewById(R.id.tvSmsParseItemNumber);
			tvSmsParsingItemInfo = (TextView) view.findViewById(R.id.tvSmsParsingItemInfo);
			AccoountName = (TextView) view.findViewById(R.id.tvaccount);
			forGONE = (LinearLayout) view.findViewById(R.id.for_gone);
			rootView = view;
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_CONTACT: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				} else {

				}
				return;
			}
		}
	}
}
