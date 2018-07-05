package com.takeda.android.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.BaseClasses.BaseFragment;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.activities.MiscActivity;
import com.takeda.android.adapters.FilterItemsAdapter;
import com.takeda.android.adapters.FilterItemsAdapter.OnItemSelected;
import com.takeda.android.adapters.PastRecordAdapter;
import com.takeda.android.async.Params;
import com.takeda.android.model.CustomerRecordModel;
import com.takeda.android.model.PastRecordModel;
import com.takeda.android.model.PastRecordModel.PurchaseArrDataModel;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PastRecordFragment extends BaseFragment implements View.OnClickListener {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

  RecyclerView pastRecordList;
  Context mContext;
  Handler mHandler;
  PastRecordAdapter pastRecordAdapter;
  ArrayList<PastRecordModel.PurchaseArrDataModel> pastRecordListArray;
  ArrayList<PastRecordModel.PurchaseArrDataModel> filterProducts;
  View mView;
  SessionManager session;
  String by_date = "date", by_product = "product";
  String sortBy;
  TextView byDate, byProduct;
  int pastRecordSelected = -1;
  private ProgressDialog dialog;
  private Spinner filterSpinner;
  private ApiInterface apiInterface;
  ArrayList<String> customerIds;
  ArrayList<String> districtIds;
  private boolean isSelected = true;
  ArrayList<String> filterItems;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getActivity();
    session = new SessionManager(mContext);

    dialog = new ProgressDialog(mContext);
    dialog.setMessage("Please Wait....");
    dialog.setCancelable(false);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    // Inflate the layout for this fragment
    mView = inflater.inflate(R.layout.fragment_past_record, container, false);
    initView(mView);

    return mView;

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  void initView(View view) {
    try {
      mView = view;
      pastRecordList = view.findViewById(R.id.pastRecordList);
      mView.findViewById(R.id.order_more_button).setOnClickListener(this);
      mView.findViewById(R.id.enquiry_button).setOnClickListener(this);
      byDate = mView.findViewById(R.id.by_date);
      byProduct = mView.findViewById(R.id.by_product);
      filterSpinner = mView.findViewById(R.id.filter_spinner);
      filterSpinner.setVisibility(View.GONE);

      byDate.setOnClickListener(this);
      byProduct.setOnClickListener(this);
      getFilterData();
      setHandler();
      fetchPastRecords(by_date);
      setFilterIconClick();


            /*pastRecordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position > 0){
                        for(int i=0;i<pastRecordListArray.size();i++){
                            pastRecordListArray.get(i).is_selected = false;
                        }
                        pastRecordListArray.get(position).is_selected = true;
                        pastRecordSelected = position;
                        mHandler.sendEmptyMessage(1);
                    }
                }
            });*/
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

 /* private void setSpinnerAdapterClick() {
    isSelected = false;
    if (filterSpinner.getOnItemSelectedListener() == null) {
      filterSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          filterSpinner.setVisibility(View.GONE);
          if (position <= customerIds.size()) {
            filterDataByCustomerId(((TextView) view).getText().toString());
          } else {
            filterDataByDistrictId(((TextView) view).getText().toString());
          }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
          filterSpinner.setVisibility(View.GONE);
        }
      });
    }
  }*/

  private void filterDataByDistrictId(String districtId) {
    if (pastRecordListArray == null) {
      pastRecordListArray = new ArrayList<>();
    }
    pastRecordListArray.clear();
    if (pastRecordAdapter != null) {
      pastRecordAdapter.notifyDataSetChanged();
    }
    dialog.show();

    try {
      JSONObject jsonObject = new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON));
      System.out.println("jsonObject======>" + new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON)));

      ApiInterface api = RestAdapterService.createService(ApiInterface.class);

      api.FilterPurchaseHistoryByDistrict(districtId,
          session.getUserDetails().get(SessionManager.KEY_USER_ID), sortBy,
          jsonObject.getString("access_token"),
          new Callback<PastRecordModel>() {
            @Override
            public void success(PastRecordModel purchaseModel, Response response) {
              System.out.println("=======success in PurchaseHistory api call=====");

              try {

                if (purchaseModel.response.status.equalsIgnoreCase("success")) {

                  ArrayList<PastRecordModel.PurchaseArrDataModel> pastRecords = new ArrayList<PastRecordModel.PurchaseArrDataModel>();
                  pastRecords = purchaseModel.response.data.purchaseData;

                  if (pastRecords.size() > 0) {
//                                    pastRecordListArray.add(new PastRecordModel(new JSONObject(),Params.type_header));

                    for (int i = 0; i < pastRecords.size(); i++) {
                      pastRecords.get(i).row_type = Params.type_header;
                      pastRecordListArray.add(pastRecords.get(i));
                    }
                  }

                  if (sortBy.equalsIgnoreCase(by_date)) {
                    byProduct.setBackgroundResource(R.drawable.unselected_textview_right);
                    byDate.setBackgroundResource(R.drawable.selected_textview_left);
                    byDate.setTypeface(Typeface.DEFAULT_BOLD);
                    byProduct.setTypeface(Typeface.DEFAULT);

                  } else if (sortBy.equalsIgnoreCase(by_product)) {
                    byProduct.setBackgroundResource(R.drawable.selected_textview_right);
                    byDate.setBackgroundResource(R.drawable.unselected_textview_left);
                    byProduct.setTypeface(Typeface.DEFAULT_BOLD);
                    byDate.setTypeface(Typeface.DEFAULT);
                  }

                  mHandler.sendEmptyMessage(1);


                } else {
                  // msgAlertDialog("Error", purchaseModel.response.statusMessage);
                }

                if (dialog.isShowing()) {
                  dialog.dismiss();
                }

              } catch (Exception e) {
                e.printStackTrace();
              }
            }

            @Override
            public void failure(RetrofitError error) {

              System.out.println("=======failed in PurchaseHistory api call=====");
              System.out.println("=error==========>" + error);
              System.out.println("error.getMessage()==========>" + error.getMessage());

              Log.e("PurchaseHistory", " failed " + String.valueOf(error));

              dialog.dismiss();


            }
          });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void filterDataByCustomerId(String customerId) {
    if (pastRecordListArray == null) {
      pastRecordListArray = new ArrayList<>();
    }
    pastRecordListArray.clear();
    if (pastRecordAdapter != null) {
      pastRecordAdapter.notifyDataSetChanged();
    }
    dialog.show();

    try {
      JSONObject jsonObject = new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON));
      System.out.println("jsonObject======>" + new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON)));

      ApiInterface api = RestAdapterService.createService(ApiInterface.class);

      api.FilterPurchaseHistoryByCustomerId(customerId,
          session.getUserDetails().get(SessionManager.KEY_USER_ID), sortBy,
          jsonObject.getString("access_token"),
          new Callback<PastRecordModel>() {
            @Override
            public void success(PastRecordModel purchaseModel, Response response) {
              System.out.println("=======success in PurchaseHistory api call=====");

              try {

                if (purchaseModel.response.status.equalsIgnoreCase("success")) {

                  ArrayList<PastRecordModel.PurchaseArrDataModel> pastRecords = new ArrayList<PastRecordModel.PurchaseArrDataModel>();
                  pastRecords = purchaseModel.response.data.purchaseData;

                  if (pastRecords.size() > 0) {
//                                    pastRecordListArray.add(new PastRecordModel(new JSONObject(),Params.type_header));

                    for (int i = 0; i < pastRecords.size(); i++) {
                      pastRecords.get(i).row_type = Params.type_header;
                      pastRecordListArray.add(pastRecords.get(i));
                    }
                  }

                  if (sortBy.equalsIgnoreCase(by_date)) {
                    byProduct.setBackgroundResource(R.drawable.unselected_textview_right);
                    byDate.setBackgroundResource(R.drawable.selected_textview_left);
                    byDate.setTypeface(Typeface.DEFAULT_BOLD);
                    byProduct.setTypeface(Typeface.DEFAULT);

                  } else if (sortBy.equalsIgnoreCase(by_product)) {
                    byProduct.setBackgroundResource(R.drawable.selected_textview_right);
                    byDate.setBackgroundResource(R.drawable.unselected_textview_left);
                    byProduct.setTypeface(Typeface.DEFAULT_BOLD);
                    byDate.setTypeface(Typeface.DEFAULT);
                  }

                  mHandler.sendEmptyMessage(1);


                } else {
                  mHandler.sendEmptyMessage(1);
                  //msgAlertDialog("Error", purchaseModel.response.statusMessage);
                }

                if (dialog.isShowing()) {
                  dialog.dismiss();
                }

              } catch (Exception e) {
                e.printStackTrace();
              }
            }

            @Override
            public void failure(RetrofitError error) {

              System.out.println("=======failed in PurchaseHistory api call=====");
              System.out.println("=error==========>" + error);
              System.out.println("error.getMessage()==========>" + error.getMessage());

              Log.e("PurchaseHistory", " failed " + String.valueOf(error));

              dialog.dismiss();


            }
          });

    } catch (Exception e) {
      e.printStackTrace();
    }

    //setProductFilterAdapter(filterProducts);
  }

  private void setProductFilterAdapter(ArrayList<PurchaseArrDataModel> filterProducts) {
    pastRecordAdapter = new PastRecordAdapter((BaseActivity) getActivity(),
        filterProducts);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
    pastRecordList.setLayoutManager(mLayoutManager);
    pastRecordList.setItemAnimator(new DefaultItemAnimator());
    pastRecordList.setAdapter(pastRecordAdapter);
  }


  private void getFilterData() {
    JSONObject jsonObject = null;
    try {
      jsonObject = new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON));

      apiInterface = RestAdapterService.createService(ApiInterface.class);
      apiInterface.getCustomerRecord(session.getUserDetails().get(SessionManager.KEY_USER_ID),
          jsonObject.getString("access_token"), new Callback<CustomerRecordModel>() {
            @Override
            public void success(CustomerRecordModel customerRecordModel, Response response) {
              customerIds = customerRecordModel.getResponse().getData()
                  .getResult().getCustomerIds();
              districtIds = customerRecordModel.getResponse().getData()
                  .getResult().getDistricts();
              filterItems = new ArrayList<>();
              filterItems.add("Customer ID");
              filterItems.addAll(customerIds);
              filterItems.add("District");
              filterItems.addAll(districtIds);
              if (filterItems.size() > 0) {
                setAdapter(filterItems);
              } else {
                if (getActivity() instanceof MiscActivity) {
                  ((MiscActivity) getActivity()).mSortIconLl.setVisibility(View.GONE);
                }
              }

            }

            @Override
            public void failure(RetrofitError error) {

            }
          });
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void setAdapter(ArrayList<String> filterItems) {
    ArrayAdapter<String> filterItemsAdpter = new ArrayAdapter<>(mContext, R.layout.spinner_row_item,
        filterItems);
    filterSpinner.setAdapter(filterItemsAdpter);
  }

  private void setFilterIconClick() {
    if (getActivity() instanceof MiscActivity) {
      ((MiscActivity) getActivity()).mSortIconLl.setVisibility(View.VISIBLE);
      ((MiscActivity) getActivity()).mSortIconLl.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          //startActivityForResult(new Intent(mContext, FilterOptionsActivity.class), Constants.FILTER_RESULT_CODE);
         /* filterSpinner.setVisibility(View.VISIBLE);
          filterSpinner.performClick();
          if (isSelected) {
            if(filterItems.size()>0) {
              filterSpinner.setSelected(false);
              filterSpinner.setSelection(filterItems.size() - 1, false);
              setSpinnerAdapterClick();
            }
          }*/
          buildFilterPopup();
        }
      });
    }
  }

  private void buildFilterPopup() {
    final Dialog dialog = new Dialog(mContext);
    //set layout custom
    dialog.setContentView(R.layout.filter_popup);
    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    final RecyclerView rvcaddy = dialog.findViewById(R.id.filter_items_recyclerView);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
    rvcaddy.setLayoutManager(mLayoutManager);
    FilterItemsAdapter filterItemsAdapter = new FilterItemsAdapter(mContext, customerIds,
        districtIds);
    // Add some item here to show the list.
    filterItemsAdapter.setOnItemSelectedListener(new OnItemSelected() {
      @Override
      public void onItemClickListener(TextView textView, String selctedItem) {
        dialog.dismiss();
        if (textView.getTag().equals(getString(R.string.customer_id))) {
          filterDataByCustomerId(selctedItem);
        } else {
          filterDataByDistrictId(selctedItem);
        }
      }
    });
    rvcaddy.setAdapter(filterItemsAdapter);
    dialog.show();

  }

  void fetchPastRecords(final String sort_type) {
    sortBy = sort_type;
    if (pastRecordListArray == null) {
      pastRecordListArray = new ArrayList<>();
    }

    pastRecordListArray.clear();

    dialog.show();

    try {
      JSONObject jsonObject = new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON));
      System.out.println("jsonObject======>" + new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON)));

      ApiInterface api = RestAdapterService.createService(ApiInterface.class);

      api.PurchaseHistory(session.getUserDetails().get(SessionManager.KEY_USER_ID), sort_type,
          jsonObject.getString("access_token"),
          new Callback<PastRecordModel>() {
            @Override
            public void success(PastRecordModel purchaseModel, Response response) {
              System.out.println("=======success in PurchaseHistory api call=====");

              try {

                if (purchaseModel.response.status.equalsIgnoreCase("success")) {

                  ArrayList<PastRecordModel.PurchaseArrDataModel> pastRecords = new ArrayList<PastRecordModel.PurchaseArrDataModel>();
                  pastRecords = purchaseModel.response.data.purchaseData;

                  if (pastRecords.size() > 0) {
//                                    pastRecordListArray.add(new PastRecordModel(new JSONObject(),Params.type_header));

                    for (int i = 0; i < pastRecords.size(); i++) {
                      pastRecords.get(i).row_type = Params.type_header;
                      pastRecordListArray.add(pastRecords.get(i));
                    }
                  }

                  if (sort_type.equalsIgnoreCase(by_date)) {
                    byProduct.setBackgroundResource(R.drawable.unselected_textview_right);
                    byDate.setBackgroundResource(R.drawable.selected_textview_left);
                    byDate.setTypeface(Typeface.DEFAULT_BOLD);
                    byProduct.setTypeface(Typeface.DEFAULT);

                  } else if (sort_type.equalsIgnoreCase(by_product)) {
                    byProduct.setBackgroundResource(R.drawable.selected_textview_right);
                    byDate.setBackgroundResource(R.drawable.unselected_textview_left);
                    byProduct.setTypeface(Typeface.DEFAULT_BOLD);
                    byDate.setTypeface(Typeface.DEFAULT);
                  }

                  mHandler.sendEmptyMessage(1);


                } else {
                  msgAlertDialog("Error", purchaseModel.response.statusMessage);
                }

                if (dialog.isShowing()) {
                  dialog.dismiss();
                }

              } catch (Exception e) {
                e.printStackTrace();
              }
            }

            @Override
            public void failure(RetrofitError error) {

              System.out.println("=======failed in PurchaseHistory api call=====");
              System.out.println("=error==========>" + error);
              System.out.println("error.getMessage()==========>" + error.getMessage());

              Log.e("PurchaseHistory", " failed " + String.valueOf(error));

              dialog.dismiss();


            }
          });

    } catch (Exception e) {
      e.printStackTrace();
    }

      /*   JSONObject jsonObject = new JSONObject();
        jsonObject.put("doctor_id",session.getUserDetails().get(SessionManager.KEY_USER_ID));
        jsonObject.put("order_by",sort_type);



       new UserTask().getJsonRequest(new resultInterface() {
            @Override
            public void Success(JSONObject response, String requestCall) {
                try {
                    JSONArray pastRecords = response.getJSONObject(Params.response_success_data).getJSONArray("purchase_history");

                    if(pastRecords.length() > 0){
                        pastRecordListArray.add(new PastRecordModel(new JSONObject(),Params.type_header));
                        for(int i=0;i<pastRecords.length();i++){
                            pastRecordListArray.add(new PastRecordModel(pastRecords.getJSONObject(i),Params.type_row));
                        }
                    }

                    if(sort_type.equalsIgnoreCase(by_date)){
                        byProduct.setBackgroundColor(AppDelegate.getColorRes(mContext,R.color.past_record_unselected));
                        byDate.setBackgroundColor(AppDelegate.getColorRes(mContext,R.color.past_record_selected));
                    }

                    else if(sort_type.equalsIgnoreCase(by_product)){
                        byProduct.setBackgroundColor(AppDelegate.getColorRes(mContext,R.color.past_record_selected));
                        byDate.setBackgroundColor(AppDelegate.getColorRes(mContext,R.color.past_record_unselected));
                    }
                }

                catch (Exception e){
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(1);
            }
        },jsonObject,true,getActivity(),((MiscActivity)getActivity()).coordinator_layout, Params.request_code_past_record);
    */
  }

  void setEmptyLayout() {

    if (pastRecordListArray.size() > 0) {
      mView.findViewById(R.id.empty_cart_list_layout).setVisibility(View.GONE);
      mView.findViewById(R.id.mainLayoutList).setVisibility(View.VISIBLE);
    } else if (pastRecordListArray.size() == 0) {
      mView.findViewById(R.id.empty_cart_list_layout).setVisibility(View.VISIBLE);
      mView.findViewById(R.id.mainLayoutList).setVisibility(View.GONE);
    }

    ((ImageView) mView.findViewById(R.id.empty_data_image)).setImageResource(R.drawable.inc_cart);
    ((TextView) mView.findViewById(R.id.empty_data_text))
        .setText("Please fill your cart with fresh fruits and vegetables.");
    ((TextView) mView.findViewById(R.id.empty_button)).setText("Add Products");
    mView.findViewById(R.id.empty_button).setVisibility(View.VISIBLE);
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  private void setHandler() {
    mHandler = new Handler() {
      @Override
      public void dispatchMessage(Message msg) {
        super.dispatchMessage(msg);
        if (msg.what == 1) {
//                    if (pastRecordAdapter != null) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                pastRecordAdapter.notifyDataSetChanged();
//                                pastRecordList.invalidate();
//                            }
//                        });
//                    } else {
          pastRecordAdapter = new PastRecordAdapter((BaseActivity) getActivity(),
              pastRecordListArray);
          RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
          pastRecordList.setLayoutManager(mLayoutManager);
          pastRecordList.setItemAnimator(new DefaultItemAnimator());
          pastRecordList.setAdapter(pastRecordAdapter);
//                    }

                    /*
                    AppDelegate.setListViewHeightBasedOnChildren(pastRecordList);
                    */
          //setEmptyLayout();
        } else if (msg.what == 2) {
          pastRecordAdapter = new PastRecordAdapter((BaseActivity) getActivity(),
              filterProducts);
          RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
          pastRecordList.setLayoutManager(mLayoutManager);
          pastRecordList.setItemAnimator(new DefaultItemAnimator());
          pastRecordList.setAdapter(pastRecordAdapter);
        }
      }
    };
  }

  @Override
  public void onClick(View v) {

    try {
      switch (v.getId()) {
        case R.id.by_date:
          fetchPastRecords(by_date);
          break;

        case R.id.by_product:
          fetchPastRecords(by_product);
          break;

        case R.id.enquiry_button:
          frgmTxn(new EnquiryFragment(), true, null);
          ((MiscActivity) getActivity())
              .setTitleText(getResources().getString(R.string.enquiry_title));
          ((MiscActivity) getActivity()).selectedId = R.id.enquiry_button;
//                    makeSalesEnquiry(null,enquiry_type_general);
          break;

        case R.id.order_more_button:
          if (pastRecordSelected > -1) {
//                        showProductsPopup(getActivity(), pastRecordListArray.get(pastRecordSelected), new Runnable() {
//                            @Override
//                            public void run() {
//                                pastRecordListArray.get(pastRecordSelected).is_selected = false;
//                                pastRecordSelected = -1;
//                                mHandler.sendEmptyMessage(1);
//                            }
//                        });
          } else {
            showToast("Please select a record.");
          }
          break;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
