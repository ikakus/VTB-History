package com.ikakus.VTB_Parser;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.ikakus.VTB_Parser.BroadcastReceivers.SMSReceiver;
import com.ikakus.VTB_Parser.Classes.*;
import com.ikakus.VTB_Parser.Fragments.BalanceFragment;
import com.ikakus.VTB_Parser.Fragments.HistoryFragment;
import com.ikakus.VTB_Parser.Interfaces.SMSReceiverListener;

import java.util.Collections;
import java.util.List;

public class MainActivity extends FragmentActivity implements SMSReceiverListener {
      public static String VTB_SENDER = "VTB Bank";
   // public static String VTB_SENDER = "+1";

    private MyPagerAdapter mAdapter;
    private ViewPager mViewPager;
    public static double mBalance;
    public static List<Transaction> mTransactions;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        startService(new Intent(this, SmsReaderService.class));
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mAdapter = new MyPagerAdapter(this.getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

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
        mTransactions = SMSParser.parseSmsToTrans(smsMessages);
        setBalanceOnStart(mTransactions);

    }

    private void setBalanceOnStart(List<Transaction> transactions) {
        int size = transactions.size();
        if (size > 0) {
            Transaction lastTransaction = transactions.get(size - 1);
            mBalance = lastTransaction.getBalance();
        }
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

    @Override
    public void onSmsReceived(String result) {
        String sms = result;
        if(sms.contains("TRANSACTION")) {
            SMSMessage smsMessage = SMSParser.parseSmsToSmsMessage(sms);
            Transaction transaction = SMSParser.parseSms(sms);
            addSms(smsMessage, this);
            //setBalance(transaction);
        }
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            if (position == 0) {
                fragment = new BalanceFragment();
            }
            if (position == 1) {
                fragment = new HistoryFragment();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
