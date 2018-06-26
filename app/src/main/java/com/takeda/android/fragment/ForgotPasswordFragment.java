package com.takeda.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.skk.lib.BaseClasses.BaseFragment;
import com.skk.lib.Interfaces.textWatcherCustom;
import com.skk.lib.utils.AppDelegate;
import com.takeda.android.R;
import com.takeda.android.async.Params;
import com.takeda.android.async.UserTask;
import com.takeda.android.async.resultInterface;
import org.json.JSONObject;


public class ForgotPasswordFragment extends BaseFragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

  boolean isEmailError = true;
  // UI references.
  private EditText mUsernameView;
  private Button submitButton;
  private TextView username_error;
  String emailText;
  View mView;
  Context mContext;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getActivity();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_forgot_pwd, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mView = view;
    init(view); // initialising the view
  }

  private void init(View view) {
    mUsernameView = view.findViewById(R.id.user_name);
    submitButton = view.findViewById(R.id.submit_button);
    username_error = view.findViewById(R.id.username_error_tv);

    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getTextFromET();
      }
    });
  }


  //Fetching the text from the form
  private void getTextFromET() {
    AppDelegate.setTextWatcher(true, mView.findViewById(R.id.user_name), new textWatcherCustom() {
      @Override
      public void addingText(int editTextViewId, String text) {
        String emailText = mUsernameView.getText().toString();
        if (emailText.length() <= 0) {
          username_error.setVisibility(View.VISIBLE);
          isEmailError = true;
        } else {
          username_error.setVisibility(View.GONE);
          isEmailError = false;
        }
      }
    });

    emailText = mUsernameView.getText().toString();

    if (!isEmailError) {
      try {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_name", emailText);

        new UserTask().getJsonRequest(new resultInterface() {
          @Override
          public void Success(JSONObject response, String requestCall) {
            try {
              frgmTxn(new LoginFragment(), true, null);
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
}
