package com.jim.pocketaccounter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.jim.pocketaccounter.helper.PocketAccounterGeneral;
import com.jim.pocketaccounter.helper.SmsParseObject;

import static android.app.Activity.RESULT_OK;

@SuppressLint({"InflateParams", "ValidFragment"})
public class SMSParseEditFragment extends Fragment {
	private EditText etSmsParseEditPhoneNumber, etSmsParseEditIncome,
					 etSmsParseEditExpense, etSmsParseAmountKeywords;
	private FrameLayout tvSmsParseEditFromContact;
	private final int PERMISSION_REQUEST_CONTACT = 5;
	private int PICK_CONTACT = 10;
	private ImageView ivToolbarMostRight;
	private RadioGroup rgSmsParseEdit;
	private RadioButton rbOnlyExpense, rbOnlyIncome, rbBoth;
	private Spinner spSmsParseEditAccount, spSmsParseEditCurrency;
	private SmsParseObject object;

	@SuppressLint("ValidFragment")
	public SMSParseEditFragment(SmsParseObject object) {
		this.object = object;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.sms_parse_edit_moder, container, false);
		tvSmsParseEditFromContact = (FrameLayout) rootView.findViewById(R.id.tvSmsParseEditFromContact);
		tvSmsParseEditFromContact.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				askForContactPermission();
			}
		});
		((PocketAccounter)getContext()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_button);
		PocketAccounter.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				((PocketAccounter)getContext()).getSupportFragmentManager().popBackStack();
				((PocketAccounter)getContext()).replaceFragment(new SMSParseFragment(), com.jim.pocketaccounter.debt.PockerTag.ACCOUNT_MANAGEMENT);

			}

		});
		((ImageView)PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel)).setVisibility(View.GONE);
		PocketAccounter.toolbar.findViewById(R.id.spToolbar).setVisibility(View.GONE);
		PocketAccounter.toolbar.setTitle(R.string.addedit);
		PocketAccounter.toolbar.setSubtitle("");
		spSmsParseEditAccount = (Spinner) rootView.findViewById(R.id.spSmsParseEditAccount);
		spSmsParseEditCurrency = (Spinner) rootView.findViewById(R.id.spSmsParseEditCurrency);
		etSmsParseEditIncome = (EditText) rootView.findViewById(R.id.etSmsParseEditIncome);
		etSmsParseEditExpense = (EditText) rootView.findViewById(R.id.etSmsParseEditExpense);
		etSmsParseAmountKeywords = (EditText) rootView.findViewById(R.id.etSmsParseAmountKeywords);
		etSmsParseEditPhoneNumber = (EditText) rootView.findViewById(R.id.etSmsParseEditPhoneNumber);
		rgSmsParseEdit = (RadioGroup) rootView.findViewById(R.id.rgSmsParseEdit);
		rbOnlyExpense = (RadioButton) rgSmsParseEdit.findViewById(R.id.rbOnlyExpense);
		rbOnlyIncome = (RadioButton) rgSmsParseEdit.findViewById(R.id.rbOnlyIncome);
		rbBoth = (RadioButton) rgSmsParseEdit.findViewById(R.id.rbBoth);
		etSmsParseEditIncome.setVisibility(View.GONE);
		etSmsParseEditExpense.setVisibility(View.GONE);
		rbOnlyExpense.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					etSmsParseEditIncome.setText("");
					etSmsParseEditExpense.setText("");
					etSmsParseEditIncome.setVisibility(View.GONE);
					etSmsParseEditExpense.setVisibility(View.GONE);
					}
			}
		});
		rbOnlyIncome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					etSmsParseEditIncome.setText("");

					etSmsParseEditExpense.setText("");
					etSmsParseEditIncome.setVisibility(View.GONE);
					etSmsParseEditExpense.setVisibility(View.GONE);
				}
			}
		});
		rbBoth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					etSmsParseEditIncome.setText("");

					etSmsParseEditExpense.setText("");
					etSmsParseEditIncome.setVisibility(View.VISIBLE);
					etSmsParseEditExpense.setVisibility(View.VISIBLE);
				}
			}
		});
		String[] accounts = new String[PocketAccounter.financeManager.getAccounts().size()];
		for (int i=0; i<PocketAccounter.financeManager.getAccounts().size(); i++)
			accounts[i] = PocketAccounter.financeManager.getAccounts().get(i).getName();
		ArrayAdapter accAdapter = new ArrayAdapter(getContext(), R.layout.spiner_gravity_right3, accounts);
		spSmsParseEditAccount.setAdapter(accAdapter);
		String[] currencies = new String[PocketAccounter.financeManager.getCurrencies().size()];
		for (int i=0; i<PocketAccounter.financeManager.getCurrencies().size(); i++)
			currencies[i] = PocketAccounter.financeManager.getCurrencies().get(i).getAbbr();
		ArrayAdapter currAdapter = new ArrayAdapter(getContext(), R.layout.spiner_gravity_right3, currencies);
		spSmsParseEditCurrency.setAdapter(currAdapter);
		if (object != null) {
			etSmsParseEditPhoneNumber.setText(object.getNumber());
			switch (object.getType()) {
				case PocketAccounterGeneral.SMS_ONLY_EXPENSE:
					rbOnlyExpense.setChecked(true);
					break;
				case PocketAccounterGeneral.SMS_ONLY_INCOME:
					rbOnlyIncome.setChecked(true);
					break;
				case PocketAccounterGeneral.SMS_BOTH:
					rbBoth.setChecked(true);
					break;
			}
			for (int i=0; i<PocketAccounter.financeManager.getAccounts().size(); i++) {
				if (PocketAccounter.financeManager.getAccounts().get(i).getId().matches(object.getAccount().getId())) {
					spSmsParseEditAccount.setSelection(i);
					break;
				}
			}
			for (int i=0; i<PocketAccounter.financeManager.getCurrencies().size(); i++) {
				if (PocketAccounter.financeManager.getCurrencies().get(i).getId().matches(object.getCurrency().getId())) {
					spSmsParseEditCurrency.setSelection(i);
					break;
				}
			}
			etSmsParseEditIncome.setText(object.getIncomeWords());
			etSmsParseEditExpense.setText(object.getExpenseWords());
			etSmsParseAmountKeywords.setText(object.getAmountWords());
		}
		ivToolbarMostRight = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
		ivToolbarMostRight.setVisibility(View.VISIBLE);
		ivToolbarMostRight.setImageResource(R.drawable.check_sign);
		ivToolbarMostRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (etSmsParseEditPhoneNumber.getText().toString().matches("")) {
					etSmsParseEditPhoneNumber.setError(getResources().getString(R.string.phone_number_warning));
					return;
				}
				if (rbBoth.isChecked()) {
					if (etSmsParseEditExpense.getText().toString().matches("")) {
						etSmsParseEditExpense.setError(getResources().getString(R.string.keyword_warning));
						return;
					}
					if (etSmsParseEditIncome.getText().toString().matches("")) {
						etSmsParseEditIncome.setError(getResources().getString(R.string.keyword_warning));
						return;
					}
				}
				if (etSmsParseAmountKeywords.getText().toString().matches("")) {
					etSmsParseAmountKeywords.setError(getResources().getString(R.string.keyword_warning));
					return;
				}
				if (object != null) {
					object.setNumber(etSmsParseEditPhoneNumber.getText().toString());
					if (rbBoth.isChecked())
						object.setType(PocketAccounterGeneral.SMS_BOTH);
					if (rbOnlyIncome.isChecked())
						object.setType(PocketAccounterGeneral.SMS_ONLY_INCOME);
					if (rbOnlyExpense.isChecked())
						object.setType(PocketAccounterGeneral.SMS_ONLY_EXPENSE);
					object.setExpenseWords(etSmsParseEditExpense.getText().toString());
					object.setIncomeWords(etSmsParseEditIncome.getText().toString());
					object.setAmountWords(etSmsParseAmountKeywords.getText().toString());
					object.setAccount(PocketAccounter.financeManager.getAccounts().get(spSmsParseEditAccount.getSelectedItemPosition()));
					object.setCurrency(PocketAccounter.financeManager.getCurrencies().get(spSmsParseEditCurrency.getSelectedItemPosition()));
				}
				else {
					SmsParseObject object = new SmsParseObject();
					object.setNumber(etSmsParseEditPhoneNumber.getText().toString());
					if (rbBoth.isChecked())
						object.setType(PocketAccounterGeneral.SMS_BOTH);
					if (rbOnlyIncome.isChecked())
						object.setType(PocketAccounterGeneral.SMS_ONLY_INCOME);
					if (rbOnlyExpense.isChecked())
						object.setType(PocketAccounterGeneral.SMS_ONLY_EXPENSE);
					object.setExpenseWords(etSmsParseEditExpense.getText().toString());
					object.setIncomeWords(etSmsParseEditIncome.getText().toString());
					object.setAmountWords(etSmsParseAmountKeywords.getText().toString());
					object.setAccount(PocketAccounter.financeManager.getAccounts().get(spSmsParseEditAccount.getSelectedItemPosition()));
					object.setCurrency(PocketAccounter.financeManager.getCurrencies().get(spSmsParseEditCurrency.getSelectedItemPosition()));
					PocketAccounter.financeManager.getSmsObjects().add(object);
				}
				PocketAccounter.financeManager.saveSmsObjects();
				InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				((PocketAccounter)getContext()).getSupportFragmentManager().popBackStack();
				((PocketAccounter)getContext()).replaceFragment(new SMSParseFragment(), com.jim.pocketaccounter.debt.PockerTag.ACCOUNT_MANAGEMENT);
			}
		});
		return rootView;
	}
	private void getContact() {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
		startActivityForResult(intent, PICK_CONTACT);
	}
	public void askForContactPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
						android.Manifest.permission.READ_CONTACTS)) {
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
											{android.Manifest.permission.READ_CONTACTS}
									, PERMISSION_REQUEST_CONTACT);
						}
					});
					builder.show();
				} else {
					ActivityCompat.requestPermissions(getActivity(),
							new String[]{android.Manifest.permission.READ_CONTACTS},
							PERMISSION_REQUEST_CONTACT);
				}
			} else {
				getContact();
			}
		} else {
			getContact();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
			Uri contactUri = data.getData();
			String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
					ContactsContract.CommonDataKinds.Phone.PHOTO_ID
			};
			Cursor cursor = getContext().getContentResolver().query(contactUri, projection,
					null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
				int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
				String number = cursor.getString(numberIndex);
				String name = cursor.getString(nameIndex);
				etSmsParseEditPhoneNumber.setText(number);
			}
		}
	}
}
