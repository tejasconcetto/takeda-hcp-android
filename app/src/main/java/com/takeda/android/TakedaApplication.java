package com.takeda.android;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.takeda.android.model.BannerModel;
import com.takeda.android.model.ProductModel;
import java.util.ArrayList;
import java.util.Collections;

//import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Akshay on 09-Dec-16.
 */

public class TakedaApplication extends Application {


  public ArrayList<BannerModel.BannerDataModel> offerBanner, bannerArray;
  public Double order_amt_limit = 250.0;
  public Double delivery_charges = 25.0;
  public ArrayList<ProductModel.CategoryArrDataModel> categoryArray;
  public ArrayList<ProductModel.ProductsArrDataModel> productListArray;

  private static Context mContext;


  @Override
  public void onCreate() {
    super.onCreate();
    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
    StrictMode.setVmPolicy(builder.build());

    mContext = this;

    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig
        .newBuilder(getApplicationContext())
        .setDownsampleEnabled(true)
        .setBitmapMemoryCacheParamsSupplier(new FrescoCacheParams(activityManager))
        .build();

    Fresco.initialize(getApplicationContext(), imagePipelineConfig);

//        Fresco.initialize(this);

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/helvetica_neue.otf")
//                .setFontAttrId(R.attr.fontPath)
//                .build());
  }

  public static Context getContext() {
    return mContext;
  }

  public void setOfferArray(ArrayList<BannerModel.BannerDataModel> bannerDataArray) {
    System.out.println("bannerDataArray in setOfferArray=========>" + bannerDataArray);
    if (offerBanner == null) {
      offerBanner = new ArrayList<>();
    }
    offerBanner.clear();

    offerBanner = bannerDataArray;

        /*try {
            if (jsonArray == null) return;

            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    offerBanner.add(new BannerModel(jsonArray.getJSONObject(i)));
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }*/
  }

  public ArrayList<BannerModel.BannerDataModel> getOfferArray() {
    if (offerBanner == null) {
      offerBanner = new ArrayList<>();
    }
    return offerBanner;
  }

  public ArrayList<BannerModel.BannerDataModel> getBannerArray() {
    if (offerBanner == null) {
      offerBanner = new ArrayList<>();
    }
    if (bannerArray == null) {
      bannerArray = new ArrayList<>();
    }
    bannerArray.clear();

    for (BannerModel.BannerDataModel banners : offerBanner) {
      if (banners.banner_type.equalsIgnoreCase("Banner")) {
        bannerArray.add(banners);
      }
    }
    return bannerArray;
  }

  public ArrayList<ProductModel.CategoryArrDataModel> getCategoryArray() {
    System.out.println("categoryArray in getCategoryArray======>" + categoryArray);
    if (categoryArray == null) {
      categoryArray = new ArrayList<>();
    }
    return categoryArray;
  }

  public ArrayList<ProductModel.ProductsArrDataModel> getProducts() {
    if (productListArray == null) {
      productListArray = new ArrayList<>();
    }
    return productListArray;
  }

  public void addCategory(ArrayList<ProductModel.CategoryArrDataModel> catArray) {
    try {
      if (categoryArray == null) {
        categoryArray = new ArrayList<>();
      }

      if (productListArray == null) {
        productListArray = new ArrayList<>();
      }

      categoryArray.clear();
      productListArray.clear();

//            categoryArray = catArray;
//            categoryArray.get(0).category_name = "All";
      ProductModel.CategoryArrDataModel categoryModelInit = new ProductModel.CategoryArrDataModel();
//            categoryModelInit.category_name = "All";
//            categoryArray.add(categoryModelInit);

      if (catArray.size() > 0) {
        for (int i = 0; i < catArray.size(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
          if (catArray.get(i).productsData.size() > 0) {
            ProductModel.CategoryArrDataModel categoryModel = new ProductModel.CategoryArrDataModel();
            categoryModel = catArray.get(i);
            categoryArray.add(categoryModel);
            setProductArray(catArray.get(i).productsData, categoryModel);
          }

          System.out
              .println("categoryArray in addCategory======>" + categoryArray.get(i).category_name);
        }
      }
      System.out.println("categoryArray in addCategory======>" + categoryArray);
//            setProductArray(jsonObject.getJSONArray("products"),categoryModel);

//            categoryArray.add("All");

            /*if(jsonArray.length() > 0){
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(jsonObject.getJSONArray("products").length() > 0){
                        CategoryListModel categoryModel = new CategoryListModel(jsonObject);
                        categoryArray.add(categoryModel);
                        setProductArray(jsonObject.getJSONArray("products"),categoryModel);
                    }
                }
            }
*/
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setProductArray(ArrayList<ProductModel.ProductsArrDataModel> productsJSONArray,
      ProductModel.CategoryArrDataModel catModel) {

    try {

      if (productsJSONArray.size() > 0) {
        for (int i = 0; i < productsJSONArray.size(); i++) {
          if (productsJSONArray.get(i).status.equalsIgnoreCase("Active")) {
            ProductModel.ProductsArrDataModel proModel = new ProductModel.ProductsArrDataModel();
            proModel = productsJSONArray.get(i);
            proModel.category_name = catModel.category_name;
            proModel.category_id = catModel.category_id;

            productListArray.add(proModel);


          }

          System.out.println(
              "productListArray in addCategory======>" + productListArray.get(i).product_name);
        }
      }

      System.out.println("productListArray in addCategory======>" + productListArray);

      Collections.sort(productListArray, new SortBasedOnName());
    } catch (Exception e) {
      e.printStackTrace();
    }


        /*try{

            if(productsJSONArray.length() > 0){
                for(int i=0;i<productsJSONArray.length();i++){
                    if(productsJSONArray.getJSONObject(i).getString("status").equalsIgnoreCase("Active")) {
                        productListArray.add(new ProductModel(productsJSONArray.getJSONObject(i),catModel));
                    }
                }
            }

            Collections.sort(productListArray, new SortBasedOnName());
        }

        catch (Exception e){
            e.printStackTrace();
        }*/
  }

//    public void touch() {
//        AppLockManager.getInstance().enableDefaultAppLockIfAvailable(TakedaApplication.this);
//        AppLockManager.getInstance().updateTouch();
//    }
}
