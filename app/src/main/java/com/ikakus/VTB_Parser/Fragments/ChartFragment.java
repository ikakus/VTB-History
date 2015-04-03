package com.ikakus.VTB_Parser.Fragments;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BaseEasingMethod;
import com.db.chart.view.animation.easing.quint.QuintEaseOut;
import com.db.chart.view.animation.style.DashAnimation;
import com.ikakus.VTB_Parser.Classes.Transaction;
import com.ikakus.VTB_Parser.MainActivity;
import com.ikakus.VTB_Parser.R;

import java.text.DecimalFormat;
import java.util.List;


public class ChartFragment extends Fragment {

    private static final String ARG_BALANCE = "balance";

    private static final int STEP = 500;
    View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_chart, container, false);

//        initLineChart(mRootView);
//        setUnderChart(mRootView);
//        updateLineChart();

        mCurrOverlapFactor = 1;
        mCurrEasing = new QuintEaseOut();
        mCurrStartX = -1;
        mCurrStartY = 0;
        mCurrAlpha = -1;

        mOldOverlapFactor = 1;
        mOldEasing = new QuintEaseOut();
        mOldStartX = -1;
        mOldStartY = 0;
        mOldAlpha = -1;
        initLineChart();
        updateLineChart();

        return mRootView;
    }

    private final static int LINE_MAX = 10;
    private final static int LINE_MIN = -10;
    private final static String[] lineLabels = {"", "ANT", "GNU", "OWL", "APE", "JAY", ""};
//    private final static float[][] lineValues = {{-5f, 6f, 2f, 9f, 0f, 1f, 5f},
//            {-9f, -2f, -4f, -3f, -7f, -5f, -3f}};
    private static LineChartView mLineChart;
    private Paint mLineGridPaint;
    private TextView mLineTooltip;


    private final TimeInterpolator enterInterpolator = new DecelerateInterpolator(1.5f);
    private final TimeInterpolator exitInterpolator = new AccelerateInterpolator();


    private void initLineChart() {

        mLineChart = (LineChartView) mRootView.findViewById(R.id.linechart);
        mLineChart.setOnEntryClickListener(lineEntryListener);
        mLineChart.setOnClickListener(lineClickListener);

        mLineGridPaint = new Paint();
        mLineGridPaint.setColor(this.getResources().getColor(R.color.line_grid));
        mLineGridPaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        mLineGridPaint.setStyle(Paint.Style.STROKE);
        mLineGridPaint.setAntiAlias(true);
        mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
    }


