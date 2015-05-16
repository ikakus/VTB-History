package com.ikakus.Card_Holder.Classes;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 404 on 06.12.2014.
 */
public class SMSReader {

    private final Activity mActivity;

    public SMSReader(Activity activity){
        mActivity = activity;
    }

    public List<String> getSMSstring(){
        List<String> sms = new ArrayList<String>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = mActivity.getContentResolver().query(uriSMSURI, null, null, null, null);

        while (cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));

            sms.add("Number: " + address + " .Message: " + body);

        }
        return sms;

    }

    public List<SMSMessage> getSMSMessage(){
        List<SMSMessage> sms = new ArrayList<SMSMessage>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = mActivity.getContentResolver().query(uriSMSURI, null, null, null, null);

        while (cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            Long lDate = cur.getLong(cur.getColumnIndexOrThrow("date"));
            Date date = new Date(lDate);

            SMSMessage smsMessage = new SMSMessage(address,body,date);
            sms.add(smsMessage);

        }
        return sms;

    }
}
