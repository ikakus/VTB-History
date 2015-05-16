package com.ikakus.Card_Holder.Classes;

import android.app.Activity;

import com.ikakus.Card_Holder.Parsers.VTBSmsParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 404 on 5/2/2015.
 */
public class ParsedSmsManager {

    public static String VTB_SENDER = "VTB Bank";

    public static void addSmsTransToBase(SMSMessage smsMessage) {

        Trans transaction = VTBSmsParser.parseSmsToTrans(smsMessage);
        if (transaction != null) {
            smsMessage.save();
            attTransToBase(transaction);
        }
    }

    public static void updateSmsBase(Activity activity) {

        SMSReader smsReader = new SMSReader(activity);
        List<SMSMessage> allSmsFromBase = SMSMessage.listAll(SMSMessage.class);
        List<SMSMessage> allSms = smsReader.getSMSMessage();

        for (SMSMessage smsMessage : (List<SMSMessage>) Utils.myReverse((ArrayList) allSms)) {
            if (smsMessage.getSender().equals(VTB_SENDER)) {
                if (!allSmsFromBase.contains(smsMessage)) {
                    ParsedSmsManager.addSmsTransToBase(smsMessage);
                    
                    allSmsFromBase.add(smsMessage);
                }
            }
        }
    }

    private static void attTransToBase(Trans transaction) {
        transaction.save();
    }
}
