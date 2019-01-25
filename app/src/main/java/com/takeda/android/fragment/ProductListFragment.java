package com.takeda.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.BaseClasses.BaseFragment;
import com.skk.lib.Interfaces.OnEnquiryClick;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.activities.MiscActivity;
import com.takeda.android.adapters.ProductAdapter;
import com.takeda.android.model.ProductModel;

import java.util.ArrayList;

public class ProductListFragment extends BaseFragment implements OnEnquiryClick,
    ProductAdapter.OnCardClickListner {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

  RecyclerView productList;
  Context mContext;
  Handler mHandler;
  ProductAdapter productAdapter;
  ArrayList<ProductModel.ProductsArrDataModel> productListArray = new ArrayList<>();
  String categoryName;
  View mView;
  ImageView filterButton;
  SessionManager session;
  String categoryId;
  int selectedPos = 0;
  Spinner categorySpinner;
  TextView titleText;
  boolean spinner_status = false;

//    ProductAdapter.OnCardClickListner listner;    /** by johnny**/


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getActivity();
    session = new SessionManager(mContext);

    //fresco initialization
    //    Fresco.initialize(mContext);   /** by johnny**/
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_product, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initView(view);
  }

  void initView(View view) {
    try {

      System.out.println(
          "---------------------------------------------------------Init view---------------------");

      mView = view;
      productList = view.findViewById(R.id.productList);
      categorySpinner = view.findViewById(R.id.spinner_new);
      titleText = view.findViewById(R.id.titleTV);
      filterButton = view.findViewById(R.id.sort_icon);

      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
      productList.setLayoutManager(layoutManager);

      productList.addItemDecoration(
          new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

      productList.setHasFixedSize(true);
      productList.setItemAnimator(new DefaultItemAnimator());
      //productList.getRecycledViewPool().setMaxRecycledViews(1,0);  // added my johnny

      mView.findViewById(R.id.order_more_button).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          showProductsPopup(getActivity(), null, new Runnable() {
            @Override
            public void run() {
              mHandler.sendEmptyMessage(1);
            }
          });
        }
      });

      setHandler();
      //productListArray = getMainApplication().getProducts();
      categoryId = getMainApplication().getCategoryArray().get(0).category_id;
//            categoryName = getMainApplication().getCategoryArray().get(0).category_name;
      setSpinnerData();
      //setProductData(getMainApplication().getCategoryArray().get(0).id,"byCategory");
      if (getMainApplication().getProducts() != null) {
        productListArray.clear();
        productListArray.addAll(getMainApplication().getProducts());
      }
      //showLogs("ProductListing","Array size : "+String.valueOf(productListArray.size()));
      mHandler.sendEmptyMessage(1);

      (mView.findViewById(R.id.backArrow)).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          getActivity().onBackPressed();
        }
      });
      categorySpinner.setVisibility(View.GONE);

