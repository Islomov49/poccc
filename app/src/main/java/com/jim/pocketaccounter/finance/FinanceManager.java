package com.jim.pocketaccounter.finance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.credit.ReckingCredit;
import com.jim.pocketaccounter.debt.DebtBorrow;
import com.jim.pocketaccounter.debt.Recking;
import com.jim.pocketaccounter.helper.PocketAccounterDatabase;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;
import com.jim.pocketaccounter.helper.SmsParseObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
	private ArrayList<BalanceObject> balanceObjectArrayLIst;
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
	public ArrayList<Double> calculateBalance(Calendar d) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String balanceSolve = prefs.getString("balance_solve", "0");
        String whole = "0", currentDay = "1";
		Calendar date = (Calendar) d.clone();
		date.set(Calendar.HOUR_OF_DAY, 23);
		date.set(Calendar.MINUTE, 59);
		date.set(Calendar.SECOND, 59);
		date.set(Calendar.MILLISECOND, 59);
		accumulateBalanceObjects();
		ArrayList<BalanceObject> objects = new ArrayList<>();
		if (balanceSolve.matches(whole)) {
			for (int i=0; i<balanceObjectArrayLIst.size(); i++) {
				if (balanceObjectArrayLIst.get(i).getCalendar().compareTo(date) <= 0)
					objects.add(balanceObjectArrayLIst.get(i));
			}
		}
		else {
			Calendar begin = (Calendar)date.clone();
			begin.set(Calendar.HOUR_OF_DAY, 0);
			begin.set(Calendar.MINUTE, 0);
			begin.set(Calendar.SECOND, 0);
			begin.set(Calendar.MILLISECOND, 0);
			for (int i=0; i<balanceObjectArrayLIst.size(); i++) {
				if (balanceObjectArrayLIst.get(i).getCalendar().compareTo(begin) >= 0 && balanceObjectArrayLIst.get(i).getCalendar().compareTo(date) <= 0)
					objects.add(balanceObjectArrayLIst.get(i));
			}
		}
		ArrayList<CurrencyAmount> accounts = new ArrayList<>();
		//calculating start money of accounts
		for (Account account : this.accounts) {
			if (account.getAmount() != 0) {
				boolean found = false;
				int pos = 0;
				for (int i=0; i<accounts.size(); i++) {
					if (accounts.get(i).getCurrency().getId().matches(account.getStartMoneyCurrency().getId())) {
						found = true;
						pos = i;
						break;
					}
				}
				if (found)
					accounts.get(pos).setAmount(accounts.get(pos).getAmount()+account.getAmount());
				else {
					CurrencyAmount currencyAmount = new CurrencyAmount();
					currencyAmount.setCurrency(account.getStartMoneyCurrency());
					currencyAmount.setAmount(account.getAmount());
					accounts.add(currencyAmount);
				}
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		for (BalanceObject object : objects) {
			Log.d("sss", format.format(object.getCalendar().getTime()) + " "+object.getSum() + " test");
			boolean found = false;
			int pos = 0;
			for (int i=0; i<accounts.size(); i++) {
				if (object.getCurrency().getId().matches(accounts.get(i).getCurrency().getId())) {
					found = true;
					pos = i;
					break;
				}
			}
			if (found) {
				//TODO currency is found
				if (object.getType() == PocketAccounterGeneral.INCOME) {
					double amount = object.getSum()+accounts.get(pos).getAmount();
					int iter = 0;
					while (amount > 0 && iter < accounts.size()) {
						if (iter == pos) {
							iter++;
							continue;
						}
						if (accounts.get(iter).getAmount() < 0) {
							double objectAmount = PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(), accounts.get(iter).getCurrency(), amount);
							if (objectAmount < Math.abs(accounts.get(iter).getAmount())) {
								accounts.get(iter).setAmount(accounts.get(iter).getAmount()+objectAmount);
								amount = 0;
							} else {
								amount = amount + PocketAccounterGeneral.getCost(object.getCalendar(), accounts.get(iter).getCurrency(), object.getCurrency(), accounts.get(iter).getAmount());
								accounts.get(iter).setAmount(0);
							}
						}
						iter++;
					}
//					if (amount != 0) {
//					}
					accounts.get(pos).setAmount(amount);
				}
				else {
					//if not enough money in found account
					if (accounts.get(pos).getAmount() <= 0) {
						double amount = object.getSum();
						boolean hasAnySumMoreThanAmount = false;
						int hasAnySumMoreThanAmountPos = 0;
						for (int i=0; i<accounts.size(); i++) {
							if (i == pos) continue;
							double findingAmount = PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(),
									accounts.get(i).getCurrency(), amount);
							if (accounts.get(i).getAmount() > findingAmount) {
								hasAnySumMoreThanAmount = true;
								hasAnySumMoreThanAmountPos = i;
								break;
							}
						}
						if (hasAnySumMoreThanAmount) {
							double findingAmount = PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(), accounts.get(hasAnySumMoreThanAmountPos).getCurrency(), amount);
							accounts.get(hasAnySumMoreThanAmountPos).setAmount(accounts.get(hasAnySumMoreThanAmountPos).getAmount()-findingAmount);
						}
						else {
							int iter = 0;
							while (amount != 0 && iter < accounts.size()) {
								if (iter == pos) {
									iter ++;
									continue;
								}
								if (accounts.get(iter).getAmount() > 0) {
									if (accounts.get(iter).getAmount() >= PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(), accounts.get(iter).getCurrency(), amount)) {
										accounts.get(iter).setAmount(accounts.get(iter).getAmount() - PocketAccounterGeneral.getCost(date, object.getCurrency(), accounts.get(iter).getCurrency(), amount));
										amount = 0;
									}
									else {
										amount = amount - PocketAccounterGeneral.getCost(object.getCalendar(), accounts.get(iter).getCurrency(), object.getCurrency(), accounts.get(iter).getAmount());
										accounts.get(iter).setAmount(0);
									}
								}
								iter++;
							}
//							if (amount != 0)
								accounts.get(pos).setAmount(accounts.get(pos).getAmount()-amount);
						}
					}
					else if(accounts.get(pos).getAmount()>0 && accounts.get(pos).getAmount() < object.getSum()){
						double amount = object.getSum()-accounts.get(pos).getAmount();
						accounts.get(pos).setAmount(0);
						boolean hasAnySumMoreThanAmount = false;
						int hasAnySumMoreThanAmountPos = 0;
						for (int i=0; i<accounts.size(); i++) {
							if (i == pos) continue;
							double findingAmount = PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(),
									accounts.get(i).getCurrency(), amount);
							if (accounts.get(i).getAmount() > findingAmount) {
								hasAnySumMoreThanAmount = true;
								hasAnySumMoreThanAmountPos = i;
								break;
							}
						}
						if (hasAnySumMoreThanAmount) {
							double findingAmount = PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(), accounts.get(hasAnySumMoreThanAmountPos).getCurrency(), amount);
							accounts.get(hasAnySumMoreThanAmountPos).setAmount(accounts.get(hasAnySumMoreThanAmountPos).getAmount()-findingAmount);
						}
						else {
							int iter = 0;
							while (amount != 0 && iter < accounts.size()) {
								if (iter == pos) {
									iter ++;
									continue;
								}
								if (accounts.get(iter).getAmount() > 0) {
									if (accounts.get(iter).getAmount() >= PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(), accounts.get(iter).getCurrency(), amount)) {
										accounts.get(iter).setAmount(accounts.get(iter).getAmount() - PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(), accounts.get(iter).getCurrency(), amount));
										amount = 0;
									}
									else {
										amount = amount - PocketAccounterGeneral.getCost(object.getCalendar(), accounts.get(iter).getCurrency(), object.getCurrency(), accounts.get(iter).getAmount());
										accounts.get(iter).setAmount(0);
									}
								}
								iter++;
							}
