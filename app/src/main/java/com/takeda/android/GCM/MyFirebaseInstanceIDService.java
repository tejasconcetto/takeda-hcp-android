package com.takeda.android.GCM;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.skk.lib.utils.SessionManager;

/**
 * Created by Akshay Bissa on 02/12/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

  private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

  @Override
  public void onTokenRefresh() {
    super.onTokenRefresh();
    String refreshedToken = FirebaseInstanceId.getInstance().getToken();

    // sending reg id to your server
    sendRegistrationToServer(refreshedToken);

    // Notify UI that registration has completed, so the progress indicator can be hidden.
    Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
    registrationComplete.putExtra("token", refreshedToken);
    LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
  }

  private void sendRegistrationToServer(final String token) {
    // sending gcm token to server
    Log.e(TAG, "sendRegistrationToServer: " + token);
    new SessionManager(getApplicationContext()).setGCMID(token);
  }

}
