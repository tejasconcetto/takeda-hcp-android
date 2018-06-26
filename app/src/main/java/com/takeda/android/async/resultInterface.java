package com.takeda.android.async;

import org.json.JSONObject;

/**
 * Created by Akshay on 14-Jun-16.
 */
public interface resultInterface {

  void Success(JSONObject response, String requestCall);
}
