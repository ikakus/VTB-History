package com.ikakus.VTB_Parser.Classes;

import com.ikakus.VTB_Parser.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 404 on 07.12.2014.
 */
public class SMSParser {
    public static List<Transaction> parseSmsToTrans(List<SMSMessage> smsMessages) {
        List<Transaction> transactionsList = new ArrayList<Transaction>();

        for (SMSMessage smsMessage : smsMessages) {
            if (smsMessage.getSender().equals(MainActivity.VTB_SENDER)) {
                Transaction transaction = null;

                transaction = parseSms(smsMessage);

                if (transaction != null) {
                    transactionsList.add(transaction);
                }
            }
        }
        return transactionsList;
    }

    public static List<Transaction> parseSmsToTransFromString(List<String> smsMessages) {
        List<Transaction> transactionsList = new ArrayList<Transaction>();
        for (String smsMessage : smsMessages) {
            Transaction transaction = parseSms(smsMessage);
            transactionsList.add(transaction);
        }
        return transactionsList;
    }

    public static Transaction parseSms(String smsMessage) {
        Transaction transaction;
        String body = smsMessage;
        //TRANSACTION 2014-12-06 12:16:55 VISA ELECTRON 10.00 GEL / ATM TBC-38 (Planeta)>Tbilisi, GE.  Balance= 581.87 GEL. THANK YOU
        Date date = getDate(body);
        double amount = getAmount(body);
        double balance = getBalance(body);
        String place = getPlace(body);

        if (balance > 0 && amount > 0 && !place.equals("")) {
            transaction = new Transaction(date, place, amount, balance);
        } else {
            return null;
        }

        return transaction;
    }

    public static SMSMessage parseSmsToSmsMessage(String sms) {
        SMSMessage smsMessage;
        String body = sms;
        //TRANSACTION 2014-12-06 12:16:55 VISA ELECTRON 10.00 GEL / ATM TBC-38 (Planeta)>Tbilisi, GE.  Balance= 581.87 GEL. THANK YOU
        Date date = getDate(body);


        smsMessage = new SMSMessage(MainActivity.VTB_SENDER, sms, date);

        return smsMessage;
    }

    private static Transaction parseSms(SMSMessage smsMessage) {
        Transaction transaction;
        String body = smsMessage.getBody();
        //TRANSACTION 2014-12-06 12:16:55 VISA ELECTRON 10.00 GEL / ATM TBC-38 (Planeta)>Tbilisi, GE.  Balance= 581.87 GEL. THANK YOU
        Date date = getDate(body);
        double amount = getAmount(body);
        double balance = getBalance(body);
        String place = getPlace(body);
        if (balance > 0 && amount > 0 && !place.equals("")) {
            transaction = new Transaction(date, place, amount, balance);
        } else {
            return null;
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

    private static double getBalance(String body) {
        double balance = -1;
        try {
            int indexStart = body.indexOf("Balance=");
            int indexEnd = body.indexOf("GEL. THANK YOU");

            String sBalance = body.substring(indexStart + "Balance=".length(), indexEnd);
            balance = Double.parseDouble(sBalance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return balance;
    }

    private static double getAmount(String body) {
        double amount = -1;
        String preAmountString = "TRANSACTION 2014-12-06 12:16:55 VISA ELECTRON";
        String sAmount;

        try {
            int index = body.indexOf("/");

            sAmount = body.substring(preAmountString.length() + 1, index - " GEL ".length());
            amount = Double.parseDouble(sAmount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return amount;
    }

    private static Date getDate(String body) {
        Date dateTime = null;
        String trans = "TRANSACTION";
        String sDateTemplate = "2014-12-06 12:16:55";
        if(body.contains(trans)) {
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
}