//    private void updateLineChart() {
//
//        mLineChart.reset();
//        LineSet dataSet = new LineSet();
//        float[] values = getValues(MainActivity.mTransactions);
//        String[] labels = getEmptyLabels(MainActivity.mTransactions);
//
//        dataSet.addPoints(labels, values);
//        dataSet.setLineColor(this.getResources().getColor(R.color.line))
//                .setLineThickness(Tools.fromDpToPx(3))
//                .setSmooth(true);
//        mLineChart.addData(dataSet);
//
//        mLineChart.setBorderSpacing(Tools.fromDpToPx(4))
////                .setHorizontalGrid(mLineGridPaint)
//                .setXAxis(false)
//                .setXLabels(XController.LabelPosition.NONE)
//                .setYAxis(false)
//                .setYLabels(YController.LabelPosition.OUTSIDE)
//                .setAxisBorderValues(LINE_MIN, LINE_MAX, STEP)
////                .setLabelsMetric(" gel")
//                .setLabelColor(getResources().getColor(R.color.yellow))
//                        // .show(getAnimation(true).setEndAction(mEnterEndAction))
//                .show()
//        ;
//    }


    private void updateLineChart() {

        mLineChart.reset();

        LineSet dataSet = new LineSet();
        float[] values = getValues(MainActivity.mTransactions);
        String[] labels = getEmptyLabels(MainActivity.mTransactions);
//        LineSet dataSet = new LineSet();
//        dataSet.addPoints(lineLabels, lineValues[0]);
        dataSet.addPoints(labels, values);
        dataSet.setDots(true)
                .setDotsColor(this.getResources().getColor(R.color.line_bg))
                .setDotsRadius(Tools.fromDpToPx(5))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(this.getResources().getColor(R.color.line))
                .setLineColor(this.getResources().getColor(R.color.line))
                .setLineThickness(Tools.fromDpToPx(3))
                .beginAt(1).endAt(lineLabels.length - 1);
        mLineChart.addData(dataSet);

        dataSet = new LineSet();
//        dataSet.addPoints(lineLabels, lineValues[1]);
        dataSet.addPoints(labels, values);
        dataSet.setLineColor(this.getResources().getColor(R.color.line))
                .setLineThickness(Tools.fromDpToPx(3))
                .setSmooth(true)
                .setDashed(true);
        mLineChart.addData(dataSet);

        mLineChart.setBorderSpacing(Tools.fromDpToPx(4))
                .setGrid(LineChartView.GridType.HORIZONTAL, mLineGridPaint)
                .setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setAxisBorderValues(LINE_MIN, LINE_MAX, STEP)
                .setLabelsFormat(new DecimalFormat("##'u'"))
                .show(getAnimation(true))
        //.show()
        ;

        mLineChart.animateSet(1, new DashAnimation());
    }


    /**
     * Play
     */
    private static ImageButton mPlayBtn;


    /**
     * Order
     */
    private static ImageButton mOrderBtn;
    private final static int[] beginOrder = {0, 1, 2, 3, 4, 5, 6};
    private final static int[] middleOrder = {3, 2, 4, 1, 5, 0, 6};
    private final static int[] endOrder = {6, 5, 4, 3, 2, 1, 0};
    private static float mCurrOverlapFactor;
    private static int[] mCurrOverlapOrder;
    private static float mOldOverlapFactor;
    private static int[] mOldOverlapOrder;


    /**
     * Ease
     */
    private static ImageButton mEaseBtn;
    private static BaseEasingMethod mCurrEasing;
    private static BaseEasingMethod mOldEasing;


    /**
     * Enter
     */
    private static ImageButton mEnterBtn;
    private static float mCurrStartX;
    private static float mCurrStartY;
    private static float mOldStartX;
    private static float mOldStartY;


    /**
     * Alpha
     */
    private static ImageButton mAlphaBtn;
    private static int mCurrAlpha;
    private static int mOldAlpha;


    private Animation getAnimation(boolean newAnim) {
        if (newAnim)
            return new Animation()
                    .setAlpha(mCurrAlpha)
                    .setEasing(mCurrEasing)
                    .setOverlap(mCurrOverlapFactor, mCurrOverlapOrder)
                    .setStartPoint(mCurrStartX, mCurrStartY);
        else
            return new Animation()
                    .setAlpha(mOldAlpha)
                    .setEasing(mOldEasing)
                    .setOverlap(mOldOverlapFactor, mOldOverlapOrder)
                    .setStartPoint(mOldStartX, mOldStartY);
    }


    private final OnEntryClickListener lineEntryListener = new OnEntryClickListener() {
        @Override
        public void onClick(int setIndex, int entryIndex, Rect rect) {

            if (mLineTooltip == null)
                showLineTooltip(setIndex, entryIndex, rect);
            else
                dismissLineTooltip(setIndex, entryIndex, rect);
        }
    };

    private final View.OnClickListener lineClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mLineTooltip != null)
                dismissLineTooltip(-1, -1, null);
        }
    };

    @SuppressLint("NewApi")
    private void showLineTooltip(int setIndex, int entryIndex, Rect rect) {

        mLineTooltip = (TextView) getActivity().getLayoutInflater().inflate(R.layout.circular_tooltip, null);
        float[] values = getValues(MainActivity.mTransactions);
        mLineTooltip.setText(Integer.toString((int) values[entryIndex]));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) Tools.fromDpToPx(35), (int) Tools.fromDpToPx(35));
        layoutParams.leftMargin = rect.centerX() - layoutParams.width / 2;
        layoutParams.topMargin = rect.centerY() - layoutParams.height / 2;
        mLineTooltip.setLayoutParams(layoutParams);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mLineTooltip.setPivotX(layoutParams.width / 2);
            mLineTooltip.setPivotY(layoutParams.height / 2);
            mLineTooltip.setAlpha(0);
            mLineTooltip.setScaleX(0);
            mLineTooltip.setScaleY(0);
            mLineTooltip.animate()
                    .setDuration(150)
                    .alpha(1)
                    .scaleX(1).scaleY(1)
                    .rotation(360)
                    .setInterpolator(enterInterpolator);
        }

        mLineChart.showTooltip(mLineTooltip);
    }

    @SuppressLint("NewApi")
    private void dismissLineTooltip(final int setIndex, final int entryIndex, final Rect rect) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mLineTooltip.animate()
                    .setDuration(100)
                    .scaleX(0).scaleY(0)
                    .alpha(0)
                    .setInterpolator(exitInterpolator).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mLineChart.removeView(mLineTooltip);
                    mLineTooltip = null;
                    if (entryIndex != -1)
                        showLineTooltip(setIndex, entryIndex, rect);
                }
            });
        } else {
            mLineChart.dismissTooltip(mLineTooltip);
            mLineTooltip = null;
            if (entryIndex != -1)
                showLineTooltip(setIndex, entryIndex, rect);
        }
    }


