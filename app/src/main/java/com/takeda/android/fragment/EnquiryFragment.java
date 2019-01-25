package com.takeda.android.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.BaseClasses.BaseFragment;
import com.skk.lib.Interfaces.OnEnquiryClick;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.activities.MiscActivity;
import com.takeda.android.adapters.EnquiryAdapter;
import com.takeda.android.model.EnquiryModel;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.takeda.android.async.Params.enquiry_type_general;
import static com.takeda.android.async.Params.enquiry_type_sales;

public class EnquiryFragment extends BaseFragment implements OnEnquiryClick {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    ListView enquiryList;
    Context mContext;
    Handler mHandler;
    EnquiryAdapter enquiryAdapter;
    ArrayList<EnquiryModel.SalesPersonArrDataModel> enquiryListArray;
    View mView;
    SessionManager session;
    AlertDialog alert1;
    private ProgressDialog dialog;

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
        return inflater.inflate(R.layout.fragment_enquiry, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    void initView(View view) {
        try {
            mView = view;
            ((MiscActivity) getActivity()).mSortIconLl.setVisibility(View.GONE);
            enquiryList = view.findViewById(R.id.enquiryList);
            mView.findViewById(R.id.general_enquiry_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog("query", -1);
                }
            });
            setHandler();
            fetchEnquiry();

            enquiryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //openDialog("call", position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void fetchEnquiry() {

        if (dialog != null) {
            dialog.show();
        }

        try {
            JSONObject jsonObject = new JSONObject(
                    session.getUserDetails().get(SessionManager.KEY_USER_JSON));
            System.out.println("jsonObject======>" + new JSONObject(
                    session.getUserDetails().get(SessionManager.KEY_USER_JSON)));

            ApiInterface api = RestAdapterService.createService(ApiInterface.class);

            api.SalesPersons(jsonObject.getString("doctor_id"), jsonObject.getString("access_token"),
                    new Callback<EnquiryModel>() {
                        @Override
                        public void success(EnquiryModel enquiryModel, Response response) {
                            System.out.println("=======success in SalesPersons api call=====");
                            dialog.dismiss();

                            if (enquiryModel.response.status.equalsIgnoreCase("success")) {

                                if (enquiryListArray == null) {
                                    enquiryListArray = new ArrayList<EnquiryModel.SalesPersonArrDataModel>();
                                }
                                enquiryListArray.clear();

                                ArrayList<EnquiryModel.SalesPersonArrDataModel> resultArr = new ArrayList<EnquiryModel.SalesPersonArrDataModel>();
                                resultArr = enquiryModel.response.data.salesPersonData;

                                if (resultArr.size() > 0) {
                                    enquiryListArray = resultArr;
                                }

                 /*  JSONArray enquiryContacts = response.getJSONObject(Params.response_success_data).getJSONArray("sales_persons");

                    if(enquiryContacts.length() > 0){
                        for(int i=0;i<enquiryContacts.length();i++){
                            enquiryListArray.add(new EnquiryModel(enquiryContacts.getJSONObject(i)));
                        }
                    }*/
                                mHandler.sendEmptyMessage(1);
                            } else {
                                msgAlertDialog("Error", enquiryModel.response.statusMessage);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            System.out.println("=======failure in SalesPersons api call=====");
                            dialog.dismiss();

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }



       /* JSONObject jsonObject = new JSONObject();
        jsonObject.put("doctor_id",session.getUserDetails().get(SessionManager.KEY_USER_ID));
        jsonObject.put("access_token",session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN))

       new UserTask().getJsonRequest(new resultInterface() {

            @Override
            public void Success(JSONObject response, String requestCall) {
                try {

                    if(enquiryListArray == null){
                        enquiryListArray = new ArrayList<EnquiryModel>();
                    }
                    enquiryListArray.clear();
                 /*   JSONArray enquiryContacts = response.getJSONObject(Params.response_success_data).getJSONArray("sales_persons");

                    if(enquiryContacts.length() > 0){
                        for(int i=0;i<enquiryContacts.length();i++){
                            enquiryListArray.add(new EnquiryModel(enquiryContacts.getJSONObject(i)));
                        }
                    }
                    mHandler.sendEmptyMessage(1);
                }

                catch (Exception e){
                    e.printStackTrace();
                }
            }
        },jsonObject,true,getActivity(),mView, Params.request_code_enquiry);*/
    }

    void setEmptyLayout() {

        if (enquiryListArray.size() > 0) {
            mView.findViewById(R.id.empty_cart_list_layout).setVisibility(View.GONE);
            mView.findViewById(R.id.mainLayoutList).setVisibility(View.VISIBLE);
        } else if (enquiryListArray.size() == 0) {
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
                    try {
                        if (enquiryAdapter != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    enquiryAdapter.notifyDataSetChanged();
                                    enquiryList.invalidate();
                                }
                            });
                        } else {
                            enquiryAdapter = new EnquiryAdapter((BaseActivity) getActivity(), enquiryListArray,
                                    EnquiryFragment.this);
                            enquiryList.setAdapter(enquiryAdapter);
                        }
                        setEmptyLayout();
                    } catch (Exception e) {
                        e.toString();
                    }
                }
            }
        };
    }

    @Override
    public void OnEmailClick(int pos) {
        openDialog("email", pos);
    }

    @Override
    public void OnCallClick(int pos) {
        openDialog("call", pos);
    }

    @Override
    public void OnProductNameClick(int pos) {
        msgAlertDialog("Responsible products", enquiryListArray.get(pos).productName);
    }

    void openDialog(final String callType, final int index) {

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

        String positiveBtnName = "";

        if (callType.equalsIgnoreCase("call")) {
            positiveBtnName = "CALL NOW";
            title.setText("Call Us Now");
//            message.setText("Call Us Now");
            message.setVisibility(View.GONE);
            contactDetail.setText(enquiryListArray.get(index).enquiry_mobile);
            dialogIcon.setImageResource(R.drawable.icn_mobile);
            posBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alert1 != null) {
                        alert1.dismiss();
                    }
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + enquiryListArray.get(index).enquiry_mobile));
                    startActivity(intent);
                }
            });
        }

        if (callType.equalsIgnoreCase("email")) {
            positiveBtnName = "EMAIL NOW";
            title.setText("Email Us Now");
            message.setText("Email Us Now");
            contactDetail.setText(enquiryListArray.get(index).enquiry_email);
            dialogIcon.setImageResource(R.drawable.icn_email);
            posBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alert1 != null) {
                        alert1.dismiss();
                    }
                    makeSalesEnquiry(enquiryListArray.get(index).id, enquiry_type_sales);
                }
            });

        } else if (callType.equalsIgnoreCase("query")) {
            positiveBtnName = "CONFIRM";
            title.setText("Enquiry");
            message.setText(
                    "Our Medical Representative/Customer service officer will contact you shortly by phone.");
            contactDetail.setVisibility(View.GONE);
            dialogIcon.setVisibility(View.GONE);
            posBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alert1 != null) {
                        alert1.dismiss();
                    }
                    makeSalesEnquiry(null, enquiry_type_general);
                }
            });
        }

        posBtn.setText(positiveBtnName);

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
