package com.jim.pocketaccounter.finance;

import android.content.Context;

import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.debt.DebtBorrow;
import com.jim.pocketaccounter.helper.PocketAccounterDatabase;
import com.jim.pocketaccounter.helper.SmsParseObject;

import java.util.ArrayList;

public class FinanceManager {
	private Context context;
	private ArrayList<Currency> currencies;
	private ArrayList<RootCategory> categories, expanses, incomes;
	private ArrayList<Account> accounts;
	private ArrayList<FinanceRecord> records;
	private ArrayList<CreditDetials> credits;
	private ArrayList<CreditDetials> creditsArchive;
	private ArrayList<SmsParseObject> objects;
	private PocketAccounterDatabase db;
	private ArrayList<DebtBorrow> debtBorrows;
	public FinanceManager(Context context) {
		this.context = context;
		db = new PocketAccounterDatabase(context);
		currencies = loadCurrencies();
		categories = loadCategories();
		accounts = loadAccounts();
		objects = loadSmsObjects();
		expanses = loadExpanses();
		incomes = loadIncomes();
		records = loadRecords();
		credits = loadCredits();
		creditsArchive=loadArchiveCredits();
		debtBorrows = loadDebtBorrows();
	}

	public ArrayList<CreditDetials> loadCredits() {
		return db.loadCredits();}
	public ArrayList<CreditDetials> getCredits() {
		return credits;}
	public void saveCredits() {db.saveDatasToCreditTable(credits);}
	public ArrayList<CreditDetials> loadArchiveCredits() {return db.loadArchiveCredits();}
	public ArrayList<CreditDetials> getArchiveCredits() {return creditsArchive;}
	public void saveArchiveCredits() {db.saveDatasToArchiveCreditTable(creditsArchive);}
	public ArrayList<Currency> getCurrencies() {
		return currencies;
	}
	public void setCurrencies(ArrayList<Currency> currencies) {
		this.currencies = currencies;
	}
	private ArrayList<Currency> loadCurrencies() {
		return db.loadCurrencies();
	}
	public ArrayList<DebtBorrow> getDebtBorrows() {
		return debtBorrows;
	}
	public void setDebtBorrows(ArrayList<DebtBorrow> debtBorrows) {
		this.debtBorrows = debtBorrows;
	}
	public Currency getMainCurrency() {
		for (int i=0; i<currencies.size(); i++) {
			if (currencies.get(i).getMain())
				return currencies.get(i);
		}
		return null;
	}
	public void saveSmsObjects() {db.saveSmsObjects(objects);}
	public ArrayList<SmsParseObject> getSmsObjects() {return objects;}
	private ArrayList<SmsParseObject> loadSmsObjects() {return db.getSmsParseObjects();}
	public ArrayList<RootCategory> getCategories() {return categories;}
	private ArrayList<RootCategory> loadCategories() {return db.loadCategories();}
	public ArrayList<Account> getAccounts() {return accounts;}
	private ArrayList<Account> loadAccounts() {return db.loadAccounts();}
	public ArrayList<FinanceRecord> getRecords() {return records; }
	public ArrayList<FinanceRecord> loadRecords() {return db.loadDailyRecords();}
	public void setRecords(ArrayList<FinanceRecord> records) {this.records = records;}
	public void saveCurrencies() {db.saveDatasToCurrencyTable(currencies);}
	public void saveAccounts() {db.saveDatasToAccountTable(accounts);}
	public void saveCategories() {db.saveDatasToCategoryTable(categories);}
	private ArrayList<RootCategory> loadExpanses() {return db.loadExpanses();}
	private ArrayList<RootCategory> loadIncomes() {return db.loadIncomes();}
	public ArrayList<RootCategory> getExpanses() {return expanses;}
	public ArrayList<RootCategory> getIncomes() {return incomes;}
	public void saveDebtBorrows () {db.saveDebtBorrowsToTable(debtBorrows);}
	public void saveRecords () {db.saveDatasToDailyRecordTable(records);}
	public void saveIncomes() {db.saveIncomes(incomes);}
	public void saveExpenses() {db.saveExpanses(expanses);}
	public ArrayList<DebtBorrow> loadDebtBorrows () {return db.loadDebtBorrows();}
	public void saveAllDatas() {
		db.saveExpanses(expanses);
		db.saveIncomes(incomes);
		db.saveDatasToCurrencyTable(currencies);
		db.saveDatasToCategoryTable(categories);
		db.saveDatasToAccountTable(accounts);
		db.saveSmsObjects(objects);
		db.saveDatasToDailyRecordTable(records);
		db.saveDatasToCreditTable(credits);
		db.saveDatasToArchiveCreditTable(creditsArchive);
		db.saveDebtBorrowsToTable(debtBorrows);
	}
}