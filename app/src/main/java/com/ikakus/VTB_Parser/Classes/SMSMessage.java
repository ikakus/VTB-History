package com.ikakus.VTB_Parser.Classes;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by 404 on 07.12.2014.
 */
public class SMSMessage extends SugarRecord{

    private String mSender;
    private String mBody;
    private Date mDate;

    public SMSMessage(){

    }
    public SMSMessage(String sender, String body, Date date) {
        setSender(sender);
        setBody(body);
        setDate(date);
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String mBody) {
        this.mBody = mBody;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String mSender) {
        this.mSender = mSender;
    }

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof SMSMessage) {
            SMSMessage ptr = (SMSMessage) v;
            retVal = ptr.mBody.equals(this.mBody);
        }

        return retVal;
    }
}
