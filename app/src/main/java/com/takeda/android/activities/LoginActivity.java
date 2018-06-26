package com.takeda.android.activities;


import android.os.Bundle;
import android.view.LayoutInflater;
import com.skk.lib.BaseClasses.BaseActivity;
import com.takeda.android.R;
import com.takeda.android.fragment.LoginFragment;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    LayoutInflater mInflater = LayoutInflater.from(this);
    frgmTxn(new LoginFragment(), false, null);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }
}

