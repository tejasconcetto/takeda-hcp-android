package com.takeda.android.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.adapters.ViewPagerAdapter;
import com.takeda.android.async.Params;
import com.takeda.android.model.BannerModel;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import me.relex.circleindicator.CircleIndicator;
import org.json.JSONObject;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

  ViewPager defaultViewpager;
  CircleIndicator defaultIndicator;
  public ArrayList<String> arrayListImage;
  Context mContext;
  SessionManager session;
  View mView;
  LinearLayout sliderLayout;
  ViewPagerAdapter adapter;
  JSONObject jsonObject;
  int currentPage = 0;
  Timer timer;
  final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
  final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
  AlertDialog alert1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_home);
    mContext = HomeActivity.this;
    session = new SessionManager(mContext);
    mView = findViewById(R.id.coordinator_layout);
    init();
    showSlider();
    hideorShowPurchaseHistory();

    if (session.isFirstTime()) {
      openDialog();
    }

  }

  private void hideorShowPurchaseHistory() {
    if (session.isShowPurchaseHistory() == 1) {
      findViewById(R.id.past_record_view).setVisibility(View.VISIBLE);
      findViewById(R.id.past_record_layout).setVisibility(View.VISIBLE);
    } else {
      findViewById(R.id.past_record_view).setVisibility(View.GONE);
      findViewById(R.id.past_record_layout).setVisibility(View.GONE);
    }
  }

  void init() {
    sliderLayout = findViewById(R.id.sliderLayout);

    ViewGroup.LayoutParams params1 = sliderLayout.getLayoutParams();
    Double height1 = layoutsize() * 0.5;
    params1.height = height1.intValue();
    sliderLayout.setLayoutParams(params1);

    defaultViewpager = findViewById(R.id.viewpager_default);
    defaultIndicator = findViewById(R.id.indicator_default);

    arrayListImage = new ArrayList<>();
    arrayListImage.clear();
    adapter = new ViewPagerAdapter(mContext, arrayListImage);
    defaultViewpager.setAdapter(adapter);
    defaultIndicator.setViewPager(defaultViewpager);

    findViewById(R.id.product_layout).setOnClickListener(this);
    findViewById(R.id.past_record_layout).setOnClickListener(this);
    findViewById(R.id.online_order_layout).setOnClickListener(this);
    findViewById(R.id.enquiry_layout).setOnClickListener(this);
    findViewById(R.id.calendar_layout).setOnClickListener(this);
    findViewById(R.id.special_offers_layout).setOnClickListener(this);
    findViewById(R.id.about_us_layout).setOnClickListener(this);
    findViewById(R.id.settings_btn).setOnClickListener(this);

    findViewById(R.id.right_arrow_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        currentPage++;
        setVPItem();
      }
    });

    findViewById(R.id.left_arrow_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        currentPage--;
        setVPItem();
      }
    });

    /*After setting the adapter use the timer */
    final Handler handler = new Handler();
    final Runnable Update = new Runnable() {
      public void run() {

        if (currentPage == arrayListImage.size()) {
          currentPage = 0;
        }
        defaultViewpager.setCurrentItem(currentPage++, true);
      }
    };

    timer = new Timer(); // This will create a new Thread
    timer.schedule(new TimerTask() { // task to be scheduled

      @Override
      public void run() {
        handler.post(Update);
      }
    }, DELAY_MS, PERIOD_MS);

    try {
      jsonObject = new JSONObject(session.getUserDetails().get(SessionManager.KEY_USER_JSON));

      ((TextView) findViewById(R.id.user_name))
          .setText("Dr. " + jsonObject.getString(Params.json_doc_name));
      ((TextView) findViewById(R.id.user_id))
          .setText("Customer# " + jsonObject.getString(Params.json_acct_number));
      ((TextView) findViewById(R.id.sales_name))
          .setText(jsonObject.getString(Params.json_doc_name));
      ((TextView) findViewById(R.id.contact_no))
          .setText("Phone " + jsonObject.getString(Params.json_mobile_number));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void showSlider() {

    ArrayList<BannerModel.BannerDataModel> offerBanner = getMainApplication().getBannerArray();
    session.showLogs("SliderBanner", "Show slider - " + offerBanner.size());

    for (BannerModel.BannerDataModel banner : offerBanner) {
      if (!banner.imageURL.equalsIgnoreCase(null) && !banner.imageURL.equalsIgnoreCase("") &&
          !banner.imageURL.equalsIgnoreCase("null") && URLUtil.isValidUrl(banner.imageURL)) {
        session.showLogs("BannerImage", banner.imageURL);
        arrayListImage.add(banner.imageURL);
      }
    }

    adapter = new ViewPagerAdapter(mContext, arrayListImage);
    defaultViewpager.setAdapter(adapter);
    defaultIndicator.setViewPager(defaultViewpager);
    defaultViewpager.setCurrentItem(0);

    //For Auto Sliding Of the Image Slider

//        final Handler handler = new Handler();
//        final Runnable Update = new Runnable() {
//            public void run() {
//                setVPItem();
//            }
//        };
//
//        timer = new Timer(); // This will create a new Thread
//        timer .schedule(new TimerTask() { // task to be scheduled
//
//            @Override
//            public void run() {
//                handler.post(Update);
//            }
//        }, 500, 3000);
//
//        defaultViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                currentPage = position;
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
  }


  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onClick(View v) {
    if (checkInternet()) {
      if (v.getId() == R.id.about_us_layout) {
        Intent activityIntent = new Intent(mContext, AboutUsActivity.class);
        startActivity(activityIntent);
      } else {
        Intent activityIntent = new Intent(mContext, MiscActivity.class);
        activityIntent.putExtra("viewId", v.getId());
        startActivity(activityIntent);
        finish();
      }
    }
  }

  void setVPItem() {
    if (currentPage == arrayListImage.size() - 1) {
      currentPage = 0;
    }
    defaultViewpager.setCurrentItem(currentPage++, true);

  }

  void openDialog() {

    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    final View promptView = layoutInflater.inflate(R.layout.notification_popup, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext,
        R.style.CustomDialog);
    alertDialogBuilder.setView(promptView);

    final Button posBtn = promptView.findViewById(R.id.posBtn);
    final Button negBtn = promptView.findViewById(R.id.negBtn);
//        final Switch notify_switch = (Switch) promptView.findViewById(R.id.notification_switch);

    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateToken(true, new Runnable() {
          @Override
          public void run() {
            if (alert1 != null) {
              alert1.dismiss();
            }
          }
        });
      }
    });

    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateToken(false, new Runnable() {
          @Override
          public void run() {
            session.setFirstTimeFalse();
            if (alert1 != null) {
              alert1.dismiss();
            }
          }
        });
      }
    });

    alert1 = alertDialogBuilder.create();
    alert1.setCancelable(false);
    alert1.show();
  }
}