//            categorySpinner.setOnItemSelectedListener(this);

            /*productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try{
                        ((MiscActivity)getActivity()).selectedId = R.id.productList;
                        ((MiscActivity)getActivity()).headerToolBar.setVisibility(View.VISIBLE);
                        Bundle bundle = new Bundle();
//                        bundle.putString("product",productListArray.get(position).jsonObject.toString());
//                        bundle.putString("category",productListArray.get(position).cat_name);
                        bundle.putSerializable("product",getMainApplication().getProducts().get(position));
                        bundle.putString("category",getMainApplication().getProducts().get(position).category_name);
                        ProductDetailsFragment productDetailsFrgm = new ProductDetailsFragment();
                        productDetailsFrgm.setArguments(bundle);
                        frgmTxn(productDetailsFrgm,true,null);
                    }

                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });*/

      filterButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          System.out.println("========click on filter button==========");
          System.out
              .println("categorySpinner.getVisibility()==========" + categorySpinner.isShown());
          categorySpinner.setVisibility(View.VISIBLE);

          System.out
              .println("categorySpinner.getVisibility()==========" + categorySpinner.isShown());
          spinner_status = true;
          categorySpinner.performClick();


        }
      });

      categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
          showLogs("SpinnerStatus", "Status - " + spinner_status);
          System.out.println("position=======>" + position);
          if (position == 0) {
            System.out.println("select All");

            setTitleText("Products");

            if (productListArray == null) {
              productListArray = new ArrayList<>();
            }
            productListArray.clear();

            for (ProductModel.ProductsArrDataModel product : getMainApplication().getProducts()) {

              productListArray.add(product);

            }

            System.out.println("productListArray in onItemSelected========>" + productListArray);

            productAdapter = new ProductAdapter((BaseActivity) getActivity(), productListArray,
                categoryName, ProductListFragment.this);
            productList.setAdapter(productAdapter);
            productAdapter.setOnCardClickListner(ProductListFragment.this);

//                        categorySpinner.setVisibility(View.GONE);
          } else {
//            if (spinner_status) {
            setTitleText(getMainApplication().getCategoryArray().get(position - 1).category_name);
            categoryId = getMainApplication().getCategoryArray().get(position - 1).category_id;
            setProductData(getMainApplication().getCategoryArray().get(position - 1).category_id,
                "byCategory");
//                        categorySpinner.setVisibility(View.GONE);
//            }
          }
          spinner_status = true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
          if (spinner_status) {
            showLogs("SpinnerSelect", "onNothingselectedcalled");
            categorySpinner.setVisibility(View.GONE);
          }
          spinner_status = true;
        }
      });

//            (mView.findViewById(R.id.spinner_title_btn)).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    categorySpinner.setVisibility(View.VISIBLE);
//                    categorySpinner.performClick();
//                }
//            });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void OnCardClicked(View view, int position) {
    Log.d("OnClick", "Card Position" + position);

    try {
      ((MiscActivity) getActivity()).selectedId = R.id.productList;
      ((MiscActivity) getActivity()).headerToolBar.setVisibility(View.VISIBLE);
      Bundle bundle = new Bundle();
//                        bundle.putString("product",productListArray.get(position).jsonObject.toString());
//                        bundle.putString("category",productListArray.get(position).cat_name);
      bundle.putSerializable("product", getMainApplication().getProducts().get(position));
      bundle.putString("category", getMainApplication().getProducts().get(position).category_name);
      ProductDetailsFragment productDetailsFrgm = new ProductDetailsFragment();
      productDetailsFrgm.setArguments(bundle);
      frgmTxn(productDetailsFrgm, true, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Override
  public void onResume() {
    super.onResume();
  }

  /**
   * added by johnny
   **/
  @Override
  public void onDestroyView() {
    System.out.println(
        "---------------------------------------------------------Set View Adapter null---------------------");
    super.onDestroyView();
    productListArray.clear();
    productListArray = null;
    //productAdapter

    productList.invalidate();
    productList.setAdapter(null);
    productList = null;

  }


  void setEmptyLayout() {

    if (productListArray.size() > 0) {
      mView.findViewById(R.id.empty_cart_list_layout).setVisibility(View.GONE);
      mView.findViewById(R.id.mainLayoutList).setVisibility(View.VISIBLE);
    } else if (productListArray.size() == 0) {
      mView.findViewById(R.id.empty_cart_list_layout).setVisibility(View.VISIBLE);
      mView.findViewById(R.id.mainLayoutList).setVisibility(View.GONE);
    }

    ((ImageView) mView.findViewById(R.id.empty_data_image)).setImageResource(R.drawable.inc_cart);
    ((TextView) mView.findViewById(R.id.empty_data_text))
        .setText("Please fill your cart with fresh fruits and vegetables.");
    ((TextView) mView.findViewById(R.id.empty_button)).setText("Add Products");
    mView.findViewById(R.id.empty_button).setVisibility(View.VISIBLE);
  }

  private void setHandler() {
    mHandler = new Handler() {
      @Override
      public void dispatchMessage(Message msg) {
        super.dispatchMessage(msg);
        if (msg.what == 1) {
          if (productAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
              @Override
              public void run() {
                productAdapter.notifyDataSetChanged();
                productList.invalidate();
              }
            });
          } else {
            ArrayList<ProductModel.ProductsArrDataModel> prodNewArray = new ArrayList<>();

            productAdapter = new ProductAdapter((BaseActivity) getActivity(), productListArray,
                categoryName, ProductListFragment.this);
            productAdapter.setOnCardClickListner(ProductListFragment.this);
            productList.setAdapter(productAdapter);


          }
          setEmptyLayout();
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

    posBtn.setText(positiveBtnName);

    final AlertDialog alert1 = alertDialogBuilder.create();
    alert1.setCancelable(false);
    alert1.show();

    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        alert1.dismiss();
      }
    });
  }

  void setSpinnerData() {
    ArrayList<String> categoryNames = new ArrayList<>();
    categoryNames.add("All");
    System.out.println(
        "getMainApplication().getCategoryArray().size()========>" + getMainApplication()
            .getCategoryArray().size());
    for (int i = 0; i < getMainApplication().getCategoryArray().size(); i++) {
      ProductModel.CategoryArrDataModel categoryModel = getMainApplication().getCategoryArray()
          .get(i);
      categoryNames.add(categoryModel.category_name);
      System.out.println("categoryModel========>" + categoryModel);
      System.out.println("categoryModel.category_id========>" + categoryModel.category_id);
      System.out.println("categoryId======>" + categoryId);
      System.out.println("categoryModel.category_name======>" + categoryModel.category_name);
      if (categoryModel.category_name.equalsIgnoreCase("All")) {
        selectedPos = i;
      } else {
        if (categoryModel.category_id.equalsIgnoreCase(categoryId)) {
          selectedPos = i;
        }
      }
    }

    ArrayAdapter<String> clientNameAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_row_item,
        categoryNames);
    categorySpinner.setAdapter(clientNameAdapter);
