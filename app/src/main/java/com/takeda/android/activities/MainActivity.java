package com.takeda.android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessaging;
import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.utils.SessionManager;
import com.splunk.mint.Mint;
import com.takeda.android.GCM.Config;
import com.takeda.android.R;

//import com.takeda.android.GCM.Config;
//import com.takeda.android.GCM.NotificationUtils;
//import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends BaseActivity {

  SessionManager session;
  BroadcastReceiver mRegistrationBroadcastReceiver;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    session = new SessionManager(this);
    Mint.initAndStartSession(this.getApplication(), "59622033");
//        Log.d("InternetCheck","Main Activity Status : "+checkInternet());

    mRegistrationBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        // checking for type intent filter
        Log.d("GCM", "Broadcast Received");
        if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
          // gcm successfully registered
          // now subscribe to `global` topic to receive app wide notifications
          FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
          displayFirebaseRegId();
        } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
          // new push notification is received
          String message = intent.getStringExtra("message");
          Log.d("NewNotification", "Push notification: " + message);
        }
      }
    };

    if (checkInternet()) {
      displayFirebaseRegId();
      checkLogin();
    }
  }

  private void displayFirebaseRegId() {
    String regId = session.getUserDetails().get(SessionManager.GCM_ID);
    Log.e("RegistrationId", "Firebase reg id: " + regId);
  }

    /*@Override
    protected void onResume() {
        super.onResume();
//
//        // register GCM registration complete receiver
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.REGISTRATION_COMPLETE));
//
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.PUSH_NOTIFICATION));
//
//        NotificationUtils.clearNotifications(getApplicationContext());
    }*/

  private void checkLogin() {
    boolean isLoggedIn = session.isLoggedIn();

    if (session.isRemMe()) {
      if (!isLoggedIn) {
        goToLogin();
      } else {
        init(new Runnable() {
          @Override
          public void run() {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
          }
        });
      }
    } else {
      goToLogin();
    }
  }

  void goToLogin() {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
      }
    }, 3000);
  }
}
