package com.takeda.android.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.skk.lib.BaseClasses.BaseFragment;
import com.skk.lib.Interfaces.textWatcherCustom;
import com.skk.lib.utils.AppDelegate;
import com.skk.lib.utils.ParamsKeys;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.async.Params;
import com.takeda.android.model.LoginModel;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;
import java.util.HashMap;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PasswordResetForgotFragment extends BaseFragment implements textWatcherCustom {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

  String callType, mobileNo;
  JSONObject params = new JSONObject();
  boolean isTokenError = true, isPwdError = true, isCPwdError = true;
  private EditText tokenOldPwd, mPasswordView, mConfirmPwdView;
  private Button submitButton;
  private TextView tokenOldPwdError, pwdError, confirmPwdError;
  String tokenOldTxt, passwordText, confirmPwdText;
  View mView;
  Context mContext;
  private ProgressDialog dialog;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getActivity();

    dialog = new ProgressDialog(mContext);
    dialog.setMessage("Please Wait....");
    dialog.setCancelable(false);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_pwd_form_screen, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mView = view;
    init(view);

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
    inputMethodManager.hideSoftInputFromWindow(
        activity.getCurrentFocus().getWindowToken(), 0);
  }

  private void init(View view) {
    tokenOldPwd = view.findViewById(R.id.token_pwd);
    mPasswordView = view.findViewById(R.id.new_pwd);
    mConfirmPwdView = view.findViewById(R.id.confirm_pwd);

    tokenOldPwdError = view.findViewById(R.id.token_error_tv);
    pwdError = view.findViewById(R.id.pwd_error_tv);
    confirmPwdError = view.findViewById(R.id.confirm_pwd_error_tv);

    submitButton = view.findViewById(R.id.submit_button);

    if (getArguments() != null) {
      try {
        callType = getArguments().getString(ParamsKeys.callType);
        mobileNo = getArguments().getString(ParamsKeys.mobile);

        if (callType.equalsIgnoreCase(ParamsKeys.changePwd)) {
          getActivity().setTitle("Reset Password");
          tokenOldPwd.setHint("Current Password");
        } else if (callType.equalsIgnoreCase(ParamsKeys.forgotPwd)) {
          tokenOldPwd.setHint("Registered Email");
          tokenOldPwd.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//                        tokenOldPwd.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/helvetica_neue.otf"));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getTextFromET();
      }
    });

  }

  private void getTextFromET() {
    AppDelegate.setTextWatcher(true, mView.findViewById(R.id.token_pwd), this);
    AppDelegate.setTextWatcher(true, mView.findViewById(R.id.new_pwd), this);
    AppDelegate.setTextWatcher(true, mView.findViewById(R.id.confirm_pwd), this);

    Log.d("BtnClicked", "Status : ButtonClicked");
    Log.d("BtnClicked",
        "Condition Status : " + String.valueOf(!isTokenError && !isPwdError && !isCPwdError));

    if (dialog != null) {
      dialog.show();
    }

    try {
      HashMap<String, String> userData = new SessionManager(mContext).getUserDetails();

      tokenOldTxt = tokenOldPwd.getText().toString();
      passwordText = mPasswordView.getText().toString();
      confirmPwdText = confirmPwdError.getText().toString();

      final SessionManager session = new SessionManager(mContext);

      if (!isTokenError && !isPwdError && !isCPwdError) {
        ApiInterface api = RestAdapterService.createService(ApiInterface.class);

        api.UserChangePassword(userData.get(SessionManager.KEY_USER_ID),
            new JSONObject(userData.get(SessionManager.KEY_USER_JSON))
                .getString(Params.json_doc_username),
            tokenOldPwd.getText().toString(), mConfirmPwdView.getText().toString(), new JSONObject(
                new SessionManager(mContext).getUserDetails().get(SessionManager.KEY_USER_JSON))
                .getString("access_token"),
            new Callback<LoginModel>() {
              @Override
              public void success(LoginModel loginModel, Response response) {

                if (dialog.isShowing()) {
                  dialog.dismiss();
                }

                try {

                  if (loginModel.response.status.equalsIgnoreCase("success")) {
                    if (callType.equalsIgnoreCase(ParamsKeys.changePwd)) {
                      getActivity().onBackPressed();
                      showToast("Password Changed Successfully.");
                    } else if (callType.equalsIgnoreCase(ParamsKeys.forgotPwd)) {
                      showToast("Password Reset. Please login with the new credentials");
                      session.logoutUser(getActivity());
                      getActivity().finish();
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

      }
    } catch (Exception e) {
      e.printStackTrace();
    }



            /*try{
                HashMap<String, String> userData = new SessionManager(mContext).getUserDetails();
                params.put("user_name",new JSONObject(userData.get(SessionManager.KEY_USER_JSON)).getString(Params.json_doc_username));
                params.put("doctor_id",userData.get(SessionManager.KEY_USER_ID));
                params.put("current_password",tokenOldPwd.getText().toString());
                params.put("password",mConfirmPwdView.getText().toString());

                tokenOldTxt = tokenOldPwd.getText().toString();
                passwordText = mPasswordView.getText().toString();
                confirmPwdText = confirmPwdError.getText().toString();

                final SessionManager session = new SessionManager(mContext);

                if(!isTokenError && !isPwdError && !isCPwdError){
                    new UserTask().getJsonRequest(new resultInterface() {
                        @Override
                        public void Success(JSONObject response, String requestCall) {
                            if(callType.equalsIgnoreCase(ParamsKeys.changePwd)) {
                                getActivity().onBackPressed();
                                showToast("Password Changed Successfully.");
                            }

                            else if(callType.equalsIgnoreCase(ParamsKeys.forgotPwd)) {
                                showToast("Password Reset. Please login with the new credentials");
                                session.logoutUser(getActivity());
                                getActivity().finish();
                            }
                        }
                    },params,true,getActivity(),mView, Params.request_change_pwd);
                }
            }

            catch (Exception e){
                e.printStackTrace();
            }*/
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void addingText(int editTextViewId, String text) {

    switch (editTextViewId) {

      case R.id.token_pwd:
        tokenOldTxt = tokenOldPwd.getText().toString();
        if (callType.equalsIgnoreCase(ParamsKeys.changePwd)) {
          if (!AppDelegate.isValidPassword(tokenOldTxt)) {
            tokenOldPwdError.setText(getString(R.string.error_incorrect_password));
            tokenOldPwdError.setVisibility(View.VISIBLE);
            isTokenError = true;
          } else {
            tokenOldPwdError.setVisibility(View.GONE);
            isTokenError = false;
          }
        }

        if (callType.equalsIgnoreCase(ParamsKeys.forgotPwd)) {
          if (!AppDelegate.isValidEmail(tokenOldTxt)) {
            tokenOldPwdError.setText(getString(R.string.error_invalid_email));
            tokenOldPwdError.setVisibility(View.VISIBLE);
            isTokenError = true;
          } else {
            tokenOldPwdError.setVisibility(View.GONE);
            isTokenError = false;
          }
        }

        break;

      case R.id.new_pwd:
        passwordText = mPasswordView.getText().toString();
        if (!AppDelegate.isValidPassword(passwordText)) {
          isPwdError = true;
          pwdError.setVisibility(View.VISIBLE);
        } else {
          isPwdError = false;
          pwdError.setVisibility(View.GONE);
        }
        break;

      case R.id.confirm_pwd:
        confirmPwdText = mConfirmPwdView.getText().toString();
        if (!confirmPwdText.equals(passwordText)) {
          isCPwdError = true;
          confirmPwdError.setVisibility(View.VISIBLE);
        } else {
          isCPwdError = false;
          confirmPwdError.setVisibility(View.GONE);
        }
        break;
    }
  }
}
