package com.takeda.android.async;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.utils.AppDelegate;
import com.skk.lib.utils.SessionManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserTask {

//    public static String BASE_URL = "http://altustendo.com/takeda_backend/api/";

  public static String requestCall;
  SessionManager session;
  View mViewFinal;

  public void getJsonRequest(final resultInterface listener, final JSONObject jsonParams,
      final boolean showDialog,
      final Activity mContext, final View view, final String requestCall) {

    UserTask.requestCall = requestCall;
    session = new SessionManager(mContext);
    mViewFinal = view;

    if (mContext instanceof BaseActivity) {
      mViewFinal = ((BaseActivity) mContext).getCoordinatorLayout();
    }

    if (!AppDelegate.isConnectingToInternet(mContext) && mViewFinal != null) {
      Snackbar snackbar = Snackbar
          .make(mViewFinal, "No Internet Connection!", Snackbar.LENGTH_INDEFINITE)
          .setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = mContext.getIntent();
              mContext.finish();
              mContext.startActivity(intent);
            }
          });
      View mView = snackbar.getView();
      TextView tv = mView.findViewById(android.support.design.R.id.snackbar_text);
      tv.setTextColor(Color.RED);
      TextView action = mView.findViewById(android.support.design.R.id.snackbar_action);
      action.setTextColor(Color.WHITE);
      snackbar.show();
    } else {
      try {
        if (showDialog) {
          AppDelegate.showLoadingDialog(mContext);
        }

        if (session.isLoggedIn()) {
          jsonParams.put(SessionManager.KEY_ACCESS_TOKEN,
              session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));
        }

        session.showLogs("FinalURL", session.getBaseURL() + requestCall);
        session.showLogs("ParamsPassing", jsonParams.toString());
      } catch (Exception e) {
        e.printStackTrace();
      }

      final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
          session.getBaseURL() + requestCall, jsonParams, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject responseServer) {

          if (showDialog) {
            AppDelegate.hideLoadingDialog(mContext);
          }

          if (responseServer != null) {
            try {
//                            Log.d("ParamsResponse","JSONData : "+response.toString());
              session.showLogs("APIResponse", responseServer.toString());
              JSONObject response = responseServer.getJSONObject("response");
              String responseResult = response.getString("status");

              if (responseResult.equalsIgnoreCase(Params.response_success)) {
                session.showLogs("Response", response.toString());
                if (requestCall.equalsIgnoreCase(Params.request_code_forgotPwd)) {
                  AppDelegate.showToast(mContext, response.getString(Params.response_message));
                }
                listener.Success(response, requestCall);
              } else if (responseResult.equalsIgnoreCase(Params.response_error)) {
                if (!requestCall.equalsIgnoreCase(Params.request_get_events) &&
                    !requestCall.equalsIgnoreCase(Params.request_code_update_push_preference) &&
                    !requestCall.equalsIgnoreCase(Params.request_code_update_token)) {
                  AppDelegate
                      .showErrorSBar(response.getString(Params.response_message), mViewFinal);
                }

                if (requestCall.equalsIgnoreCase(Params.request_code_make_order) && response
                    .has("error_data")) {

                }

                if (response.getString(Params.response_message).contains("Invalid Token")) {
                  session.logoutUser(mContext);
                  AppDelegate.showToast(mContext,
                      "You are already logged in from other device. Invalid Session");
                }
              }
            } catch (JSONException jsonException) {
              jsonException.printStackTrace();
            }
          } else {
            if (!requestCall.equalsIgnoreCase(Params.request_get_events) &&
                !requestCall.equalsIgnoreCase(Params.request_code_update_push_preference) &&
                !requestCall.equalsIgnoreCase(Params.request_code_update_token)) {
              AppDelegate.showErrorSBar("Please Check your Internet Connection", mViewFinal);
            }
          }
        }
      }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          Log.d("Volley Error", error.toString());
          if (!requestCall.equalsIgnoreCase(Params.request_get_events) &&
              !requestCall.equalsIgnoreCase(Params.request_code_update_push_preference) &&
              !requestCall.equalsIgnoreCase(Params.request_code_update_token)) {
            AppDelegate
                .showErrorSBar("Slow Internet Connection. Please check your Internet", mViewFinal);
          }
