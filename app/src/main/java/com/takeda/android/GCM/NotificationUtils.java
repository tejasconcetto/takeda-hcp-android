package com.takeda.android.GCM;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import com.takeda.android.R;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by amitkumar on 02/12/16.
 */

public class NotificationUtils {

  private static String TAG = NotificationUtils.class.getSimpleName();
  private Context mContext;
  NotificationManager mNotificationManager;

  public NotificationUtils(Context mContext) {
    this.mContext = mContext;
  }

  public void showNotificationMessage(String title, String message, String timeStamp,
      Intent intent) {
    showNotificationMessage(title, message, timeStamp, intent, null);
  }

  public void showNotificationMessage(final String title, final String message,
      final String timeStamp, Intent intent, String imageUrl) {
    // Check for empty push message
    if (TextUtils.isEmpty(message)) {
      return;
    }

    // notification icon
    final int icon = R.drawable.app_logo;

    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    final PendingIntent resultPendingIntent =
        PendingIntent.getActivity(
            mContext,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        );

    final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);

    if (!TextUtils.isEmpty(imageUrl)) {

      if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl)
          .matches()) {
        Bitmap bitmap = getBitmapFromURL(imageUrl);

        if (bitmap != null) {
          showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp,
              resultPendingIntent);
        } else {
          showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent);
        }
      }
    } else {
      showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent);
      playNotificationSound();
    }
  }


  private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title,
      String message, String timeStamp, PendingIntent resultPendingIntent) {

    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
    inboxStyle.addLine(message);

    Notification notification;
    notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
        .setAutoCancel(true)
        .setContentTitle(title)
        .setContentIntent(resultPendingIntent)
        .setStyle(inboxStyle)
        .setSmallIcon(R.drawable.app_logo)
        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
        .setContentText(message)
        .build();

    //                .setWhen(getTimeMilliSec(timeStamp))

    NotificationManager notificationManager = (NotificationManager) mContext
        .getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(Config.NOTIFICATION_ID, notification);
  }

  private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon,
      String title, String message, String timeStamp, PendingIntent resultPendingIntent) {
    NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
    bigPictureStyle.setBigContentTitle(title);
    bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
    bigPictureStyle.bigPicture(bitmap);
    Notification notification;
    notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
        .setAutoCancel(true)
        .setContentTitle(title)
        .setContentIntent(resultPendingIntent)
        .setStyle(bigPictureStyle)
        .setSmallIcon(R.drawable.app_logo)
        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
        .setContentText(message)
        .build();

    NotificationManager notificationManager = (NotificationManager) mContext
        .getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);
  }

  /**
   * Downloading push notification image before displaying it in
   * the notification tray
   */
  public Bitmap getBitmapFromURL(String strURL) {
    try {
      URL url = new URL(strURL);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.connect();
      InputStream input = connection.getInputStream();
      Bitmap myBitmap = BitmapFactory.decodeStream(input);
      return myBitmap;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  // Playing notification sound
  public void playNotificationSound() {
    try {
      Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
      r.play();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Method checks if the app is in background or not
   */
  public static boolean isAppIsInBackground(Context context) {
    boolean isInBackground = true;
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
      List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
      for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
          for (String activeProcess : processInfo.pkgList) {
            if (activeProcess.equals(context.getPackageName())) {
              isInBackground = false;
            }
          }
        }
      }
    } else {
      List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
      ComponentName componentInfo = taskInfo.get(0).topActivity;
      if (componentInfo.getPackageName().equals(context.getPackageName())) {
        isInBackground = false;
      }
    }

    return isInBackground;
  }

  // Clears notification tray messages
  public static void clearNotifications(Context context) {
    NotificationManager notificationManager = (NotificationManager) context
        .getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();
  }

  public static long getTimeMilliSec(String timeStamp) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      Date date = format.parse(timeStamp);
      return date.getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return 0;
  }

