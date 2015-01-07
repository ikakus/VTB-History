package com.ikakus.VTB_Parser.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import com.ikakus.VTB_Parser.Interfaces.SMSReceiverListener;
import com.ikakus.VTB_Parser.MainActivity;

/**
 * Created by i.dadiani on 10/30/2014.
 */
public class SMSReceiver extends BroadcastReceiver{
    private final String TAG = this.getClass().getSimpleName();
    private SMSReceiverListener mSmsReceiverListener;
//    private String mDedicatedSource = "VTB Bank";
    private String mDedicatedSource = MainActivity.VTB_SENDER;

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
            Object[] smsextras = (Object[]) extras.get( "pdus" );

            for (Object smsextra : smsextras) {
                SmsMessage smsmsg = SmsMessage.createFromPdu((byte[]) smsextra);
                String strMsgBody = smsmsg.getMessageBody();
                String strMsgSrc = smsmsg.getOriginatingAddress();

                strMessage += "SMS from " + strMsgSrc + " : " + strMsgBody;

                if (strMsgSrc.equals(mDedicatedSource)) {
                    mSmsReceiverListener.onSmsReceived(strMsgBody);
                }
                Log.i(TAG, strMessage);
            }

        }

    }

}