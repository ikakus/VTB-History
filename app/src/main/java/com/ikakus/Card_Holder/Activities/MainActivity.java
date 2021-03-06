package com.ikakus.Card_Holder.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.ikakus.Card_Holder.Classes.ParsedSmsManager;
import com.ikakus.Card_Holder.Classes.Trans;
import com.ikakus.Card_Holder.Enum.Orientation;
import com.ikakus.Card_Holder.Fragments.ChartFragment;
import com.ikakus.Card_Holder.Fragments.HistoryFragment;
import com.ikakus.Card_Holder.Fragments.NoTransactionsFragment;
import com.ikakus.Card_Holder.R;
import com.ikakus.Card_Holder.Service.SmsReaderService;
import com.splunk.mint.Mint;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static double mBalance;
    public static double mLastAmount;
    public static boolean mIsIncome = false;
    public static List<Trans> mTransactions;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private MyPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private Orientation orientation = Orientation.Portrait;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Mint.initAndStartSession(this, "c75e842f");

        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            startMainService();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }

    }

    private void startMainService() {
        startService(new Intent(this, SmsReaderService.class));
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(this.getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ParsedSmsManager.updateSmsBase(MainActivity.this);
        mTransactions = Trans.listAll(Trans.class);
        setBalanceOnStart(mTransactions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startMainService();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "READ_SMS Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();

            orientation = Orientation.Landscape;
            mAdapter = new MyPagerAdapter(this.getSupportFragmentManager());
            mViewPager.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            orientation = Orientation.Portrait;
            mAdapter = new MyPagerAdapter(this.getSupportFragmentManager());
            mViewPager.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setBalanceOnStart(List<Trans> transactions) {
        int size = transactions.size();
        if (size > 0) {
            Trans lastTransaction = transactions.get(size - 1);
            mBalance = lastTransaction.getBalance();
            mLastAmount = lastTransaction.getAmount();
            mIsIncome = lastTransaction.isIncome();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            if (mTransactions.size() > 0) {

                if (orientation == Orientation.Landscape) {
                    if (position == 0) {
                        fragment = new ChartFragment();
                    }
                }
                if (orientation == Orientation.Portrait) {
                    if (position == 0) {
                        fragment = new HistoryFragment();
                    }
                }

            } else {

                fragment = new NoTransactionsFragment();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            if (orientation == Orientation.Landscape) {
                return 1;
            } else {
                return 1;
            }
        }
    }
}
