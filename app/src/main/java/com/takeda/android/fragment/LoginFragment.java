package com.takeda.android.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.BaseClasses.BaseFragment;
import com.skk.lib.Interfaces.textWatcherCustom;
import com.skk.lib.utils.AppDelegate;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.activities.HomeActivity;
import com.takeda.android.async.Params;
import com.takeda.android.async.UserTask;
import com.takeda.android.async.resultInterface;
import com.takeda.android.model.LoginModel;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginFragment extends BaseFragment implements View.OnClickListener, textWatcherCustom {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

  boolean isEmailError = true, isPwdError = true;
  // TODO: Rename and change types of parameters
  private EditText mMobileNoView, mPasswordView;
  private TextView email_error, pwd_error;
  private Button tv_need_help;
  String emailText, passwordText;
  View mView;
  Context mContext;
  SessionManager session;
  private int _bCount = 0;
  private long _lastClickTime = 0L;
  AlertDialog alert1;
  private ApiInterface mAPIService;
  private ProgressDialog dialog;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getActivity();
    session = new SessionManager(mContext);
//        mAPIService = ApiUtils.getAPIService();

    dialog = new ProgressDialog(mContext);
    dialog.setMessage("Please Wait....");
    dialog.setCancelable(false);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_login, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    init(view);//Initialising the view

    setupUI(view);
  }

  public void setupUI(View view) {

    // Set up touch listener for non-text box views to hide keyboard.
    if (!(view instanceof EditText)) {
      view.setOnTouchListener(new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
          hideSoftKeyboard(getActivity());
          return false;
        }
      });
    }

    //If a layout container, iterate over children and seed recursion.
    if (view instanceof ViewGroup) {
      for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
        View innerView = ((ViewGroup) view).getChildAt(i);
        setupUI(innerView);
      }
    }
  }

  public static void hideSoftKeyboard(Activity activity) {
    InputMethodManager inputMethodManager =
        (InputMethodManager) activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE);
    View focusedView = activity.getCurrentFocus();
    if (focusedView != null) {
      inputMethodManager.hideSoftInputFromWindow(
          activity.getCurrentFocus().getWindowToken(), 0);
    }
  }

  private void init(View view) {
    mView = view;
    mMobileNoView = view.findViewById(R.id.mobile_number_et);
    mPasswordView = view.findViewById(R.id.password);
    email_error = view.findViewById(R.id.email_error_tv);
    pwd_error = view.findViewById(R.id.pwd_error_tv);

    if (session.getUserDetails().get(SessionManager.KEY_USER_ID) != null) {
      try {
//                Gson gson = new Gson();
//                String json = gson.toJson(new JSONObject(session.getUserDetails().get(SessionManager.KEY_USER_JSON)));
        System.out.println(
            "username======>" + session.getUserDetails().get(SessionManager.KEY_USER_JSON));
        System.out.println("username======>" + new JSONObject(
            session.getUserDetails().get(SessionManager.KEY_USER_JSON)));
        mMobileNoView.setText(
            new JSONObject(session.getUserDetails().get(SessionManager.KEY_USER_JSON))
                .getString(Params.json_doc_username));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    view.findViewById(R.id.tv_forgotPassword).setOnClickListener(this);
    view.findViewById(R.id.email_sign_in_button).setOnClickListener(this);
    view.findViewById(R.id.logo_layout).setOnClickListener(this);
    view.findViewById(R.id.tv_need_help).setOnClickListener(this);

  }

  private void getTextFromET() {
    AppDelegate.setTextWatcher(true, mView.findViewById(R.id.mobile_number_et), this);
    AppDelegate.setTextWatcher(true, mView.findViewById(R.id.password), this);

    emailText = mMobileNoView.getText().toString();
    passwordText = mPasswordView.getText().toString();

    if (!isEmailError && !isPwdError) {
      try {
        loginCall();
      } catch (Exception e) {
        Log.d("Exception", e.toString());
        e.printStackTrace();
      }
    }
  }

  void loginCall() {

    System.out.println("=======in loginCall=====");
    if (dialog != null) {
      dialog.show();
    }

    ApiInterface api = RestAdapterService.createService(ApiInterface.class);

    try {
      api.UserLogin(emailText, passwordText, new Callback<LoginModel>() {
        @Override
        public void success(final LoginModel loginModel, final Response response) {
          System.out.println("=======success in loginCall=====");
          System.out.println("status=====>" + loginModel.response.status);

          dialog.dismiss();

          try {

            if (loginModel.response.status.equalsIgnoreCase("success")) {

              System.out.println("loginModel.response.dataUser.term_condition========>"
                  + loginModel.response.dataUser.term_condition);

              if (loginModel.response.dataUser.term_condition.equalsIgnoreCase("No")) {
                openTandCDialog(new String[]{loginModel.response.dataUser.doctor_id,
                        loginModel.response.dataUser.access_token}
                    , new OtpResponse() {
                      @Override
                      public void onResult(Object result) {
                        try {
                          loginRedirect(loginModel);
                        } catch (Exception e) {
                          e.printStackTrace();
                        }
                      }
                    }, mView, true);
              } else {
                loginRedirect(loginModel);
              }

              session.setRememberNotification(SessionManager.IS_REM_ME,
                  ((CheckBox) mView.findViewById(R.id.remember_me_chck_box)).isChecked());

            } else {
              msgAlertDialog("Error", loginModel.response.statusMessage);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

        @Override
        public void failure(RetrofitError error) {
          dialog.dismiss();

          System.out.println("failure in loginCall");

          Log.e("failure", String.valueOf(error.getResponse().getStatus()));
          Log.e("failure", String.valueOf(error.getCause()));

          Toast.makeText(mContext, "Something Wrong", Toast.LENGTH_SHORT).show();
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  void loginRedirect(final LoginModel response) {

    System.out.println("firstLogin==========>" + response.response.dataUser.first_login);
    if (response.response.dataUser.first_login.equalsIgnoreCase("Yes")) {
      AdditionalInfoFragment addInfoFrag = new AdditionalInfoFragment();
      Bundle bundle = new Bundle();
      bundle.putString(SessionManager.KEY_USER_JSON, response.response.dataUser.toJSON());
      addInfoFrag.setArguments(bundle);
      frgmTxn(addInfoFrag, true, null);
    } else {
      session.showLogs("CallCart",
          "Value : " + String.valueOf(getActivity().getIntent().getStringExtra("callFrom")));
      session.createLoginSession(response.response.dataUser.doctor_id,
          response.response.dataUser.mobile_number,
          response.response.dataUser.access_token,
          response.response.dataUser.toJSON());
      session.setKeyDocTitle(response.response.dataUser.title);
      ((BaseActivity) getActivity()).init(new Runnable() {
        @Override
        public void run() {
          try {
            startActivity(new Intent(getActivity(), HomeActivity.class));
            getActivity().finish();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

    }
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onClick(View v) {

    if (checkInternet()) {
      switch (v.getId()) {
        case R.id.registerTv:
          break;

        case R.id.email_sign_in_button:
          getTextFromET();
          break;

        case R.id.tv_forgotPassword:
//                    frgmTxn(new ForgotPasswordFragment(),true,null);

          LayoutInflater layoutInflater1 = LayoutInflater.from(mContext);
          final View promptView1 = layoutInflater1.inflate(R.layout.enquiry_dialog, null);
          AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(mContext,
              R.style.CustomDialog);
          alertDialogBuilder1.setView(promptView1);

          final Button posBtn1 = promptView1.findViewById(R.id.posBtn);
          final Button negBtn1 = promptView1.findViewById(R.id.negBtn);
          final ImageView dialogIcon1 = promptView1.findViewById(R.id.dialogIcon);
          final TextView contactDetail1 = promptView1.findViewById(R.id.contactDetail);
          final TextView message1 = promptView1.findViewById(R.id.contactMsg);
          final TextView title1 = promptView1.findViewById(R.id.dialog_title);

          title1.setText("Forgot Password");
          title1.setGravity(Gravity.CENTER);

          message1.setText("Confirm to reset Password?");
          message1.setGravity(Gravity.CENTER);

          promptView1.findViewById(R.id.email_details_layout).setVisibility(View.GONE);

//                    dialogIcon.setImageResource(R.drawable.icn_mobile);
          posBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              if (alert1 != null) {
                alert1.dismiss();
              }

              AppDelegate.setTextWatcher(true, mView.findViewById(R.id.mobile_number_et),
                  new textWatcherCustom() {
                    @Override
                    public void addingText(int editTextViewId, String text) {
                      System.out.println(
                          "mMobileNoView.getText().toString()=======>" + mMobileNoView.getText()
                              .toString());
                      String emailText = mMobileNoView.getText().toString();
                      //                                        username_error.setVisibility(View.VISIBLE);
//                                        username_error.setVisibility(View.GONE);
                      isEmailError = emailText.length() <= 0;
                    }
                  });
              emailText = mMobileNoView.getText().toString();

              System.out.println("isEmailError=======>" + isEmailError);

              if (!isEmailError) {
                try {
                  JSONObject jsonObject = new JSONObject();
                  jsonObject.put("user_name", emailText);

                  new UserTask().getJsonRequest(new resultInterface() {
                    @Override
                    public void Success(JSONObject response, String requestCall) {
                      try {
//                                                frgmTxn(new LoginFragment(),true,null);
                        msgAlertDialog("Password Reset",
                            "Your password has been changed and an email has been sent to your email.");

                      } catch (Exception e) {
                        e.printStackTrace();
                      }
                    }
                  }, jsonObject, true, (Activity) mContext, mView, Params.request_code_forgotPwd);
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }

            }
          });
          posBtn1.setText("Yes");
          negBtn1.setText("No");

          alert1 = alertDialogBuilder1.create();
          alert1.setCancelable(false);
          alert1.show();

          negBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              alert1.dismiss();
            }
          });

          break;

        case R.id.logo_layout:
          if (_lastClickTime < (System.currentTimeMillis() - 1000)) {
            _bCount = 0;
          }
          _bCount++;
          _lastClickTime = System.currentTimeMillis();

          if (_bCount == 15) {
            openDialog();
          }
          break;

        case R.id.tv_need_help:
          System.out.println("Click on need help ");

          LayoutInflater layoutInflater = LayoutInflater.from(mContext);
          final View promptView = layoutInflater.inflate(R.layout.enquiry_dialog, null);
          AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext,
              R.style.CustomDialog);
          alertDialogBuilder.setView(promptView);

          final Button posBtn = promptView.findViewById(R.id.posBtn);
          final Button negBtn = promptView.findViewById(R.id.negBtn);
          final ImageView dialogIcon = promptView.findViewById(R.id.dialogIcon);
          final TextView contactDetail = promptView.findViewById(R.id.contactDetail);
          final TextView message = promptView.findViewById(R.id.contactMsg);
          final TextView title = promptView.findViewById(R.id.dialog_title);

//                    positiveBtnName = "CALL NOW";
//                    title.setText("Call Us Now");
//            message.setText("Call Us Now");
          message.setVisibility(View.GONE);
          contactDetail.setText(getString(R.string.default_phone_number));
//                    dialogIcon.setImageResource(R.drawable.icn_mobile);
          posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (alert1 != null) {
                alert1.dismiss();
              }
              Intent intent = new Intent(Intent.ACTION_DIAL);
              intent.setData(Uri.parse("tel:" + getString(R.string.default_phone_number)));
              startActivity(intent);
            }
          });

          posBtn.setText("Call");
          negBtn.setText("Cancel");

          alert1 = alertDialogBuilder.create();
          alert1.setCancelable(false);
          alert1.show();

          negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              alert1.dismiss();
            }
          });


                    /*final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//                    alertDialogBuilder.setTitle("");
                    alertDialogBuilder.setMessage(getString(R.string.default_phone_number));
//                    alertDialogBuilder.setIcon(AppDelegate.getDrawable(BaseActivity.this,R.drawable.terms_and_conditions));

                    alertDialogBuilder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if(alert1!= null){
                                alert1.dismiss();
                            }
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+getString(R.string.default_phone_number)));
                            startActivity(intent);
                        }
                    });

                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(alert1!= null){
                                alert1.dismiss();
                            }
                        }
                    });

                    alert1 = alertDialogBuilder.create();
                    alert1.setCancelable(true);
                    alert1.show();*/

          break;
      }
    }
  }

  @Override
  public void addingText(int editTextViewId, String text) {

    switch (editTextViewId) {

      case R.id.mobile_number_et:
        String emailText = mMobileNoView.getText().toString();
        if (emailText.length() > 0) {
          email_error.setVisibility(View.GONE);
          isEmailError = false;
        } else {
          email_error.setVisibility(View.VISIBLE);
          isEmailError = true;
        }
        break;

      case R.id.password:
        String password = mPasswordView.getText().toString();
        if (!AppDelegate.isValidPassword(password)) {
          isPwdError = true;
          pwd_error.setVisibility(View.VISIBLE);
        } else {
          isPwdError = false;
          pwd_error.setVisibility(View.GONE);
        }
        break;
    }
  }

  void openDialog() {

    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    final View promptView = layoutInflater.inflate(R.layout.man_dialog, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext,
        R.style.CustomDialog);
    alertDialogBuilder.setView(promptView);

    final RadioGroup rbGrp = promptView.findViewById(R.id.url_rbGrp);
    final SessionManager session = new SessionManager(mContext);

    promptView.findViewById(R.id.crossBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (alert1 != null) {
          alert1.dismiss();
        }
      }
    });

    rbGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        String btnText = ((RadioButton) promptView.findViewById(group.getCheckedRadioButtonId()))
            .getText().toString().toLowerCase();

        if (btnText.contains("altus")) {
          session.setBaseURL(SessionManager.BASE_URL_ALTUS);
        } else if (btnText.contains("amazon")) {
          session.setBaseURL(SessionManager.BASE_URL_AWS);
        } else if (btnText.contains("mentem")) {
          session.setBaseURL(SessionManager.BASE_URL);
        }

        if (alert1 != null) {
          alert1.dismiss();
        }
      }
    });

    alert1 = alertDialogBuilder.create();
    alert1.setCancelable(false);
    alert1.show();
  }
}