//
//
//    private void initLineChart(View root) {
//
//        mLineChart = (LineChartView) root.findViewById(R.id.linechart);
//        mLineGridPaint = new Paint();
//        mLineGridPaint.setColor(this.getResources().getColor(R.color.white));
//        mLineGridPaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
//        mLineGridPaint.setStyle(Paint.Style.STROKE);
//        mLineGridPaint.setAntiAlias(true);
//        mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
//    }

    float[] getValues(List<Transaction> transactions) {
        float[] values = new float[transactions.size()];

        int count = 0;

        for (Transaction transaction : transactions) {
            values[count] = (float) transaction.getBalance();
            count++;
        }

        return values;
    }

    //
//    void setUnderChart(View root) {
//        TextView first = (TextView) root.findViewById(R.id.first_date);
//        TextView last = (TextView) root.findViewById(R.id.last_date);
//
//        int count = 0;
//        List<Transaction> transactions = MainActivity.mTransactions;
//        for (Transaction transaction : transactions) {
//            if (count == 0) {
//                DateFormat timeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.ROOT);
//
//                Date date = transaction.getDateTime();
//                String formattedDate = timeInstance.format(date);
//                first.setText(formattedDate);
//            }
//            if (count == transactions.size() - 1) {
//                DateFormat timeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.ROOT);
//
//                Date date = transaction.getDateTime();
//                String formattedDate = timeInstance.format(date);
//                last.setText(formattedDate);
//            }
//            count++;
//        }
//    }
//
    String[] getEmptyLabels(List<Transaction> transactions) {
        String[] label = new String[transactions.size()];
        int count = 0;
        for (Transaction transaction : transactions) {
            label[count] = "";
            count++;
        }

        return label;
    }
//
//
//    private void updateLineChart() {
//
//        mLineChart.reset();
//        LineSet dataSet = new LineSet();
//        float[] values = getValues(MainActivity.mTransactions);
//        String[] labels = getEmptyLabels(MainActivity.mTransactions);
//
//        dataSet.addPoints(labels, values);
//        dataSet.setLineColor(this.getResources().getColor(R.color.line))
//                .setLineThickness(Tools.fromDpToPx(3))
//                .setSmooth(true);
//        mLineChart.addData(dataSet);
//
//        mLineChart.setBorderSpacing(Tools.fromDpToPx(4))
////                .setHorizontalGrid(mLineGridPaint)
//                .setXAxis(false)
//                .setXLabels(XController.LabelPosition.NONE)
//                .setYAxis(false)
//                .setYLabels(YController.LabelPosition.OUTSIDE)
//                .setAxisBorderValues(LINE_MIN, LINE_MAX, STEP)
////                .setLabelsMetric(" gel")
//                .setLabelColor(getResources().getColor(R.color.yellow))
//                        // .show(getAnimation(true).setEndAction(mEnterEndAction))
//                .show()
//        ;
//    }

}
