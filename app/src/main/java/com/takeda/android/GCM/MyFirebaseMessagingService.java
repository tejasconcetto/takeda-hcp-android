package com.takeda.android.GCM;

/**
 * Created by amitkumar on 02/12/16.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.takeda.android.activities.MiscActivity;
import org.json.JSONObject;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

  private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

  private NotificationUtils notificationUtils;
//    TagoveApplication context = ((TagoveApplication)getApplicationContext());

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    try {
      Log.e(TAG, "From: " + remoteMessage.getFrom());
      Log.e(TAG, "Data: " + String.valueOf(remoteMessage.getData()));
      Log.e(TAG, "Data Size: " + remoteMessage.getData().size());
//            Toast.makeText(getApplicationContext(),"App Bg Status - "+NotificationUtils.isAppIsInBackground(getApplicationContext()),
//                    Toast.LENGTH_LONG).show();
      Log.e(TAG,
          "App Bg status: " + NotificationUtils.isAppIsInBackground(getApplicationContext()));

      if (remoteMessage == null) {
        return;
      }

      if (remoteMessage.getData().size() > 0) {
        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

        try {
          JSONObject json = new JSONObject(remoteMessage.getData());
          Log.e(TAG, "Final JSON: " + json.toString());
//                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
          handleDataMessage(json);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      // Check if message contains a notification payload.
      else if (remoteMessage.getNotification() != null) {
        Log.e(TAG, "Notification: " + remoteMessage.getNotification());
        Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
        //handleNotification(remoteMessage.getNotification().getBody());
//                handleDataMessage(remoteMessage.getNotification().getTitle(),
//                        remoteMessage.getNotification().getBody());
      }

      // Check if message contains a data payload.
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handleNotification(String message) {
    try {
//            JSONObject notificationBody = new JSONObject(message);
//        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
      // app is in foreground, broadcast the push message
      Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
      pushNotification.putExtra("message", message);
//            pushNotification.putExtra("message", notificationBody.has("event_title") ? notificationBody.getString("event_title") :
//                "Takeda Event Coming Soon");
      LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

      // play notification sound
      NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
      notificationUtils.playNotificationSound();
//        }else{
//            // If the app is in background, firebase itself handles the notification
//        }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handleDataMessage(JSONObject jsonObject) {
//        Log.e(TAG, "push json: " + json.toString());

    try {
      //JSONObject json = jsonObject.getJSONObject("message");
      String title = "Upcoming Event for Takeda",
          message = jsonObject.getString("event_title");
      String imageUrl = "";
      String timestamp = "";

      Intent resultIntent_new = new Intent(getApplicationContext(), MiscActivity.class);
      resultIntent_new.putExtra("status_type", "event_notify");
      resultIntent_new.putExtra("event_id", jsonObject.getString("event_id"));
      resultIntent_new.putExtra("event_date", jsonObject.getString("event_date"));

      Log.d(TAG, "URL STatus : " + imageUrl);
      if (TextUtils.isEmpty(imageUrl) && !URLUtil.isValidUrl(imageUrl)) {
        showNotificationMessage(getApplicationContext(), title, message, timestamp,
            resultIntent_new);
      } else {
        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp,
            resultIntent_new, imageUrl);
      }
      //notificationUtils.playNotificationSound();
    } catch (Exception e) {
      Log.e(TAG, "Json Exception: " + e.getMessage());
    }
  }

  /**
   * Showing notification with text only
   */
  private void showNotificationMessage(Context context, String title, String message,
      String timeStamp, Intent intent) {
    notificationUtils = new NotificationUtils(context);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
  }

  /**
   * Showing notification with text and image
   */
  private void showNotificationMessageWithBigImage(Context context, String title, String message,
      String timeStamp, Intent intent, String imageUrl) {
    notificationUtils = new NotificationUtils(context);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
  }
}
