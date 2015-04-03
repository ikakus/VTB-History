package com.ikakus.VTB_Parser.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ikakus.VTB_Parser.MainActivity;
import com.ikakus.VTB_Parser.R;


public class BalanceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_balance, container, false);

        setBalance(
                Double.toString(MainActivity.mBalance),
                Double.toString(MainActivity.mLastAmount),
                rootView);

        return rootView;
    }

    private void setBalance(String balance, String amount, View root) {
        TextView balanceView = (TextView) root.findViewById(R.id.balance);
        TextView amountView = (TextView) root.findViewById(R.id.amount);
        balanceView.setText(balance);
        if (MainActivity.mIsIncome) {
            amountView.setText("+" + amount);
            amountView.setTextColor(getResources().getColor(R.color.green));
        } else {
            amountView.setText("-" + amount);
        }
    }


}
