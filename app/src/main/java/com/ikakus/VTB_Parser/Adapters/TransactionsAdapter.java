package com.ikakus.VTB_Parser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ikakus.VTB_Parser.Classes.Transaction;
import com.ikakus.VTB_Parser.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        RelativeLayout linearLayout = (RelativeLayout) rowView.findViewById(R.id.divider);
        TextView textViewMont = (TextView) rowView.findViewById(R.id.month);
        TextView textViewSum = (TextView) rowView.findViewById(R.id.sum);

        if (position > 0) {
            Date datePrev = items.get(position - 1).getDateTime();
            Date dateCurr = items.get(position).getDateTime();

            if (dateCurr.getMonth() != datePrev.getMonth()) {
                linearLayout.setVisibility(View.VISIBLE);
                SimpleDateFormat month_date = new SimpleDateFormat("MMMMMMMMM");
                String month_name = month_date.format(dateCurr);

                double sum = calculateSumForMonth(position, items);

                textViewMont.setText(month_name);
                textViewSum.setText(Double.toString(sum));
            }
        }

        if (position == 0) {
            Date dateCurr = items.get(position).getDateTime();
            linearLayout.setVisibility(View.VISIBLE);
            SimpleDateFormat month_date = new SimpleDateFormat("MMMMMMMMM");
            String month_name = month_date.format(dateCurr);

            double sum = calculateSumForMonth(position, items);

            textViewMont.setText(month_name);
            textViewSum.setText(Double.toString(sum));
        }

        double amount = items.get(position).getAmount();
        textViewAmount.setText(Double.toString(amount));

        double balance = items.get(position).getBalance();
        textViewBalance.setText(Double.toString(balance));

        DateFormat timeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.ROOT);

        Date date = items.get(position).getDateTime();
        String formattedDate = timeInstance.format(date);

        textViewDate.setText(formattedDate);

        return rowView;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    private double calculateSumForMonth(int position, ArrayList<Transaction> items) {
        double sum = 0;
        int count = 0;

        while(items.get(position).getDateTime().getMonth() ==
              items.get(position + count).getDateTime().getMonth()){

            if( (position + count) < items.size()-1) {
                sum += items.get(position + count).getAmount();
                count++;
            }else{
                break;
            }
        }
        return round(sum,2);
    }
}
