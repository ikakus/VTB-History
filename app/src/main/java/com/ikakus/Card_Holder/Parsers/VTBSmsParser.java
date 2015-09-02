package com.ikakus.Card_Holder.Parsers;

import com.ikakus.Card_Holder.Classes.ParsedSmsManager;
import com.ikakus.Card_Holder.Classes.SMSMessage;
import com.ikakus.Card_Holder.Classes.Trans;
import com.ikakus.Card_Holder.Enum.Currency;

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
public class VTBSmsParser {

    private static double mLastBalance = 0;

    public static List<Trans> parseSmsListToTransList(List<SMSMessage> smsMessages) {
        List<Trans> transactionsList = new ArrayList<Trans>();

        for (SMSMessage smsMessage : smsMessages) {
            if (smsMessage.getSender().equals(ParsedSmsManager.VTB_SENDER)) {
                Trans transaction = null;
                transaction = parseSmsToTrans(smsMessage);

                if (transaction != null) {
                    transactionsList.add(transaction);
                }
            }
        }
        return transactionsList;
    }

    public static SMSMessage parseStrToSmsMessage(String sms) {
        SMSMessage smsMessage;
        //TRANSACTION 2014-12-06 12:16:55 VISA ELECTRON 10.00 GEL / ATM TBC-38 (Planeta)>Tbilisi, GE.  Balance= 581.87 GEL. THANK YOU
        Date date = getDate(sms);
        smsMessage = new SMSMessage(ParsedSmsManager.VTB_SENDER, sms, date);
        return smsMessage;
    }

    public static Trans parseSmsToTrans(SMSMessage smsMessage) {

        Trans transaction = null;


        if (smsMessage.getBody().contains("TRANSACTION")) {
            String body = smsMessage.getBody();
            //TRANSACTION 2014-12-06 12:16:55 VISA ELECTRON 10.00 GEL / ATM TBC-38 (Planeta)>Tbilisi, GE.  Balance= 581.87 GEL. THANK YOU
            Date date = getDate(body);
            double amount = getOutcomeAmount(body);
            double balance = getBalance(body);
            String place = getPlace(body);
            if (balance > 0 && amount > 0 && !place.equals("")) {
                transaction = new Trans(date, place, amount, balance, smsMessage.getBody(), getCurrency(body));
                mLastBalance = balance;
            } else {
                return null;
            }

        } else if (smsMessage.getBody().contains("VTB. tkven chagericxat")) {
            String body = smsMessage.getBody();
            double amount = getIncomeAmount(body);
            double balance = round(mLastBalance + amount, 2);
            transaction = new Trans(smsMessage.getDate(), "", amount, balance, smsMessage.getBody(), getCurrency(body));
            transaction.setIsIncome(true);
        } else if (smsMessage.getBody().contains("ganaghdeba")) {
            String body = smsMessage.getBody();
            //Bankomatshi ganaghdeba, baratit:VISA ELECTRON,500.00GEL/ATM VTB (Gldani BR)>Tbilisi, GE,2015-07-15 19:51:48, balansi:1828.86GEL
            Date date = getDateNewFormat(body);
            double amount = getOutcomeAmountNew(body);
            double balance = getBalanceNew(body);
            String place = getPlace(body);
            if (balance > 0 && amount > 0) {
                transaction = new Trans(date, place, amount, balance, smsMessage.getBody(), getCurrency(body));
                mLastBalance = balance;
            } else {
                return null;
            }
        } else if (smsMessage.getBody().contains("Gadakhda baratit")) {

//            Gadakhda baratit:VISA ELECTRON,20.00GEL/Burusport (Kavataradze str)>Tbilisi, GE,2015-08-23 14:24:19, balansi:1748.82GEL

            String body = smsMessage.getBody();
            Date date = getDateNewFormat(body);
            double amount = getOutcomeAmountNew(body);
            double balance = getBalanceNew(body);
            String place = getPlace(body);
            if (balance > 0 && amount > 0 ) {
                transaction = new Trans(date, place, amount, balance, smsMessage.getBody(), getCurrency(body));
                mLastBalance = balance;
            } else {
                return null;
            }
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

        int indexStart = body.indexOf(startString);
        int indexEnd = body.indexOf(endString);

        return getDouble(body, indexStart, indexEnd, startString.length());
    }

    private static double getBalance(String body) {
        String startString = "Balance=";
        String endString = "GEL. THANK YOU";

        int indexStart = body.indexOf(startString);
        int indexEnd = body.indexOf(endString);

        return getDouble(body, indexStart, indexEnd, startString.length());
    }

    private static double getBalanceNew(String body) {
        String startString = "balansi:";
        String endString = "GEL";

        int indexStart = body.indexOf(startString);
        int indexEnd = body.lastIndexOf(endString);

        return getDouble(body, indexStart, indexEnd, startString.length());
    }

    private static double getDouble(String body, int startIndex, int endIndex, int len) {
        double balance = -1;
        try {

            String sBalance = body.substring(startIndex + len, endIndex);
            balance = Double.parseDouble(sBalance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return balance;
    }

    private static double getOutcomeAmountNew(String body) {
        String startString = "";
        String endString = "";
        if (body.contains("MAESTRO")) {
            startString = "MAESTRO ";
        } else if (body.contains("MASTER")) {
            startString = "MASTER CARD ";
        } else if (body.contains("Bankomatshi ganaghdeba")) {
            startString = ":VISA ELECTRON,";
        } else if (body.contains("Gadakhda baratit")) {
            startString = ":VISA ELECTRON,";

        } else {
            startString = "VISA ELECTRON";
        }
        Currency currency = getCurrency(body);
        switch (currency) {
            case GEL:
                endString = "GEL";
                break;
            case USD:
                endString = "USD";
                break;
        }
        int indexStart = body.indexOf(startString);
        int indexEnd = body.indexOf(endString);

        return getDouble(body, indexStart, indexEnd, startString.length());
    }

    private static double getOutcomeAmount(String body) {
        String startString = "";
        String endString = "";
        if (body.contains("MAESTRO")) {
            startString = "TRANSACTION 2014-12-06 12:16:55 MAESTRO ";
        } else if (body.contains("MASTER")) {
            startString = "TRANSACTION 2014-12-06 12:16:55 MASTER CARD ";
        } else if (body.contains("VTB")) {
            startString = "Bankomatshi ganaghdeba, baratit:";
        } else {
            startString = "TRANSACTION 2014-12-06 12:16:55 VISA ELECTRON ";
        }

        Currency currency = getCurrency(body);

        switch (currency) {
            case GEL:
                endString = " GEL ";
                break;
            case USD:
                endString = " USD ";
                break;
        }


        int indexStart = body.indexOf(startString);
        int indexEnd = body.indexOf(endString);

        return getDouble(body, indexStart, indexEnd, startString.length());

    }

    private static Currency getCurrency(String body) {
        if (body.contains(" USD ")) {
            return Currency.USD;
        } else if (body.contains(" GEL ")) {
            return Currency.GEL;
        } else {
            return Currency.GEL;
        }
    }

    private static Date getDateNewFormat(String body) {
        Date dateTime = null;
        String start = "GE,";
        String sDateTemplate = "2014-12-06 12:16:55";
        int StartIndex = body.indexOf(start);
        if(StartIndex == -1){
            String altStart = "GE ,";
            StartIndex = body.indexOf(altStart);
        }
        int EndIndex = start.length() + sDateTemplate.length();
        if ( body.length() > 25) {
            String date = body.substring(StartIndex + start.length(), StartIndex + EndIndex);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = date.replace(",","");
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
