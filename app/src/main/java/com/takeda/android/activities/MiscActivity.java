package com.takeda.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.skk.lib.BaseClasses.BaseActivity;
import com.takeda.android.R;
import com.takeda.android.fragment.CalendarFragment;
import com.takeda.android.fragment.EnquiryFragment;
import com.takeda.android.fragment.PastRecordFragment;
import com.takeda.android.fragment.ProductListFragment;
import com.takeda.android.fragment.SettingFragment;
import com.takeda.android.fragment.SplOffersFragment;

public class MiscActivity extends BaseActivity {

  TextView titleTextView;
  public int selectedId;
  public View coordinator_layout;
  public View headerToolBar;
  public LinearLayout mSortIconLl;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_misc);
    headerToolBar = findViewById(R.id.headerToolbar);
    titleTextView = findViewById(R.id.titleTV);
    coordinator_layout = findViewById(R.id.coordinator_layout);
    mSortIconLl = findViewById(R.id.sort_icon_ll);

    (findViewById(R.id.backArrow)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    Bundle bundle = getIntent().getExtras();
    if (bundle.containsKey("status_type")) {
      headerToolBar.setVisibility(View.VISIBLE);
      selectedId = R.id.calendar_layout;
      CalendarFragment calFrag = new CalendarFragment();
      calFrag.setArguments(bundle);
      titleTextView.setText(getResources().getString(R.string.calendar_title));
      frgmTxn(calFrag, true, null);
    } else {
      selectedId = bundle == null ? R.id.product_layout : bundle.getInt("viewId");
      loadFragment(selectedId);
    }
  }

  @Override
  public void onBackPressed() {
    if (selectedId == R.id.uppdate_profile) {
      super.onBackPressed();
      selectedId = R.id.settings_btn;
    } else if (selectedId == R.id.enquiry_button) {
      super.onBackPressed();
      titleTextView.setText(getResources().getString(R.string.past_records_title));
      selectedId = R.id.past_record_layout;
    } else if (selectedId == R.id.productList) {
      headerToolBar.setVisibility(View.GONE);
      selectedId = R.id.product_layout;
      loadFragment(selectedId);
    } else {
      startActivity(new Intent(this, HomeActivity.class));
      finish();
    }
  }

  public void loadFragment(int selectedId) {

    try {
      headerToolBar.setVisibility(View.VISIBLE);

      if (selectedId == R.id.product_layout) {
        headerToolBar.setVisibility(View.GONE);
        titleTextView.setText(getResources().getString(R.string.product_title));
        frgmTxn(new ProductListFragment(), true, null);
      } else if (selectedId == R.id.calendar_layout) {
        titleTextView.setText(getResources().getString(R.string.calendar_title));
        frgmTxn(new CalendarFragment(), true, null);
      } else if (selectedId == R.id.enquiry_layout || selectedId == R.id.enquiry_button) {
        titleTextView.setText(getResources().getString(R.string.enquiry_title));
        frgmTxn(new EnquiryFragment(), true, null);
      } else if (selectedId == R.id.settings_btn) {
        titleTextView.setText(getResources().getString(R.string.settings_title));
        frgmTxn(new SettingFragment(), true, null);
      } else if (selectedId == R.id.past_record_layout) {
        titleTextView.setText(getResources().getString(R.string.past_records_title));
        frgmTxn(new PastRecordFragment(), true, null);
      } else if (selectedId == R.id.special_offers_layout) {
        titleTextView.setText(getResources().getString(R.string.offer_title));
        frgmTxn(new SplOffersFragment(), true, null);
      }

//            else if(selectedId == R.id.about_us_layout){
//                titleTextView.setText(getResources().getString(R.string.app_name));
//                frgmTxn(new AboutUsFragment(),true,null);
//            }

    } catch (Exception e) {
      Log.d("Exception", e.toString());
      e.printStackTrace();
    }
  }

  public void setTitleText(String title) {
    titleTextView.setText(title);
  }

}

