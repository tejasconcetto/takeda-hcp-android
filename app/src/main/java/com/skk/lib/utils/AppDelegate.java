package com.skk.lib.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.skk.lib.Interfaces.textWatcherCustom;
import com.takeda.android.R;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Akshay on 07-Dec-16.
 */

public class AppDelegate {

  public static int pagerPosition = -1;

  public static ProgressDialog loadingDialog;
  public static int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0;
  public static int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
  public static boolean b = false;
  public static boolean internetCheck = false;
  public static boolean waitingForResponse = true;
  public static String androidPermission[] = {
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE};

  public static boolean checkPermission(Context mContext, int REQUEST_CODE) {

    int result = ContextCompat.checkSelfPermission(mContext, androidPermission[REQUEST_CODE]);
    Log.d("PermissionCheck", "Permission result : " + result);
    return result == PackageManager.PERMISSION_GRANTED;
  }

  public static void requestPermission(Activity mActivity, View mView, int PERMISSION_REQUEST_CODE,
      Runnable runnable) {

    if (!checkPermission(mActivity, PERMISSION_REQUEST_CODE)) {
      {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
            androidPermission[PERMISSION_REQUEST_CODE])) {
          Toast.makeText(mActivity,
              mActivity.getResources().getString(R.string.allow_permissions_msg), Toast.LENGTH_LONG)
              .show();
        } else {
          ActivityCompat.requestPermissions(mActivity,
              new String[]{androidPermission[PERMISSION_REQUEST_CODE]}, PERMISSION_REQUEST_CODE);
        }
      }
    } else {
      Log.d("Permission", "GRANTED");
      permissionResult(PERMISSION_REQUEST_CODE, mActivity, mView, runnable);
    }
  }

  public static void permissionResult(int PERMISSION_REQUEST_CODE, Activity mActivity, View mView,
      Runnable runnable) {

    if (PERMISSION_REQUEST_CODE == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
      requestPermission(mActivity, mView, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE, runnable);
    }

//        else if(PERMISSION_REQUEST_CODE == AppDelegate.PERMISSION_REQUEST_CAMERA){
//            requestPermission(mActivity,mView,PERMISSION_REQUEST_RECORD_AUDIO, runnable);
//        }
//
//        else if (PERMISSION_REQUEST_CODE == PERMISSION_REQUEST_RECORD_AUDIO){
//            requestPermission(mActivity,mView,PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE,runnable);
//        }

    else if (PERMISSION_REQUEST_CODE == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
      if (runnable != null) {
        runnable.run();
      }
    }
  }

  public static int getColorRes(Context mContext, int colorResId) {
    final int version = Build.VERSION.SDK_INT;
    if (version >= 23) {
      return ContextCompat.getColor(mContext, colorResId);
    } else {
      return mContext.getResources().getColor(colorResId);
    }
  }

  public static Drawable getDrawable(Context mContext, int drawableResId) {
    final int version = Build.VERSION.SDK_INT;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      return mContext.getResources().getDrawable(drawableResId, null);
    } else {
      return mContext.getResources().getDrawable(drawableResId);
    }
  }

  public static void showHideLoading(Context mContext) {

    ProgressDialog progressDialog = new ProgressDialog(mContext);
    progressDialog.setCancelable(false);
    progressDialog.setMessage("Wait...");
    progressDialog.show();

  }

  public static void showLoadingDialog(Context mContext) {
    try {
      if (loadingDialog != null && loadingDialog.isShowing() | mContext == null) {
        return;
      }

      loadingDialog = new ProgressDialog(mContext);
      loadingDialog.setCancelable(false);
      loadingDialog.setMessage("Wait...");
      loadingDialog.show();
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("Exception", e.toString());
    }
  }

  public static void hideLoadingDialog(Context mContext) {
    try {
      if (loadingDialog != null && loadingDialog.isShowing()) {
        loadingDialog.dismiss();
      }
      loadingDialog = null;
    } catch (Exception e) {
      Log.d("Exception", e.toString());
      e.printStackTrace();
    }
  }

