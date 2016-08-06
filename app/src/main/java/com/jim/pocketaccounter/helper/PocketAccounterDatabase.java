package com.jim.pocketaccounter.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.credit.ReckingCredit;
import com.jim.pocketaccounter.debt.DebtBorrow;
import com.jim.pocketaccounter.debt.Person;
import com.jim.pocketaccounter.debt.Recking;
import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.CurrencyCost;
import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.finance.SubCategory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class PocketAccounterDatabase extends SQLiteOpenHelper {
	private Context context;
	private SimpleDateFormat dateFormat = null;
	public PocketAccounterDatabase(Context context) {
		super(context, "PocketAccounterDatabase", null, 5);
		this.context = context;
		dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	}
	
	//onCreate method 
	//Here was created all tables which needs for finance department	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//currency_table
		db.execSQL("create table currency_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "currency_name TEXT,"
				+ "currency_sign TEXT,"
				+ "currency_id TEXT,"
				+ "currency_main INTEGER,"
				+ "empty TEXT"
				+ ");");

		//currency costs
		db.execSQL("create table currency_costs_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "currency_id TEXT,"
				+ "date TEXT,"
				+ "cost REAL,"
				+ "empty TEXT"
				+ ");");

		//category_table
		db.execSQL("create table category_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "category_name TEXT,"
				+ "category_id TEXT,"
				+ "category_type INTEGER,"
				+ "icon TEXT,"
				+ "empty TEXT"

				+ ");");

		db.execSQL("create table incomes_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "category_name TEXT,"
				+ "category_id TEXT,"
				+ "category_type INTEGER,"
				+ "icon TEXT,"
				+ "empty TEXT"
				+ ");");

		db.execSQL("create table expanses_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "category_name TEXT,"
				+ "category_id TEXT,"
				+ "category_type INTEGER,"
				+ "icon TEXT,"
				+ "empty TEXT"
				+ ");");

		//subcategries table
		db.execSQL("create table subcategory_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "subcategory_name TEXT,"
				+ "subcategory_id TEXT,"
				+ "category_id TEXT,"
				+ "icon TEXT,"
				+ "empty TEXT"

				+ ");");

		//account table
		db.execSQL("CREATE TABLE account_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "account_name TEXT,"
				+ "account_id TEXT,"
				+ "icon INTEGER,"
				+ "empty TEXT"
				+ ");");
		//daily_record_table
		db.execSQL("CREATE TABLE daily_record_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "date TEXT,"
				+ "category_id TEXT,"
				+ "subcategory_id TEXT,"
				+ "account_id TEXT,"
				+ "currency_id TEXT,"
				+ "record_id TEXT, "
				+ "amount REAL, "
				+ "empty TEXT"

				+ ");");
		//debt_borrow_table
		db.execSQL("CREATE TABLE debt_borrow_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "person_name TEXT,"
				+ "person_number TEXT,"
				+ "taken_date TEXT,"
				+ "return_date TEXT,"
				+ "calculate INTEGER,"
				+ "to_archive INTEGER,"
				+ "type INTEGER,"
				+ "account_id TEXT,"
				+ "currency_id TEXT,"
				+ "amount REAL,"
				+ "id TEXT,"
				+ "photo_id TEXT,"
				+ "empty TEXT"

				+ ");");
		//debtborrow recking table
		db.execSQL("CREATE TABLE debtborrow_recking_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "pay_date TEXT,"
				+ "amount REAL,"
				+ "id TEXT,"
				+ "account_id TEXT,"
				+ "comment TEXT,"
				+ "empty TEXT"

				+ ");");
		//credit
		db.execSQL("CREATE TABLE credit_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "credit_name TEXT,"
				+ "icon_id INTEGER,"
				+ "key_for_include INTEGER,"
				+ "key_for_archive INTEGER,"
				+ "taken_date TEXT,"
				+ "percent REAL,"
				+ "percent_interval TEXT,"
				+ "period_time TEXT,"
				+ "period_time_tip TEXT," //man qoshdim p.s. sardor
				+ "credit_id TEXT,"
				+ "credit_value REAL,"
				+ "credit_value_with_percent REAL,"
				+ "currency_id TEXT,"
				+ "empty TEXT"

				+ ");");
		db.execSQL("CREATE TABLE credit_archive_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "credit_name TEXT,"
				+ "icon_id INTEGER,"
				+ "key_for_include INTEGER,"
				+ "key_for_archive INTEGER,"
				+ "taken_date TEXT,"
				+ "percent REAL,"
				+ "percent_interval TEXT,"
				+ "period_time TEXT,"
				+ "period_time_tip TEXT," //man qoshdim p.s. sardor
				+ "credit_id TEXT,"
				+ "credit_value REAL,"
				+ "credit_value_with_percent REAL,"
				+ "currency_id TEXT, "
				+ "empty TEXT"

				+ ");");
		//recking_of_credit
		db.execSQL("CREATE TABLE credit_recking_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "pay_date TEXT,"
				+ "amount REAL,"
				+ "account_id TEXT,"
				+ "comment TEXT,"
				+ "credit_id TEXT, "
				+ "empty TEXT"

				+ ");");
		db.execSQL("CREATE TABLE credit_recking_table_arch ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "pay_date TEXT,"
				+ "amount REAL,"
				+ "account_id TEXT,"
				+ "comment TEXT,"
				+ "credit_id TEXT,"
				+ "empty TEXT"
				+ ");");
		db.execSQL("CREATE TABLE sms_parsing_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "number TEXT,"
				+ "income_words TEXT,"
				+ "expense_words TEXT,"
				+ "amount_words TEXT,"
				+ "account_id TEXT,"
				+ "currency_id TEXT,"
				+ "type INTEGER,"
				+ "empty TEXT"
				+ ");");
		initCurrencies(db);
		initDefault(db);
		initIncomesAndExpanses(db);
		initAccounts(db);
	}

	public ArrayList<SmsParseObject> getSmsParseObjects() {
		ArrayList<Currency> currencies = loadCurrencies();
		ArrayList<Account> accounts = loadAccounts();
		ArrayList<SmsParseObject> result = new ArrayList<SmsParseObject>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("sms_parsing_table", null, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SmsParseObject object = new SmsParseObject();
			object.setNumber(cursor.getString(cursor.getColumnIndex("number")));
			object.setIncomeWords(cursor.getString(cursor.getColumnIndex("income_words")));
			object.setExpenseWords(cursor.getString(cursor.getColumnIndex("expense_words")));
			object.setAmountWords(cursor.getString(cursor.getColumnIndex("amount_words")));
			String accountId = cursor.getString(cursor.getColumnIndex("account_id"));
			for (int i=0; i<accounts.size(); i++) {
				if (accountId.matches(accounts.get(i).getId())) {
					object.setAccount(accounts.get(i));
					break;
				}
			}
			String currencyId = cursor.getString(cursor.getColumnIndex("currency_id"));
			for (int i=0; i<currencies.size(); i++) {
				if (currencyId.matches(currencies.get(i).getId())) {
					object.setCurrency(currencies.get(i));
					break;
				}
			}
			object.setType(cursor.getInt(cursor.getColumnIndex("type")));
			result.add(object);
			cursor.moveToNext();
		}
		return result;
	}

	public void saveSmsObjects(ArrayList<SmsParseObject> objects) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try{

			db.execSQL("DELETE FROM sms_parsing_table");
			for (int i=0; i<objects.size(); i++) {
				ContentValues values = new ContentValues();
				values.put("number", objects.get(i).getNumber());
				values.put("income_words", objects.get(i).getIncomeWords());
				values.put("expense_words", objects.get(i).getExpenseWords());
				values.put("amount_words", objects.get(i).getAmountWords());
				values.put("account_id", objects.get(i).getAccount().getId());
				values.put("currency_id", objects.get(i).getCurrency().getId());
				values.put("type", objects.get(i).getType());
				db.insert("sms_parsing_table", null, values);
			}
			db.setTransactionSuccessful();
		}
		catch (Exception o){
			o.printStackTrace();
		}
		finally {
			db.endTransaction();
			db.close();
		}

	}

	private void initCurrencies(SQLiteDatabase db) {
		String [] CurrencyNames = context.getResources().getStringArray(R.array.base_currencies);
		String [] Currency_id = context.getResources().getStringArray(R.array.currency_ids);
		String [] Currency_cost = context.getResources().getStringArray(R.array.currency_costs);
		String [] Currency_abbrs = context.getResources().getStringArray(R.array.base_abbrs);
		ContentValues contentValues = new ContentValues();
		for (int i = 0; i < 3; i++) {
			contentValues.clear();
			contentValues.put("currency_name", CurrencyNames[i]);
			contentValues.put("currency_id", Currency_id[i]);
			contentValues.put("currency_sign", Currency_abbrs[i]);
			contentValues.put("currency_main", i==0);
			db.insert("currency_table", null, contentValues);
			contentValues.clear();
			contentValues.put("currency_id", Currency_id[i]);
			contentValues.put("cost", Double.parseDouble(Currency_cost[i]));
			contentValues.put("date", dateFormat.format(Calendar.getInstance().getTime()));
			db.insert("currency_costs_table", null, contentValues);
		}
	}

	public void saveDebtBorrowsToTable(ArrayList<DebtBorrow> debtBorrows) {
		SQLiteDatabase db = getWritableDatabase();

		db.beginTransaction();
		try{

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			db.execSQL("DELETE FROM debt_borrow_table");
			db.execSQL("DELETE FROM debtborrow_recking_table");
			for (DebtBorrow debtBorrow : debtBorrows) {
				ContentValues values = new ContentValues();
				values.put("person_name", debtBorrow.getPerson().getName());
				values.put("person_number", debtBorrow.getPerson().getPhoneNumber());
				values.put("photo_id", debtBorrow.getPerson().getPhoto());
				values.put("taken_date", dateFormat.format(debtBorrow.getTakenDate().getTime()));
				if (debtBorrow.getReturnDate() != null) {
					values.put("return_date", dateFormat.format(debtBorrow.getReturnDate().getTime()));
				} else {
					values.put("return_date", "");
				}
				values.put("type", debtBorrow.getType());
				values.put("account_id", debtBorrow.getAccount().getId());
				values.put("currency_id", debtBorrow.getCurrency().getId());
				values.put("amount", debtBorrow.getAmount());
				values.put("id", debtBorrow.getId());
				values.put("calculate", debtBorrow.isCalculate());
				values.put("to_archive", debtBorrow.isTo_archive());
				values.put("empty", debtBorrow.getInfo());
				db.insert("debt_borrow_table", null, values);
				values.clear();
				ArrayList<Recking> list = debtBorrow.getReckings();
				for (Recking rc : list) {
					values.put("pay_date", rc.getPayDate());
					values.put("amount", rc.getAmount());
					values.put("id", debtBorrow.getId());
					values.put("account_id", rc.getAccountId());
					values.put("comment", rc.getComment());
					db.insert("debtborrow_recking_table", null, values);
					values.clear();
				}

			}
			db.setTransactionSuccessful();
		}
		catch (Exception o){
			o.printStackTrace();
		}
		finally {
			db.endTransaction();
			db.close();
		}



	}
	public void saveDatasToCreditTable(ArrayList<CreditDetials> credits) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		db.beginTransaction();
		try{

			db.execSQL("DELETE FROM credit_table");
			db.execSQL("DELETE FROM credit_recking_table");
			SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			for (int i=0; i<credits.size(); i++) {
				values.put("credit_name", credits.get(i).getCredit_name());
				values.put("icon_id", credits.get(i).getIcon_ID());
				values.put("key_for_include", credits.get(i).isKey_for_include());
				values.put("key_for_archive", credits.get(i).isKey_for_archive());
				values.put("taken_date", format.format(credits.get(i).getTake_time().getTime()));
				values.put("percent", credits.get(i).getProcent());
				values.put("percent_interval", Long.toString(credits.get(i).getProcent_interval()));
				values.put("period_time", Long.toString(credits.get(i).getPeriod_time()));
				values.put("period_time_tip", Long.toString(credits.get(i).getPeriod_time_tip())); //ps Sardor
				values.put("credit_id", credits.get(i).getMyCredit_id());
				values.put("credit_value", credits.get(i).getValue_of_credit());
				values.put("credit_value_with_percent", credits.get(i).getValue_of_credit_with_procent());
				values.put("currency_id", credits.get(i).getValyute_currency().getId());
				values.put("empty", credits.get(i).getInfo());
				db.insert("credit_table", null, values);
				values.clear();
				for (int j=0; j<credits.get(i).getReckings().size(); j++) {
					values.put("pay_date", Long.toString(credits.get(i).getReckings().get(j).getPayDate()));
					values.put("amount", credits.get(i).getReckings().get(j).getAmount());
					values.put("account_id", credits.get(i).getReckings().get(j).getAccountId());
					values.put("comment", credits.get(i).getReckings().get(j).getComment());
					values.put("credit_id", credits.get(i).getReckings().get(j).getMyCredit_id());
					db.insert("credit_recking_table", null, values);
					values.clear();
				}
			}
			db.setTransactionSuccessful();
		}
		catch (Exception o){
			o.printStackTrace();
		}
		finally {
			db.endTransaction();
			db.close();

		}


	}
	private void initAccounts(SQLiteDatabase db) {
		String[] accountNames = context.getResources().getStringArray(R.array.account_names);
		String[] accountIds = context.getResources().getStringArray(R.array.account_ids);

		String[] accountIcons = context.getResources().getStringArray(R.array.account_icons);
		int[] icons = new int[accountIcons.length];
		for (int i=0; i<accountIcons.length; i++) {
			int resId = context.getResources().getIdentifier(accountIcons[i], "drawable", context.getPackageName());
			icons[i] = resId;
		}
		ContentValues values = new ContentValues();
		for (int i=0; i<accountNames.length; i++) {
			values.put("account_name", accountNames[i]);
			values.put("icon", icons[i]);
			values.put("account_id", accountIds[i]);
			db.insert("account_table", null, values);
		}
	}
	public void saveDatasToArchiveCreditTable(ArrayList<CreditDetials> credits) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		db.beginTransaction();
		try{

			db.execSQL("DELETE FROM credit_archive_table");
			db.execSQL("DELETE FROM credit_recking_table_arch");
			SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			for (int i=0; i<credits.size(); i++) {
				values.put("credit_name", credits.get(i).getCredit_name());
				values.put("icon_id", credits.get(i).getIcon_ID());
				values.put("key_for_include", credits.get(i).isKey_for_include());
				values.put("key_for_archive", credits.get(i).isKey_for_archive());
				values.put("taken_date", format.format(credits.get(i).getTake_time().getTime()));
				values.put("percent", credits.get(i).getProcent());
				values.put("percent_interval", Long.toString(credits.get(i).getProcent_interval()));
				values.put("period_time", Long.toString(credits.get(i).getPeriod_time()));
				values.put("period_time_tip", Long.toString(credits.get(i).getPeriod_time_tip())); //ps Sardor
				values.put("credit_id", credits.get(i).getMyCredit_id());
				values.put("credit_value", credits.get(i).getValue_of_credit());
				values.put("credit_value_with_percent", credits.get(i).getValue_of_credit_with_procent());
				values.put("currency_id", credits.get(i).getValyute_currency().getId());
				db.insert("credit_archive_table", null, values);
				values.clear();
				for (int j=0; j<credits.get(i).getReckings().size(); j++) {
					values.put("pay_date", Long.toString(credits.get(i).getReckings().get(j).getPayDate()));
					values.put("amount", credits.get(i).getReckings().get(j).getAmount());
					values.put("account_id", credits.get(i).getReckings().get(j).getAccountId());
					values.put("comment", credits.get(i).getReckings().get(j).getComment());
					values.put("credit_id", credits.get(i).getReckings().get(j).getMyCredit_id());
					db.insert("credit_recking_table_arch", null, values);
					values.clear();
				}
			}
			db.setTransactionSuccessful();
		}
		catch (Exception o){
			o.printStackTrace();
		}
		finally {
			db.endTransaction();
			db.close();

		}

	}

	public ArrayList<CreditDetials> loadCredits() {
		ArrayList<CreditDetials> result = new ArrayList<>();
		ArrayList<Currency> currencies = loadCurrencies();
		SQLiteDatabase db = getReadableDatabase();
		Cursor curCreditTable = db.query("credit_table", null, null, null, null, null, null);
		Cursor curCreditRecking = db.query("credit_recking_table", null, null, null, null, null, null);
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		curCreditTable.moveToFirst();
		while (!curCreditTable.isAfterLast()) {
			CreditDetials credit = new CreditDetials();
			credit.setCredit_name(curCreditTable.getString(curCreditTable.getColumnIndex("credit_name")));
			credit.setIcon_ID(curCreditTable.getInt(curCreditTable.getColumnIndex("icon_id")));
			try {
				Calendar takenDate = Calendar.getInstance();
				takenDate.setTime(format.parse(curCreditTable.getString(curCreditTable.getColumnIndex("taken_date"))));
				credit.setTake_time(takenDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			credit.setProcent(curCreditTable.getDouble(curCreditTable.getColumnIndex("percent")));
			credit.setProcent_interval(Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("percent_interval"))));
			credit.setPeriod_time(Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("period_time"))));
			credit.setMyCredit_id(Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("credit_id"))));
			credit.setValue_of_credit(curCreditTable.getDouble(curCreditTable.getColumnIndex("credit_value")));
			credit.setValue_of_credit_with_procent(curCreditTable.getDouble(curCreditTable.getColumnIndex("credit_value_with_percent")));
			credit.setPeriod_time_tip(Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("period_time_tip"))));
			credit.setKey_for_include(curCreditTable.getInt(curCreditTable.getColumnIndex("key_for_include"))!=0);
			credit.setKey_for_archive(curCreditTable.getInt(curCreditTable.getColumnIndex("key_for_archive"))!=0);
			String currencyId = curCreditTable.getString(curCreditTable.getColumnIndex("currency_id"));
			Currency currency = null;
			for (int i = 0; i<currencies.size(); i++)  {
				if (currencyId.matches(currencies.get(i).getId())) {
					currency = currencies.get(i);
					break;
				}
			}
			credit.setValyute_currency(currency);

			ArrayList<ReckingCredit> reckings = new ArrayList<ReckingCredit>();
			curCreditRecking.moveToFirst();
			while(!curCreditRecking.isAfterLast()) {
				if (Long.parseLong(curCreditRecking.getString(curCreditRecking.getColumnIndex("credit_id"))) == Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("credit_id")))) {
					double amount = curCreditRecking.getDouble(curCreditRecking.getColumnIndex("amount"));
					long payDate = Long.parseLong(curCreditRecking.getString(curCreditRecking.getColumnIndex("pay_date")));
					String comment = curCreditRecking.getString(curCreditRecking.getColumnIndex("comment"));
					String accountId = curCreditRecking.getString(curCreditRecking.getColumnIndex("account_id"));
					long creditId = Long.parseLong(curCreditRecking.getString(curCreditRecking.getColumnIndex("credit_id")));
					ReckingCredit newReckingCredit = new ReckingCredit(payDate, amount, accountId, creditId, comment);
					reckings.add(newReckingCredit);
				}
				curCreditRecking.moveToNext();
			}
			credit.setReckings(reckings);
			credit.setInfo(curCreditTable.getString(curCreditTable.getColumnIndex("empty")));
			result.add(credit);
			curCreditTable.moveToNext();
		}
		return result;
	}

	public ArrayList<CreditDetials> loadArchiveCredits() {
		ArrayList<CreditDetials> result = new ArrayList<>();
		ArrayList<Currency> currencies = loadCurrencies();
		SQLiteDatabase db = getReadableDatabase();
		Cursor curCreditTable = db.query("credit_archive_table", null, null, null, null, null, null);
		Cursor curCreditRecking = db.query("credit_recking_table_arch", null, null, null, null, null, null);
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		curCreditTable.moveToFirst();
		while (!curCreditTable.isAfterLast()) {
			CreditDetials credit = new CreditDetials();
			credit.setCredit_name(curCreditTable.getString(curCreditTable.getColumnIndex("credit_name")));
			credit.setIcon_ID(curCreditTable.getInt(curCreditTable.getColumnIndex("icon_id")));
			try {
				Calendar takenDate = Calendar.getInstance();
				takenDate.setTime(format.parse(curCreditTable.getString(curCreditTable.getColumnIndex("taken_date"))));
				credit.setTake_time(takenDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			credit.setProcent(curCreditTable.getDouble(curCreditTable.getColumnIndex("percent")));
			credit.setProcent_interval(Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("percent_interval"))));
			credit.setPeriod_time(Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("period_time"))));
			credit.setMyCredit_id(Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("credit_id"))));
			credit.setValue_of_credit(curCreditTable.getDouble(curCreditTable.getColumnIndex("credit_value")));
			credit.setValue_of_credit_with_procent(curCreditTable.getDouble(curCreditTable.getColumnIndex("credit_value_with_percent")));
			credit.setPeriod_time_tip(Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("period_time_tip"))));
			credit.setKey_for_include(curCreditTable.getInt(curCreditTable.getColumnIndex("key_for_include"))!=0);
			credit.setKey_for_archive(curCreditTable.getInt(curCreditTable.getColumnIndex("key_for_archive"))!=0);
			String currencyId = curCreditTable.getString(curCreditTable.getColumnIndex("currency_id"));
			Currency currency = null;
			for (int i = 0; i<currencies.size(); i++)  {
				if (currencyId.matches(currencies.get(i).getId())) {
					currency = currencies.get(i);
					break;
				}
			}
			credit.setValyute_currency(currency);

			ArrayList<ReckingCredit> reckings = new ArrayList<ReckingCredit>();
			curCreditRecking.moveToFirst();
			while(!curCreditRecking.isAfterLast()) {
				if (Long.parseLong(curCreditRecking.getString(curCreditRecking.getColumnIndex("credit_id"))) == Long.parseLong(curCreditTable.getString(curCreditTable.getColumnIndex("credit_id")))) {
					double amount = curCreditRecking.getDouble(curCreditRecking.getColumnIndex("amount"));
					long payDate = Long.parseLong(curCreditRecking.getString(curCreditRecking.getColumnIndex("pay_date")));
					String comment = curCreditRecking.getString(curCreditRecking.getColumnIndex("comment"));
					String accountId = curCreditRecking.getString(curCreditRecking.getColumnIndex("account_id"));
					long creditId = Long.parseLong(curCreditRecking.getString(curCreditRecking.getColumnIndex("credit_id")));
					ReckingCredit newReckingCredit = new ReckingCredit(payDate, amount, accountId, creditId, comment);
					reckings.add(newReckingCredit);
				}
				curCreditRecking.moveToNext();
			}
			credit.setReckings(reckings);
			result.add(credit);
			curCreditTable.moveToNext();
		}
		return result;
	}
	private void initDefault(SQLiteDatabase db) {
		String[] catValues = context.getResources().getStringArray(R.array.cat_values);
		String[] catTypes = context.getResources().getStringArray(R.array.cat_types);
		String[] catIcons = context.getResources().getStringArray(R.array.cat_icons);
		for (int i=0; i<catValues.length; i++) {
			int resId = context.getResources().getIdentifier(catValues[i], "string", context.getPackageName());
			int iconId = context.getResources().getIdentifier(catIcons[i], "drawable", context.getPackageName());
			ContentValues values = new ContentValues();
			values.put("category_name", context.getResources().getString(resId));
			values.put("category_id", catValues[i]);
			values.put("category_type", catTypes[i]);
			values.put("icon", iconId);
			db.insert("category_table", null, values);
			int arrayId = context.getResources().getIdentifier(catValues[i], "array", context.getPackageName());
			if (arrayId != 0) {
				int subcatIconArrayId = context.getResources().getIdentifier(catValues[i]+"_icons", "array", context.getPackageName());
				String[] subCats = context.getResources().getStringArray(arrayId);
				String[] tempIcons = context.getResources().getStringArray(subcatIconArrayId);
				int[] subCatIcons = new int[tempIcons.length];
				for (int j=0; j<tempIcons.length; j++)
					subCatIcons[j] = context.getResources().getIdentifier(tempIcons[j], "drawable", context.getPackageName());
				for (int j=0; j<subCats.length; j++) {
					values.clear();
					values.put("subcategory_name", subCats[j]);
					values.put("subcategory_id", subCats[j]);
					values.put("category_id", catValues[i]);
					values.put("icon", subCatIcons[j]);
					db.insert("subcategory_table", null, values);
				}
			}
		}
	}

	private void initIncomesAndExpanses(SQLiteDatabase db) {
		Cursor catCursor = db.query("category_table", null, null, null, null, null, null);
		Cursor subcatCursor = db.query("subcategory_table", null, null, null, null, null, null);
		ArrayList<RootCategory> categories = new ArrayList<RootCategory>();
		catCursor.moveToFirst();
		while(!catCursor.isAfterLast()) {
			RootCategory newCategory = new RootCategory();
			newCategory.setName(catCursor.getString(catCursor.getColumnIndex("category_name")));
			String catId = catCursor.getString(catCursor.getColumnIndex("category_id"));
			newCategory.setId(catId);
			newCategory.setType(catCursor.getInt(catCursor.getColumnIndex("category_type")));
			newCategory.setIcon(catCursor.getString(catCursor.getColumnIndex("icon")));
			subcatCursor.moveToFirst();
			ArrayList<SubCategory> subCats = new ArrayList<SubCategory>();
			while(!subcatCursor.isAfterLast()) {
				if (subcatCursor.getString(subcatCursor.getColumnIndex("category_id")).matches(catId)) {
					SubCategory newSubCategory = new SubCategory();
					newSubCategory.setName(subcatCursor.getString(subcatCursor.getColumnIndex("subcategory_name")));
					newSubCategory.setId(subcatCursor.getString(subcatCursor.getColumnIndex("subcategory_id")));
					newSubCategory.setParentId(catId);
					newSubCategory.setIcon(subcatCursor.getString(subcatCursor.getColumnIndex("icon")));
					subCats.add(newSubCategory);
				}
				subcatCursor.moveToNext();
			}
			newCategory.setSubCategories(subCats);
			categories.add(newCategory);
			catCursor.moveToNext();
		}
		catCursor.close();
		subcatCursor.close();
		ArrayList<RootCategory> incomes = new ArrayList<>();
		ArrayList<RootCategory> expanses = new ArrayList<>();
		for (int i=0; i<categories.size(); i++) {
			if (categories.get(i).getType() == PocketAccounterGeneral.INCOME)
				incomes.add(categories.get(i));
			else
				expanses.add(categories.get(i));
		}
		while(incomes.size() < PocketAccounterGeneral.INCOME_BUTTONS_COUNT)
			incomes.add(null);
		while (expanses.size() < PocketAccounterGeneral.EXPANCE_BUTTONS_COUNT)
			expanses.add(null);
		ContentValues values = new ContentValues();
		for (int i=0; i<incomes.size(); i++) {
			if (incomes.get(i) == null) {
				values.put("category_name", context.getResources().getString(R.string.no_category));
				db.insert("incomes_table", null, values);
				continue;
			}
			values.put("category_name", incomes.get(i).getName());
			values.put("category_id", incomes.get(i).getId());
			values.put("category_type", incomes.get(i).getType());
			values.put("icon", incomes.get(i).getIcon());
			db.insert("incomes_table", null, values);
		}
		values.clear();
		for (int i=0; i<expanses.size(); i++) {
			if (expanses.get(i) == null) {
				values.put("category_name", context.getResources().getString(R.string.no_category));
				db.insert("expanses_table", null, values);
				continue;
			}
			values.put("category_name", expanses.get(i).getName());
			values.put("category_id", expanses.get(i).getId());
			values.put("category_type", expanses.get(i).getType());
			values.put("icon", expanses.get(i).getIcon());
			db.insert("expanses_table", null, values);
		}
	}

	public void saveIncomes(ArrayList<RootCategory> incomes) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try{
			ContentValues values = new ContentValues();
			db.execSQL("DELETE FROM incomes_table");
			for (int i=0; i<incomes.size(); i++) {
				if (incomes.get(i) == null) {
					values.put("category_name", context.getResources().getString(R.string.no_category));
					db.insert("incomes_table", null, values);
					continue;
				}
				values.put("category_name", incomes.get(i).getName());
				values.put("category_id", incomes.get(i).getId());
				values.put("category_type", incomes.get(i).getType());
				values.put("icon", incomes.get(i).getIcon());
				db.insert("incomes_table", null, values);
			}

			db.setTransactionSuccessful();
		}
		catch (Exception o){
			o.printStackTrace();
		}
		finally {
			db.endTransaction();
			db.close();

		}

	}

	public void saveExpanses(ArrayList<RootCategory> expanses) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try{

			ContentValues values = new ContentValues();
			db.execSQL("DELETE FROM expanses_table");
			for (int i=0; i<expanses.size(); i++) {
				if (expanses.get(i) == null) {
					values.put("category_name", context.getResources().getString(R.string.no_category));
					db.insert("expanses_table", null, values);
//				continue;
				} else {
					values.put("category_name", expanses.get(i).getName());
					values.put("category_id", expanses.get(i).getId());
					values.put("category_type", expanses.get(i).getType());
					values.put("icon", expanses.get(i).getIcon());
					db.insert("expanses_table", null, values);
				}
			}
			db.setTransactionSuccessful();
		}
		catch (Exception o){
			o.printStackTrace();
		}
		finally {
			db.endTransaction();
			db.close();
		}

	}

	public ArrayList<RootCategory> loadIncomes() {
		ArrayList<RootCategory> result = new ArrayList<RootCategory>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("incomes_table", null, null, null, null, null, null);
		cursor.moveToFirst();

		while(!cursor.isAfterLast()) {
			RootCategory newCategory = new RootCategory();
			if (cursor.getString(cursor.getColumnIndex("category_name")).matches(context.getResources().getString(R.string.no_category))) {
				result.add(null);
				cursor.moveToNext();
				continue;
			}
			newCategory.setName(cursor.getString(cursor.getColumnIndex("category_name")));
			newCategory.setId(cursor.getString(cursor.getColumnIndex("category_id")));
			newCategory.setType(cursor.getInt(cursor.getColumnIndex("category_type")));
			newCategory.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
			result.add(newCategory);
			cursor.moveToNext();
		}
		return result;
	}

	public ArrayList<RootCategory> loadExpanses() {
		ArrayList<RootCategory> result = new ArrayList<RootCategory>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("expanses_table", null, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			RootCategory newCategory = new RootCategory();
			if (cursor.getString(cursor.getColumnIndex("category_name")).matches(context.getResources().getString(R.string.no_category))) {
				result.add(null);
//				cursor.moveToNext();
//				continue;
			} else {
				newCategory.setName(cursor.getString(cursor.getColumnIndex("category_name")));
				newCategory.setId(cursor.getString(cursor.getColumnIndex("category_id")));
				newCategory.setType(cursor.getInt(cursor.getColumnIndex("category_type")));
				newCategory.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
				result.add(newCategory);
			}
			cursor.moveToNext();
		}
		return result;
	}


	public void saveDatasToCurrencyTable(ArrayList<Currency> currencies) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();

		try{

			db.execSQL("DELETE FROM currency_table");
			db.execSQL("DELETE FROM currency_costs_table");
			for (int i=0; i<currencies.size(); i++) {
				ContentValues values = new ContentValues();
				values.put("currency_name", currencies.get(i).getName());
				values.put("currency_sign", currencies.get(i).getAbbr());
				values.put("currency_id", currencies.get(i).getId());
				values.put("currency_main", currencies.get(i).getMain());
				db.insert("currency_table", null, values);
				for (int j=0; j<currencies.get(i).getCosts().size(); j++) {
					values.clear();
					values.put("currency_id", currencies.get(i).getId());
					values.put("date", dateFormat.format(currencies.get(i).getCosts().get(j).getDay().getTime()));
					values.put("cost", currencies.get(i).getCosts().get(j).getCost());
					db.insert("currency_costs_table", null, values);
				}
			}
			db.setTransactionSuccessful();
		}
		catch (Exception o){
			o.printStackTrace();
		}
		finally {
			db.endTransaction();
			db.close();
		}


	}

	//loading currencies
	public ArrayList<Currency> loadCurrencies() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor curCursor = db.query("currency_table", null, null, null, null, null, null, null);
		Cursor curCostCursor = db.query("currency_costs_table", null, null, null, null, null, null, null);
		ArrayList<Currency> result = new ArrayList<Currency>();
		curCursor.moveToFirst();
		while (!curCursor.isAfterLast()) {
			Currency newCurrency = new Currency(curCursor.getString(curCursor.getColumnIndex("currency_name")));
			newCurrency.setAbbr(curCursor.getString(curCursor.getColumnIndex("currency_sign")));
			String currId = curCursor.getString(curCursor.getColumnIndex("currency_id"));
			newCurrency.setId(currId);
			newCurrency.setMain(curCursor.getInt(curCursor.getColumnIndex("currency_main"))!=0);
			curCostCursor.moveToFirst();
			while(!curCostCursor.isAfterLast()) {
				if (curCostCursor.getString(curCostCursor.getColumnIndex("currency_id")).matches(currId)) {
					CurrencyCost newCurrencyCost = new CurrencyCost();
					try {
						Calendar day = Calendar.getInstance();
						day.setTime(dateFormat.parse(curCostCursor.getString(curCostCursor.getColumnIndex("date"))));
						newCurrencyCost.setDay(day);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					newCurrencyCost.setCost(curCostCursor.getDouble(curCostCursor.getColumnIndex("cost")));
					newCurrency.getCosts().add(newCurrencyCost);
				}
				curCostCursor.moveToNext();
			}
			result.add(newCurrency);
			curCursor.moveToNext();
		}
		curCostCursor.close();
		curCursor.close();
		return result;
	}
	//----------Currencies end -------------

	//----------Categories begin------------
	//saving categories
	public void saveDatasToCategoryTable(ArrayList<RootCategory> categories) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();

		try{

			db.execSQL("DELETE FROM category_table");
			db.execSQL("DELETE FROM subcategory_table");
			for (int i=0; i<categories.size(); i++) {
				ContentValues values = new ContentValues();
				values.put("category_name", categories.get(i).getName());
				values.put("category_id", categories.get(i).getId());
				values.put("category_type", categories.get(i).getType());
				values.put("icon", categories.get(i).getIcon());
				db.insert("category_table", null, values);
				for (int j=0; j<categories.get(i).getSubCategories().size(); j++) {
					values.clear();
					values.put("subcategory_name", categories.get(i).getSubCategories().get(j).getName());
					values.put("subcategory_id", categories.get(i).getSubCategories().get(j).getId());
					values.put("category_id", categories.get(i).getId());
					values.put("icon", categories.get(i).getSubCategories().get(j).getIcon());
					db.insert("subcategory_table", null, values);
				}
			}
			db.setTransactionSuccessful();
		}
		catch (Exception o){
			o.printStackTrace();
		}
		finally {
			db.endTransaction();
			db.close();
		}

	}
	//loading categories
	public ArrayList<RootCategory> loadCategories() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor catCursor = db.query("category_table", null, null, null, null, null, null);
		Cursor subcatCursor = db.query("subcategory_table", null, null, null, null, null, null);
		ArrayList<RootCategory> result = new ArrayList<RootCategory>();
		catCursor.moveToFirst();
		while(!catCursor.isAfterLast()) {
			RootCategory newCategory = new RootCategory();
			newCategory.setName(catCursor.getString(catCursor.getColumnIndex("category_name")));
			String catId = catCursor.getString(catCursor.getColumnIndex("category_id"));
			newCategory.setId(catId);
			newCategory.setType(catCursor.getInt(catCursor.getColumnIndex("category_type")));
			newCategory.setIcon(catCursor.getString(catCursor.getColumnIndex("icon")));
			subcatCursor.moveToFirst();
			ArrayList<SubCategory> subCats = new ArrayList<SubCategory>();
			while(!subcatCursor.isAfterLast()) {
				if (subcatCursor.getString(subcatCursor.getColumnIndex("category_id")).matches(catId)) {
					SubCategory newSubCategory = new SubCategory();
					newSubCategory.setName(subcatCursor.getString(subcatCursor.getColumnIndex("subcategory_name")));
					newSubCategory.setId(subcatCursor.getString(subcatCursor.getColumnIndex("subcategory_id")));
					newSubCategory.setParentId(catId);
					newSubCategory.setIcon(subcatCursor.getString(subcatCursor.getColumnIndex("icon")));
					subCats.add(newSubCategory);
				}
				subcatCursor.moveToNext();
			}
			newCategory.setSubCategories(subCats);
			result.add(newCategory);
			catCursor.moveToNext();
		}
		catCursor.close();
		subcatCursor.close();
		return result;
	}
	//----------Categories end--------------

	//----------Purses begin----------------
	//saving purses
	public void saveDatasToAccountTable(ArrayList<Account> purses) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();
		try{
			db.execSQL("DELETE FROM account_table");
			for (int i=0; i<purses.size(); i++) {
				ContentValues values = new ContentValues();
				values.put("account_name", purses.get(i).getName());
				values.put("account_id", purses.get(i).getId());
				values.put("icon", purses.get(i).getIcon());
				values.put("start_amount", purses.get(i).getAmount());
				values.put("currency_id", purses.get(i).getCurrency().getId());
				values.put("is_limited", purses.get(i).isLimited());
				values.put("limit_amount", purses.get(i).getLimitSum());
				db.insert("account_table", null, values);
			}
			db.setTransactionSuccessful();
		}
		catch (Exception o){
			o.printStackTrace();
		}
		finally {
			db.endTransaction();
			db.close();
		}

	}
	//get all purses
	public ArrayList<Account> loadAccounts() {
		ArrayList<Account> result = new ArrayList<Account>();
		ArrayList<Currency> currencies = loadCurrencies();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("account_table", null, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			Account newAccount = new Account();
			newAccount.setName(cursor.getString(cursor.getColumnIndex("account_name")));
			newAccount.setId(cursor.getString(cursor.getColumnIndex("account_id")));
			newAccount.setIcon(cursor.getInt(cursor.getColumnIndex("icon")));
			newAccount.setAmount(cursor.getDouble(cursor.getColumnIndex("start_amount")));
			String currencyId = cursor.getString(cursor.getColumnIndex("currency_id"));
			for (Currency currency:currencies) {
				if (currency.getId().matches(currencyId)) {
					newAccount.setCurrency(currency);
					break;
				}
			}
			newAccount.setLimited(cursor.getInt(cursor.getColumnIndex("is_limited")) != 0);
			newAccount.setLimitSum(cursor.getDouble(cursor.getColumnIndex("limit_amount")));
			result.add(newAccount);
			cursor.moveToNext();
		}
		return result;
	}
	//----------Purses end------------------
	//----------daily records begin---------
	//	saving daily_record_table
	public void saveDatasToDailyRecordTable(ArrayList<FinanceRecord> records) {

		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();

		try{

			db.execSQL("DELETE FROM daily_record_table");
			ContentValues values = new ContentValues();
			for (int i=0; i<records.size(); i++) {
				Calendar cal = records.get(i).getDate();
				String date = dateFormat.format(cal.getTime());
				values.put("date", date);
				values.put("category_id", records.get(i).getCategory().getId());
				if (records.get(i).getSubCategory() == null)
					values.put("subcategory_id", context.getResources().getString(R.string.no_category));
				else
					values.put("subcategory_id", records.get(i).getSubCategory().getId());
				values.put("account_id", records.get(i).getAccount().getId());
				values.put("currency_id", records.get(i).getCurrency().getId());
				values.put("record_id", records.get(i).getRecordId());
				values.put("amount", records.get(i).getAmount());
				db.insert("daily_record_table", null, values);
			}
			db.setTransactionSuccessful();
		}
		catch (Exception o){
			o.printStackTrace();
		}
		finally {
			db.endTransaction();
			db.close();
		}

	}
	//laoding datas from daily_record_compound_table
	public ArrayList<FinanceRecord> loadDailyRecords() {
		ArrayList<Currency> currencies = loadCurrencies();
		ArrayList<RootCategory> categories = loadCategories();
		ArrayList<Account> purses = loadAccounts();
		ArrayList<FinanceRecord> result = new ArrayList<FinanceRecord>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("daily_record_table", null, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			FinanceRecord newRecord = new FinanceRecord();
			Calendar cal = Calendar.getInstance();
			String date = cursor.getString(cursor.getColumnIndex("date"));
			try {
				cal.setTime(dateFormat.parse(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			newRecord.setDate(cal);
			for (int i=0; i<categories.size(); i++) {
				if (cursor.getString(cursor.getColumnIndex("category_id")).equals(categories.get(i).getId())) {
					newRecord.setCategory(categories.get(i));
					if (cursor.getString(cursor.getColumnIndex("subcategory_id")).matches(context.getResources().getString(R.string.no_category))) {
						newRecord.setSubCategory(null);
						break;
					}
					for (int j=0; j<categories.get(i).getSubCategories().size(); j++) {
						if (cursor.getString(cursor.getColumnIndex("subcategory_id")).matches(categories.get(i).getSubCategories().get(j).getId()))
							newRecord.setSubCategory(categories.get(i).getSubCategories().get(j));
					}
					break;
				}
			}
			for (int i=0; i<purses.size(); i++) {
				if (cursor.getString(cursor.getColumnIndex("account_id")).matches(purses.get(i).getId()))
					newRecord.setAccount(purses.get(i));
			}
			for (int i=0; i<currencies.size(); i++) {
				if (cursor.getString(cursor.getColumnIndex("currency_id")).matches(currencies.get(i).getId()))
					newRecord.setCurrency(currencies.get(i));
			}
			newRecord.setRecordId(cursor.getString(cursor.getColumnIndex("record_id")));
			newRecord.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
			result.add(newRecord);
			cursor.moveToNext();
		}
		return result;
	}
	public ArrayList<DebtBorrow> loadDebtBorrows() {
		ArrayList<DebtBorrow> result = new ArrayList();
		ArrayList<Account> accounts = loadAccounts();
		SQLiteDatabase db = getReadableDatabase();
		Cursor dbCursor = db.query("debt_borrow_table", null, null, null, null, null, null);
		Cursor reckCursor = db.query("debtborrow_recking_table", null, null, null, null, null, null);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		dbCursor.moveToFirst();
		while (!dbCursor.isAfterLast()) {
			DebtBorrow newDebtBorrow = new DebtBorrow();
			Person newPerson = new Person();
			newPerson.setName(dbCursor.getString(dbCursor.getColumnIndex("person_name")));
			newPerson.setPhoneNumber(dbCursor.getString(dbCursor.getColumnIndex("person_number")));
			newPerson.setPhoto(dbCursor.getString(dbCursor.getColumnIndex("photo_id")));
			newDebtBorrow.setPerson(newPerson);
			try {
				Calendar takenCalendar = Calendar.getInstance();
				Calendar returnCalendar = Calendar.getInstance();
				takenCalendar.setTime(dateFormat.parse(dbCursor.getString(dbCursor.getColumnIndex("taken_date"))));
				if (dbCursor.getString(dbCursor.getColumnIndex("return_date")).matches(""))
					returnCalendar = null;
				else
					returnCalendar.setTime(dateFormat.parse(dbCursor.getString(dbCursor.getColumnIndex("return_date"))));
				newDebtBorrow.setTakenDate(takenCalendar);
				newDebtBorrow.setReturnDate(returnCalendar);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String accountId = dbCursor.getString(dbCursor.getColumnIndex("account_id"));
			String currencyId = dbCursor.getString(dbCursor.getColumnIndex("currency_id"));
			for (int i=0; i<accounts.size(); i++) {
				if (accounts.get(i).getId().matches(accountId)) {
					newDebtBorrow.setAccount(accounts.get(i));
					break;
				}
			}
			newDebtBorrow.setCalculate(dbCursor.getInt(dbCursor.getColumnIndex("calculate")) == 0 ? false : true);
			ArrayList<Currency> currencies = loadCurrencies();
			for (Currency cr : currencies) {
				if (cr.getId().equals(currencyId)) {
					newDebtBorrow.setCurrency(cr);
					break;
				}
			}
			newDebtBorrow.setAmount(dbCursor.getDouble(dbCursor.getColumnIndex("amount")));
			newDebtBorrow.setType(dbCursor.getInt(dbCursor.getColumnIndex("type")));
			newDebtBorrow.setTo_archive(dbCursor.getInt(dbCursor.getColumnIndex("to_archive")) == 0 ? false : true);
			String id = dbCursor.getString(dbCursor.getColumnIndex("id"));
			newDebtBorrow.setId(id);
			reckCursor.moveToFirst();
			ArrayList<Recking> list = new ArrayList<Recking>();
			while (!reckCursor.isAfterLast()) {
				if (id.matches(reckCursor.getString(reckCursor.getColumnIndex("id")))) {
					list.add(new Recking(reckCursor.getString(reckCursor.getColumnIndex("pay_date")),
							reckCursor.getDouble(reckCursor.getColumnIndex("amount")), id,
							reckCursor.getString(reckCursor.getColumnIndex("account_id")),
							reckCursor.getString(reckCursor.getColumnIndex("comment"))
					));
				}
				reckCursor.moveToNext();
			}
			newDebtBorrow.setInfo(dbCursor.getString(dbCursor.getColumnIndex("empty")));
			newDebtBorrow.setReckings(list);
			result.add(newDebtBorrow);
			dbCursor.moveToNext();
		}
		return result;
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		Log.d("sss", "onUpgrade");
		if (arg1 == 1 && arg2 == 4) {
			Log.d("sss", "1->4");
			db.execSQL("DROP TABLE planning_table");
			db.execSQL("DROP TABLE debt_table");
			db.execSQL("DROP TABLE borrow_table");
			db.execSQL("DROP TABLE debt_archive_table");
			db.execSQL("DROP TABLE currency_table");
			db.execSQL("DROP TABLE category_table");
			db.execSQL("DROP TABLE purse_table");
			db.execSQL("DROP TABLE daily_record_table");
			db.execSQL("DROP TABLE currency_exchange_table");
			//currency_table
			db.execSQL("create table currency_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "currency_name TEXT,"
					+ "currency_sign TEXT,"
					+ "currency_id TEXT,"
					+ "currency_main INTEGER,"
					+ "empty TEXT"
					+ ");");

			//currency costs
			db.execSQL("create table currency_costs_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "currency_id TEXT,"
					+ "date TEXT,"
					+ "cost REAL,"
					+ "empty TEXT"
					+ ");");

			//category_table
			db.execSQL("create table category_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "category_name TEXT,"
					+ "category_id TEXT,"
					+ "category_type INTEGER,"
					+ "icon INTEGER,"
					+ "empty TEXT"
					+ ");");

			db.execSQL("create table incomes_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "category_name TEXT,"
					+ "category_id TEXT,"
					+ "category_type INTEGER,"
					+ "icon INTEGER,"
					+ "empty TEXT"
					+ ");");

			db.execSQL("create table expanses_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "category_name TEXT,"
					+ "category_id TEXT,"
					+ "category_type INTEGER,"
					+ "icon INTEGER,"
					+ "empty TEXT"
					+ ");");

			//subcategries table
			db.execSQL("create table subcategory_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "subcategory_name TEXT,"
					+ "subcategory_id TEXT,"
					+ "category_id TEXT,"
					+ "icon INTEGER,"
					+ "empty TEXT"
					+ ");");

			//account table
			db.execSQL("CREATE TABLE account_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "account_name TEXT,"
					+ "account_id TEXT,"
					+ "icon INTEGER,"
					+ "empty TEXT"
					+ ");");
			//daily_record_table
			db.execSQL("CREATE TABLE daily_record_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "date TEXT,"
					+ "category_id TEXT,"
					+ "subcategory_id TEXT,"
					+ "account_id TEXT,"
					+ "currency_id TEXT,"
					+ "record_id TEXT, "
					+ "amount REAL, "
					+ "empty TEXT"
					+ ");");
			//debt_borrow_table
			db.execSQL("CREATE TABLE debt_borrow_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "person_name TEXT,"
					+ "person_number TEXT,"
					+ "taken_date TEXT,"
					+ "return_date TEXT,"
					+ "calculate INTEGER,"
					+ "to_archive INTEGER,"
					+ "type INTEGER,"
					+ "account_id TEXT,"
					+ "currency_id TEXT,"
					+ "amount REAL,"
					+ "id TEXT,"
					+ "photo_id TEXT,"
					+ "empty TEXT"
					+ ");");
			//debtborrow recking table
			db.execSQL("CREATE TABLE debtborrow_recking_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "pay_date TEXT,"
					+ "amount REAL,"
					+ "id TEXT,"
					+ "account_id TEXT,"
					+ "comment TEXT,"
					+ "empty TEXT"
					+ ");");
			//credit
			db.execSQL("CREATE TABLE credit_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "credit_name TEXT,"
					+ "icon_id INTEGER,"
					+ "key_for_include INTEGER,"
					+ "key_for_archive INTEGER,"
					+ "taken_date TEXT,"
					+ "percent REAL,"
					+ "percent_interval TEXT,"
					+ "period_time TEXT,"
					+ "period_time_tip TEXT," //man qoshdim p.s. sardor
					+ "credit_id TEXT,"
					+ "credit_value REAL,"
					+ "credit_value_with_percent REAL,"
					+ "currency_id TEXT,"
					+ "empty TEXT"
					+ ");");
			db.execSQL("CREATE TABLE credit_archive_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "credit_name TEXT,"
					+ "icon_id INTEGER,"
					+ "key_for_include INTEGER,"
					+ "key_for_archive INTEGER,"
					+ "taken_date TEXT,"
					+ "percent REAL,"
					+ "percent_interval TEXT,"
					+ "period_time TEXT,"
					+ "period_time_tip TEXT," //man qoshdim p.s. sardor
					+ "credit_id TEXT,"
					+ "credit_value REAL,"
					+ "credit_value_with_percent REAL,"
					+ "currency_id TEXT, "
					+ "empty TEXT"
					+ ");");
			//recking_of_credit
			db.execSQL("CREATE TABLE credit_recking_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "pay_date TEXT,"
					+ "amount REAL,"
					+ "account_id TEXT,"
					+ "comment TEXT,"
					+ "credit_id TEXT, "
					+ "empty TEXT"
					+ ");");
			db.execSQL("CREATE TABLE credit_recking_table_arch ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "pay_date TEXT,"
					+ "amount REAL,"
					+ "account_id TEXT,"
					+ "comment TEXT,"
					+ "credit_id TEXT,"
					+ "empty TEXT"
					+ ");");
			db.execSQL("CREATE TABLE sms_parsing_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "number TEXT,"
					+ "income_words TEXT,"
					+ "expense_words TEXT,"
					+ "amount_words TEXT,"
					+ "account_id TEXT,"
					+ "currency_id TEXT,"
					+ "type INTEGER,"
					+ "empty TEXT"
					+ ");");
			initCurrencies(db);
			initDefault(db);
			initIncomesAndExpanses(db);
			initAccounts(db);
			upgradeFromThreeToFour(db);
		}
		if (arg1 == 2 && arg2 == 4) {
			Log.d("sss", "2->4");
			db.execSQL("CREATE TABLE sms_parsing_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "number TEXT,"
					+ "income_words TEXT,"
					+ "expense_words TEXT,"
					+ "amount_words TEXT,"
					+ "account_id TEXT,"
					+ "currency_id TEXT,"
					+ "type INTEGER,"
					+ "empty TEXT"
					+ ");");
			upgradeFromThreeToFour(db);
		}
		if (arg1 == 3 && arg2 == 4) {
			Log.d("sss", "3->4");
			upgradeFromThreeToFour(db);
		}
		if (arg1 == 4 && arg2 == 5) {
			ArrayList<Account> result = new ArrayList<Account>();
			Cursor cursor = db.query("account_table", null, null, null, null, null, null);
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				Account newAccount = new Account();
				newAccount.setName(cursor.getString(cursor.getColumnIndex("account_name")));
				newAccount.setId(cursor.getString(cursor.getColumnIndex("account_id")));
				newAccount.setIcon(cursor.getInt(cursor.getColumnIndex("icon")));
				result.add(newAccount);
				cursor.moveToNext();
			}
			db.execSQL("DROP TABLE account_table");
			//account table
			db.execSQL("CREATE TABLE account_table ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "account_name TEXT,"
					+ "account_id TEXT,"
					+ "icon INTEGER,"
					+ "start_amount REAL,"
					+ "currency_id TEXT,"
					+ "is_limited INTEGER,"
					+ "limit_amount REAL"
					+ ");");
		}
	}
	private void upgradeFromThreeToFour(SQLiteDatabase db) {
		String[] resCatsId = context.getResources().getStringArray(R.array.cat_values);
		String[] resCatIcons = context.getResources().getStringArray(R.array.cat_icons);
		String[] allIcons = context.getResources().getStringArray(R.array.icons);
		int[] allIconsId = new int[allIcons.length];
		for (int i=0; i<allIcons.length; i++)
			allIconsId[i] = context.getResources().getIdentifier(allIcons[i], "drawable", context.getPackageName());
		Cursor catsCursor = db.query("category_table", null, null, null, null, null, null);
		Cursor subCatsCursor = db.query("subcategory_table", null, null, null, null, null, null);
		catsCursor.moveToFirst();
		ArrayList<RootCategory> categories = new ArrayList<>();
		while (!catsCursor.isAfterLast()) {
			RootCategory category = new RootCategory();
			category.setName(catsCursor.getString(catsCursor.getColumnIndex("category_name")));
			category.setType(catsCursor.getInt(catsCursor.getColumnIndex("category_type")));
			String id = catsCursor.getString(catsCursor.getColumnIndex("category_id"));
			boolean catIdFound = false;
			int pos = 0;
			for (int i=0; i<resCatsId.length; i++) {
				if (resCatsId[i].matches(id)) {
					catIdFound = true;
					pos = i;
					break;
				}
			}
			ArrayList<SubCategory> subCategories = new ArrayList<>();
			if (catIdFound) {
				category.setIcon(resCatIcons[pos]);
				subCatsCursor.moveToFirst();
				while(!subCatsCursor.isAfterLast()) {
					if (id.matches(subCatsCursor.getString(subCatsCursor.getColumnIndex("category_id")))) {
						SubCategory subCategory = new SubCategory();
						subCategory.setName(subCatsCursor.getString(subCatsCursor.getColumnIndex("subcategory_name")));
						String subCatId = subCatsCursor.getString(subCatsCursor.getColumnIndex("subcategory_id"));
						int subcatIconArrayId = context.getResources().getIdentifier(id, "array", context.getPackageName());
						if (subcatIconArrayId != 0) {
							boolean q = false;
							int s = 0;
							String[] scn = context.getResources().getStringArray(subcatIconArrayId);
							for (int i=0; i<scn.length; i++) {
								if (scn[i].matches(subCatId)) {
									q = true;
									s = i;
									break;
								}
							}
							if(q){
								int h = context.getResources().getIdentifier(id+"_icons", "array", context.getPackageName());
								String[] subCatsId = context.getResources().getStringArray(h);
								subCategory.setIcon(subCatsId[s]);
							}
							else {
								int subCatIconId = subCatsCursor.getInt(subCatsCursor.getColumnIndex("icon"));
								boolean f = false;
								int p = 0;
								for (int i=0; i<allIconsId.length; i++) {
									if (subCatIconId == allIconsId[i]) {
										f = true;
										p = i;
										break;
									}
								}
								if (f)
									subCategory.setIcon(allIcons[p]);
								else
									subCategory.setIcon("category_not_selected");
							}
						} else {
							boolean s = false;
							int a = 0;
							for (int i=0; i<allIconsId.length; i++) {
								if (allIconsId[i] == subCatsCursor.getInt(subCatsCursor.getColumnIndex("icon"))) {
									s = true;
									a = i;
									break;
								}
							}
							if (s)
								subCategory.setIcon(allIcons[a]);
							else
								subCategory.setIcon("category_not_selected");
						}
						subCategory.setId(subCatId);
						subCategories.add(subCategory);
					}
					subCatsCursor.moveToNext();
				}
			}
			else {
				int iconId = catsCursor.getInt(catsCursor.getColumnIndex("icon"));
				boolean found = false;
				pos = 0;
				for (int i=0; i<allIconsId.length; i++) {
					if (allIconsId[i] == iconId) {
						found = true;
						pos = i;
						break;
					}
				}
				if (found)
					category.setIcon(allIcons[pos]);
				else
					category.setIcon("category_not_selected");
				subCatsCursor.moveToFirst();
				while (!subCatsCursor.isAfterLast()) {
					if (id.matches(subCatsCursor.getString(subCatsCursor.getColumnIndex("category_id")))) {
						SubCategory subCategory = new SubCategory();
						subCategory.setName(subCatsCursor.getString(subCatsCursor.getColumnIndex("subcategory_name")));
						String subCatId = subCatsCursor.getString(subCatsCursor.getColumnIndex("subcategory_id"));
						subCategory.setId(subCatId);
						iconId = subCatsCursor.getInt(subCatsCursor.getColumnIndex("icon"));
						found = false;
						pos = 0;
						for (int i=0; i<allIconsId.length; i++) {
							if (allIconsId[i] == iconId) {
								found = true;
								pos = i;
								break;
							}
						}
						if (found)
							subCategory.setIcon(allIcons[pos]);
						else
							subCategory.setIcon("category_not_selected");
						subCategories.add(subCategory);
					}
					subCatsCursor.moveToNext();
				}
			}
			category.setId(id);
			category.setSubCategories(subCategories);
			categories.add(category);
			catsCursor.moveToNext();
		}
		db.execSQL("DROP TABLE category_table");
		db.execSQL("DROP TABLE subcategory_table");
		db.execSQL("create table category_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "category_name TEXT,"
				+ "category_id TEXT,"
				+ "category_type INTEGER,"
				+ "icon TEXT,"
				+ "empty TEXT"
				+ ");");
		//subcategries table
		db.execSQL("create table subcategory_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "subcategory_name TEXT,"
				+ "subcategory_id TEXT,"
				+ "category_id TEXT,"
				+ "icon TEXT,"
				+ "empty TEXT"
				+ ");");
		//saving categories begin
		for (int i=0; i<categories.size(); i++) {
			ContentValues values = new ContentValues();
			values.put("category_name", categories.get(i).getName());
			values.put("category_id", categories.get(i).getId());
			values.put("category_type", categories.get(i).getType());
			values.put("icon", categories.get(i).getIcon());
			db.insert("category_table", null, values);
			for (int j=0; j<categories.get(i).getSubCategories().size(); j++) {
				values.clear();
				values.put("subcategory_name", categories.get(i).getSubCategories().get(j).getName());
				values.put("subcategory_id", categories.get(i).getSubCategories().get(j).getId());
				values.put("category_id", categories.get(i).getId());
				values.put("icon", categories.get(i).getSubCategories().get(j).getIcon());
				db.insert("subcategory_table", null, values);
			}
		}
		//saving categories end
		Cursor incomesCursor = db.query("incomes_table", null, null, null, null, null, null);
		Log.d("sss", incomesCursor.getCount()+"");
		ArrayList<String> incomesId = new ArrayList<>();
		incomesCursor.moveToFirst();
		while (!incomesCursor.isAfterLast()) {
			incomesId.add(incomesCursor.getString(incomesCursor.getColumnIndex("category_id")));
			incomesCursor.moveToNext();
		}
		db.execSQL("DROP TABLE incomes_table");
		db.execSQL("create table incomes_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "category_name TEXT,"
				+ "category_id TEXT,"
				+ "category_type INTEGER,"
				+ "icon TEXT,"
				+ "empty TEXT"
				+ ");");
		for (int i=0; i<incomesId.size(); i++) {
			ContentValues values = new ContentValues();
			if (incomesId.get(i) == null) {
				values.put("category_name", context.getResources().getString(R.string.no_category));
				db.insert("incomes_table", null, values);
				continue;
			}
			for (int j=0; j<categories.size(); j++) {
				if (incomesId.get(i).matches(categories.get(j).getId())) {
					values.put("category_name", categories.get(j).getName());
					values.put("category_id", categories.get(j).getId());
					values.put("category_type", categories.get(j).getType());
					values.put("icon", categories.get(j).getIcon());
					db.insert("incomes_table", null, values);
					break;
				}
			}
		}
		Cursor expensesCursor = db.query("expanses_table", null, null, null, null, null, null);
		ArrayList<String> expensesId = new ArrayList<>();
		expensesCursor.moveToFirst();
		while (!expensesCursor.isAfterLast()) {
			expensesId.add(expensesCursor.getString(incomesCursor.getColumnIndex("category_id")));
			expensesCursor.moveToNext();
		}

		db.execSQL("DROP TABLE expanses_table");
		db.execSQL("create table expanses_table ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "category_name TEXT,"
				+ "category_id TEXT,"
				+ "category_type INTEGER,"
				+ "icon TEXT,"
				+ "empty TEXT"
				+ ");");
		for (int i=0; i<expensesId.size(); i++) {
			ContentValues values = new ContentValues();
			if (expensesId.get(i) == null) {
				values.put("category_name", context.getResources().getString(R.string.no_category));
				db.insert("expanses_table", null, values);
				continue;
			}
			for (int j=0; j<categories.size(); j++) {
				if (expensesId.get(i).matches(categories.get(j).getId())) {
					values.put("category_name", categories.get(j).getName());
					values.put("category_id", categories.get(j).getId());
					values.put("category_type", categories.get(j).getType());
					values.put("icon", categories.get(j).getIcon());
					db.insert("expanses_table", null, values);
					break;
				}
			}
		}
	}
}