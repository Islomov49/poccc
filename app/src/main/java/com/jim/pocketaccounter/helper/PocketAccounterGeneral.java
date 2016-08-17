package com.jim.pocketaccounter.helper;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.FinanceRecord;
import com.jim.pocketaccounter.finance.RootCategory;

import java.util.Calendar;

public class PocketAccounterGeneral {
	public static final int NORMAL_MODE = 0;
	public static final int EDIT_MODE = 1;
	public static final int INCOME=0;
	public static final int EXPENSE =1;
	public static final int EXPANCE_BUTTONS_COUNT = 16;
	public static final int INCOME_BUTTONS_COUNT = 4;
	public static final int NO_MODE = 0, EXPANSE_MODE = 1, INCOME_MODE = 2;
	public static final int MAIN = 0, DETAIL = 1;
	public static final int SMS_ONLY_EXPENSE = 0, SMS_ONLY_INCOME = 1, SMS_BOTH = 2;
	public static final String EVERY_DAY="EVERY_DAY", EVERY_WEEK="EVERY_WEEK", EVERY_MONTH="EVERY_MONTH";
	public static double getCost(FinanceRecord record) {
		double amount = 0.0;
		if (record.getCurrency().getMain())
			return record.getAmount();
		double koeff = 1.0;
		long diff = record.getDate().getTimeInMillis() - record.getCurrency().getCosts().get(0).getDay().getTimeInMillis();
		if (diff < 0) {
			koeff = record.getCurrency().getCosts().get(0).getCost();
			return record.getAmount()/koeff;
		}
		int pos = 0;
		while (diff >= 0 && pos < record.getCurrency().getCosts().size()) {
			diff = record.getDate().getTimeInMillis() - record.getCurrency().getCosts().get(pos).getDay().getTimeInMillis();
			if(diff>=0)
			koeff = record.getCurrency().getCosts().get(pos).getCost();
			pos++;
		}
		amount = record.getAmount()/koeff;
		return amount;
	}
	public static double getCost(Calendar date, Currency currency, double amount) {
		if (currency.getMain()) return amount;
		double koeff = 1.0;
		long diff = date.getTimeInMillis() - currency.getCosts().get(0).getDay().getTimeInMillis();
		if (diff < 0) {
			koeff = currency.getCosts().get(0).getCost();
			return amount/koeff;
		}
		int pos = 0;
		while (diff >= 0 && pos < currency.getCosts().size()) {
			diff = date.getTimeInMillis() - currency.getCosts().get(pos).getDay().getTimeInMillis();
			if(diff>=0)
			koeff = currency.getCosts().get(pos).getCost();
			pos++;
		}
		amount = amount/koeff;
		return amount;
	}

	public static double getCost(Calendar date, Currency fromCurrency,Currency toCurrency, double amount) {
		//TODO tekwir bir yana
		if (fromCurrency.getId().matches(toCurrency.getId())) return amount;
		double tokoeff = 1.0;
		double fromkoeff2 = 1.0;
		long todiff1 = date.getTimeInMillis() - toCurrency.getCosts().get(0).getDay().getTimeInMillis();
		long fromdiff = date.getTimeInMillis() - fromCurrency.getCosts().get(0).getDay().getTimeInMillis();

		if (todiff1 < 0) {
			tokoeff = toCurrency.getCosts().get(0).getCost();
		}
		if(fromdiff < 0){
			fromkoeff2 = fromCurrency.getCosts().get(0).getCost();
		}


		int pos = 0;
		while (todiff1 >= 0 && pos < toCurrency.getCosts().size()) {
			todiff1 = date.getTimeInMillis() - toCurrency.getCosts().get(pos).getDay().getTimeInMillis();
			if(todiff1>=0)
			tokoeff = toCurrency.getCosts().get(pos).getCost();
			pos++;
		}
		pos=0;
		while (fromdiff >= 0 && pos < toCurrency.getCosts().size()) {
			fromdiff = date.getTimeInMillis() - toCurrency.getCosts().get(pos).getDay().getTimeInMillis();
			if(fromdiff>=0)
			fromkoeff2 = toCurrency.getCosts().get(pos).getCost();
			pos++;
		}


		amount = amount/fromkoeff2*tokoeff;
		return amount;
	}

	public static double calculateAction(RootCategory category, Calendar date) {
		double result = 0.0;
		if (category == null) return 0.0;
		Calendar begin = (Calendar) date.clone();
		begin.set(Calendar.HOUR_OF_DAY, 0);
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		begin.set(Calendar.MILLISECOND, 0);
		Calendar end = (Calendar) date.clone();
		end.set(Calendar.HOUR_OF_DAY, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);
		end.set(Calendar.MILLISECOND, 59);
		for (int i=0; i<PocketAccounter.financeManager.getRecords().size(); i++) {
			FinanceRecord record = PocketAccounter.financeManager.getRecords().get(i);
			if (record.getDate().compareTo(begin) >= 0 && record.getDate().compareTo(end) <= 0 &&
					record.getCategory().getId().matches(category.getId()))
				result = result + getCost(record);
		}
		double totalAmount = 0.0;
		for (int i=0; i<PocketAccounter.financeManager.getRecords().size(); i++) {
			FinanceRecord record = PocketAccounter.financeManager.getRecords().get(i);
			if (record.getDate().compareTo(begin) >= 0 && record.getDate().compareTo(end) <= 0
					&& record.getCategory().getType() == category.getType())
				totalAmount = totalAmount + getCost(record);
		}
		if (totalAmount == 0.0) return 0.0;
		result = 100*result/totalAmount;
		return result;
	}

}
