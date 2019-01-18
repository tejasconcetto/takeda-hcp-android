package com.takeda.android.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.BaseClasses.BaseFragment;
import com.skk.lib.Interfaces.textWatcherCustom;
import com.skk.lib.utils.AppDelegate;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.activities.HomeActivity;
import com.takeda.android.async.Params;
import com.takeda.android.model.LoginModel;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by Ratan on 7/29/2015.
 */
public class AdditionalInfoFragment extends BaseFragment implements textWatcherCustom {

  View mView;
  Activity mContext;
  SessionManager session;
  EditText mobileNumber, email, userName, mPasswordView, mConfirmPwdView;
  TextView mobileNumberTV, emailTV, userNameTV, mPasswordViewTV, mConfirmPwdViewTV;
  boolean isEmailError = false, isMNoError = false, isUNameError = false, isPwdError = true, isCPwdError = true;
  String password = "";
  JSONObject jsonObject = new JSONObject();

  private ProgressDialog dialog;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    mContext = getActivity();
    session = new SessionManager(mContext);
    dialog = new ProgressDialog(mContext);
    mView = inflater.inflate(R.layout.fragment_additional_info, null);
    init(mView);

    mView.findViewById(R.id.proceed_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getTextFromET();
      }
    });
    return mView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

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


  void getTextFromET() {

    if (!checkUpdateCall()) {
      AppDelegate.setTextWatcher(true, mPasswordView, this);
      AppDelegate.setTextWatcher(true, mConfirmPwdView, this);
      AppDelegate.setTextWatcher(true, userName, this);
    }

    AppDelegate.setTextWatcher(true, email, this);

    try {
      if (!isUNameError && !isEmailError && !isPwdError && !isCPwdError) {
        if (dialog != null) {
          dialog.show();
        }
        ApiInterface api = RestAdapterService.createService(ApiInterface.class);
        api.UserProfileUpdateFirst(jsonObject.getString("doctor_id"), userName.getText().toString(),
            email.getText().toString(),
            mobileNumber.getText().toString(), mPasswordView.getText().toString(),
            jsonObject.getString("access_token"),
            new Callback<LoginModel>() {
              @Override
              public void success(LoginModel loginModel, Response response) {
                if (dialog.isShowing()) {
                  dialog.dismiss();
                }

                try {
                  if (loginModel.response.status.equalsIgnoreCase("success")) {
                    if (getArguments() != null && getArguments()
                        .containsKey(SessionManager.KEY_USER_JSON)) {

                      showLogs("UpdateCallCheck", "call to update profile : " + checkUpdateCall());

//                                JSONObject response = responseJSON.getJSONObject(Params.response_success_data);
                      session.createLoginSession(loginModel.response.dataUser.doctor_id,
                          loginModel.response.dataUser.mobile_number,
                          loginModel.response.dataUser.access_token,
                          loginModel.response.dataUser.toJSON());

                      if (!checkUpdateCall()) {
                        session.showLogs("CallCart", "Value : " + String
                            .valueOf(getActivity().getIntent().getStringExtra("callFrom")));
//                                    startActivity(new Intent(getActivity(), HomeActivity.class));
//                                    getActivity().finish();

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
                    } else {
                      showSBar(loginModel.response.statusMessage);
                      getActivity().onBackPressed();
                    }
                  } else {
                    msgAlertDialog("Error", loginModel.response.statusMessage);
                  }


                } catch (Exception e) {
                  e.printStackTrace();
                }
              }

              @Override
              public void failure(RetrofitError error) {

                if (dialog.isShowing()) {
                  dialog.dismiss();
                }

              }
            });

       /* JSONObject params = new JSONObject();

        try{
            if(!isUNameError && !isEmailError && !isPwdError && !isCPwdError){
                params.put("doctor_id",jsonObject.getString("doctor_id"));
                params.put("user_name",userName.getText().toString());
                params.put("email",email.getText().toString());
                params.put("mobile_number",mobileNumber.getText().toString());
                params.put("password",mPasswordView.getText().toString());
                params.put("access_token",jsonObject.getString("access_token"));

                new UserTask().getJsonRequest(new resultInterface() {
                    @Override
                    public void Success(JSONObject responseJSON, String requestCall) {
                        try{
                            if(getArguments() != null && getArguments().containsKey(SessionManager.KEY_USER_JSON)){

                                showLogs("UpdateCallCheck","call to update profile : "+checkUpdateCall());

                                JSONObject response = responseJSON.getJSONObject(Params.response_success_data);
                                session.createLoginSession(response.getString("doctor_id").toString(),
                                        response.getString("mobile_number").toString(),
                                        response.getString("access_token").toString(),
                                        response.toString());

                                if(!checkUpdateCall()){
                                    session.showLogs("CallCart","Value : "+String.valueOf(getActivity().getIntent().getStringExtra("callFrom")));
//                                    startActivity(new Intent(getActivity(), HomeActivity.class));
//                                    getActivity().finish();

                                    ((BaseActivity)getActivity()).init(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                startActivity(new Intent(getActivity(), HomeActivity.class));
                                                getActivity().finish();
                                            }
                                            catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                                else{
                                    showSBar(responseJSON.getString(Params.response_message));
                                    getActivity().onBackPressed();
                                }
                            }
                        }

                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },params,true,getActivity(),mView,Params.request_code_profile_update);*/
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  boolean checkUpdateCall() {
    boolean returnVal = false;
    if (getArguments().containsKey("callFrom") && getArguments().getString("callFrom")
        .equalsIgnoreCase("updateProfile")) {
      returnVal = true;
    }
    return returnVal;
  }

  void init(View mView) {
    try {
      userName = mView.findViewById(R.id.username_et);
      mobileNumber = mView.findViewById(R.id.mobile_number_et);
      email = mView.findViewById(R.id.user_email_et);
      mPasswordView = mView.findViewById(R.id.password_et);
      mConfirmPwdView = mView.findViewById(R.id.cpassword_et);
      userNameTV = mView.findViewById(R.id.username_et_error_tv);
      mobileNumberTV = mView.findViewById(R.id.mobile_number_et_error_tv);
      emailTV = mView.findViewById(R.id.email_et_error_tv);
      mPasswordViewTV = mView.findViewById(R.id.password_et_error_tv);
      mConfirmPwdViewTV = mView.findViewById(R.id.cpassword_et_error_tv);

      if (getArguments() != null) {
        jsonObject = new JSONObject(getArguments().getString(SessionManager.KEY_USER_JSON));
        ((TextView) mView.findViewById(R.id.user_name))
            .setText("Dr. " + jsonObject.getString("doctor_name"));
        ((TextView) mView.findViewById(R.id.user_fax_no))
            .setText(jsonObject.getString("fax_number"));
        ((TextView) mView.findViewById(R.id.user_account_no))
            .setText(jsonObject.getString("account_number"));
        ((TextView) mView.findViewById(R.id.user_speciality))
            .setText(jsonObject.getJSONObject("specialty").getString("specialty_title"));
        if (jsonObject.has("address")) {

          ((TextView) mView.findViewById(R.id.user_address))
              .setText(jsonObject.getString("address"));

        } else {
          ((TextView) mView.findViewById(R.id.user_address)).setText("");
        }
        ((TextView) mView.findViewById(R.id.user_sector))
            .setText(jsonObject.getJSONObject("sector").getString("sector_name"));

        //userName.setText(jsonObject.getString("user_name"));
        mobileNumber.setText(jsonObject.getString(Params.json_mobile_number));
        email.setText(jsonObject.getString("email"));

        if (checkUpdateCall()) {
          mView.findViewById(R.id.usernameLayout).setVisibility(View.GONE);
          mView.findViewById(R.id.pwdLayout).setVisibility(View.GONE);
          mView.findViewById(R.id.cpwdLayout).setVisibility(View.GONE);
          isCPwdError = false;
          isPwdError = false;
          isUNameError = false;
        }
      }

      mView.findViewById(R.id.proceed_button).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          getTextFromET();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addingText(int editTextViewId, String text) {

    switch (editTextViewId) {
      case R.id.username_et:
        String userNameText = userName.getText().toString();
        if (userNameText.length() > 0) {
          userNameTV.setVisibility(View.GONE);
          isUNameError = false;
        } else {
          userNameTV.setVisibility(View.VISIBLE);
          isUNameError = true;
        }
        break;

      case R.id.mobile_number_et:
        String mNumber = mobileNumber.getText().toString();
        if (!AppDelegate.isValidMobileNumber(mNumber)) {
          isMNoError = true;
          mobileNumberTV.setVisibility(View.VISIBLE);
        } else {
          isMNoError = false;
          mobileNumberTV.setVisibility(View.GONE);
        }
        break;

      case R.id.user_email_et:
        String emailText = email.getText().toString();
        if (!AppDelegate.isValidEmail(emailText)) {
          isEmailError = true;
          emailTV.setVisibility(View.VISIBLE);
        } else {
          isEmailError = false;
          emailTV.setVisibility(View.GONE);
        }
        break;

      case R.id.password_et:
        password = mPasswordView.getText().toString();
        if (!AppDelegate.isValidPassword(password)) {
          isPwdError = true;
          mPasswordViewTV.setVisibility(View.VISIBLE);
        } else {
          isPwdError = false;
          mPasswordViewTV.setVisibility(View.GONE);
        }
        break;

      case R.id.cpassword_et:
        String confirm_password = mConfirmPwdView.getText().toString();
        if (!password.equals(confirm_password) || !AppDelegate.isValidPassword(confirm_password)) {
          isCPwdError = true;
          mConfirmPwdViewTV.setVisibility(View.VISIBLE);
        } else {
          isCPwdError = false;
          mConfirmPwdViewTV.setVisibility(View.GONE);
        }
        break;
    }
  }
}
