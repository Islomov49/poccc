package com.jim.pocketaccounter.credit.notificat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.debt.DebtBorrow;
import com.jim.pocketaccounter.finance.FinanceManager;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class NotificationManagerCredit {
	
	private Context context;
	private int OK=0;
	private AlarmManager alarmManager;
	private ArrayList<CreditDetials> myCredits;
	private ArrayList<DebtBorrow> myDebdbor;
	private FinanceManager myFinance;
	private int count = 0;
	private int count_intent = 0;
	final static long forDay=1000L*60L*60L*24L;
	final static long forMoth=1000L*60L*60L*24L*30L;
	final static long forYear=1000L*60L*60L*24L*365L;
	final static long forWeek=1000L*60L*60L*24L*7L;

	public NotificationManagerCredit(Context context) {
		this.context = context;
		alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		myFinance= PocketAccounter.financeManager;
		myCredits= myFinance.getCredits();
		myDebdbor=myFinance.getDebtBorrows();
	}
		
	public void notificSetCredit() throws InterruptedException {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		String time = prefs.getString("planningNotifTime", "09:00");
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Calendar cal = Calendar.getInstance();
		Calendar morning_time = Calendar.getInstance();
		try {
			morning_time.setTime(df.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		cal.set(Calendar.HOUR_OF_DAY, morning_time.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, morning_time.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, 0);
		for (CreditDetials item : myCredits) {
			if (item.isKey_for_archive()) continue;
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			boolean isNotify = preferences.getBoolean("general_notif", true);
			Calendar returnDate= (Calendar) item.getTake_time().clone();
			long period_tip=item.getPeriod_time_tip();
			long period_voqt=item.getPeriod_time();
			int voqt_soni= (int) (period_voqt/period_tip);
			if(period_tip==forDay){
				returnDate.add(Calendar.DAY_OF_YEAR, (int) voqt_soni);
			}
			else if(period_tip==forWeek){
				returnDate.add(Calendar.WEEK_OF_YEAR, (int) voqt_soni);
			}
			else if(period_tip==forMoth){
				returnDate.add(Calendar.MONTH, (int) voqt_soni);
			}
			else {
				returnDate.add(Calendar.YEAR, (int) voqt_soni);
			}

			if (isNotify) {
				String info = item.getInfo();
				if (info == null)
					info = PocketAccounterGeneral.EVERY_WEEK + ":0";
				StringTokenizer tokenizer = new StringTokenizer(info, ":");
				ArrayList<String> a = new ArrayList<>();
				ArrayList<Calendar> calendars = new ArrayList<>();
				Calendar beg = Calendar.getInstance();
				while (tokenizer.hasMoreTokens())
					a.add(tokenizer.nextToken());
				switch (a.get(0)) {
					case PocketAccounterGeneral.EVERY_DAY:
						if (item.getTake_time().compareTo(beg) >= 0)
							beg = (Calendar) item.getTake_time().clone();
						beg.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
						beg.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
						beg.set(Calendar.SECOND, 0);
						int countEveryDay = 0;
						if (returnDate != null) {
							Calendar end = (Calendar) returnDate.clone();
							while(beg.compareTo(end) <= 0 && countEveryDay < 10) {
								if (beg.compareTo(Calendar.getInstance()) <= 0) {
									beg.add(Calendar.DAY_OF_MONTH, 1);
									continue;
								}
								calendars.add((Calendar)beg.clone());
								countEveryDay++;
								beg.add(Calendar.DAY_OF_MONTH, 1);
							}
						}
						else {
							while(countEveryDay < 10) {
								if (beg.compareTo(Calendar.getInstance()) <= 0) {
									beg.add(Calendar.DAY_OF_MONTH, 1);
									continue;
								}
								calendars.add((Calendar)beg.clone());
								countEveryDay++;
								beg.add(Calendar.DAY_OF_MONTH, 1);
							}
						}
						break;
					case PocketAccounterGeneral.EVERY_WEEK:
						ArrayList<Integer> ints = new ArrayList<>();
						if (a.size() > 1) {
							StringTokenizer token = new StringTokenizer(a.get(1), ",");
							while (token.hasMoreTokens())
								ints.add(Integer.parseInt(token.nextToken()));
						}
						if (ints.size() == 0) return;
						int count = 0;
						beg = Calendar.getInstance();
						if (item.getTake_time().compareTo(Calendar.getInstance()) >= 0)
							beg = (Calendar)item.getTake_time().clone();
						beg.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
						beg.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
						beg.set(Calendar.SECOND, 0);
						if (returnDate != null) {
							Calendar end = (Calendar)returnDate.clone();
							while (beg.compareTo(end) <= 0 && count < 10) {
								if (beg.compareTo(Calendar.getInstance()) <= 0) {
									beg.add(Calendar.WEEK_OF_YEAR, 1);
									continue;
								}
								boolean dayFound = false;
								for (int i=0; i<ints.size(); i++) {
									if (getWeekDay(ints.get(i)) == beg.get(Calendar.DAY_OF_WEEK)) {
										dayFound = true;
										break;
									}
								}
								if (!dayFound) {
									beg.add(Calendar.DAY_OF_YEAR, 1);
									continue;
								}
								calendars.add((Calendar)beg.clone());
								beg.add(Calendar.DAY_OF_YEAR, 1);
								count++;
							}
						}
						else {
							while (count > 10) {
								if (beg.compareTo(Calendar.getInstance()) <= 0) {
									beg.add(Calendar.WEEK_OF_YEAR, 1);
									continue;
								}
								boolean dayFound = false;
								for (int i=0; i<ints.size(); i++) {
									if (getWeekDay(ints.get(i)) == beg.get(Calendar.DAY_OF_WEEK)) {
										dayFound = true;
										break;
									}
								}
								if (!dayFound) {
									beg.add(Calendar.DAY_OF_YEAR, 1);
									continue;
								}
								calendars.add((Calendar)beg.clone());
								beg.add(Calendar.DAY_OF_YEAR, 1);
								count++;
							}
						}
						break;
					case PocketAccounterGeneral.EVERY_MONTH:
						info = item.getInfo();
						tokenizer = new StringTokenizer(info, ":");
						a = new ArrayList<>();
						beg = Calendar.getInstance();
						while (tokenizer.hasMoreTokens())
							a.add(tokenizer.nextToken());
						if (beg.compareTo(item.getTake_time()) <= 0)
							beg = (Calendar) item.getTake_time().clone();
						beg.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
						beg.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
						beg.set(Calendar.SECOND, 0);
						Log.d("ttttt", "notificSetCredit: "+info);
						int day  = Integer.parseInt(a.get(1));
						int max = beg.getActualMaximum(Calendar.DAY_OF_MONTH);
						beg.set(Calendar.DAY_OF_MONTH, Math.min(day, max));
						int countOfDays = 0;
						if (returnDate != null) {
							Calendar end = (Calendar) returnDate.clone();
							while (beg.compareTo(end) <= 0 && countOfDays < 10) {
								if (beg.compareTo(Calendar.getInstance()) <= 0) {
									beg.add(Calendar.MONTH, 1);
									continue;
								}
								countOfDays++;
								calendars.add((Calendar)beg.clone());
								beg.add(Calendar.MONTH, 1);
								beg.set(Calendar.DAY_OF_MONTH, Math.min(day, beg.getActualMaximum(Calendar.DAY_OF_MONTH)));
							}
						}
						else {
							while (countOfDays < 10) {
								if (beg.compareTo(Calendar.getInstance()) <= 0) {
									beg.add(Calendar.MONTH, 1);
									continue;
								}
								countOfDays++;
								calendars.add((Calendar)beg.clone());
								beg.add(Calendar.MONTH, 1);
								beg.set(Calendar.DAY_OF_MONTH, Math.min(day, beg.getActualMaximum(Calendar.DAY_OF_MONTH)));
							}
						}
						break;
				}
				String msg = context.getString(R.string.notif_credit);
				SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
				Log.d("ttttt", "notificSetCredit: "+calendars.size());
				for (int i=0; i<calendars.size(); i++) {
					Log.d("ttttt", "notificSetCredit: "+format.format(calendars.get(i).getTime()));
					Intent intent = new Intent(context, AlarmReceiver.class);
					intent.putExtra("msg", msg);
					intent.putExtra("title", item.getCredit_name());
					intent.putExtra("TIP", AlarmReceiver.TO_CRIDET);
					intent.setData(Uri.parse("custom://" + count_intent));
					intent.setAction(String.valueOf(count_intent));

					PendingIntent pendingIntent = PendingIntent.getBroadcast(context, count_intent++,
							intent, 0);
					if (count_intent>450){
						prefs.edit().putInt("KEY_T",count_intent).apply();
						return;	}
					Log.d("gogogo", "notificSetCredit: "+count_intent);
					alarmManager.set(AlarmManager.RTC_WAKEUP, calendars.get(i).getTimeInMillis(), pendingIntent);
				}
				prefs.edit().putInt("KEY_T",count_intent).apply();
			}
		}
	}

	public void notificSetDebt() throws InterruptedException {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		String time = prefs.getString("planningNotifTime", "09:00");
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Calendar cal = Calendar.getInstance();
		Calendar morning_time = Calendar.getInstance();
		try {
			morning_time.setTime(df.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		cal.set(Calendar.HOUR_OF_DAY, morning_time.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, morning_time.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, 0);
		for (DebtBorrow item : myDebdbor) {
			if (item.isTo_archive()) continue;
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			boolean isNotify = preferences.getBoolean("general_notif", true);
			if (isNotify) {
				String info = item.getInfo();
				if (info == null)
					info = PocketAccounterGeneral.EVERY_WEEK + ":0";
				StringTokenizer tokenizer = new StringTokenizer(info, ":");
				ArrayList<String> a = new ArrayList<>();
				ArrayList<Calendar> calendars = new ArrayList<>();
				Calendar beg = Calendar.getInstance();
				while (tokenizer.hasMoreTokens())
					a.add(tokenizer.nextToken());
				switch (a.get(0)) {
					case PocketAccounterGeneral.EVERY_DAY:
						if (item.getTakenDate().compareTo(beg) >= 0)
							beg = (Calendar) item.getTakenDate().clone();
						beg.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
						beg.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
						beg.set(Calendar.SECOND, 0);
						int countEveryDay = 0;
						SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
						if (item.getReturnDate() != null) {
							Calendar end = (Calendar) item.getReturnDate().clone();
							while(beg.compareTo(end) <= 0 && countEveryDay < 10 ) {
								if (beg.compareTo(Calendar.getInstance()) <= 0) {
									beg.add(Calendar.DAY_OF_MONTH, 1);
									continue;
								}
								calendars.add((Calendar)beg.clone());
								countEveryDay++;
								beg.add(Calendar.DAY_OF_MONTH, 1);
							}
						}
						else {
							while(countEveryDay < 10) {
								if (beg.compareTo(Calendar.getInstance()) <= 0) {
									beg.add(Calendar.DAY_OF_MONTH, 1);
									continue;
								}
								calendars.add((Calendar)beg.clone());
								countEveryDay++;
								beg.add(Calendar.DAY_OF_MONTH, 1);
							}
						}
						break;
					case PocketAccounterGeneral.EVERY_WEEK:
						ArrayList<Integer> ints = new ArrayList<>();
						if (a.size() > 1) {
							StringTokenizer token = new StringTokenizer(a.get(1), ",");
							while (token.hasMoreTokens())
								ints.add(Integer.parseInt(token.nextToken()));
						}
						if (ints.size() == 0) return;
						int count = 0;
						beg = Calendar.getInstance();
						if (item.getTakenDate().compareTo(Calendar.getInstance()) >= 0)
							beg = (Calendar)item.getTakenDate().clone();
						beg.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
						beg.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
						beg.set(Calendar.SECOND, 0);
						if (item.getReturnDate() != null) {
							Calendar end = (Calendar)item.getReturnDate().clone();
							while (beg.compareTo(end) <= 0 && count < 10) {
								if (beg.compareTo(Calendar.getInstance()) <= 0) {
									beg.add(Calendar.DAY_OF_MONTH, 1);
									continue;
								}
								boolean dayFound = false;
								for (int i=0; i<ints.size(); i++) {
									if (getWeekDay(ints.get(i)) == beg.get(Calendar.DAY_OF_WEEK)) {
										dayFound = true;
										break;
									}
								}
								if (!dayFound) {
									beg.add(Calendar.DAY_OF_YEAR, 1);
									continue;
								}
								calendars.add((Calendar)beg.clone());
								beg.add(Calendar.DAY_OF_YEAR, 1);
								count++;
							}
						}
						else {
							while (count < 10) {
								if (beg.compareTo(Calendar.getInstance()) <= 0) {
									beg.add(Calendar.DAY_OF_MONTH, 1);
									continue;
								}
								boolean dayFound = false;
								for (int i=0; i<ints.size(); i++) {
									if (getWeekDay(ints.get(i)) == beg.get(Calendar.DAY_OF_WEEK)) {
										dayFound = true;
										break;
									}
								}
								if (!dayFound) {
									beg.add(Calendar.DAY_OF_YEAR, 1);
									continue;
								}
								calendars.add((Calendar)beg.clone());
								beg.add(Calendar.DAY_OF_YEAR, 1);
								count++;
							}
						}
						break;
					case PocketAccounterGeneral.EVERY_MONTH:
						info = item.getInfo();
						tokenizer = new StringTokenizer(info, ":");
						a = new ArrayList<>();
						beg = Calendar.getInstance();
						if (beg.compareTo(item.getTakenDate()) <= 0)
							beg = (Calendar) item.getTakenDate().clone();
						while (tokenizer.hasMoreTokens())
							a.add(tokenizer.nextToken());
						beg.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
						beg.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
						beg.set(Calendar.SECOND, 0);
						int day  = Integer.parseInt(a.get(1));
						int max = beg.getActualMaximum(Calendar.DAY_OF_MONTH);
						beg.set(Calendar.DAY_OF_MONTH, Math.min(day, max));
						int countOfDays = 0;
						if (item.getReturnDate() != null) {
							Calendar end = (Calendar) item.getReturnDate().clone();
							while (beg.compareTo(end) <= 0 && countOfDays < 10) {
								if (beg.compareTo(Calendar.getInstance()) <= 0) {
									beg.add(Calendar.DAY_OF_MONTH, 1);
									continue;
								}
								calendars.add(beg);
								countOfDays++;
								beg.add(Calendar.MONTH, 1);
								beg.set(Calendar.DAY_OF_MONTH, Math.min(day, beg.getActualMaximum(Calendar.DAY_OF_MONTH)));
							}
						}
						else {
							while (countOfDays < 10) {
								if (beg.compareTo(Calendar.getInstance()) <= 0) {
									beg.add(Calendar.DAY_OF_MONTH, 1);
									continue;
								}
								countOfDays++;
								calendars.add(beg);
								beg.add(Calendar.MONTH, 1);
								beg.set(Calendar.DAY_OF_MONTH, Math.min(day, beg.getActualMaximum(Calendar.DAY_OF_MONTH)));
							}
						}
						break;
				}
				String msg = context.getString(R.string.notif_debt);
				SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
				for (int i=0; i<calendars.size(); i++) {
					Log.d("sss", format.format(calendars.get(i).getTime()));
					Intent intent = new Intent(context, AlarmReceiver.class);
					intent.setData(Uri.parse("custom://" + count_intent));
					intent.setAction(String.valueOf(count_intent));

					intent.putExtra("msg", msg);
					intent.putExtra("title", item.getPerson().getName());
					intent.putExtra("TIP", AlarmReceiver.TO_DEBT);
					intent.putExtra("photoPath", (item.getPerson().getPhoto().matches("0") || item.getPerson().getPhoto().matches("")) ?
							"" : item.getPerson().getPhoto());
					PendingIntent pendingIntent = PendingIntent.getBroadcast(context, count_intent++,
							intent, 0);
					if (count_intent>450){
						prefs.edit().putInt("KEY_T",count_intent).commit();
						return;	}
					Log.d("gogogo", "notificSetDebt: "+count_intent);
					alarmManager.set(AlarmManager.RTC_WAKEUP, calendars.get(i).getTimeInMillis(), pendingIntent);
				}
				prefs.edit().putInt("KEY_T",count_intent).commit();
			}
		}
	}
	public int getWeekDay(int i) {
		switch (i) {
			case 0:
				return Calendar.MONDAY;
			case 1:
				return Calendar.TUESDAY;
			case 2:
				return Calendar.WEDNESDAY;
			case 3:
				return Calendar.THURSDAY;
			case 4:
				return Calendar.FRIDAY;
			case 5:
				return Calendar.SATURDAY;
			case 6:
				return Calendar.SUNDAY;
		}
		return 100;
	}
	public void cancelAllNotifs() {
		count_intent=0;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent updateServiceIntent = new Intent(context, AlarmReceiver.class);

		for (int i = 0; i <=prefs.getInt("KEY_T",500); i++) {
		updateServiceIntent.setData(Uri.parse("custom://" + i));
		updateServiceIntent.setAction(String.valueOf(i));
				PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(context, i, updateServiceIntent, 0);
			Log.d("gogogo", "cancelAllNotifs: "+i);
			try {
				alarmManager.cancel(pendingUpdateIntent);
			} catch (Exception e) {
				Log.e("AlarmSet", "AlarmManager update was not canceled. " + e.toString());
			}
		finally {
				pendingUpdateIntent.cancel();
			}
		}


	}
	
}
