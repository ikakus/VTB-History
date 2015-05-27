package com.ikakus.Card_Holder.Classes;

import com.ikakus.Card_Holder.Enum.Currency;
import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by 404 on 07.12.2014.
 */
public class Trans extends SugarRecord {

    private Date mDateTime;
    private double mAmount = 0;
    private double mBalance = 0;
    private String mPlace = "";
    private boolean mIsIncome = false;
    private String mComment = "";
    private String mTitle = "";
    private String mSmsBody = "";
    private Currency mCurrency = Currency.GEL;

    public Trans() {

    }

    public Trans(Date date, String place, double amount, double balance, String smsBody, Currency currency) {
        setDateTime(date);
        setPlace(place);
        setAmount(amount);
        setBalance(balance);
        setSmsBody(smsBody);
        setCurrency(currency);
    }

    public Currency getCurrency() {
        return mCurrency;
    }

    public void setCurrency(Currency mCurrency) {
        this.mCurrency = mCurrency;
    }

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

    public boolean isIncome() {
        return mIsIncome;
    }

    public void setIsIncome(boolean mIsIncome) {
        this.mIsIncome = mIsIncome;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String mComment) {
        this.mComment = mComment;
    }

    public String getSmsBody() {
        return mSmsBody;
    }

    public void setSmsBody(String mSmsBody) {
        this.mSmsBody = mSmsBody;
    }
}