//							if (amount != 0)
								accounts.get(pos).setAmount(accounts.get(pos).getAmount()-amount);
						}
					} else {
						accounts.get(pos).setAmount(accounts.get(pos).getAmount()-object.getSum());
					}
				}
			}
			else {
				//TODO currency is not found
				CurrencyAmount currencyAmount = new CurrencyAmount();
				currencyAmount.setCurrency(object.getCurrency());
				if (object.getType() == PocketAccounterGeneral.INCOME) {
					double amount = object.getSum();
					int iter = 0;
					while (amount > 0 && iter < accounts.size()) {
						if (iter == pos) {
							iter++;
							continue;
						}
						if (accounts.get(iter).getAmount() < 0) {
							double objectAmount = PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(), accounts.get(iter).getCurrency(), amount);
							if (objectAmount < Math.abs(accounts.get(iter).getAmount())) {
								accounts.get(iter).setAmount(accounts.get(iter).getAmount()+objectAmount);
								amount = 0;
							} else {
								amount = amount + PocketAccounterGeneral.getCost(object.getCalendar(), accounts.get(iter).getCurrency(), object.getCurrency(), accounts.get(iter).getAmount());
								accounts.get(iter).setAmount(0);
							}
						}
						iter++;
					}
//					if (amount != 0) {
//					}
					currencyAmount.setAmount(amount);
				}
				else {
					double amount = object.getSum();
					boolean hasAnySumMoreThanAmount = false;
					int hasAnySumMoreThanAmountPos = 0;
					for (int i=0; i<accounts.size(); i++) {
						double findingAmount = PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(),
								accounts.get(i).getCurrency(), amount);
						if (accounts.get(i).getAmount() >= findingAmount) {
							hasAnySumMoreThanAmount = true;
							hasAnySumMoreThanAmountPos = i;
							break;
						}
					}
					if (hasAnySumMoreThanAmount) {
						double findingAmount = PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(), accounts.get(hasAnySumMoreThanAmountPos).getCurrency(), amount);
						accounts.get(hasAnySumMoreThanAmountPos).setAmount(accounts.get(hasAnySumMoreThanAmountPos).getAmount()-findingAmount);
					}
					else {
						int iter = 0;
						while (amount != 0 && iter < accounts.size()) {
							if (accounts.get(iter).getAmount() > 0) {
								if (accounts.get(iter).getAmount() >= PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(), accounts.get(iter).getCurrency(), amount)) {
									accounts.get(iter).setAmount(accounts.get(iter).getAmount() - PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(), accounts.get(iter).getCurrency(), amount));
									amount = 0;
								}
								else {
									amount = amount - PocketAccounterGeneral.getCost(object.getCalendar(), accounts.get(iter).getCurrency(), object.getCurrency(), accounts.get(iter).getAmount());
									accounts.get(iter).setAmount(0);
								}
							}
							iter++;
						}
						currencyAmount.setAmount(-amount);
					}
				}
				accounts.add(currencyAmount);
			}
		}
		ArrayList<Double> result = new ArrayList<>();
		Double expense = 0.0d, income = 0.0d, balance = 0.0d;
		for (BalanceObject object : objects) {
			if (object.getType() == PocketAccounterGeneral.INCOME) {
				income = income + PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(), object.getSum());
				Log.d("sss", "currency: "+object.getCurrency().getAbbr() + " amount: "+PocketAccounterGeneral.getCost(date, object.getCurrency(), object.getSum()));
			} else {
				expense = expense + PocketAccounterGeneral.getCost(object.getCalendar(), object.getCurrency(), object.getSum());
				Log.d("sss", "currency: "+object.getCurrency().getAbbr() + " amount: "+PocketAccounterGeneral.getCost(date, object.getCurrency(), object.getSum()));

			}
		}
		result.add(income);
		result.add(expense);
		for (CurrencyAmount currencyAmount : accounts) {
			Log.d("sss", currencyAmount.getCurrency().getAbbr() + " amount: "+currencyAmount.getAmount());
			balance = balance + PocketAccounterGeneral.getCost(date, currencyAmount.getCurrency(), currencyAmount.getAmount());
		}
		result.add(balance);
		return result;
	}
	private void accumulateBalanceObjects() {
		balanceObjectArrayLIst = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		for (FinanceRecord record : records) {
			BalanceObject balanceObject = new BalanceObject();
			balanceObject.setCurrency(record.getCurrency());
			balanceObject.setType(record.getCategory().getType());
			balanceObject.setSum(record.getAmount());
			balanceObject.setCalendar(record.getDate());
			balanceObjectArrayLIst.add(balanceObject);
		}
		for (DebtBorrow debtBorrow : debtBorrows) {
			if (debtBorrow.isCalculate()) {
				BalanceObject balanceObject = new BalanceObject();
				if (debtBorrow.getType() == DebtBorrow.BORROW)
					balanceObject.setType(PocketAccounterGeneral.EXPENSE);
				else
					balanceObject.setType(PocketAccounterGeneral.INCOME);
				balanceObject.setCurrency(debtBorrow.getCurrency());
				balanceObject.setSum(debtBorrow.getAmount());
				balanceObject.setCalendar(debtBorrow.getTakenDate());
				balanceObjectArrayLIst.add(balanceObject);
				for (Recking recking : debtBorrow.getReckings()) {
					balanceObject = new BalanceObject();
					if (debtBorrow.getType() == DebtBorrow.BORROW)
						balanceObject.setType(PocketAccounterGeneral.INCOME);
					else
						balanceObject.setType(PocketAccounterGeneral.EXPENSE);
					balanceObject.setCurrency(debtBorrow.getCurrency());
					balanceObject.setSum(recking.getAmount());
					Calendar cal = Calendar.getInstance();
					try {
						cal.setTime(format.parse(recking.getPayDate()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					balanceObject.setCalendar(cal);
					balanceObjectArrayLIst.add(balanceObject);
				}
			}
		}
		for (CreditDetials creditDetials : credits) {
			if (creditDetials.isKey_for_include()) {
				for (ReckingCredit reckingCredit : creditDetials.getReckings()) {
					BalanceObject balanceObject = new BalanceObject();
					balanceObject.setCurrency(creditDetials.getValyute_currency());
					balanceObject.setType(PocketAccounterGeneral.EXPENSE);
					balanceObject.setSum(reckingCredit.getAmount());
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(reckingCredit.getPayDate());
					balanceObject.setCalendar(cal);
					balanceObjectArrayLIst.add(balanceObject);
				}
			}
		}
		//sorting array
		for(int i = balanceObjectArrayLIst.size()-1 ; i > 0 ; i--){
			for(int j = 0 ; j < i ; j++){
				if( balanceObjectArrayLIst.get(j).getCalendar().compareTo(balanceObjectArrayLIst.get(j+1).getCalendar()) > 0){
					BalanceObject balanceObject = balanceObjectArrayLIst.get(j);
					balanceObjectArrayLIst.set(j, balanceObjectArrayLIst.get(j+1));
					balanceObjectArrayLIst.set(j+1, balanceObject);
				}
        	}
    	}
		for (BalanceObject balanceObject : balanceObjectArrayLIst) {
			Log.d("sss", balanceObject.getCurrency().getAbbr() + "\n" + format.format(balanceObject.getCalendar().getTime())+"\n"+
			balanceObject.getType() + "\n"+balanceObject.getSum());
		}
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