//    public static void showNotification(TagoveApplication context, Class movingTo, JSONArray notificationData){
//        // Using RemoteViews to bind custom layouts into Notification
//        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.customnotification_layout);
//
//        // Set Notification Title and Text
//        String strtitle = context.getString(R.string.app_name);
//        String strtext = context.getString(R.string.app_name);
//
//        try{
//
//            switch (notificationData.getString(0)){
//                case "call":
//                    break;
//
//                case "":
//                    break;
//            }
//            // Open NotificationView Class on Notification Click
//            Intent intent = new Intent(context, movingTo);
//            intent.putExtra("title", strtitle);
//            intent.putExtra("text", strtext);
//            // Open NotificationView.java Activity
//            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                    .setSmallIcon(R.mipmap.logo_white_title)
//                    .setAutoCancel(false)
//                    .setContentIntent(pIntent)
//                    .setContent(remoteViews);
//
//            // Locate and set the Image into customnotificationtext.xml ImageViews
//            remoteViews.setImageViewResource(R.id.imagenotileft,R.mipmap.logo_white_title);
//            remoteViews.setImageViewResource(R.id.imagenotiright,R.mipmap.ic_launcher);
//
//            // Locate and set the Text into customnotificationtext.xml TextViews
//            remoteViews.setTextViewText(R.id.title,context.getString(R.string.app_name));
//            //   remoteViews.setTextViewText(R.id.text,context.getString(R.string.customnotificationtext));
//
//            // Create Notification Manager
//            // NotificationManager notificationmanager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
//            // Build Notification with Notification Manager
//            // notificationmanager.notify(0, builder.build());
//        }
//
//        catch(Exception e){
//            AppDelegate.LogE(e.toString());
//        }
//    }

  int getPercentage(int localValue, int containerValue) {
    return (int) (localValue * 100.0 / containerValue);
  }

  int getCenter(int videoCount, int videoSize, int containerSize) {
    return getPercentage((int) Math.round((containerSize - videoSize * videoCount) / 2.0),
        containerSize);
  }

  private final static long KB_FACTOR = 1024;
  private final static long MB_FACTOR = 1024 * KB_FACTOR;
  private final static long GB_FACTOR = 1024 * MB_FACTOR;

  public static double getByteSize(String arg0) {
    int caseNo = 0;

    if (arg0.contains("KB")) {
      arg0 = arg0.replace("KB", "");
      caseNo = 1;
    } else if (arg0.contains("MB")) {
      arg0 = arg0.replace("MB", "");
      caseNo = 1;
    } else if (arg0.contains("GB")) {
      arg0 = arg0.replace("GB", "");
      caseNo = 3;
    }

    arg0 = arg0.replaceAll(" ", "");

    Log.d("Arg0", "final string : " + arg0);
    Log.d("Arg0", "case final : " + caseNo);
    double ret = Double.parseDouble(arg0);

    switch (caseNo) {
      case 3:
        return ret * GB_FACTOR;
      case 2:
        return ret * MB_FACTOR;
      case 1:
        return ret * KB_FACTOR;
    }
    return -1;
  }

  public static boolean currentVersionSupportBigNotification() {
    int sdkVersion = Build.VERSION.SDK_INT;
    return sdkVersion >= Build.VERSION_CODES.JELLY_BEAN;
  }

  public static boolean haveNetworkConnection(Context mContext) {
    if (mContext != null) {
      return AppDelegate.isConnectingToInternet(mContext);
    } else {
      return false;
    }
  }

  public static boolean isConnectingToInternet(Context _context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) _context
        .getSystemService(Context.CONNECTIVITY_SERVICE);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Network[] networks = connectivityManager.getAllNetworks();
//            NetworkInfo networkInfo;
//            for (Network mNetwork : networks) {
//                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
//                Log.d("NetworkConnected", "NETWORKNAME LIST : " + networkInfo.getTypeName());
//                if (networkInfo.isConnectedOrConnecting()) {
//                    Log.d("NetworkConnected", "NETWORKNAME: " + networkInfo.getTypeName());
//                    if(networkInfo.getTypeName().equalsIgnoreCase("MOBILE")){
//                        try {
//                            Class cmClass = Class.forName(connectivityManager.getClass().getName());
//                            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
//                            method.setAccessible(true); // Make the method callable
//                            // get the setting for "mobile data"
//                            Log.d("NetworkConnected", "MOBILE Connected Status: " + (Boolean)method.invoke(connectivityManager));
//                            return (Boolean)method.invoke(connectivityManager);
//                        } catch (Exception e) {
//                            // Some problem accessible private API
//                            // TODO do whatever error handling you want here
//                        }
//                    }
//
//                    else{
//                        return true;
//                    }
//                }
//            }
//        } else {
    if (connectivityManager != null) {
      //noinspection deprecation
      NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
      if (info != null) {
        for (NetworkInfo anInfo : info) {
          if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
            Log.d("NetworkConnected", "NETWORKNAME: " + anInfo.getTypeName());
            return true;
          }
        }
      }
    }
