package com.ikakus.Card_Holder.Fragments;

import android.animation.TimeInterpolator;
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
import com.ikakus.Card_Holder.Activities.MainActivity;
import com.ikakus.Card_Holder.Classes.Trans;
import com.ikakus.Card_Holder.Classes.Utils;
import com.ikakus.Card_Holder.R;

import java.text.DecimalFormat;
import java.util.List;

public class ChartFragment extends Fragment {

    private static final int STEP = 500;
    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_chart, container, false);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

        mRootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
                                       int oldBottom) {
                // its possible that the layout is not complete in which case
                // we will get all zero values for the positions, so ignore the event
                if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                    return;
                }

                initLineChart();
                updateLineChart();

                // Do what you need to do with the height/width since they are now set
            }
        });

        return mRootView;
    }

    //    private final static int LINE_MAX = 2500;
    private final static int LINE_MIN = 0;
    private LineChartView mLineChart;
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

    private void updateLineChart() {

        mLineChart.reset();

        TextView firstDate = (TextView) mRootView.findViewById(R.id.first_date);
        TextView lastDate = (TextView) mRootView.findViewById(R.id.last_date);

        firstDate.setText(MainActivity.mTransactions.get(0).getDateTime().toString());
        lastDate.setText(MainActivity.mTransactions.get(MainActivity.mTransactions.size() - 1).getDateTime().toString());

        LineSet dataSet = new LineSet();
        float[] values = getValues(MainActivity.mTransactions);
        String[] labels = getEmptyLabels(MainActivity.mTransactions);

        dataSet.addPoints(labels, values);
        dataSet.setLineColor(this.getResources().getColor(R.color.line))
                .setLineThickness(Tools.fromDpToPx(3))
                .setSmooth(true)
                .setDashed(true);
        mLineChart.addData(dataSet);

        int lineMax;
        float maxValue = Utils.getMax(values);
        lineMax = getLineMax(maxValue);

        mLineChart.setBorderSpacing(Tools.fromDpToPx(4))
                .setGrid(LineChartView.GridType.HORIZONTAL, mLineGridPaint)
                .setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setAxisBorderValues(LINE_MIN, lineMax, STEP)
                .setLabelsFormat(new DecimalFormat("##' gel'"))
                .show(getAnimation(true))
        //.show()
        ;

        mLineChart.animateSet(0, new DashAnimation());
    }

    /**
     * Order
     */

    private static float mCurrOverlapFactor;
    private static int[] mCurrOverlapOrder;
    private static float mOldOverlapFactor;
    private static int[] mOldOverlapOrder;
    /**
     * Ease
     */
    private static BaseEasingMethod mCurrEasing;
    private static BaseEasingMethod mOldEasing;
    /**
     * Enter
     */
    private static float mCurrStartX;
    private static float mCurrStartY;
    private static float mOldStartX;
    private static float mOldStartY;
    /**
     * Alpha
     */
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

    private int getLineMax(float realMax) {
        int max = 0;

        if (realMax % STEP == 0) {
            int dTimes = (int) realMax / STEP;
            max = STEP * (dTimes);
        } else {
            int dTimes = (int) realMax / STEP;
            max = STEP * (dTimes) + STEP;
        }

        return max;
    }

    private final OnEntryClickListener lineEntryListener = new OnEntryClickListener() {
        @Override
        public void onClick(int setIndex, int entryIndex, Rect rect) {

            if (mLineTooltip == null) {
                showLineTooltip(setIndex, entryIndex, rect);
            } else {
                dismissLineTooltip(setIndex, entryIndex, rect);
            }
        }
    };

    private final View.OnClickListener lineClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mLineTooltip != null)
                dismissLineTooltip(-1, -1, null);
        }
    };

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

    float[] getValues(List<Trans> transactions) {
        float[] values = new float[transactions.size()];

        int count = 0;

        for (Trans transaction : transactions) {
            values[count] = (float) transaction.getBalance();
            count++;
        }

        return values;
    }

    String[] getEmptyLabels(List<Trans> transactions) {
        String[] label = new String[transactions.size()];
        int count = 0;
        for (Trans transaction : transactions) {
            label[count] = "";
            count++;
        }

        return label;
    }
}
