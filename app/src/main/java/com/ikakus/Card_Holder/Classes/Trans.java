package com.ikakus.Card_Holder.Classes;

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
    private String Comment = "";
    private String Title = "";
    private String SmsBody = "";

    public Trans() {

    }

    public Trans(Date date, String place, double amount, double balance, String smsBody) {
        setDateTime(date);
        setPlace(place);
        setAmount(amount);
        setBalance(balance);
        setSmsBody(smsBody);
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
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getSmsBody() {
        return SmsBody;
    }

    public void setSmsBody(String smsBody) {
        SmsBody = smsBody;
    }


}
