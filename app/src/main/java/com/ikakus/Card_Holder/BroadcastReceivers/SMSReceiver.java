package com.ikakus.Card_Holder.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ikakus.Card_Holder.Classes.ParsedSmsManager;
import com.ikakus.Card_Holder.Interfaces.SMSReceiverListener;

/**
 * Created by i.dadiani on 10/30/2014.
 */
public class SMSReceiver extends BroadcastReceiver{
    private final String TAG = this.getClass().getSimpleName();
    private SMSReceiverListener mSmsReceiverListener;

    private String mDedicatedSource = ParsedSmsManager.VTB_SENDER;

    public SMSReceiver(SMSReceiverListener smsReceiverListener){
        mSmsReceiverListener = smsReceiverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle extras = intent.getExtras();

        String strMessage = "";

        if ( extras != null )
        {
            Object[] smsExtras = (Object[]) extras.get( "pdus" );

            for (Object smsExtra : smsExtras) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) smsExtra);
                String strMsgBody = smsMessage.getMessageBody();
                String strMsgSrc = smsMessage.getOriginatingAddress();

                strMessage += "SMS from " + strMsgSrc + " : " + strMsgBody;

                if (strMsgSrc.equals(mDedicatedSource)) {
                    mSmsReceiverListener.onSmsReceived(strMsgBody);
                }
                Log.i(TAG, strMessage);
            }

        }

    }

}