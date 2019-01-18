package com.takeda.android.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.skk.lib.BaseClasses.BaseFragment;
import com.skk.lib.utils.AppDelegate;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.model.BannerModel;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SplOffersFragment extends BaseFragment {

    TabLayout tabLayout;
    MyAdapter viewPagerAdapter;
    ViewPager viewPager;
    TabHost tabHost;
    Context mContext;
    Handler mHandler;
    View mView, coordinator_layout;
    LayoutInflater inflater;
    ArrayList<BannerModel.BannerDataModel> bannersList = new ArrayList<>();
    SessionManager session;
    AlertDialog alert1;
    private ProgressDialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         *Inflate tab_layout and setup Views.
         */
        mContext = getActivity();
        session = new SessionManager(mContext);
        setHandler();
        mView = inflater.inflate(R.layout.sploffers_layout, null);
        coordinator_layout = mView.findViewById(R.id.coordinator_layout);

        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);

        tabLayout = mView.findViewById(R.id.tabs);
        viewPager = mView.findViewById(R.id.viewpager);
        tabHost = new TabHost(getActivity(), null);

        fetchBanners();

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);

        session.showLogs("PagerPosition", "Position : " + AppDelegate.pagerPosition);
        viewPager.setCurrentItem(0);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    class MyAdapter extends PagerAdapter {

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            SimpleDraweeView imgflag;
            final BannerModel.BannerDataModel banner = bannersList.get(position);
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.sploffers_viewpager_item, container, false);
            imgflag = itemView.findViewById(R.id.flag);

