package com.takeda.android.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.BaseClasses.BaseFragment;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.activities.MiscActivity;
import com.takeda.android.adapters.ViewPagerAdapter;
import com.takeda.android.async.DownloadFileAsync;
import com.takeda.android.model.ProductModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Ratan on 7/29/2015.
 */
public class ProductDetailsFragment extends BaseFragment {

  View mView;
  Activity mContext;
  SessionManager session;
  CheckBox remeberMe;
  String productName = "";
  ViewPager defaultViewpager;
  CircleIndicator defaultIndicator;
  public ArrayList<String> arrayListImage = new ArrayList<>();
  LinearLayout sliderLayout;
  ViewPagerAdapter adapter;
  int currentPage = 0;
  Timer timer;
  final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
  final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    mContext = getActivity();
    session = new SessionManager(mContext);
    mView = inflater.inflate(R.layout.fragment_product_details, null);
    init(mView);
    return mView;
  }

  void init(View mView) {
    try {
      if (getArguments() != null) {
        final ProductModel.ProductsArrDataModel prdctModel = (ProductModel.ProductsArrDataModel) getArguments()
            .getSerializable("product");
        productName = prdctModel.product_name;
        showLogs("ArraySize",
            "Array List Original Size : " + String.valueOf(prdctModel.product_images.size()));
        for (int i = 0; i < prdctModel.product_images.size(); i++) {
          arrayListImage.add(prdctModel.product_images.get(i).product_image_url);
        }
/*
                ((TextView)mView.findViewById(R.id.product_name)).setText(productName);
                ((TextView)mView.findViewById(R.id.tv_category)).setText(getArguments().getString("category"));
                ((TextView)mView.findViewById(R.id.tv_description)).setText(AppDelegate.checkString(prdctModel.description));
                ((TextView)mView.findViewById(R.id.tv_ingredient)).setText(AppDelegate.checkString(prdctModel.ingredient));
                ((TextView)mView.findViewById(R.id.tv_drug_class)).setText(AppDelegate.checkString(prdctModel.drug_class));
                ((TextView)mView.findViewById(R.id.tv_indication)).setText(AppDelegate.checkString(prdctModel.indication));
                ((TextView)mView.findViewById(R.id.tv_dosage)).setText(AppDelegate.checkString(prdctModel.dosage));
                ((TextView)mView.findViewById(R.id.tv_reference)).setText(AppDelegate.checkString(prdctModel.reference));
*/
        mView.findViewById(R.id.order_more_btn).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            showProductsPopup(getActivity(), prdctModel, null);
          }
        });

        if (URLUtil.isValidUrl(prdctModel.product_pdf_url)) {
          mView.findViewById(R.id.download_btn).setVisibility(View.VISIBLE);
          mView.findViewById(R.id.download_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              checkPermission(new Runnable() {
                @Override
                public void run() {
                  downloadFile(prdctModel);
                }
              });
            }
          });
        } else {
          mView.findViewById(R.id.download_btn).setVisibility(View.GONE);
        }

        showLogs("URLStatus", "Product URL - " + prdctModel.product_detail_url);
        showLogs("URLStatus",
            "Product Final URL - " + prdctModel.product_detail_url + "/" + session.getUserDetails()
                .get(SessionManager.KEY_USER_ID) +
                "/" + session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));

        WebView webView = mView.findViewById(R.id.web_view_frag);
        webView.getSettings().setJavaScriptEnabled(true);
        //This the the enabling of the zoom controls
        webView.getSettings().setBuiltInZoomControls(true);

        //This will zoom out the WebView
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setInitialScale(1);
//                webView.loadUrl(prdctModel.product_detail_url+"/"+session.getUserDetails().get(SessionManager.KEY_USER_ID)+
//                        "/"+session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));
        webView.loadUrl(prdctModel.product_detail_url);
        showSlider();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void downloadFile(ProductModel.ProductsArrDataModel prdctModel) {
    if (prdctModel != null) {
      new DownloadFileAsync((BaseActivity) getActivity(), prdctModel.product_pdf_url,
          new DownloadFileAsync.downloadFileResponse() {
            @Override
            public void onSuccess(File filePath) {
              try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(filePath), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                getActivity().startActivity(intent);
              } catch (ActivityNotFoundException e) {
                showToast("No application found to open file.");
                e.printStackTrace();
              } catch (Exception e) {
                e.printStackTrace();
                showToast("Error occurred. Please try again.");
              }
            }

            @Override
            public void onError(String error) {
              showLogs("DownloadAsync", "Error occurred. Please try again.");
            }
          }, prdctModel.product_name + ".pdf", null, false).execute();
    }

  }

  void showSlider() {

    showLogs("ArraySize", "Array List Size : " + String.valueOf(arrayListImage.size()));

    if (arrayListImage != null && arrayListImage.size() > 0) {
      sliderLayout = mView.findViewById(R.id.sliderLayout);
      defaultViewpager = mView.findViewById(R.id.viewpager_default);
      defaultIndicator = mView.findViewById(R.id.indicator_default);

      ViewGroup.LayoutParams params1 = sliderLayout.getLayoutParams();
      Double height1 = layoutsize() * 0.5;
      params1.height = height1.intValue();
      sliderLayout.setLayoutParams(params1);

      adapter = new ViewPagerAdapter(mContext, arrayListImage);
      defaultViewpager.setAdapter(adapter);
      defaultIndicator.setViewPager(defaultViewpager);
      defaultViewpager.setCurrentItem(0);

      final Handler handler = new Handler();
      final Runnable Update = new Runnable() {
        public void run() {
          setVPItem();
        }
      };

      timer = new Timer(); // This will create a new Thread
      timer.schedule(new TimerTask() { // task to be scheduled

        @Override
        public void run() {
          handler.post(Update);
        }
      }, 500, 3000);

      defaultViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
          currentPage = position;
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
      });

      mView.findViewById(R.id.right_arrow_btn).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          currentPage++;
          setVPItem();
        }
      });

      mView.findViewById(R.id.left_arrow_btn).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          currentPage--;
          setVPItem();
        }
      });
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    ((MiscActivity) getActivity()).setTitleText(productName);
  }

  void setVPItem() {
    if (currentPage == arrayListImage.size() - 1) {
      currentPage = 0;
    }
    defaultViewpager.setCurrentItem(currentPage++, true);

  }

}
