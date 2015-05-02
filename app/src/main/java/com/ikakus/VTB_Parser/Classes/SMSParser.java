package com.ikakus.VTB_Parser.Classes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 404 on 07.12.2014.
 */
public class SMSParser {

    private static double mLastBalance = 0;

    public static List<Transaction> parseSmsToTrans(List<SMSMessage> smsMessages) {
        List<Transaction> transactionsList = new ArrayList<Transaction>();

        for (SMSMessage smsMessage : smsMessages) {
            if (smsMessage.getSender().equals(ParsedSmsManager.VTB_SENDER)) {
                Transaction transaction = null;
                transaction = parseSms(smsMessage);

                if (transaction != null) {
                    transactionsList.add(transaction);
                }
            }
        }
        return transactionsList;
    }

    public static SMSMessage parseSmsToSmsMessage(String sms) {
        SMSMessage smsMessage;
        //TRANSACTION 2014-12-06 12:16:55 VISA ELECTRON 10.00 GEL / ATM TBC-38 (Planeta)>Tbilisi, GE.  Balance= 581.87 GEL. THANK YOU
        Date date = getDate(sms);
        smsMessage = new SMSMessage(ParsedSmsManager.VTB_SENDER, sms, date);
        return smsMessage;
    }

    private static Transaction parseSms(SMSMessage smsMessage) {

        Transaction transaction = null;

        if (smsMessage.getBody().contains("TRANSACTION")) {
            String body = smsMessage.getBody();
            //TRANSACTION 2014-12-06 12:16:55 VISA ELECTRON 10.00 GEL / ATM TBC-38 (Planeta)>Tbilisi, GE.  Balance= 581.87 GEL. THANK YOU
            Date date = getDate(body);
            double amount = getOutcomeAmount(body);
            double balance = getBalance(body);
            String place = getPlace(body);
            if (balance > 0 && amount > 0 && !place.equals("")) {
                transaction = new Transaction(date, place, amount, balance);
                mLastBalance = balance;
            } else {
                return null;
            }

        } else if (smsMessage.getBody().contains("VTB. tkven chagericxat")) {
            String body = smsMessage.getBody();
            double amount = getIncomeAmount(body);
            double balance = round(mLastBalance + amount, 2);
            transaction = new Transaction(smsMessage.getDate(), "", amount, balance);
            transaction.setIsIncome(true);
        }
        return transaction;
    }

    private static String getPlace(String body) {
        String place = "";
        try {

            int indexStart = body.indexOf("/");
            int indexEnd = body.indexOf("Balance=");
            place = body.substring(indexStart + 1, indexEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return place;
    }

    private static double getIncomeAmount(String body) {
        String startString = "tkven chagericxat :";
        String endString = "GEL barati";

        return getDouble(body, startString, endString);
    }

    private static double getBalance(String body) {
        String startString = "Balance=";
        String endString = "GEL. THANK YOU";

        return getDouble(body, startString, endString);
    }

    private static double getDouble(String body, String startString, String endString) {
        double balance = -1;
        try {
            int indexStart = body.indexOf(startString);
            int indexEnd = body.indexOf(endString);

            String sBalance = body.substring(indexStart + startString.length(), indexEnd);
            balance = Double.parseDouble(sBalance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return balance;
    }

    private static double getOutcomeAmount(String body) {
        String startString = "";
        String endString = "";
        if (body.contains("MAESTRO")) {
            startString = "TRANSACTION 2014-12-06 12:16:55 MAESTRO ";
        } else if (body.contains("MASTER")) {
            startString = "TRANSACTION 2014-12-06 12:16:55 MASTER CARD ";
        } else {
            startString = "TRANSACTION 2014-12-06 12:16:55 VISA ELECTRON ";
        }

        if(body.contains(" USD ")) {
            endString = " USD ";
        }else{
            endString = " GEL ";
        }
        return getDouble(body, startString, endString);

    }

    private static Date getDate(String body) {
        Date dateTime = null;
        String trans = "TRANSACTION";
        String sDateTemplate = "2014-12-06 12:16:55";
        if (body.contains(trans) && body.length() > 25) {
            String date = body.substring(trans.length() + 1, trans.length() + sDateTemplate.length() + 1);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                dateTime = format.parse(date);
                System.out.println(dateTime);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return dateTime;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
