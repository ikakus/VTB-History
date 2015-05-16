package com.ikakus.Card_Holder.Classes;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by 404 on 5/3/2015.
 */
public class NumberAnimator {

    public void animateBalance(final TextView balanceView, final String balance) {
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(1000);

        final char cArr[] = balance.toCharArray();
        final char newArr[] = new char[cArr.length];
        Arrays.fill(newArr, '0');
        balanceView.setText(String.valueOf(newArr));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                animation.setDuration(999999000);
                for (int i = 0; i < cArr.length; i++) {
                    if ("0123456789".contains(String.valueOf(cArr[i]))) {
                        int newValue = Integer.parseInt(String.valueOf(newArr[i]));
                        int oldValue = Integer.parseInt(String.valueOf(cArr[i]));
                        if (newValue < oldValue) {
                            newValue++;
                            newArr[i] = (newValue + "").charAt(0);
                        }
                    } else {
                        newArr[i] = cArr[i];
                    }
                }

                balanceView.setText(String.valueOf(newArr));
            }
        });
        animator.setObjectValues(0, 9);//Integer.parseInt(balance));
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round((endValue - startValue) * fraction);
            }
        });

        long g = animator.getDuration();
//        animator.setStartDelay(500);
        animator.start();
    }


    private class AnimateBalanceAsync extends AsyncTask<Void, Void, Void> {

        private final Activity mActivity;
        private String mBalance;
        TextView mBalanceView;

        public AnimateBalanceAsync(TextView balanceView, final String balance, Activity activity) {
            mBalance = balance;
            mBalanceView = balanceView;
            mActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            final char cArr[] = mBalance.toCharArray();
            final char newArr[] = new char[cArr.length];
            Arrays.fill(newArr, '0');
            mBalanceView.setText(String.valueOf(newArr));
            for (int k = 0; k < 10; k++) {
                for (int i = 0; i < cArr.length; i++) {
                    if ("0123456789".contains(String.valueOf(cArr[i]))) {
                        int newValue = Integer.parseInt(String.valueOf(newArr[i]));
                        int oldValue = Integer.parseInt(String.valueOf(cArr[i]));
                        if (newValue < oldValue) {
                            newValue++;
                            newArr[i] = (newValue + "").charAt(0);
                        }
                    } else {
                        newArr[i] = cArr[i];
                    }
                }


                mBalanceView.setText(String.valueOf(newArr));


            }
            return null;
        }
    }


    public void UIthreadAnim(final TextView balanceView, final String balance, final Activity activity) {


        new Thread() {
            public int j;

            public void run() {

                final char cArr[] = balance.toCharArray();
                final char newArr[] = new char[cArr.length];
                Arrays.fill(newArr, '0');

                // balanceView.setText(String.valueOf(newArr));
                while (j++ < 10) {

                    for (int i = 0; i < cArr.length; i++) {
                        if ("0123456789".contains(String.valueOf(cArr[i]))) {
                            int newValue = Integer.parseInt(String.valueOf(newArr[i]));
                            int oldValue = Integer.parseInt(String.valueOf(cArr[i]));
                            if (newValue < oldValue) {
                                newValue++;
                                newArr[i] = (newValue + "").charAt(0);
                            }
                        } else {
                            newArr[i] = cArr[i];
                        }
                    }

                    try {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                balanceView.setText(String.valueOf(newArr));
                            }
                        });
                        Thread.sleep(50 + j*10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    public void animateDouble(final TextView balanceView, double number) {
        ValueAnimator animator = new ValueAnimator();

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                double val = (double) animation.getAnimatedValue();
                val = MathUtils.round(val, 2);
                balanceView.setText(String.valueOf(val));
            }
        });
        animator.setObjectValues(0.0, number);//Integer.parseInt(balance));
        animator.setEvaluator(new TypeEvaluator<Double>() {
            public Double evaluate(float fraction, Double startValue, Double endValue) {
                return ((endValue - startValue) * fraction);
            }
        });
        animator.setDuration(50);
        animator.start();
    }

    public void animateInt(final TextView balanceView, int number) {
        ValueAnimator animator = new ValueAnimator();

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();

                balanceView.setText(String.valueOf(val));
            }
        });
        animator.setObjectValues(0, number);//Integer.parseInt(balance));
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round((endValue - startValue) * fraction);
            }
        });
        animator.setDuration(5000);
        animator.start();
    }

}
