package com.ikakus.VTB_Parser.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ikakus.VTB_Parser.Classes.MathUtils;
import com.ikakus.VTB_Parser.Classes.NumberAnimator;
import com.ikakus.VTB_Parser.MainActivity;
import com.ikakus.VTB_Parser.R;


public class BalanceFragment extends Fragment {

    View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_balance, container, false);

        setBalance(
                Double.toString(MainActivity.mBalance),
                Double.toString(MainActivity.mLastAmount),
                mRootView);

        return mRootView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (isVisibleToUser) {
//                Log.d("MyFragment", "Not visible anymore.  Stopping audio.");

                setBalance(
                        Double.toString(MainActivity.mBalance),
                        Double.toString(MainActivity.mLastAmount),
                        mRootView);
            } else {

                TextView balanceView = (TextView) mRootView.findViewById(R.id.balance);
                balanceView.setText("0");
            }
        }
    }

    private void setBalance(String balance, String amount, View root) {
        TextView balanceView = (TextView) root.findViewById(R.id.balance);
        TextView amountView = (TextView) root.findViewById(R.id.amount);
        NumberAnimator numberAnimator = new NumberAnimator();
        numberAnimator.animateDouble(balanceView, MathUtils.round(Double.parseDouble(balance), 2));

        if (MainActivity.mIsIncome) {
            amountView.setText("+" + amount);
            amountView.setTextColor(getResources().getColor(R.color.green));
        } else {
            amountView.setText("-" + amount);
        }
    }


}