//        categorySpinner.setSelected(false);
//        categorySpinner.setSelection(0,true);
    //setTitleText(getMainApplication().getCategoryArray().get(selectedPos).cat_name);

//        categorySpinner.setOnItemClickListener(this);

//        (mView.findViewById(R.id.header_layout)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                categorySpinner.performClick();
//            }
//        });*/
  }

  void setProductData(String searchParam, String listingType) {

    //listingType = "byCategory"
    //listingType = "byName"

    if (productListArray == null) {
      productListArray = new ArrayList<>();
    }
    productListArray.clear();

    if (listingType.equalsIgnoreCase("byCategory")) {
      for (ProductModel.CategoryArrDataModel category : getMainApplication().getCategoryArray()) {
        if (category.category_id.equalsIgnoreCase(searchParam)) {
          setTitleText(category.category_name);
          break;
        }
      }

      for (ProductModel.ProductsArrDataModel product : getMainApplication().getProducts()) {
        if (product.category_id.equalsIgnoreCase(searchParam)) {
          productListArray.add(product);
        }
      }
        /*    for(CategoryListModel category : getMainApplication().getCategoryArray()){
                if(category.id.equalsIgnoreCase(searchParam)){
                    setTitleText(category.cat_name);
                    break;
                }
            }

            for(ProductModel product : getMainApplication().getProducts()){
                if(product.cat_id.equalsIgnoreCase(searchParam)){
                    productListArray.add(product);
                }
            }
        */
    } else if (listingType.equalsIgnoreCase("byName")) {
      for (ProductModel.ProductsArrDataModel product : getMainApplication().getProducts()) {
//                showLogs("SearchParam","Param : "+searchParam+", Keywords : "+product.str_Keywords);
////                if(product.product_cat_id.equalsIgnoreCase(categoryId) &&
////                        product.str_Keywords.toLowerCase().contains(searchParam.toLowerCase())){
//                if(product.str_Keywords.toLowerCase().contains(searchParam.toLowerCase())){
//                    productListArray.add(product);
//                }
      }
    }

    showLogs("ProductListing", "Array size : " + String.valueOf(productListArray.size()));
    mHandler.sendEmptyMessage(1);
//        isFirstTime = false;
  }

  public void setTitleText(String title) {
    titleText.setText(title);
  }

    /*@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



    }*/

    /*@Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }*/
}
