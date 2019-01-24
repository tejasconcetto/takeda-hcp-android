package com.skk.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.takeda.android.BuildConfig;
import com.takeda.android.activities.LoginActivity;
import com.takeda.android.model.EventModal;

import java.util.ArrayList;
import java.util.HashMap;

public class SessionManager {

  // Shared Preferences
  SharedPreferences pref;

  // Editor for Shared preferences
  Editor editor;

  // Context
  Context _context;

  // Shared pref mode
  int PRIVATE_MODE = 0;

  // Sharedpref file name
  private static final String PREF_NAME = "Takeda";

  public static final String GCM_ID = "gcmToken";
  public static final String IS_DEVELOPER = "false";
  public static final String IS_REM_ME = "IsRememberMe";
  private static final String IS_LOGIN = "IsLoggedIn";
  public static final String KEY_USER_ID = "userid";
  public static final String KEY_DOC_TITLE = "title";
  public static final String KEY_USER_JSON = "userJSON";
  public static final String KEY_ACCESS_TOKEN = "access_token";
  public static final String IS_FIRST_TIME = "first_time";
  public static final String KEY_NOTIFICATION_STATUS = "notify_status";
  public static final String KEY_BASE_URL = "base_url";
  public static final String KEY_PURCHASE_HISTORY_FLAG = "purchase_history_flag";
  public static final String KEY_CALENDAR_SYNC_FLAG = "calendar_sync";
  public static String BASE_URL = "http://mentem.in/takeda_backend/api/Dummy";
  public static String BASE_URL_AWS = "http://13.229.235.139/api/";
  //UAT URL -> "http://13.229.235.139/api/";
  // "http://192.168.123.107/takeda-hcp-Admin/api/";
  // "http://ec2-52-77-230-85.ap-southeast-1.compute.amazonaws.com/api/";
  //live URL -> public static String BASE_URL_AWS = "https://hcp.takeda-hk.com/api/";
  public static String BASE_URL_ALTUS = "http://altustendo.com/takeda_backend/api/Dummy";

  // Constructor
  public SessionManager(Context context) {
    this._context = context;
    pref = _context.getSharedPreferences(PREF_NAME, 0);
    editor = pref.edit();
  }

  /**
   * Create login session
   */
  public void createLoginSession(String id, String phone, String access_token, String jsonString) {
    // Storing login value as TRUE
    editor.putBoolean(IS_LOGIN, true);
    editor.putString(KEY_USER_ID, id);
    editor.putString(KEY_ACCESS_TOKEN, access_token);
    editor.putString(KEY_USER_JSON, jsonString);

    if (phone.equalsIgnoreCase("9461144182")) {
      editor.putBoolean(IS_DEVELOPER, true);
    }
    // commit changes
    editor.commit();
  }


  public void setKeyDocTitle(String title) {
    editor.putString(KEY_DOC_TITLE, title).apply();
  }

  public String getKeyDocTitle() {
    return pref.getString(KEY_DOC_TITLE, "");
  }
  /**
   * Check login method wil check user login status
   * If false it will redirect user to login page
   * Else won't do anything
   */
  public void checkLogin() {
    // Check login status
    if (!this.isLoggedIn()) {
      // user is not logged in redirect him to Login Activity
//            Intent i = new Intent(_context, MainActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            _context.startActivity(i);
    }
  }

  public void showLogs(String TAG, String message) {
    if (BuildConfig.DEBUG || pref.getBoolean(IS_DEVELOPER, false)) {
      Log.d(TAG, message);
    }
  }

  /**
   * Get stored session data
   */
  public HashMap<String, String> getUserDetails() {
    HashMap<String, String> user = new HashMap<String, String>();
    user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
    user.put(KEY_ACCESS_TOKEN, pref.getString(KEY_ACCESS_TOKEN, null));
    user.put(KEY_USER_JSON, pref.getString(KEY_USER_JSON, null));
    user.put(GCM_ID, pref.getString(GCM_ID, null));
    return user;
  }

  public void setKeyPurchaseHistoryFlag(int purchaseHistoryFlag) {
    pref.edit().putInt(KEY_PURCHASE_HISTORY_FLAG, purchaseHistoryFlag).apply();
  }

  public int isShowPurchaseHistory() {
    return pref.getInt(KEY_PURCHASE_HISTORY_FLAG, 1);
  }

  /**
   * Clear session details
   */
  public void logoutUser(Activity mActivity) {

    boolean isFirstTime = pref.getBoolean(IS_FIRST_TIME, true);
    String gcmTOken = pref.getString(GCM_ID, null);
    boolean isToNotify = pref.getBoolean(KEY_NOTIFICATION_STATUS, false);
    editor = pref.edit();
    editor.clear();

    editor.putBoolean(IS_FIRST_TIME, isFirstTime);
    editor.putBoolean(IS_DEVELOPER, false);
    editor.putString(GCM_ID, gcmTOken);
    editor.putBoolean(KEY_NOTIFICATION_STATUS, isToNotify);
    editor.commit();

    mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
    mActivity.finish();
  }

  public void setRememberNotification(String key, boolean status) {
    editor = pref.edit();
    editor.putBoolean(key, status);
    editor.commit();
  }

  /**
   * Quick check for login
   **/
  // Get Login State
  public boolean isLoggedIn() {
    return pref.getBoolean(IS_LOGIN, false);
  }

  public boolean isRemMe() {
    return pref.getBoolean(IS_REM_ME, false);
  }

  public boolean isToNotify() {
    return pref.getBoolean(KEY_NOTIFICATION_STATUS, false);
  }

  public boolean isFirstTime() {
    return pref.getBoolean(IS_FIRST_TIME, true);
  }

  public boolean isDeveloper() {
    return pref.getBoolean(IS_DEVELOPER, true);
  }

  public void setDeveloper(boolean developerStatus) {
    editor.putBoolean(IS_DEVELOPER, developerStatus);
    editor.commit();
  }

  public void setGCMID(String gcm_token) {
    showLogs("GCMToken", gcm_token);
    editor.putString(GCM_ID, gcm_token);
    editor.commit();
  }

  public void setFirstTimeFalse() {
    editor.putBoolean(IS_FIRST_TIME, false);
    editor.commit();
  }

  public void setBaseURL(String url) {
    editor.putString(KEY_BASE_URL, url);
    editor.commit();
    showLogs("BASE_URL", "URL - " + getBaseURL());
  }

  public String getBaseURL() {
    return pref.getString(KEY_BASE_URL, BASE_URL_AWS);
  }

  public void setAddressArrayData(ArrayList<EventModal> userAddressModals) {

  }

  public void setCalendarSync(boolean status) {
    editor = pref.edit();
    editor.putBoolean(KEY_CALENDAR_SYNC_FLAG, status);
    editor.apply();
  }

  public boolean isCalendarSync() {
    return pref.getBoolean(KEY_CALENDAR_SYNC_FLAG, false);
  }

}