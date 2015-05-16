package com.ikakus.Card_Holder.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.ikakus.Card_Holder.Activities.MainActivity;
import com.ikakus.Card_Holder.Adapters.TransactionsAdapter;
import com.ikakus.Card_Holder.Classes.MathUtils;
import com.ikakus.Card_Holder.Classes.NumberAnimator;
import com.ikakus.Card_Holder.Classes.Trans;
import com.ikakus.Card_Holder.R;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class HistoryFragment extends Fragment {

    View mRootView;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_history, container, false);

        StickyListHeadersListView listView = (StickyListHeadersListView ) mRootView.findViewById(R.id.lisview_history);
        TransactionsAdapter transactionsAdapter = new TransactionsAdapter(getActivity(), R.layout.history_item, (ArrayList<Trans>) MainActivity.mTransactions);

        listView.setAdapter(transactionsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trans transaction = MainActivity.mTransactions.get((MainActivity.mTransactions.size() - 1) - position);
                Toast.makeText(getActivity(), transaction.getSmsBody(), Toast.LENGTH_LONG).show();
            }
        });

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
