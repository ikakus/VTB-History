package com.ikakus.VTB_Parser;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.ikakus.VTB_Parser.BroadcastReceivers.SMSReceiver;
import com.ikakus.VTB_Parser.Classes.DatabaseHandler;
import com.ikakus.VTB_Parser.Classes.SMSMessage;
import com.ikakus.VTB_Parser.Classes.SMSParser;
import com.ikakus.VTB_Parser.Interfaces.SMSReceiverListener;

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

    private void addSms(SMSMessage smsMessage, Context context) {
        DatabaseHandler smsReaderDbHelper = new DatabaseHandler(context);
        smsReaderDbHelper.addSms(smsMessage);
    }

    @Override
    public void onSmsReceived(String result) {
        if (result.contains("TRANSACTION")) {
            SMSMessage smsMessage = SMSParser.parseSmsToSmsMessage(result);
            addSms(smsMessage, this);
        }
    }
}