//    private void generateBigPictireStyleNotification(Intent intent) {
//        Bitmap remote_picture = null;
//
//        Notification notification = new Notification();
//
//        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        try {
//            Log.d("ImageURLNotification","Large Icon Image URL - "+intent.getExtras().getString("largeIcon"));
//            remote_picture = BitmapFactory.decodeStream(
//                    (InputStream) new URL(intent.getExtras().getString("largeIcon")).getContent());
////            http://whysoblu.com/wp-content/uploads/2011/06/800__vera_cruz_blu-ray_3_1.jpg
////            intent.getExtras().getString("largeIcon")
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // Add the big picture to the style.
//
//        // Creates an explicit intent for an ResultActivity to receive.
//        Intent resultIntent = new Intent(this, MainActivity.class);
//
//        // This ensures that the back button follows the recommended
//        // convention for the back key.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Adds the back stack for the Intent (but not the Intent itself).
////        stackBuilder.addParentStack(ResultActivity.class);
//
//        // Adds the Intent that starts the Activity to the top of the stack.
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
//                0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        if(!(remote_picture==null))
//        {
//            notification = setBigPictureNotification(intent, remote_picture);
//            // Create the style object with BigPictureStyle subclass.
//        }
//
//        if(remote_picture==null)
//        {
//            notification = setNormalNotification(intent);
//
//        }
//
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        if (intent.getExtras().getString("sound").equalsIgnoreCase("1")) {
//            // Play default notification sound
//            notification.defaults |= Notification.DEFAULT_SOUND;
//        }
//
//        if (intent.getExtras().getString("vibrate").equalsIgnoreCase("1")) {
//            // Vibrate if vibrate is enabled
//            notification.defaults |= Notification.DEFAULT_VIBRATE;
//        }
//        mNotificationManager.notify(0, notification);
//    }
//
//    /*
//    ********Normal Text Style Notification ***********
//     */
//    private Notification setNormalNotification(Intent intent) {
//
//        // Creates an explicit intent for an ResultActivity to receive.
//        Intent resultIntent = new Intent(this, MainActivity.class);
//
//        // This ensures that the back button follows the recommended
//        // convention for the back key.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Adds the back stack for the Intent (but not the Intent itself).
//        //        stackBuilder.addParentStack(ResultActivity.class);
//
//        // Adds the Intent that starts the Activity to the top of the stack.
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
//                0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        return new android.support.v4.app.NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.noti_icon)
//                .setAutoCancel(true)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.large_icon))
//                .setContentIntent(resultPendingIntent)
//                .setContentTitle("BlickBee")
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(intent.getExtras().getString("msg")))
//                .setContentText(intent.getExtras().getString("msg")).build();
//    }
//
//    /*
//        ********Big Picture Style Notification ***********
// */
//    private Notification setBigPictureNotification(Intent intent, Bitmap remote_picture) {
//
//        NotificationCompat.BigPictureStyle notiStyle = new
//                NotificationCompat.BigPictureStyle();
//        notiStyle.setBigContentTitle("BlickBee");
//        notiStyle.setSummaryText(intent.getExtras().getString("msg"));
//
//        notiStyle.bigPicture(remote_picture);
//
//        // Creates an explicit intent for an ResultActivity to receive.
//        Intent resultIntent = new Intent(this, MainActivity.class);
//
//        // This ensures that the back button follows the recommended
//        // convention for the back key.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Adds the back stack for the Intent (but not the Intent itself).
//        //        stackBuilder.addParentStack(ResultActivity.class);
//
//        // Adds the Intent that starts the Activity to the top of the stack.
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
//                0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        return new android.support.v4.app.NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.noti_icon)
//                .setAutoCancel(true)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.large_icon))
//                .setContentIntent(resultPendingIntent)
//                .setContentTitle("BlickBee")
//                .setContentText(intent.getExtras().getString("msg"))
//                .setStyle(notiStyle).build();
//    }
//
//    @SuppressWarnings("deprecation")
//    @SuppressLint("NewApi")
//    private void showNotification(String str_message, int noti_id, Intent intent) {
//        try {
//            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            PendingIntent contentIntent = PendingIntent.getActivity(
//                    getApplicationContext(), (int) System.currentTimeMillis(),
//                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            Notification notification;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                Notification.Builder builder = new Notification.Builder(
//                        mContext);
//                builder.setContentIntent(contentIntent)
//                        .setSmallIcon(R.drawable.ic_add_white_24dp)
//                        .setTicker(str_message)
//                        .setContentText(str_message)
//                        .setWhen(System.currentTimeMillis())
////                        .setStyle(new Notification.BigPictureStyle()
////                                .bigPicture(bigBitmap)
////                                .setBigContentTitle("big title"))
//                        .setAutoCancel(true)
//                        .setStyle(
//                                new Notification.BigTextStyle()
//                                        .bigText(str_message))
//                        .setContentTitle(getString(R.string.app_name));
//                notification = builder.build();
//            } else {
//                notification = new Notification(R.drawable.ic_add_white_24dp,
//                        str_message, System.currentTimeMillis());
//            }
//            notification.defaults = Notification.DEFAULT_SOUND
//                    | Notification.DEFAULT_VIBRATE;
//            notification.flags |= Notification.FLAG_AUTO_CANCEL;
//            try {
//                mNotificationManager.notify(noti_id, notification);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                mNotificationManager.notify(0, notification);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void cancelNotifiation(Context mContext,
//                                         String notification_id) {
//        try {
//            if (mNotificationManager == null)
//                mNotificationManager = (NotificationManager) mContext
//                        .getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.cancel(Integer.parseInt(notification_id));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//

}
