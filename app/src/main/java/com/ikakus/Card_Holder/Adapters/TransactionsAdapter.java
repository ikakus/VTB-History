package com.ikakus.Card_Holder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ikakus.Card_Holder.Classes.Trans;
import com.ikakus.Card_Holder.Classes.Utils;
import com.ikakus.Card_Holder.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by 404 on 07.01.2015.
 */
public class TransactionsAdapter extends ArrayAdapter<Trans> implements StickyListHeadersAdapter {

    private ArrayList<Trans> items;
    private Context context;
    private LayoutInflater inflater;
    private HashMap<String, Double> monthlyOutcomeHashMap;

    public TransactionsAdapter(Context context, int resource, ArrayList<Trans> items) {
        super(context, resource, items);

        this.items = Utils.myReverse(items);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        monthlyOutcomeHashMap = new HashMap<>();
        calculateOutcomeSumForMonth(items);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.history_item, parent, false);
        TextView textViewAmount = (TextView) rowView.findViewById(R.id.amount);
        TextView textViewBalance = (TextView) rowView.findViewById(R.id.balance);
        TextView textViewDate = (TextView) rowView.findViewById(R.id.date);

//        setHeaders(position, rowView);

        double amount = items.get(position).getAmount();
        if (items.get(position).isIncome()) {
            textViewAmount.setTextColor(context.getResources().getColor(R.color.green));
            textViewAmount.setText("+" + Double.toString(amount));
        } else {
            textViewAmount.setText("-" + Double.toString(amount));
        }

        double balance = items.get(position).getBalance();
        textViewBalance.setText(Double.toString(balance));
        DateFormat timeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.ROOT);
        Date date = items.get(position).getDateTime();
        String formattedDate = timeInstance.format(date);
        textViewDate.setText(formattedDate);

        return rowView;
    }

    private void setHeaders(int position, View rowView) {
        if (position > 0) {
            Date datePrev = items.get(position - 1).getDateTime();
            Date dateCurr = items.get(position).getDateTime();

            if (dateCurr.getMonth() != datePrev.getMonth()) {
                setMonthData(position, rowView, dateCurr);
            }
        }

        if (position == 0) {
            Date dateCurr = items.get(position).getDateTime();
            setMonthData(position, rowView, dateCurr);
        }
    }

    private void setMonthData(int position, final View rowView, Date dateCurr) {
        TextView textViewMonth = (TextView) rowView.findViewById(R.id.month);
        TextView textViewSum = (TextView) rowView.findViewById(R.id.sum);
        TextView textViewIn = (TextView) rowView.findViewById(R.id.in);
        TextView textViewOut = (TextView) rowView.findViewById(R.id.out);
        TextView textViewSumTotal = (TextView) rowView.findViewById(R.id.sum_total);

        SimpleDateFormat month_date = new SimpleDateFormat("MMMMMMMMM");
        String month_name = month_date.format(dateCurr);

        double OutSum = getOutcomeSumForMonth(position);
        double InSum = calculateIncomeSumForMonth(position, items);
        double totalSum = Utils.round(InSum - OutSum, 2);

        textViewIn.setText("+" + InSum);
        textViewOut.setText("-" + OutSum);
        textViewSumTotal.setText("=" + totalSum);

        if (OutSum > InSum) {
            textViewSumTotal.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            textViewSumTotal.setTextColor(context.getResources().getColor(R.color.green));
        }

        textViewMonth.setText(month_name);
        textViewSum.setText(" -" + OutSum);
    }

    private void calculateOutcomeSumForMonth(ArrayList<Trans> transactions) {
        double sum = 0;

        for (Trans trans : transactions) {
            SimpleDateFormat month_date = new SimpleDateFormat("MMyy");
            String key = month_date.format(trans.getDateTime());
            monthlyOutcomeHashMap.put(key, sum);
        }

        for (Trans trans : transactions) {
            SimpleDateFormat month_date = new SimpleDateFormat("MMyy");
            String key = month_date.format(trans.getDateTime());

            if(!trans.isIncome()) {
                sum = monthlyOutcomeHashMap.get(key);
                sum += trans.getAmount();
                monthlyOutcomeHashMap.put(key, sum);
            }

        }

    }

    private double getOutcomeSumForMonth(int position) {
        Date dateCurr = items.get(position).getDateTime();
        SimpleDateFormat month_date = new SimpleDateFormat("MMyy");
        String key = month_date.format(dateCurr);
        double sum = monthlyOutcomeHashMap.get(key);
        return Utils.round(sum, 2);
    }

    private double calculateIncomeSumForMonth(int position, ArrayList<Trans> items) {
        double sum = 0;
        int count = 0;

        while (items.get(position).getDateTime().getMonth() ==
                items.get(position + count).getDateTime().getMonth()) {

            if (items.size() - 1 == position + count) {
                if (items.get(position + count).isIncome()) {
                    sum += items.get(position + count).getAmount();
                }
                break;
            }

            if ((position + count) < items.size()) {
                if (items.get(position + count).isIncome()) {
                    sum += items.get(position + count).getAmount();
                }
                count++;
            } else {
                break;
            }

            if (items.size() == 1) {
                break;
            }
        }
        return Utils.round(sum, 2);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        Date dateCurr = items.get(position).getDateTime();
        convertView = inflater.inflate(R.layout.header, parent, false);

        setMonthData(position, convertView, dateCurr);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        SimpleDateFormat month_date = new SimpleDateFormat("MMMMMMMMM");
        Date dateCurr = items.get(position).getDateTime();
        String month_name = month_date.format(dateCurr);

        return month_name.charAt(0);
    }
}
