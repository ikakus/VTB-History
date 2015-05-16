package com.ikakus.Card_Holder.Service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.ikakus.Card_Holder.BroadcastReceivers.SMSReceiver;
import com.ikakus.Card_Holder.Classes.ParsedSmsManager;
import com.ikakus.Card_Holder.Classes.SMSMessage;
import com.ikakus.Card_Holder.Interfaces.SMSReceiverListener;
import com.ikakus.Card_Holder.Parsers.VTBSmsParser;

import java.util.List;

/**
 * Created by 404 on 07.01.2015.
 */
public class SmsReaderService extends Service implements SMSReceiverListener {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        this.registerReceiver(new SMSReceiver(this), intentFilter);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSmsReceived(String result) {
        List<SMSMessage> allSmsFromBase = SMSMessage.listAll(SMSMessage.class);
        SMSMessage smsMessage = VTBSmsParser.parseStrToSmsMessage(result);
        if (!allSmsFromBase.contains(smsMessage)) {
            ParsedSmsManager.addSmsTransToBase(smsMessage);
            allSmsFromBase.add(smsMessage);
        }

    }

}
