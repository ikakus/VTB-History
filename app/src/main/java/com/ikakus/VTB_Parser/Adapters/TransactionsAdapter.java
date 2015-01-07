package com.ikakus.VTB_Parser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ikakus.VTB_Parser.Classes.Transaction;
import com.ikakus.VTB_Parser.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 404 on 07.01.2015.
 */
public class TransactionsAdapter extends ArrayAdapter<Transaction> {

    private ArrayList<Transaction> items;
    private Context context;

    public TransactionsAdapter(Context context, int resource, ArrayList<Transaction> items) {
        super(context, resource, items);
        Collections.reverse(items);
        this.items = items;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.history_item, parent, false);
        TextView textViewAmount = (TextView) rowView.findViewById(R.id.amount);
        TextView textViewBalance = (TextView) rowView.findViewById(R.id.balance);
        TextView textViewDate = (TextView) rowView.findViewById(R.id.date);

        double amount = items.get(position).getAmount();
        textViewAmount.setText(Double.toString(amount));

        double balance = items.get(position).getBalance();
        textViewBalance.setText(Double.toString(balance));

        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.ROOT);

        Date date = items.get(position).getDateTime();
        String formattedDate = f.format(date);
        textViewDate.setText(formattedDate);

        return rowView;
    }
}
