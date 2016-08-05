package com.jim.pocketaccounter.debt;

import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.Currency;
import java.util.ArrayList;
import java.util.Calendar;

public class DebtBorrow {
    private Person person;
    private Calendar takenDate, returnDate;
    private int type;
    private Account account;
    private Currency currency;
    private boolean calculate;
    private boolean to_archive = false;
    private double amount;
    private ArrayList<Recking> reckings;
    public static final int DEBT = 1, BORROW = 0;
    private String info = "";


    private String id; //"debt_"+UUID.randowUUID().toString();

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public DebtBorrow(Person person, Calendar takenDate, Calendar returnDate,
                      String id, Account account, Currency currency,
                      double amount, int type, boolean calculate) {
        this.person = person;
        this.takenDate = takenDate;
        this.returnDate = returnDate;
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        reckings = new ArrayList<>();
        this.type = type;
        this.id = id;
        this.calculate = calculate;
    }

    public DebtBorrow(Person person, Calendar takenDate,
                      String id, Account account, Currency currency,
                      double amount, int type, boolean calculate) {
        this.person = person;
        this.takenDate = takenDate;
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        reckings = new ArrayList<>();
        this.type = type;
        this.id = id;
        this.calculate = calculate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DebtBorrow() {}
    public Person getPerson() {return person;}
    public void setPerson(Person person) {this.person = person;}
    public Calendar getTakenDate() {return takenDate;}
    public void setTakenDate(Calendar takenDate) {this.takenDate = (Calendar)takenDate.clone();}
    public Calendar getReturnDate() {return returnDate;}
    public void setReturnDate(Calendar returnDate) {this.returnDate = returnDate;}
    public Account getAccount() {return account;}
    public void setAccount(Account account) {this.account = account;}
    public Currency getCurrency() {return currency;}
    public void setCurrency(Currency currency) {this.currency = currency;}
    public double getAmount() {return amount;}
    public void setAmount(double amount) {this.amount = amount;}
    public int getType() {
        return type;
    }
    public boolean isTo_archive() {return to_archive;}
    public void setTo_archive(boolean to_archive) {this.to_archive = to_archive;}
    public void setType(int type) {
        this.type = type;
    }
    public boolean isCalculate() {
        return calculate;
    }
    public void setCalculate(boolean calculate) {
        this.calculate = calculate;
    }
    public ArrayList<Recking> getReckings() {
        return reckings;
    }
    public void setReckings(ArrayList<Recking> reckings) {
        this.reckings = reckings;
    }
    public void addRecking(Recking recking) {
        reckings.add(recking);
    }
}