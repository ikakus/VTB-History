package com.ikakus.VTB_Parser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.ikakus.VTB_Parser.Classes.DatabaseHandler;
import com.ikakus.VTB_Parser.Classes.SMSMessage;
import com.ikakus.VTB_Parser.Classes.SMSParser;
import com.ikakus.VTB_Parser.Classes.SMSReader;
import com.ikakus.VTB_Parser.Classes.Transaction;
import com.ikakus.VTB_Parser.Fragments.BalanceFragment;
import com.ikakus.VTB_Parser.Fragments.ChartFragment;
import com.ikakus.VTB_Parser.Fragments.HistoryFragment;

import java.util.Collections;
import java.util.List;

public class MainActivity extends FragmentActivity {
    public static String VTB_SENDER = "VTB Bank";
//    public static String VTB_SENDER = "+1";

    private MyPagerAdapter mAdapter;
    private ViewPager mViewPager;
    public static double mBalance;
    public static double mLastAmount;
    public static boolean mIsIncome = false;
    public static List<Transaction> mTransactions;
    int orient = 0;

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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();

            orient = 1;
            mAdapter = new MyPagerAdapter(this.getSupportFragmentManager());
            mViewPager.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            orient = 0;
            mAdapter = new MyPagerAdapter(this.getSupportFragmentManager());
            mViewPager.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setBalanceOnStart(List<Transaction> transactions) {
        int size = transactions.size();
        if (size > 0) {
            Transaction lastTransaction = transactions.get(size - 1);
            mBalance = lastTransaction.getBalance();
            mLastAmount = lastTransaction.getAmount();
            mIsIncome = lastTransaction.isIncome();
        }
    }

    private void addSms(SMSMessage smsMessage, Context context) {
        DatabaseHandler smsReaderDbHelper = new DatabaseHandler(context);
        smsReaderDbHelper.addSms(smsMessage);
    }

    private void updateBase(Activity activity) {
        DatabaseHandler smsReaderDbHelper = new DatabaseHandler(activity);
        SMSReader smsReader = new SMSReader(activity);
        List<SMSMessage> allSmsFromBase = smsReaderDbHelper.getAllSms();
        List<SMSMessage> allSms = smsReader.getSMSMessage();
        Collections.reverse(allSms);

        for (SMSMessage smsMessage : allSms) {
            if (smsMessage.getSender().equals(VTB_SENDER)) {
                if (!allSmsFromBase.contains(smsMessage)) {
                    addSms(smsMessage, activity);
                    allSmsFromBase.add(smsMessage);
                }
            }
        }
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            if(orient==1) {
                if (position == 0) {
                    fragment = new ChartFragment();
                }
            }else {
                if (position == 0) {
                    fragment = new BalanceFragment();
                }
                if (position == 1) {
                    fragment = new HistoryFragment();
                }
            }

            return fragment;
        }

        @Override
        public int getCount() {
            if(orient==1) {
                return 1;
            }else {
                return 2;
            }
        }
    }
}
