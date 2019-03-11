package com.skk.lib.BaseClasses;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.skk.lib.SmsListenerModule.OnReciveSMS;
import com.skk.lib.utils.AppDelegate;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.TakedaApplication;
import com.takeda.android.async.Params;
import com.takeda.android.async.UserTask;
import com.takeda.android.async.resultInterface;
import com.takeda.android.model.BannerModel;
import com.takeda.android.model.ProductModel;
import com.takeda.android.model.TermsAndConditionsModel;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Akshay on 07-Dec-16.
 */

public class BaseActivity extends AppCompatActivity {

  public static final long DISCONNECT_TIMEOUT = 1800000; // 30 min = 30 * 60 * 1000 ms


  SessionManager session;
  public boolean wantToClose = false;
  String otpTextFinal;
  public static OnReciveSMS onReciveSMS;
  View mView;
  AlertDialog alertDialog;
  AlertDialog alertDialog1;
  Runnable runnablePerm;
  private ProgressDialog dialog;

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    session = new SessionManager(this);

    dialog = new ProgressDialog(this);
    dialog.setMessage("Please Wait....");
    dialog.setCancelable(false);

    checkInternet();
  }

  public HashMap<String, String> getSessionData() {
    return session.getUserDetails();
  }

  public void showLogs(String TAG, String message) {
    session.showLogs(TAG, message);
  }

  public boolean checkInternet() {
    View mView = findViewById(R.id.coordinator_layout);
    if (!AppDelegate.isConnectingToInternet(this) && mView != null) {
      showSBarAction("No Internet Connection!", "RETRY", new Runnable() {
        @Override
        public void run() {
          Intent intent = getIntent();
          finish();
          startActivity(intent);
        }
      });
      return false;
    }
    return true;
  }

  public void showToast(String message) {
    try {
      if (getMainApplication() != null) {
        Toast.makeText(getMainApplication(), message, Toast.LENGTH_LONG).show();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public TakedaApplication getMainApplication() {
    return (TakedaApplication) getApplicationContext();
  }

  public Integer layoutsize() {
    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;
    //int height = size.y;
    return width;
  }

  public void frgmTxn(Fragment frgm, boolean addBackStack, String addBackStackTAG) {
    if (addBackStack) {
      String tag = addBackStackTAG;
      getFragmentManager().beginTransaction().replace(R.id.frame_container, frgm)
          .addToBackStack(tag).commit();
    } else {
      getFragmentManager().beginTransaction().replace(R.id.frame_container, frgm).commit();
    }
  }

  public View getCoordinatorLayout() {
    return findViewById(R.id.coordinator_layout);
  }

  public void showSBar(String Message) {
    try {
      final Snackbar snackbar = Snackbar
          .make(getCoordinatorLayout(), Message, Snackbar.LENGTH_INDEFINITE);
      snackbar.setAction("CLOSE", new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          snackbar.dismiss();
        }
      });
      View mView = snackbar.getView();
      TextView tv = mView.findViewById(android.support.design.R.id.snackbar_text);
      tv.setTextColor(Color.WHITE);
      snackbar.show();
    } catch (Exception e) {
      Log.d("Exception", "context is null at showing toast.");
      e.printStackTrace();
    }
  }

  public void showSBarAction(String Message, String btn_name, final Runnable runnable) {
    try {
      final Snackbar snackbar = Snackbar
          .make(getCoordinatorLayout(), Message, Snackbar.LENGTH_INDEFINITE);
      snackbar.setAction(btn_name, new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          snackbar.dismiss();
          if (runnable != null) {
            runnable.run();
          }
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

  public void init(final Runnable runnable) {

    System.out.println("=======Banner api call=====");

    if (dialog != null) {
      dialog.show();
    }

    try {
      JSONObject jsonObject = new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON));

      System.out.println("jsonObject======>" + new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON)));

      ApiInterface api = RestAdapterService.createService(ApiInterface.class);

      api.Banners(jsonObject.getString("doctor_id"),
          jsonObject.getJSONObject("locality").getString("location_id"),
          jsonObject.getJSONObject("specialty").getString("specialty_id"),
          jsonObject.getJSONObject("sector").getString("sector_id"),
          jsonObject.getJSONObject("class").getString("class_id"), "", "",
          jsonObject.getString("access_token"), new Callback<BannerModel>() {
            @Override
            public void success(BannerModel bannerModel, Response response) {
              System.out.println("=======success in Banners api call=====");

              if (dialog.isShowing()) {
                dialog.dismiss();
              }

              try {

                if (bannerModel.response.status.equalsIgnoreCase("success")) {
                  ArrayList<BannerModel.BannerDataModel> resultArr = new ArrayList<BannerModel.BannerDataModel>();
                  resultArr = bannerModel.response.data.bannerData;
                  getMainApplication().setOfferArray(resultArr);
                  fetchProducts(runnable);

                } else {
                  msgAlertDialog("Error", bannerModel.response.statusMessage);
                }

              } catch (Exception e) {
                e.printStackTrace();
              }
            }

            @Override
            public void failure(RetrofitError error) {
              System.out.println("=======failed in Banners api call=====");
              if (dialog.isShowing()) {
                dialog.dismiss();
              }
            }
          });

    } catch (Exception e) {
      e.printStackTrace();
    }

       /* try{
            JSONObject jsonObject = new JSONObject(session.getUserDetails().get(SessionManager.KEY_USER_JSON));

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("doctor_id",jsonObject.getString("doctor_id"));
            jsonObj.put("banner_location",jsonObject.getJSONObject("locality").getString("location_id"));
            jsonObj.put("banner_specialty",jsonObject.getJSONObject("specialty").getString("specialty_id"));
            jsonObj.put("banner_sector",jsonObject.getJSONObject("sector").getString("sector_id"));
            jsonObj.put("banner_class",jsonObject.getJSONObject("class").getString("class_id"));
            jsonObj.put("banner_included","");
            jsonObj.put("banner_excluded","");
      //      jsonObj.put("banner_type","Products");

            new UserTask().getJsonRequest(new resultInterface() {
                @Override
                public void Success(JSONObject response, String requestCall) {

                    try{
                        getMainApplication().setOfferArray(response.getJSONObject(Params.response_success_data).getJSONArray("banners"));
                        fetchProducts(runnable);
                    }

                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            },jsonObj,true,this,getCoordinatorLayout(), Params.request_code_banners);
        }
        catch (Exception e){
            e.printStackTrace();
        }*/
  }

  void fetchProducts(final Runnable runnable) {

    if (dialog != null) {
      dialog.show();
    }

    try {
      JSONObject jsonObject = new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON));
      System.out.println("jsonObject======>" + new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON)));

      ApiInterface api = RestAdapterService.createService(ApiInterface.class);
      api.Products(jsonObject.getString("doctor_id"), jsonObject.getString("access_token"),
          new Callback<ProductModel>() {
            @Override
            public void success(ProductModel productModel, Response response) {
              System.out.println("=======success in Products api call=====");
              if (dialog.isShowing()) {
                dialog.dismiss();
              }
              try {

                if (productModel.response.status.equalsIgnoreCase("success")) {

                  ArrayList<ProductModel.CategoryArrDataModel> resultArr = new ArrayList<ProductModel.CategoryArrDataModel>();
                  resultArr = productModel.response.data.catData;
                  getMainApplication().addCategory(resultArr);
                  session.setKeyPurchaseHistoryFlag(productModel.response.data.purchaseHistoryFlag);
//                            getMainApplication().addCategory(response.getJSONObject(Params.response_success_data).getJSONArray("categories"));
                  if (runnable != null) {
                    runnable.run();
                  }
                } else {
                  msgAlertDialog("Error", productModel.response.statusMessage);
                }

              } catch (Exception e) {
                e.printStackTrace();
              }
            }

            @Override
            public void failure(RetrofitError error) {
              System.out.println("=======failed in Products api call=====");
              if (dialog.isShowing()) {
                dialog.dismiss();
              }
            }
          });

    } catch (Exception e) {
      e.printStackTrace();
    }

        /*JSONObject jsonObject = new JSONObject();
        jsonObject.put("doctor_id",session.getUserDetails().get(SessionManager.KEY_USER_ID));
        jsonObject.put("access_token",session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));

        new UserTask().getJsonRequest(new resultInterface() {
            @Override
            public void Success(JSONObject response, String requestCall) {
                try {
                    getMainApplication().addCategory(response.getJSONObject(Params.response_success_data).getJSONArray("categories"));
                    if(runnable != null){
                        runnable.run();
                    }
               }

                catch (Exception e){
                    e.printStackTrace();
                }
            }
        },jsonObject,true,this,getCoordinatorLayout(), Params.request_code_products);*/
  }

  public void openTandCDialog(final String[] doc_data, final BaseFragment.OtpResponse otpResponse,
      final View fragView, final boolean toAccept) {

    if (dialog != null) {
      dialog.show();
    }

    ApiInterface api = RestAdapterService.createService(ApiInterface.class);

    api.TermsAndConditions(doc_data[0],
        doc_data[1], new Callback<TermsAndConditionsModel>() {
          @Override
          public void success(TermsAndConditionsModel termsAndConditionsModel, Response response) {

            System.out.println("success in TermsAndConditions========");

            if (dialog.isShowing()) {
              dialog.dismiss();
            }

            try {
              if (termsAndConditionsModel.response.status.equalsIgnoreCase("success")) {

                LayoutInflater layoutInflater = LayoutInflater.from(BaseActivity.this);
                final View promptView = layoutInflater
                    .inflate(R.layout.term_condition_dialog, null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    BaseActivity.this, R.style.CustomDialog);
                alertDialogBuilder.setView(promptView);

                final Button posBtn = promptView.findViewById(R.id.posBtn);
                final Button negBtn = promptView.findViewById(R.id.negBtn);
                final Button closeBtn = promptView.findViewById(R.id.closeBtn);
                final ImageView dialogIcon = promptView.findViewById(R.id.dialogIcon);
                final TextView message = promptView.findViewById(R.id.contactMsg);
                final TextView title = promptView.findViewById(R.id.dialog_title);

                title.setText(getResources().getString(R.string.terms_of_service));

                message.setText(AppDelegate
                    .checkString(termsAndConditionsModel.response.dataUser.terms_and_conditions));
                message.setMovementMethod(new ScrollingMovementMethod());
                dialogIcon.setImageResource(R.drawable.terms_and_conditions);

//                                alertDialogBuilder.setTitle(getResources().getString(R.string.terms_of_service));
//                                alertDialogBuilder.setMessage(AppDelegate.checkString(termsAndConditionsModel.response.dataUser.terms_and_conditions));
//                                alertDialogBuilder.setIcon(AppDelegate.getDrawable(BaseActivity.this, R.drawable.terms_and_conditions));

//

                System.out.println("toAccept========>" + toAccept);

                if (toAccept) {

                  closeBtn.setVisibility(View.GONE);
                  posBtn.setVisibility(View.VISIBLE);
                  negBtn.setVisibility(View.VISIBLE);

                  posBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      try {

                        if (dialog != null) {
                          dialog.show();
                        }

                        ApiInterface api = RestAdapterService.createService(ApiInterface.class);

                        api.TermsAndConditionsAccept(doc_data[0], doc_data[1], true,
                            (Calendar.getInstance().getTimeInMillis() / 1000),
                            new Callback<TermsAndConditionsModel>() {
                              @Override
                              public void success(TermsAndConditionsModel termsAndConditionsModel,
                                  Response response) {
                                if (otpResponse != null) {
                                  otpResponse.onResult("");
                                }
                                alertDialog1.dismiss();

                                if (dialog.isShowing()) {
                                  dialog.dismiss();
                                }

                              }

                              @Override
                              public void failure(RetrofitError error) {

                                if (dialog.isShowing()) {
                                  dialog.dismiss();
                                }


                              }
                            });


                      } catch (Exception e) {
                        e.printStackTrace();
                      }

                    }
                  });

                  negBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                                            alertDialog.dismiss();

                      msgAlertDialog("Takeda",
                          "Use of this application requires you to accept this agreements.");
                    }
                  });

                                    /*alertDialogBuilder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            try {

                                                if (dialog != null)
                                                    dialog.show();

                                                ApiInterface api= RestAdapterService.createService(ApiInterface.class);

                                                api.TermsAndConditionsAccept(doc_data[0], doc_data[1], true, (Calendar.getInstance().getTimeInMillis() / 1000),
                                                        new Callback<TermsAndConditionsModel>() {
                                                            @Override
                                                            public void success(TermsAndConditionsModel termsAndConditionsModel, Response response) {
                                                                if (otpResponse != null) {
                                                                    otpResponse.onResult("");
                                                                }
                                                                alertDialog.dismiss();
                                                            }

                                                            @Override
                                                            public void failure(RetrofitError error) {

                                                            }
                                                        });

                                                *//*JSONObject jsonObject = new JSONObject();
                                                jsonObject.put("accepted", true);
                                                jsonObject.put("doctor_id", doc_data[0]);
                                                jsonObject.put(SessionManager.KEY_ACCESS_TOKEN, doc_data[1]);
                                                jsonObject.put("timeStamp", (Calendar.getInstance().getTimeInMillis() / 1000));

                                                new UserTask().getJsonRequest(new resultInterface() {
                                                    @Override
                                                    public void Success(JSONObject response, String requestCall) {
                                                        if (otpResponse != null) {
                                                            otpResponse.onResult("");
                                                        }
                                                        alertDialog.dismiss();
                                                    }
                                                }, jsonObject, true, BaseActivity.this, fragView, Params.request_code_accepttandc);
                                            *//*
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });*/

                } else {
                  closeBtn.setVisibility(View.VISIBLE);
                  posBtn.setVisibility(View.GONE);
                  negBtn.setVisibility(View.GONE);

                  closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      alertDialog1.dismiss();
                    }
                  });
                }

                alertDialog1 = alertDialogBuilder.create();
                alertDialog1.setCancelable(!toAccept);
                alertDialog1.show();
              } else {
                msgAlertDialog("Error", termsAndConditionsModel.response.statusMessage);
              }

            } catch (Exception e) {
              e.printStackTrace();
            }

          }

          @Override
          public void failure(RetrofitError error) {

            System.out.println("failure in TermsAndConditions========");

            if (dialog.isShowing()) {
              dialog.dismiss();
            }

          }
        });

    if (dialog.isShowing()) {
      dialog.dismiss();
    }
        /*
        new UserTask().getJsonRequest(new resultInterface() {
            @Override
            public void Success(JSONObject response, String requestCall) {
                try{
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BaseActivity.this);
                    alertDialogBuilder.setTitle(getResources().getString(R.string.terms_of_service));
                    alertDialogBuilder.setMessage(AppDelegate.checkString(response.getJSONObject(Params.response_success_data).getString("terms_and_conditions")));
                    alertDialogBuilder.setIcon(AppDelegate.getDrawable(BaseActivity.this,R.drawable.terms_and_conditions));

                    alertDialogBuilder.setNegativeButton("Close",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });

                    if(toAccept){


                        alertDialogBuilder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                try{
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("accepted", true);
                                    jsonObject.put("doctor_id", doc_data[0]);
                                    jsonObject.put(SessionManager.KEY_ACCESS_TOKEN, doc_data[1]);
                                    jsonObject.put("timeStamp", (Calendar.getInstance().getTimeInMillis() / 1000));

                                    new UserTask().getJsonRequest(new resultInterface() {
                                        @Override
                                        public void Success(JSONObject response, String requestCall) {
                                            if(otpResponse != null){
                                                otpResponse.onResult("");
                                            }
                                            alertDialog.dismiss();
                                        }
                                    },jsonObject,true,BaseActivity.this,fragView, Params.request_code_accepttandc);
                                }

                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });

                    }

                    alertDialog = alertDialogBuilder.create();
                    alertDialog.setCancelable(!toAccept);
                    alertDialog.show();
                }

                catch (Exception e){
                    e.printStackTrace();
                }
            }
        },new JSONObject().put("doctor_id",doc_data[0]).put(SessionManager.KEY_ACCESS_TOKEN,doc_data[1]),
                true,BaseActivity.this,mView,Params.request_code_fetchtandc);

                */
  }

  public void msgAlertDialog(String title, String message) {

    LayoutInflater layoutInflater = LayoutInflater.from(BaseActivity.this);
    final View promptView = layoutInflater.inflate(R.layout.message_dialog_box, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BaseActivity.this,
        R.style.CustomDialog);
    alertDialogBuilder.setView(promptView);

//        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BaseActivity.this);
    final Button posBtn = promptView.findViewById(R.id.posBtn);
    final Button negBtn = promptView.findViewById(R.id.negBtn);
    final TextView messageView = promptView.findViewById(R.id.contactDetail);
    final TextView titleView = promptView.findViewById(R.id.dialog_title);

    titleView.setText(title);
    messageView.setText(message);

//        alertDialogBuilder.setTitle(title);
//        alertDialogBuilder.setMessage(message);
//        alertDialogBuilder.setIcon(AppDelegate.getDrawable(BaseActivity.this,R.drawable.terms_and_conditions));
//        LayoutInflater inflater = BaseActivity.this.getLayoutInflater();

//        alertDialogBuilder.setView(inflater.inflate(R.drawable.dialog_bg, null));

//        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                alertDialog.dismiss();
//            }
//        });

    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (alertDialog != null) {
          alertDialog.dismiss();
        }

      }
    });

    negBtn.setVisibility(View.GONE);

    alertDialog = alertDialogBuilder.create();
    alertDialog.setCancelable(true);
    alertDialog.show();
  }

  public void openURL(String url) {
    if (URLUtil.isValidUrl(url)) {
      try {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
      } catch (Exception e) {
        showSBar("No app found to open link.");
      }
    } else {
      showLogs("URLError", "Invalid URL");
    }
  }

  public void checkPermission(Runnable runnable) {
    runnablePerm = runnable;
    AppDelegate.requestPermission(this, getCoordinatorLayout(),
        AppDelegate.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE, runnable);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    Log.d("RequestCode", "req Code : " + requestCode);

    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      AppDelegate.permissionResult(requestCode, this, null, runnablePerm);
    } else {
      showSBarAction(getResources().getString(R.string.allow_permissions_msg), "Settings",
          new Runnable() {
            @Override
            public void run() {
              Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(intent);
            }
          });
    }
  }

  public void updateToken(final boolean val, final Runnable runnable) {
    try {
      if (val) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("doctor_id", session.getUserDetails().get(SessionManager.KEY_USER_ID));
        jsonObject.put("device_token", session.getUserDetails().get(SessionManager.GCM_ID));

        new UserTask().getJsonRequest(new resultInterface() {
          @Override
          public void Success(JSONObject response, String requestCall) {
            session.setFirstTimeFalse();
            session.setRememberNotification(SessionManager.KEY_NOTIFICATION_STATUS, val);
            updateValue(runnable);
          }
        }, jsonObject, false, this, null, Params.request_code_update_token);
      } else {
        session.setFirstTimeFalse();
        session.setRememberNotification(SessionManager.KEY_NOTIFICATION_STATUS, val);
        updateValue(runnable);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void updateValue(final Runnable runnable) {
    try {
      JSONObject jsonParams = new JSONObject();
      jsonParams.put("doctor_id", session.getUserDetails().get(SessionManager.KEY_USER_ID));
      jsonParams.put("push_preference", String.valueOf(session.isToNotify()));
      jsonParams.put("device_token", session.getUserDetails().get(SessionManager.GCM_ID));

      new UserTask().getJsonRequest(new resultInterface() {
        @Override
        public void Success(JSONObject response, String requestCall) {
          if (runnable != null) {
            runnable.run();
          }
        }
      }, jsonParams, false, this, null, Params.request_code_update_push_preference);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

//    public TakedaApplication getApp() {
//        return (TakedaApplication) this.getApplication();
//    }
//
//    @Override
//    public void onUserInteraction() {
//        super.onUserInteraction();
//
//        getApp().touch();
//        Log.d("TAG", "User interaction to " + this.toString());
//    }


  private Handler disconnectHandler = new Handler() {
    public void handleMessage(Message msg) {
    }
  };

  private Runnable disconnectCallback = new Runnable() {
    @Override
    public void run() {
      // Perform any required operation on disconnect

      System.out.println("=======disconnectCallback=======");
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            getApplicationContext().finish();

      System.exit(1);
    }
  };

  public void resetDisconnectTimer() {
    disconnectHandler.removeCallbacks(disconnectCallback);
    disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
  }

  public void stopDisconnectTimer() {
    disconnectHandler.removeCallbacks(disconnectCallback);
  }

  @Override
  public void onUserInteraction() {
    resetDisconnectTimer();
  }

  @Override
  public void onResume() {
    super.onResume();
    resetDisconnectTimer();
  }

  @Override
  public void onStop() {
    super.onStop();
    stopDisconnectTimer();
  }
}


