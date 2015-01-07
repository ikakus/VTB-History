package com.ikakus.VTB_Parser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.ikakus.VTB_Parser.BroadcastReceivers.SMSReceiver;
import com.ikakus.VTB_Parser.Classes.*;
import com.ikakus.VTB_Parser.Interfaces.SMSReceiverListener;

import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity implements SMSReceiverListener {
      public static String VTB_SENDER = "VTB Bank";
   // public static String VTB_SENDER = "+1";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        startService(new Intent(this, SmsReaderService.class));

        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        this.registerReceiver(new SMSReceiver(this), intentFilter);

        DatabaseHandler smsReaderDbHelper = new DatabaseHandler(this);
        List<SMSMessage> smsMessages = null;

        updateBase(MainActivity.this);

        try {
            smsMessages = smsReaderDbHelper.getAllSms();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Transaction> transactions = SMSParser.parseSmsToTrans(smsMessages);
        setBalaceOnStart(transactions);

    }

    private void setBalaceOnStart(List<Transaction> transactions) {
        int size = transactions.size();
        if (size > 0) {
            Transaction lastTransaction = transactions.get(size - 1);
            setBalance(lastTransaction);

        } else {
            TextView balance = (TextView) findViewById(R.id.balance);
            balance.setText(Double.toString(0.0));
        }
    }

    private void setBalance(Transaction lastTransaction) {
        TextView balance = (TextView) findViewById(R.id.balance);
        balance.setText(Double.toString(lastTransaction.getBalance()));
    }

    private void onFirstStart() {
        SMSReader smsReader = new SMSReader(MainActivity.this);
        List<SMSMessage> allSMS = smsReader.getSMSMessage();
        fetchBase(allSMS, this);
    }

    private void addSms(SMSMessage smsMessage, Context context) {
        DatabaseHandler smsReaderDbHelper = new DatabaseHandler(context);
        smsReaderDbHelper.addSms(smsMessage);
    }

    private void updateBase(Context context) {
        DatabaseHandler smsReaderDbHelper = new DatabaseHandler(context);
        SMSReader smsReader = new SMSReader(MainActivity.this);
        List<SMSMessage> allSmsFromBase = smsReaderDbHelper.getAllSms();
        List<SMSMessage> allSms = smsReader.getSMSMessage();
        Collections.reverse(allSms);

        for (SMSMessage smsMessage : allSms) {
            if (smsMessage.getSender().equals(VTB_SENDER)) {
                if (!allSmsFromBase.contains(smsMessage)) {
                    addSms(smsMessage, context);
                }
            }
        }
    }

    private void fetchBase(List<SMSMessage> list, Context context) {
        Collections.reverse(list);
        for (SMSMessage smsMessage : list) {
            if (smsMessage.getSender().equals(VTB_SENDER)) {
                addSms(smsMessage, context);
            }
        }
    }

    @Override
    public void onSmsReceived(String result) {
        String sms = result;
        if(sms.contains("TRANSACTION")) {
            SMSMessage smsMessage = SMSParser.parseSmsToSmsMessage(sms);
            Transaction transaction = SMSParser.parseSms(sms);
            addSms(smsMessage, this);
            setBalance(transaction);
        }
    }
}
