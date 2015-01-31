package com.ikakus.VTB_Parser.Fragments;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.ikakus.VTB_Parser.Classes.Transaction;
import com.ikakus.VTB_Parser.MainActivity;
import com.ikakus.VTB_Parser.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class BalanceFragment extends Fragment {

    private static final String ARG_BALANCE = "balance";
    private static final int LINE_MIN = 0;
    private static final int STEP = 500;
    private static int LINE_MAX = 0;

    private static LineChartView mLineChart;

    private String mBalance;
    private Paint mLineGridPaint;
    private TextView mLineTooltip;

    public BalanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param balance Parameter 1.
     * @return A new instance of fragment BalanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BalanceFragment newInstance(String balance) {
        BalanceFragment fragment = new BalanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BALANCE, balance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBalance = getArguments().getString(ARG_BALANCE);
            // setLMax();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_balance, container, false);


        setBalance(
                Double.toString(MainActivity.mBalance),
                Double.toString(MainActivity.mLastAmount),
                rootView);

        initLineChart(rootView);
        setUnderChart(rootView);
        updateLineChart();

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

    private void initLineChart(View root) {

        mLineChart = (LineChartView) root.findViewById(R.id.linechart);
        mLineGridPaint = new Paint();
        mLineGridPaint.setColor(this.getResources().getColor(R.color.white));
        mLineGridPaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        mLineGridPaint.setStyle(Paint.Style.STROKE);
        mLineGridPaint.setAntiAlias(true);
        mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
    }

    double getMaxBalance(List<Transaction> transactions) {
        double max = 0;
        for (Transaction transaction : transactions) {
            double balance = transaction.getBalance();
            if (balance > max) {
                max = balance;
            }
        }

        return max;
    }


    float[] getValues(List<Transaction> transactions) {
        float[] values = new float[transactions.size()];

        int count = 0;

        for (Transaction transaction : transactions) {
            values[count] = (float) transaction.getBalance();
            count++;
        }

        return values;
    }

    void setUnderChart(View root) {
        TextView first = (TextView) root.findViewById(R.id.first_date);
        TextView last = (TextView) root.findViewById(R.id.last_date);

        int count = 0;
        List<Transaction> transactions = MainActivity.mTransactions;
        for (Transaction transaction : transactions) {
            if (count == 0) {
                DateFormat timeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.ROOT);

                Date date = transaction.getDateTime();
                String formattedDate = timeInstance.format(date);
                first.setText(formattedDate);
            }
            if (count == transactions.size() - 1) {
                DateFormat timeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.ROOT);

                Date date = transaction.getDateTime();
                String formattedDate = timeInstance.format(date);
                last.setText(formattedDate);
            }
            count++;
        }
    }

    String[] getEmptyLabels(List<Transaction> transactions) {
        String[] label = new String[transactions.size()];
        int count = 0;
        for (Transaction transaction : transactions) {
            label[count] = "";
            count++;
        }

        return label;
    }


    private void updateLineChart() {

        mLineChart.reset();
        LineSet dataSet = new LineSet();
        float[] values = getValues(MainActivity.mTransactions);
        String[] labels = getEmptyLabels(MainActivity.mTransactions);

        dataSet.addPoints(labels, values);
        dataSet.setLineColor(this.getResources().getColor(R.color.line))
                .setLineThickness(Tools.fromDpToPx(3))
                .setSmooth(true);
        mLineChart.addData(dataSet);

        mLineChart.setBorderSpacing(Tools.fromDpToPx(4))
                .setHorizontalGrid(mLineGridPaint)
                .setXAxis(false)
                .setXLabels(XController.LabelPosition.NONE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setAxisBorderValues(LINE_MIN, LINE_MAX, STEP)
                .setLabelsMetric(" gel")
                .setLabelColor(getResources().getColor(R.color.yellow))
                        // .show(getAnimation(true).setEndAction(mEnterEndAction))
                .show()
        ;
    }

}