//                    AppDelegate.showToast(mContext,"" + "Slow Internet Connection. Please check your Internet");

          if (showDialog) {
            AppDelegate.hideLoadingDialog(mContext);
          }
        }
      });

//            StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL+requestCall, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String responseString) {
//                    if(showDialog)
//                        AppDelegate.hideLoadingDialog(mContext);
//
//                    if(responseString!=null)
//                    {
//                        try{
////                            Log.d("ParamsResponse","JSONData : "+response.toString());
//                            session.showLogs("APIResponse",responseString.toString());
//
//                            JSONObject response = new JSONObject(responseString).getJSONObject("response");
//                            String responseResult = response.getString("status");
//                            if(responseResult.equalsIgnoreCase(Params.response_success)){
//                                session.showLogs("Response",response.toString());
//                                listener.Success(response.getJSONObject("data"),requestCall);
//                            }
//
//                            else if(responseResult.equalsIgnoreCase(Params.response_error)){
//                                if(!requestCall.equalsIgnoreCase(Params.request_get_events)){
//                                    AppDelegate.showErrorSBar(response.getString(Params.response_message),view);
//                                }
//
//                                if(requestCall.equalsIgnoreCase(Params.request_code_make_order) && response.has("error_data")){
//
//                                }
//                            }
//                        } catch(Exception jsonException) {
//                            jsonException.printStackTrace();
//                        }
//                    }
//
//                    else {
//                        AppDelegate.showErrorSBar("Please Check your Internet Connection",view);
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.d("Volley Error", error.toString());
//                    AppDelegate.showToast(mContext,"" + "Slow Internet Connection. Please check your Internet");
//
//                    if(showDialog)
//                        AppDelegate.hideLoadingDialog(mContext);
//                }
//            }) {
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                     Map<String, String> map = new HashMap<String, String>();
//                     map.put("Content-Type", "multipart/form-data");
//                     return map;
//                }
//
//                /**
//                 * I have copied the style of this method from its original method from com.Android.Volley.Request
//                 * @return
//                 * @throws AuthFailureError
//                 */
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//
//                    Map<String, String> params = new HashMap<>();
//                    try {
//                        params = toMap(jsonParams);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    //yeah, I copied this from the base method.
//                    if (params != null && params.size() > 0) {
//                        return params.toString().getBytes();
////                        session.showLogs("MapOutput",String.valueOf(encodeParameters(params,getParamsEncoding())));
////                        return encodeParameters(params, getParamsEncoding());
//                    }
//                    return null;
//                }
//            };

      jsonObjectRequest.setRetryPolicy(
          new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(30), 0,
              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
      Volley.newRequestQueue(mContext).add(jsonObjectRequest).setShouldCache(false);
    }
  }

  public static Map<String, String> toMap(JSONObject jsonObject) {
    Map<String, String> map = new HashMap<>();

    try {
      Iterator<String> keysItr = jsonObject.keys();
      while (keysItr.hasNext()) {
        String key = keysItr.next();
        Object value = jsonObject.get(key);

        if (value instanceof JSONArray) {
          value = toList((JSONArray) value);
        } else if (value instanceof JSONObject) {
          value = toMap((JSONObject) value);
        }
        map.put(key, String.valueOf(value));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return map;
  }

  public static List<String> toList(JSONArray array) {
    List<String> list = new ArrayList<String>();

    try {
      for (int i = 0; i < array.length(); i++) {
        Object value = array.get(i);
        if (value instanceof JSONArray) {
          value = toList((JSONArray) value);
        } else if (value instanceof JSONObject) {
          value = toMap((JSONObject) value);
        }
        list.add(String.valueOf(value));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
    StringBuilder encodedParams = new StringBuilder();
    try {
      for (Map.Entry<String, String> entry : params.entrySet()) {
        encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
        encodedParams.append('=');
        encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
        encodedParams.append('&');
      }
      return encodedParams.toString().getBytes(paramsEncoding);
    } catch (UnsupportedEncodingException uee) {
      throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
    }
  }
}
