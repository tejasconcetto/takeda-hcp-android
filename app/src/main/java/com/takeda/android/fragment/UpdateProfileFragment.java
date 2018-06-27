package com.takeda.android.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.skk.lib.BaseClasses.BaseFragment;
import com.skk.lib.Interfaces.textWatcherCustom;
import com.skk.lib.utils.AppDelegate;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.async.Params;
import com.takeda.android.model.LoginModel;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by premgoyal98 on 12.02.18.
 */

public class UpdateProfileFragment extends BaseFragment implements textWatcherCustom {

  View mView;
  Activity mContext;
  SessionManager session;
  EditText mobileNumber, email;
  TextView mobileNumberTV, emailTV;
  boolean isEmailError = false, isMNoError = false;

  JSONObject jsonObject = new JSONObject();

  private ProgressDialog dialog;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    mContext = getActivity();
    session = new SessionManager(mContext);

    dialog = new ProgressDialog(mContext);
    dialog.setMessage("Please Wait....");
    dialog.setCancelable(false);

    mView = inflater.inflate(R.layout.fragment_update_profile_screen, null);
    init(mView);

    setupUI(mView);

    mView.findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getTextFromET();
      }
    });
    return mView;
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
    inputMethodManager.hideSoftInputFromWindow(
        activity.getCurrentFocus().getWindowToken(), 0);
  }

  void getTextFromET() {

    AppDelegate.setTextWatcher(true, email, this);

    JSONObject params = new JSONObject();

    try {
      if (!isEmailError) {

        if (dialog != null) {
          dialog.show();
        }

        ApiInterface api = RestAdapterService.createService(ApiInterface.class);

        api.UserProfileUpdate(jsonObject.getString("doctor_id"), email.getText().toString(),
            mobileNumber.getText().toString(),
            jsonObject.getString("access_token"), new Callback<LoginModel>() {
              @Override
              public void success(LoginModel loginModel, Response response) {
                if (dialog.isShowing()) {
                  dialog.dismiss();
                }

                try {

                  if (loginModel.response.status.equalsIgnoreCase("success")) {

                    try {
                      if (getArguments() != null && getArguments()
                          .containsKey(SessionManager.KEY_USER_JSON)) {

//                                                JSONObject response = responseJSON.getJSONObject(Params.response_success_data);
                        session.createLoginSession(loginModel.response.dataUser.doctor_id,
                            loginModel.response.dataUser.mobile_number,
                            loginModel.response.dataUser.access_token,
                            loginModel.response.dataUser.toJSON());

                        showSBar(loginModel.response.statusMessage);
                        getActivity().onBackPressed();

                      }
                    } catch (Exception e) {
                      e.printStackTrace();
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


               /* params.put("doctor_id",jsonObject.getString("doctor_id"));
                params.put("email",email.getText().toString());
                params.put("mobile_number",mobileNumber.getText().toString());
                params.put("access_token",jsonObject.getString("access_token"));

                new UserTask().getJsonRequest(new resultInterface() {
                    @Override
                    public void Success(JSONObject responseJSON, String requestCall) {
                        try{
                            if(getArguments() != null && getArguments().containsKey(SessionManager.KEY_USER_JSON)){


                                JSONObject response = responseJSON.getJSONObject(Params.response_success_data);
                                session.createLoginSession(response.getString("doctor_id").toString(),
                                        response.getString("mobile_number").toString(),
                                        response.getString("access_token").toString(),
                                        response.toString());


                                    showSBar(responseJSON.getString(Params.response_message));
                                    getActivity().onBackPressed();

                            }
                        }

                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },params,true,getActivity(),mView,Params.request_code_profile_update);

                */
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void init(View mView) {
    try {

      mobileNumber = mView.findViewById(R.id.mobile_number_et);
      email = mView.findViewById(R.id.user_email_et);
      mobileNumberTV = mView.findViewById(R.id.mobile_number_et_error_tv);
      emailTV = mView.findViewById(R.id.email_et_error_tv);

      if (getArguments() != null) {
        jsonObject = new JSONObject(getArguments().getString(SessionManager.KEY_USER_JSON));

        mobileNumber.setText(jsonObject.getString(Params.json_mobile_number));
        email.setText(jsonObject.getString("email"));


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

    }
  }

}
