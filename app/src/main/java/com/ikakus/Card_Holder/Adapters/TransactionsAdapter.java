package com.ikakus.Card_Holder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ikakus.Card_Holder.Classes.Trans;
import com.ikakus.Card_Holder.Classes.Utils;
import com.ikakus.Card_Holder.Enum.Currency;
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

    private ArrayList<Trans> mItems;
    private Context mContext;
    private LayoutInflater mInflater;
    private HashMap<String, Double> mMonthlyOutcomeHashMap;

    public TransactionsAdapter(Context context, int resource, ArrayList<Trans> items) {
        super(context, resource, items);

        this.mItems = Utils.myReverse(items);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mMonthlyOutcomeHashMap = calculateOutcomeSumForMonth(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.history_item, parent, false);
        TextView textViewAmount = (TextView) rowView.findViewById(R.id.amount);
        TextView textViewBalance = (TextView) rowView.findViewById(R.id.balance);
        TextView textViewDate = (TextView) rowView.findViewById(R.id.date);

        Trans transaction = mItems.get(position);
        double amount = transaction.getAmount();
        if (transaction.isIncome()) {
            textViewAmount.setTextColor(mContext.getResources().getColor(R.color.green));
            textViewAmount.setText("+" + Double.toString(amount) + " " + transaction.getCurrency().name());
        } else {
            textViewAmount.setText("-" + Double.toString(amount) + " " + transaction.getCurrency().name());
        }

        double balance = transaction.getBalance();
        textViewBalance.setText(Double.toString(balance));
        DateFormat timeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.ROOT);
        Date date = transaction.getDateTime();
        String formattedDate = timeInstance.format(date);
        textViewDate.setText(formattedDate);

        return rowView;
    }

    private void setMonthData(int position, final View rowView, Date dateCurr) {
        TextView textViewMonth = (TextView) rowView.findViewById(R.id.month);
        TextView textViewSum = (TextView) rowView.findViewById(R.id.sum);
        TextView textViewIn = (TextView) rowView.findViewById(R.id.in);
        TextView textViewOut = (TextView) rowView.findViewById(R.id.out);
        TextView textViewSumTotal = (TextView) rowView.findViewById(R.id.sum_total);

        SimpleDateFormat month_date = new SimpleDateFormat("MMMMMMMMM");
        String month_name = month_date.format(dateCurr);

        double OutSumGel = getGELOutcomeSumForMonth(position);
        double OutSumUSD = getUSDOutcomeSumForMonth(position);
        double InSum = calculateIncomeSumForMonth(position, mItems);
        double totalSum = Utils.round(InSum - OutSumGel, 2);

        textViewIn.setText("+" + InSum);
        textViewOut.setText("-" + OutSumGel);
        textViewSumTotal.setText("=" + totalSum);

        textViewMonth.setText(month_name);
        textViewSum.setText("-" + OutSumUSD + " USD " + " -" + OutSumGel);
    }

    private HashMap<String, Double> calculateOutcomeSumForMonth(ArrayList<Trans> transactions) {
        HashMap<String, Double> monthlyOutcomeHashMap = new HashMap<>();
        double sum = 0;

        for (Trans trans : transactions) {
            SimpleDateFormat month_date = new SimpleDateFormat("MMyy");
            String key = month_date.format(trans.getDateTime()) + trans.getCurrency().name();
            monthlyOutcomeHashMap.put(key, sum);
        }

        for (Trans trans : transactions) {
            SimpleDateFormat month_date = new SimpleDateFormat("MMyy");
            String key = month_date.format(trans.getDateTime()) + trans.getCurrency().name();

            if (!trans.isIncome()) {
                sum = monthlyOutcomeHashMap.get(key);
                sum += trans.getAmount();
                monthlyOutcomeHashMap.put(key, sum);
            }

        }
        return monthlyOutcomeHashMap;
    }

    private double getGELOutcomeSumForMonth(int position) {
        Date dateCurr = mItems.get(position).getDateTime();
        SimpleDateFormat month_date = new SimpleDateFormat("MMyy");
        String key = month_date.format(dateCurr) + Currency.GEL.name();
        double sum = mMonthlyOutcomeHashMap.get(key);
        return Utils.round(sum, 2);
    }

    private double getUSDOutcomeSumForMonth(int position) {
        Date dateCurr = mItems.get(position).getDateTime();
        SimpleDateFormat month_date = new SimpleDateFormat("MMyy");
        String key = month_date.format(dateCurr) + Currency.USD.name();
        double sum = 0;
        try {
            sum = mMonthlyOutcomeHashMap.get(key);
        } catch (NullPointerException ignored) {

        }
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
        Date dateCurr = mItems.get(position).getDateTime();
        convertView = mInflater.inflate(R.layout.header, parent, false);

        setMonthData(position, convertView, dateCurr);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        SimpleDateFormat month_date = new SimpleDateFormat("MMMMMMMMM");
        Date dateCurr = mItems.get(position).getDateTime();
        String month_name = month_date.format(dateCurr);


        return month_name.charAt(0) + month_name.charAt(1) + month_name.charAt(2);
    }
}