//        }
    return false;
  }


  public static boolean hasActiveInternetConnection(final Context context) {

    new AsyncTask() {

      @Override
      protected void onPreExecute() {
        super.onPreExecute();
        internetCheck = false;
        waitingForResponse = true;
      }

      @Override
      protected Object doInBackground(Object[] params) {
        try {
          if (isConnectingToInternet(context)) {
            try {
              HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com")
                  .openConnection());
              urlc.setRequestProperty("User-Agent", "Test");
              urlc.setRequestProperty("Connection", "close");
              //urlc.setConnectTimeout(1500);
              urlc.connect();
              internetCheck = (urlc.getResponseCode() == 200);
              waitingForResponse = false;
              return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
              Log.e("InternetCheck", "Error checking internet connection", e);
            }
          } else {
            Log.d("InternetCheck", "No network available!");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        return false;
      }

      @Override
      protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.d("InternetCheck", "onPostExecute : " + String.valueOf(o));
//                internetCheck = (boolean)o;
//                waitingForResponse = false;
      }
    }.execute();
    while (waitingForResponse) {
      Log.d("InternetCheck", "WaitResponse check : " + internetCheck);
      Log.d("InternetCheck", "Response got from listener : " + internetCheck);
    }
    return internetCheck;
  }

  /**
   * method to show Toast just pass the context and your message.
   */
  public static void showToast(Context mContext, String Message) {
    try {
      if (mContext != null) {
        Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
      } else {
        Log.d("Exception", "context is null at showing toast.");
      }
    } catch (Exception e) {
      Log.d("Exception", "context is null at showing toast.");
      e.printStackTrace();
    }
  }

  public static boolean isValidEmail(String email) {
    String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  /**
   * Hides the soft keyboard
   */
  public static void hideSoftKeyboard(Context context, EditText view) {
    try {
      InputMethodManager imm = (InputMethodManager) context
          .getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void showSoftKeyboard(Context context, EditText view) {
    try {
      InputMethodManager inputMethodManager =
          (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
      inputMethodManager.toggleSoftInputFromWindow(
          view.getApplicationWindowToken(),
          InputMethodManager.SHOW_FORCED, 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static boolean isValidPassword(String password) {
    boolean isValid = true;
    if (password.length() <= 0 || password == null || password.equalsIgnoreCase("")) {
      isValid = false;
    }
    return isValid;
  }

  //Mobile Number Validation
  public static boolean isValidMobileNumber(String string) {
    return string.matches("\\d{10}");
  }

  //String Validation (Eg-name etc) but excluding Address String
  public static boolean isValidString(String string) {

    boolean isValid = true;

    if (string.length() == 0 || string.equalsIgnoreCase(null)
        || string.equalsIgnoreCase("")) {
      isValid = false;
    } else {
      String[] invalid_chars = {"~", "`", "$", "!", "@", "#", "%", "^", "&", "*", "(", ")", "-",
          "_", "+", "=", "{", "}", "|", ":", ";", "0", "1", "2", "3", "4", "5", "6", "7", "8",
          "9",
          "<", ",", ">", "?", "/", "[", "]", ".", "'"};
      for (String fetchedChar : invalid_chars) {
        if (string.contains(fetchedChar)) {
          isValid = false;
          break;
        }
      }
    }
    return isValid;
  }


  public static boolean isValidNameString(String string) {

    boolean isValid = true;

    if (string.length() == 0 || string.equalsIgnoreCase(null) || string.equalsIgnoreCase("")) {
      isValid = false;
    } else {
      String[] invalid_chars = {"~", "`", "$", "!", "@", "#", "%", "^", "&", "*", "(", ")", "-",
          "_", "+", "=", "{", "}", "|", ":", ";", "0", "1", "2", "3", "4", "5", "6", "7", "8",
          "9",
          "<", ",", ">", "?", "/", "[", "]", ".", "'"};
      for (String fetchedChar : invalid_chars) {
        if (string.contains(fetchedChar)) {
          isValid = false;
          break;
        }
      }
//            String STRING_PATTERN = "^[_A-Za-z\\+]";
//            Pattern pattern = Pattern.compile(STRING_PATTERN);
//            Matcher matcher = pattern.matcher(string);
//            isValid = matcher.matches();
    }
    return isValid;
  }

  public static void setTextWatcher(boolean immStatus, final View editTextViewId,
      final textWatcherCustom listener) {

    //immStatus : True - Need immediate response
    //immStatus : False - Do not Need immediate response

    if (listener == null || editTextViewId == null) {
      return;
    }

    EditText editText = (EditText) editTextViewId;

    if (immStatus) {
      listener.addingText(editTextViewId.getId(), editText.getText().toString());
    }

    ((EditText) editTextViewId).addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Log.d("TextChanged","BeforeTextChanged Called : "+s.toString());
        listener.addingText(editTextViewId.getId(), s.toString());
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        listener.addingText(editTextViewId.getId(), s.toString());
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
  }

  public static void loadImageFromPicasaRactangle(Context mContext,
      final ImageView imageView, String str_image) {
//        Bitmap bitmapCache;
//        Bitmap bitmapMask = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_logo);
//        bitmapCache = bitmapMask;
//        imageView.setImageBitmap(bitmapCache);

//        Fresco.initialize(mContext);

    if (str_image != null && !str_image.equalsIgnoreCase("")
        && Patterns.WEB_URL.matcher(str_image).matches()) {
//            bitmapMask = BitmapFactory.decodeResource(
//                    mContext.getResources(), R.drawable.logo);
//            imageView.setImageBitmap(bitmapMask);
      try {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Picasso.with(mContext).load(str_image)
                            .placeholder(mContext.getResources().getDrawable(R.drawable.app_logo,null))
                            .into(imageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    if(imageView != null){
                                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                    }
                                }

                                @Override
                                public void onError() {

                                }
                            });
                }

                else{
                    Picasso.with(mContext).load(str_image)
                            .placeholder(mContext.getResources().getDrawable(R.drawable.app_logo))
                            .into(imageView,new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if(imageView != null){
                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }*/

        Uri uriTwo = Uri.parse(str_image);

        imageView.setImageURI(uriTwo);

                /*ImagePipeline imagePipeline = Fresco.getImagePipeline();
                Uri uri = Uri.parse(str_image);
                imagePipeline.evictFromMemoryCache(uri);
                imageView.setImageURI(uri);*/



               /* Glide.with(mContext)
                        .load(str_image)
                        .into(imageView);*/

      } catch (OutOfMemoryError e) {
        Log.d("AppDelegatePicasso", "Exception : " + e.toString());
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
//        else {
//            bitmapMask = BitmapFactory.decodeResource(
//                    mContext.getResources(), R.drawable.logo);
//            imageView.setImageBitmap(bitmapMask);
//        }
  }

  public static void setListViewHeightBasedOnChildren(ListView listView) {
    ListAdapter listAdapter = listView.getAdapter();
    if (listAdapter == null) {
      // pre-condition
      return;
    }
    int totalHeight = 0;
    int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
        View.MeasureSpec.AT_MOST);
    for (int i = 0; i < listAdapter.getCount(); i++) {
      View listItem = listAdapter.getView(i, null, listView);
      listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
      totalHeight += listItem.getMeasuredHeight();
    }
    totalHeight += listView.getPaddingBottom() + listView.getPaddingTop();
    ViewGroup.LayoutParams params = listView.getLayoutParams();
    params.height = totalHeight
        + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    listView.setLayoutParams(params);
    listView.requestLayout();
  }

  public static void showErrorSBar(String Message, View view) {
    try {
      final Snackbar snackbar = Snackbar.make(view, Message, Snackbar.LENGTH_INDEFINITE);
      snackbar.setAction("CLOSE", new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          snackbar.dismiss();
        }
      });
      View mView = snackbar.getView();
      TextView tv = mView.findViewById(android.support.design.R.id.snackbar_text);
      tv.setTextColor(Color.RED);
      TextView action = mView.findViewById(android.support.design.R.id.snackbar_action);
      action.setTextColor(Color.WHITE);
      snackbar.show();

    } catch (Exception e) {
      Log.d("Exception", "context is null at showing toast.");
      e.printStackTrace();
    }
  }

  public static String checkString(String sampleString) {
    String returnString = sampleString;
    if (sampleString == null || sampleString.equalsIgnoreCase(null) || sampleString
        .equalsIgnoreCase("null") ||
        sampleString.equalsIgnoreCase("")) {
      returnString = "";
    }

//        if(hasHTMLTags(sampleString)){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      returnString = Html.fromHtml(returnString, Html.FROM_HTML_MODE_LEGACY).toString().trim();
    } else {
      returnString = Html.fromHtml(returnString).toString().trim();
    }
//        }
    return returnString;
  }

  public static Date getDate(long time) {
    try {
      Date date = new Date(time * 1000L); // *1000 is to convert seconds to milliseconds
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd"); // the format of your date
      sdf.setTimeZone(TimeZone.getTimeZone(Calendar.getInstance().getTimeZone()
          .getID())); // give a timezone reference for formating (see comment at the bottom
      String formattedDate = sdf.format(date);
      return sdf.parse(formattedDate);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean hasHTMLTags(String text) {
    String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    Pattern pattern = Pattern.compile(HTML_PATTERN);

    Matcher matcher = pattern.matcher(text);
    return matcher.matches();
  }
}