//            ViewGroup.LayoutParams params1 = imgflag.getLayoutParams();
////            Double height1 = layoutsize() * 0.5;
//            params1.height = ViewGroup.LayoutParams.MATCH_PARENT;
//            imgflag.setLayoutParams(params1);

            AppDelegate.loadImageFromPicasaRactangle(mContext, imgflag, banner.imageURL);
            ((TextView) itemView.findViewById(R.id.banner_name)).setText(banner.name);
            ((TextView) itemView.findViewById(R.id.tv_description))
                    .setText("Description : " + AppDelegate.checkString(banner.description));
            ((TextView) itemView.findViewById(R.id.tv_effective_date))
                    .setText("Effective From : " + banner.effective_date);
            ((TextView) itemView.findViewById(R.id.tv_expiry_date))
                    .setText("Lasts Upto : " + banner.expiry_date);

            showLogs("ViewPager", "Accept Status : " + bannersList.get(position).accept_status);
            showLogs("ViewPager", "OfferName : " + bannersList.get(position).name);

            //if (!bannersList.get(position).accept_status) {
            itemView.findViewById(R.id.accept_offer_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openDialog(position, bannersList.get(position));
                        }
                    });
      /*} else if (bannersList.get(position).accept_status) {
//                ((Button)itemView.findViewById(R.id.accept_offer_button)).setText("Accepted");
        itemView.findViewById(R.id.accept_offer_button).setAlpha(0.5f);
      }*/

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }

        @Override
        public int getCount() {
            return bannersList.size();
        }

    }

    private void setHandler() {
        mHandler = new Handler() {
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);
                if (msg.what == 1) {
                    if (viewPagerAdapter != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewPagerAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        viewPagerAdapter = new MyAdapter();
                        viewPager.setAdapter(viewPagerAdapter);
                    }

                }
            }
        };
    }

    void fetchBanners() {
        if (bannersList == null) {
            bannersList = new ArrayList<>();
        }
        bannersList.clear();
        if (getMainApplication().getOfferArray() != null) {
            for (int i = 0; i < getMainApplication().getOfferArray().size(); i++) {
                if (getMainApplication().getOfferArray().get(i).banner_type
                        .equalsIgnoreCase("Special Offer")) {
                    bannersList.add(getMainApplication().getOfferArray().get(i));
                }
            }
        }
        mHandler.sendEmptyMessage(1);
    }

    void updateBanners(String id) {
        if (getMainApplication().getOfferArray() != null) {
            for (int i = 0; i < getMainApplication().getOfferArray().size(); i++) {
                if (getMainApplication().getOfferArray().get(i).id.equals(id)) {
                    getMainApplication().getOfferArray().get(i).accept_status = true;
                }
            }
        }
        mHandler.sendEmptyMessage(1);
    }

    void openDialog(final int position, final BannerModel.BannerDataModel banner) {

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

        title.setText("Special Events/ Special Offers");
        message.setText("Do you need more information about this Event/Offer?");

        promptView.findViewById(R.id.email_details_layout).setVisibility(View.GONE);

        dialogIcon.setImageResource(R.drawable.icn_mobile);
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog != null) {
                    dialog.show();
                }

                try {

                    ApiInterface api = RestAdapterService.createService(ApiInterface.class);

                    api.AcceptSpecialOffers(
                            new SessionManager(mContext).getUserDetails().get(SessionManager.KEY_USER_ID),
                            bannersList.get(position).id,
                            new JSONObject(session.getUserDetails().get(SessionManager.KEY_USER_JSON))
                                    .getString("access_token"),
                            new Callback<BannerModel>() {
                                @Override
                                public void success(BannerModel bannerModel, Response response) {
                                    System.out.println("=======success in AcceptSpecialOffers api call=====");
                                    dialog.dismiss();

                                    try {
                                        if (alert1 != null) {
                                            alert1.dismiss();
                                        }
                                        bannersList.get(position).accept_status = true;
                                        updateBanners(banner.id);
                                        AppDelegate.pagerPosition = position;
                                        session.showLogs("PagerPosition", "Position : " + AppDelegate.pagerPosition);
                                        viewPager.setAdapter(new MyAdapter());
                                        //frgmTxn(new SplOffersFragment(),false,null);

                                        if (AppDelegate.pagerPosition > -1) {
                                            viewPager.setCurrentItem(AppDelegate.pagerPosition);
                                        } else {
                                            viewPager.setCurrentItem(0);
                                        }
                                        msgAlertDialog("Accepted",
                                                "Our Medical Representative/Customer service officer will contact you shortly by phone");

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    System.out.println("=======failure in AcceptSpecialOffers api call=====");
                                    dialog.dismiss();


                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                }


                /*try{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("banner_id",bannersList.get(position).id);
                    jsonObject.put("doctor_id",new SessionManager(mContext).getUserDetails().get(SessionManager.KEY_USER_ID));

                    new UserTask().getJsonRequest(new resultInterface() {
                        @Override
                        public void Success(JSONObject response, String requestCall) {
                            try{
                                if(alert1!= null){
                                    alert1.dismiss();
                                }
                                bannersList.get(position).accept_status = true;
                                updateBanners(banner.id);
                                AppDelegate.pagerPosition = position;
                                session.showLogs("PagerPosition","Position : "+AppDelegate.pagerPosition);
                                viewPager.setAdapter(new MyAdapter());
                                //frgmTxn(new SplOffersFragment(),false,null);

                                if(AppDelegate.pagerPosition > -1){
                                    viewPager.setCurrentItem(AppDelegate.pagerPosition);
                                }
                                else{
                                    viewPager.setCurrentItem(0);
                                }
                                msgAlertDialog("Accepted","Our Medical Representative/Customer service officer will contact you shortly by phone");
                            }

                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    },jsonObject,true,(Activity) mContext,mView, Params.request_code_accept_offer);
                }

                catch (Exception e){
                    e.printStackTrace();
                }*/
            }
        });
        posBtn.setText("Yes");
        negBtn.setText("No");

        alert1 = alertDialogBuilder.create();
        alert1.setCancelable(false);
        alert1.show();

        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert1.dismiss();
            }
        });
    }

}
