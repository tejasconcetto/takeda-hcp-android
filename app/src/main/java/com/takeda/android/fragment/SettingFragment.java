package com.takeda.android.fragment;

import static com.skk.lib.utils.ParamsKeys.callType;
import static com.skk.lib.utils.ParamsKeys.changePwd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.BaseClasses.BaseFragment;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.activities.LoginActivity;
import com.takeda.android.activities.MiscActivity;
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
public class SettingFragment extends BaseFragment implements View.OnClickListener {

  View mView;
  Activity mContext;
  SessionManager session;
  CheckBox remeberMe;
  ToggleButton notificationSwitch;
  AlertDialog alert1;

  private ProgressDialog dialog;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    mContext = getActivity();
    session = new SessionManager(mContext);
    mView = inflater.inflate(R.layout.settings_fragment, null);
    remeberMe = mView.findViewById(R.id.remember_me_chck_box);
    notificationSwitch = mView.findViewById(R.id.notification_switch);

    dialog = new ProgressDialog(mContext);
    dialog.setMessage("Please Wait....");
    dialog.setCancelable(false);

    init(mView);

    remeberMe.setOnClickListener(this);
    mView.findViewById(R.id.change_pwd).setOnClickListener(this);
    mView.findViewById(R.id.uppdate_profile).setOnClickListener(this);
    mView.findViewById(R.id.logout_button).setOnClickListener(this);
    notificationSwitch.setOnClickListener(this);
    return mView;
  }

  void init(View mView) {
    try {
      JSONObject jsonObject = new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON));
      String doc_name = jsonObject.getString(Params.json_doc_name).equals("") ? "-"
          : jsonObject.getString(Params.json_doc_name);
      String email = jsonObject.getString("email").equals("") ? "-" : jsonObject.getString("email");
      String fax_no =
          jsonObject.getString("fax_number").equals("") ? "-" : jsonObject.getString("fax_number");
      String account_no = jsonObject.getString(Params.json_acct_number).equals("") ? "-"
          : jsonObject.getString(Params.json_acct_number);
      String speciality =
          jsonObject.getJSONObject("specialty").getString("specialty_title").equals("") ? "-"
              : jsonObject.getJSONObject("specialty").getString("specialty_title");
      String address =
          jsonObject.getString("address").equals("") ? "-" : jsonObject.getString("address");
      String sector = jsonObject.getJSONObject("sector").getString("sector_name").equals("") ? "-"
          : jsonObject.getJSONObject("sector").getString("sector_name");
      String phone = jsonObject.getString(Params.json_mobile_number).equals("") ? "-"
          : jsonObject.getString(Params.json_mobile_number);

      ((TextView) mView.findViewById(R.id.user_name)).setText("Dr. " + doc_name);
      ((TextView) mView.findViewById(R.id.user_email)).setText(email);
      ((TextView) mView.findViewById(R.id.user_fax_no)).setText(fax_no);
      ((TextView) mView.findViewById(R.id.user_account_no)).setText(account_no);
      ((TextView) mView.findViewById(R.id.user_speciality)).setText(speciality);
      ((TextView) mView.findViewById(R.id.user_address)).setText(address);
      ((TextView) mView.findViewById(R.id.user_sector)).setText(sector);
      ((TextView) mView.findViewById(R.id.user_phone)).setText(phone);

      ((TextView) mView.findViewById(R.id.app_version)).setText("App Version " +
          getActivity().getPackageManager()
              .getPackageInfo(getActivity().getPackageName(), 0).versionName);

      remeberMe.setChecked(session.isRemMe());

      notificationSwitch.setChecked(session.isToNotify());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    ((MiscActivity) getActivity()).setTitleText(getResources().getString(R.string.settings_title));
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.logout_button:

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

        title.setText("Logout");
        title.setGravity(Gravity.CENTER);

        message.setText("Are you sure you want to logout?");
        message.setGravity(Gravity.CENTER);

        promptView.findViewById(R.id.email_details_layout).setVisibility(View.GONE);

        dialogIcon.setImageResource(R.drawable.icn_mobile);
        posBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            alert1.dismiss();

            if (dialog != null) {
              dialog.show();
            }

            try {
              JSONObject jsonObject = new JSONObject();
              jsonObject.put("doctor_id", session.getUserDetails().get(SessionManager.KEY_USER_ID));

              ApiInterface api = RestAdapterService.createService(ApiInterface.class);

              api.UserLogout(session.getUserDetails().get(SessionManager.KEY_USER_ID),
                  new JSONObject(session.getUserDetails().get(SessionManager.KEY_USER_JSON))
                      .getString("access_token"),
                  new Callback<LoginModel>() {
                    @Override
                    public void success(LoginModel loginModel, Response response) {
                      if (dialog.isShowing()) {
                        dialog.dismiss();
                      }
                      if (loginModel.response.status.equalsIgnoreCase("success")) {

//                                            notificationSwitch.setChecked(false);
                        startActivity(new Intent(mContext, LoginActivity.class));
                        getActivity().finish();

//                                            if (session.isRemMe())
//                                            {
//                                                startActivity(new Intent(mContext,LoginActivity.class));
//                                                getActivity().finish();
//                                            }
//                                            else {
//                                                session.logoutUser(mContext);
//                                            }
                      } else {
                        msgAlertDialog("Error", loginModel.response.statusMessage);
                      }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                      if (dialog.isShowing()) {
                        dialog.dismiss();
                      }

                    }
                  });

                    /*new UserTask().getJsonRequest(new resultInterface() {
                        @Override
                        public void Success(JSONObject response, String requestCall) {
                            session.logoutUser(mContext);
                        }
                    },jsonObject,true,mContext,mView,Params.request_code_logout);*/
            } catch (Exception e) {
              e.printStackTrace();
            }

          }
        });
        posBtn.setText("Confirm");
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

        break;

      case R.id.notification_switch:
        ((BaseActivity) getActivity()).updateToken(notificationSwitch.isChecked(), null);
        break;

      case R.id.remember_me_chck_box:
        session.setRememberNotification(SessionManager.IS_REM_ME,
            ((CheckBox) mView.findViewById(R.id.remember_me_chck_box)).isChecked());
        break;

      case R.id.uppdate_profile:
        ((BaseActivity) getActivity()).updateToken(notificationSwitch.isChecked(), null);

        ((MiscActivity) getActivity()).selectedId = R.id.uppdate_profile;
        ((MiscActivity) getActivity())
            .setTitleText(getResources().getString(R.string.update_profile_title));
        UpdateProfileFragment addInfoFrag = new UpdateProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SessionManager.KEY_USER_JSON,
            session.getUserDetails().get(SessionManager.KEY_USER_JSON));
        bundle.putString("callFrom", "updateProfile");
        addInfoFrag.setArguments(bundle);
        frgmTxn(addInfoFrag, true, null);
        break;

      case R.id.change_pwd:
        ((MiscActivity) getActivity()).selectedId = R.id.uppdate_profile;
        ((MiscActivity) getActivity())
            .setTitleText(getResources().getString(R.string.change_pwd_title));
        PasswordResetForgotFragment frgtFrag = new PasswordResetForgotFragment();
        Bundle bundleFrgm = new Bundle();
//                bundleFrgm.putString(SessionManager.KEY_USER_JSON,session.getUserDetails().get(SessionManager.KEY_USER_JSON));
        bundleFrgm.putString(callType, changePwd);
        frgtFrag.setArguments(bundleFrgm);
        frgmTxn(frgtFrag, true, null);
        break;
    }
  }
}
