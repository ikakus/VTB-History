package com.ikakus.VTB_Parser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ikakus.VTB_Parser.Classes.Transaction;
import com.ikakus.VTB_Parser.Classes.Utils;
import com.ikakus.VTB_Parser.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        this.items = Utils.myReverse(items);
        this.context = context;

    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.history_item, parent, false);
        TextView textViewAmount = (TextView) rowView.findViewById(R.id.amount);
        TextView textViewBalance = (TextView) rowView.findViewById(R.id.balance);
        TextView textViewDate = (TextView) rowView.findViewById(R.id.date);


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

    private void setMonthData(int position, final View rowView, Date dateCurr) {

        RelativeLayout dividerLayout = (RelativeLayout) rowView.findViewById(R.id.divider);
        TextView textViewMonth = (TextView) rowView.findViewById(R.id.month);
        TextView textViewSum = (TextView) rowView.findViewById(R.id.sum);
        TextView textViewIn = (TextView) rowView.findViewById(R.id.in);
        TextView textViewOut = (TextView) rowView.findViewById(R.id.out);
        TextView textViewSumTotal = (TextView) rowView.findViewById(R.id.sum_total);
        dividerLayout.setVisibility(View.VISIBLE);

        dividerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout infoLayout = (LinearLayout) rowView.findViewById(R.id.info);
                if(infoLayout.getVisibility() == View.VISIBLE) {
                    infoLayout.setVisibility(View.GONE);
                }else {
                    infoLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        SimpleDateFormat month_date = new SimpleDateFormat("MMMMMMMMM");
        String month_name = month_date.format(dateCurr);

        double OutSum = calculateOutcomeSumForMonth(position, items);
        double InSum = calculateIncomeSumForMonth(position, items);
        double totalSum = round(InSum - OutSum, 2);

        textViewIn.setText("+" + InSum);
        textViewOut.setText("-" + OutSum);

        textViewSumTotal.setText("=" + totalSum);

        if(OutSum > InSum){
            textViewSumTotal.setTextColor(context.getResources().getColor(R.color.red));
        }else {
            textViewSumTotal.setTextColor(context.getResources().getColor(R.color.green));
        }

        textViewMonth.setText(month_name);

        textViewSum.setText(" -" + OutSum);
    }

    private double calculateOutcomeSumForMonth(int position, ArrayList<Transaction> items) {
        double sum = 0;
        int count = 0;

        while (items.get(position).getDateTime().getMonth() ==
                items.get(position + count).getDateTime().getMonth()) {

            if (items.size() - 1 == position + count) {
                if (!items.get(position + count).isIncome()) {
                    sum += items.get(position + count).getAmount();
                }
                break;
            }

            if ((position + count) < items.size()) {
                if (!items.get(position + count).isIncome()) {
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
        return round(sum, 2);

    }

    private double calculateIncomeSumForMonth(int position, ArrayList<Transaction> items) {
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
        return round(sum, 2);
    }
}
