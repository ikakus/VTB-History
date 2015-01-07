package com.ikakus.VTB_Parser.Classes;

import java.util.Date;

/**
 * Created by 404 on 07.12.2014.
 */
public class Transaction {

    public Date getDateTime() {
        return mDateTime;
    }

    public void setDateTime(Date dateTime) {
        mDateTime = dateTime;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double amount) {
        mAmount = amount;
    }

    public double getBalance() {
        return mBalance;
    }

    public void setBalance(double balance) {
        mBalance = balance;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String place) {
        mPlace = place;
    }

    public Transaction(Date date, String place, double amount, double balance){
        setDateTime(date);
        setPlace(place);
        setAmount(amount);
        setBalance(balance);
    }

    public Transaction(){

    }

    private Date mDateTime;
    private double mAmount;
    private double mBalance;
    private String mPlace;
}
