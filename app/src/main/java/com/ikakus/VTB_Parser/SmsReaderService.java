package com.ikakus.VTB_Parser;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.ikakus.VTB_Parser.BroadcastReceivers.SMSReceiver;
import com.ikakus.VTB_Parser.Classes.ParsedSmsManager;
import com.ikakus.VTB_Parser.Classes.SMSMessage;
import com.ikakus.VTB_Parser.Classes.VTBSmsParser;
import com.ikakus.VTB_Parser.Interfaces.SMSReceiverListener;

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
            ParsedSmsManager.addSmsToBase(smsMessage);
            allSmsFromBase.add(smsMessage);
        }

    }

}
