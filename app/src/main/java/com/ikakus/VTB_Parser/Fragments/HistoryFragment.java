package com.ikakus.VTB_Parser.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ikakus.VTB_Parser.Adapters.TransactionsAdapter;
import com.ikakus.VTB_Parser.Classes.Transaction;
import com.ikakus.VTB_Parser.MainActivity;
import com.ikakus.VTB_Parser.R;

import java.util.ArrayList;


public class HistoryFragment extends Fragment {

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
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.lisview_history);
        TransactionsAdapter transactionsAdapter = new TransactionsAdapter(getActivity(),R.layout.history_item,(ArrayList<Transaction>) MainActivity.mTransactions);

        listView.setAdapter(transactionsAdapter);


        return rootView;
    }
}
