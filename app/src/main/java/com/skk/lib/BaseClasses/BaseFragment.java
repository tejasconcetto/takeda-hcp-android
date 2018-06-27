package com.skk.lib.BaseClasses;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.TakedaApplication;
import com.takeda.android.model.EnquiryModel;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Akshay on 07-Dec-16.
 */

public class BaseFragment extends Fragment {

  View mView;
  private ProgressDialog dialog;
  Context mContext;
  SessionManager session;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mContext = getActivity();

    session = new SessionManager(mContext);

    dialog = new ProgressDialog(mContext);
    dialog.setMessage("Please Wait....");
    dialog.setCancelable(false);
  }


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mView = super.onCreateView(inflater, container, savedInstanceState);
    checkInternet();
    return mView;
  }

  public boolean checkInternet() {
    return ((BaseActivity) getActivity()).checkInternet();
//        showLogs("CheckInternet","mView "+String.valueOf(mView));
//        showLogs("CheckInternet","Coordinator Layout "+String.valueOf(mView.findViewById(R.id.coordinator_layout)));
//
//        CoordinatorLayout layout = (CoordinatorLayout) mView.findViewById(R.id.coordinator_layout);
//        if(!AppDelegate.isConnectingToInternet(getActivity()) && mView != null){
//            Snackbar snackbar = Snackbar
//                    .make(layout, "No Internet Connection!", Snackbar.LENGTH_INDEFINITE)
//                    .setAction("RETRY", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = getActivity().getIntent();
//                            getActivity().finish();
//                            startActivity(intent);
//                        }
//                    });
//            View view = snackbar.getView();
//            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
//            tv.setTextColor(Color.RED);
//            snackbar.show();
//            return false;
//        }
//        return true;
  }

  public static boolean isValidOTP(String string) {
    return string.length() > 5 && string.length() < 6;
  }

  public TakedaApplication getMainApplication() {
    if (getActivity() instanceof BaseActivity) {
      return ((BaseActivity) getActivity()).getMainApplication();
    }
    return null;
  }

  public void showToast(String message) {
    try {
      Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void makeSalesEnquiry(String sales_id, String enquiry_type) {

    if (dialog != null) {
      dialog.show();
    }

    try {
      JSONObject jsonObject = new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON));
      System.out.println("jsonObject======>" + new JSONObject(
          session.getUserDetails().get(SessionManager.KEY_USER_JSON)));

      ApiInterface api = RestAdapterService.createService(ApiInterface.class);

      api.SalesEnquiry(
          new SessionManager(getActivity()).getUserDetails().get(SessionManager.KEY_USER_ID),
          sales_id,
          enquiry_type, jsonObject.getString("access_token"), new Callback<EnquiryModel>() {
            @Override
            public void success(EnquiryModel enquiryModel, Response response) {
              System.out.println("=======success in SalesEnquiry api call=====");
              dialog.dismiss();

              try {
                msgAlertDialog("Enquiry Submitted",
                    "We have received your request and will get in touch with you shortly.");
              } catch (Exception e) {
                e.printStackTrace();
              }
            }

            @Override
            public void failure(RetrofitError error) {
              System.out.println("=======failure in SalesEnquiry api call=====");
              dialog.dismiss();

            }
          });

    } catch (Exception e) {
      e.printStackTrace();
    }

       /* try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("doctor_id",new SessionManager(getActivity()).getUserDetails().get(SessionManager.KEY_USER_ID));
            jsonObject.put("sales_id",sales_id);
            jsonObject.put("enquiry_type",enquiry_type);

            new UserTask().getJsonRequest(new resultInterface() {
                @Override
                public void Success(JSONObject response, String requestCall) {
                    try {
                        msgAlertDialog("Enquiry Submitted","We have received your request and will get in touch with you shortly.");
                    }

                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            },jsonObject,true,getActivity(),mView, Params.request_code_sales_enquiry);
        }

        catch (Exception e){
            e.printStackTrace();
        }*/
  }

  public void showSBar(String Message) {
    try {
      if (getActivity() instanceof BaseActivity) {
        View view = ((BaseActivity) getActivity()).getCoordinatorLayout();
        final Snackbar snackbar = Snackbar.make(view, Message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("CLOSE", new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            snackbar.dismiss();
          }
        });
        View mView = snackbar.getView();
        TextView tv = mView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        TextView action = mView.findViewById(android.support.design.R.id.snackbar_action);
        action.setTextColor(Color.RED);
        snackbar.show();
      }
    } catch (Exception e) {
      Log.d("Exception", "context is null at showing toast.");
      e.printStackTrace();
    }
  }

  public View getCoordintorLayout() {
    return ((BaseActivity) getActivity()).getCoordinatorLayout();
  }

  public void showLogs(String TAG, String message) {
    new SessionManager(getMainApplication()).showLogs(TAG, message);
  }

  public interface OtpResponse {

    void onResult(Object result);
  }

  public Integer layoutsize() {
    WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;
    //int height = size.y;
    return width;
  }


  public void frgmTxn(Fragment frgm, boolean addBackStack, String addBackStackTAG) {
    if (addBackStack) {
      String tag = addBackStackTAG;
      getFragmentManager().beginTransaction().replace(R.id.frame_container, frgm)
          .addToBackStack(tag).commit();
    } else {
      getFragmentManager().beginTransaction().replace(R.id.frame_container, frgm).commit();
    }
  }

  public void showProductsPopup(final Activity mContext, Object objectModel,
      final Runnable runnable) {

    final SessionManager session = new SessionManager(mContext);
    // get prompts.xml view
    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    final View promptView = layoutInflater.inflate(R.layout.order_product_dialog, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(),
        R.style.CustomDialog);
    alertDialogBuilder.setView(promptView);

    final Spinner products = promptView.findViewById(R.id.spinner);
    final EditText productQty = promptView.findViewById(R.id.product_quantity);

    setSpinnerData(promptView, products);

       /* if(objectModel != null && objectModel instanceof PastRecordModel){
            PastRecordModel pastRecord = (PastRecordModel) objectModel;
            for(int i=0; i<getMainApplication().getProducts().size() ; i++){
                ProductModel productModel = getMainApplication().getProducts().get(i);
                showLogs("compareVal","Model Id : "+productModel.id+", PastRecord Id : "+pastRecord.product_id);
                if(productModel.id.equalsIgnoreCase(pastRecord.product_id)){
                    showLogs("compareVal","We got it Model Id : "+productModel.id+", PastRecord Id : "+pastRecord.product_id);
                    products.setSelection(i+1);
                }
            }
            productQty.setText(pastRecord.quantity);
            //           houseNoET.setText(addressModal.);
        }
*/
        /*if(objectModel != null && objectModel instanceof ProductModel){
            ProductModel prodctModel = (ProductModel) objectModel;
            for(int i=0; i<getMainApplication().getProducts().size() ; i++){
                ProductModel productModel = getMainApplication().getProducts().get(i);
                if(productModel.id.equalsIgnoreCase(prodctModel.id)){
                    products.setSelection(i+1);
                }
            }
        }
*/
    final AlertDialog alert1 = alertDialogBuilder.create();
    alert1.setCancelable(false);
    alert1.show();

    promptView.findViewById(R.id.order_product).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        Integer productPosition = products.getSelectedItemPosition();
        if (productPosition < 1) {
          showToast("Please select a product");
        }

        final String quantity = productQty.getText().toString();
        if (quantity.length() == 0) {
          productQty.setError("The field must not be empty");
        }

        if (productPosition >= 1 && quantity.length() > 0) {

                    /*try{
                        ProductModel product = getMainApplication().getProducts().get(productPosition-1);

                        final JSONObject jsonObject = new JSONObject();
                        jsonObject.put("doctor_id",session.getUserDetails().get(SessionManager.KEY_USER_ID));
                        jsonObject.put("product_id",product.id);
                        jsonObject.put("quantity",quantity);

                        new UserTask().getJsonRequest(new resultInterface() {
                            @Override
                            public void Success(JSONObject response, String requestCall) {

                                try{
                                    if(runnable != null){
                                        runnable.run();
                                    }
                                    alert1.dismiss();
                                    msgAlertDialog("Success",response.getString(Params.response_message));
                                }

                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        },jsonObject,true,mContext,promptView,Params.request_code_place_order);
                    }

                    catch (Exception e){
                        e.printStackTrace();
                    }*/
        }
      }
    });

    promptView.findViewById(R.id.crossBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        alert1.dismiss();
      }
    });
  }

  void setSpinnerData(View mView, final Spinner spinner) {
        /*ArrayList<String> productNames = new ArrayList<>();
        new SessionManager(getActivity()).showLogs("ProductSize","Array Size : "+getMainApplication().getProducts().size());
        productNames.add("----Please select a product----");
        for(ProductModel product : getMainApplication().getProducts()){
            productNames.add(product.product_name);
        }
        new SessionManager(getActivity()).showLogs("AreaSize","Array List Size : "+productNames.size());

        ArrayAdapter<String> clientNameAdapter = new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item,
                                                 productNames);
        spinner.setAdapter(clientNameAdapter);

        (mView.findViewById(R.id.spinnerLayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.performClick();
            }
        });
*/
  }

  public void openTandCDialog(final String doc_data[], final BaseFragment.OtpResponse otpResponse,
      final View fragView, final boolean toAccept) throws Exception {
    ((BaseActivity) getActivity()).openTandCDialog(doc_data, otpResponse, fragView, toAccept);
  }

  public void msgAlertDialog(String title, String message) {
    ((BaseActivity) getActivity()).msgAlertDialog(title, message);
  }

  public void checkPermission(Runnable runnable) {
    ((BaseActivity) getActivity()).checkPermission(runnable);
  }

  public View getCoordinatorLayout() {
    return ((BaseActivity) getActivity()).getCoordinatorLayout();
  }
}
