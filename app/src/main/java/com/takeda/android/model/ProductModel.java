package com.takeda.android.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Bharat Gupta on 9/6/2015.
 */
public class ProductModel implements Serializable {

  @SerializedName("response")
  public ProductResponceModel response = new ProductResponceModel();

  public class ProductResponceModel implements Serializable {

    @SerializedName("status")
    public String status;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("data")
    public DataModel data = new DataModel();


  }

  public class DataModel implements Serializable {

    @SerializedName("categories")
    public ArrayList<CategoryArrDataModel> catData = new ArrayList<CategoryArrDataModel>();

    @SerializedName("purchase_history_flag")
    public int purchaseHistoryFlag;
  }

  public static class CategoryArrDataModel implements Serializable {

    @SerializedName("category_id")
    public String category_id;

    @SerializedName("category_name")
    public String category_name;

    @SerializedName("products")
    public ArrayList<ProductsArrDataModel> productsData = new ArrayList<ProductsArrDataModel>();

  }

  public static class ProductsArrDataModel implements Serializable {

    @SerializedName("product_id")
    public String id;

    @SerializedName("product_name")
    public String product_name;

    @SerializedName("description")
    public String description;

    @SerializedName("drug_class")
    public String drug_class;

    @SerializedName("indication")
    public String indication;

    @SerializedName("ingredient")
    public String ingredient;

    @SerializedName("reference")
    public String reference;

    @SerializedName("dosage")
    public String dosage;

    @SerializedName("specialty_title")
    public String spl_name;

    @SerializedName("product_detail_url")
    public String product_detail_url;

    @SerializedName("product_pdf_url")
    public String product_pdf_url;


    @SerializedName("quantity")
    public String quantity;

    @SerializedName("status")
    public String status;

    @SerializedName("category_id")
    public String category_id;

    @SerializedName("category_name")
    public String category_name;

    @SerializedName("product_images")
    public ArrayList<ProductImageUrlModel> product_images = new ArrayList<ProductImageUrlModel>();


  }

  public static class ProductImageUrlModel implements Serializable {

    @SerializedName("product_image_url")
    public String product_image_url;

  }

    /*public String id,cat_id,spl_id,spl_name,product_name,cat_name, description, product_image_url, product_all_img;
    public String ingredient,drug_class,dosage,indication, reference, product_url, pdf_url;

    public ArrayList<String> product_images = new ArrayList<>();
    public CategoryListModel catModel;
    public JSONObject jsonObject;

    public ProductModel(JSONObject jsonObject,CategoryListModel catModel){

        if(jsonObject == null) return;

        try{
            this.jsonObject = jsonObject;
            this.catModel = catModel;
            id = AppDelegate.checkString(jsonObject.getString("product_id"));
            product_name = AppDelegate.checkString(jsonObject.getString("product_name"));
            description = AppDelegate.checkString(jsonObject.getString("description"));
            drug_class = AppDelegate.checkString(jsonObject.getString("drug_class"));
            indication = AppDelegate.checkString(jsonObject.getString("indication"));
            ingredient = AppDelegate.checkString(jsonObject.getString("ingredient"));
            reference = AppDelegate.checkString(jsonObject.getString("reference"));
            spl_name = AppDelegate.checkString(jsonObject.getJSONObject("specialty").getString("specialty_title"));

            product_url = URLUtil.isValidUrl(jsonObject.getString("product_detail_url_android")) ? jsonObject.getString("product_detail_url").trim() : "";
            pdf_url = "";

            Log.d("URLDetail","Detailed URL - "+product_url);

            if(jsonObject.has("product_pdf_url"))
                pdf_url = URLUtil.isValidUrl(jsonObject.getString("product_pdf_url")) ? jsonObject.getString("product_pdf_url").trim() : "";

            if(catModel != null){
                cat_id = AppDelegate.checkString(catModel.id);
                cat_name = AppDelegate.checkString(catModel.cat_name);
            }

            if(jsonObject.getJSONArray("product_images").length() > 0){
                product_image_url = jsonObject.getJSONArray("product_images").getJSONObject(0).getString("product_image_url");
                for(int i=0;i<jsonObject.getJSONArray("product_images").length();i++){
                    product_images.add(jsonObject.getJSONArray("product_images").getJSONObject(i).getString("product_image_url"));
                }
                product_all_img = jsonObject.getJSONArray("product_images").toString();
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }*/
}
