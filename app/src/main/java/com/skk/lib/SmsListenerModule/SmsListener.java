/*
package com.skk.lib.SmsListenerModule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import com.skk.lib.BaseClasses.BaseActivity;

*/
/**
 * Created by bharat on 19/10/15.
 *//*


public class SmsListener extends BroadcastReceiver {

  private Bundle bundle;
  private SmsMessage currentSMS;
  private String message;

  @Override
  public void onReceive(Context context, Intent intent) {
    // TODO Auto-generated method stub
    Log.d("SmsListener", "onReceive");

    if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
      Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
      SmsMessage[] msgs = null;
      String msg_from;
      String msgBody = "";
      if (bundle != null) {
        //---retrieve the SMS message received---
        try {
          Object[] pdus = (Object[]) bundle.get("pdus");
          msgs = new SmsMessage[pdus.length];

          if (msgs != null) {
            for (int i = 0; i < msgs.length; i++) {
              msgs[i] = getIncomingMessage(pdus[i], bundle);
              msg_from = msgs[i].getOriginatingAddress();
              msgBody = msgs[i].getMessageBody();
            }
            if (BaseActivity.onReciveSMS != null) {
              msgBody = msgBody.replaceAll("[^\\d.]", "");
              msgBody = msgBody.replace(".", "");
              BaseActivity.onReciveSMS.setOnReciveSMS("sms", msgBody.trim());
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
    SmsMessage currentSMS;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      String format = bundle.getString("format");
      currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
    } else {
      currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
    }
    return currentSMS;
  }
}
*/
