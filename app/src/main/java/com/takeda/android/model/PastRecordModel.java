package com.takeda.android.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Bharat Gupta on 9/6/2015.
 */
public class PastRecordModel implements Serializable {

  @SerializedName("response")
  public PuchaseResponceModel response = new PuchaseResponceModel();

  public class PuchaseResponceModel implements Serializable {

    @SerializedName("status")
    public String status;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("data")
    public DataModel data = new DataModel();


  }

  public class DataModel implements Serializable {

    @SerializedName("purchase_history")
    public ArrayList<PurchaseArrDataModel> purchaseData = new ArrayList<PurchaseArrDataModel>();

  }

  public static class PurchaseArrDataModel implements Serializable {

    @SerializedName("purchase_id")
    public String purchase_id;

    @SerializedName("purchase_date")
    public String purchase_date;

    @SerializedName("product")
    public ProductNameModel products = new ProductNameModel();

    @SerializedName("sales")
    public String price;

    @SerializedName("bonus")
    public String bonus;

    @SerializedName("customer_id")
    public String customerId;

    @SerializedName("district")
    public String district;

    @SerializedName("event_period")
    public String period;

    @SerializedName("quantity")
    public String quantity;

    public String row_type;

    public boolean is_selected = false;

  }

  public static class ProductNameModel implements Serializable {

    @SerializedName("product_name")
    public String product_name;

  }


    /*public String id,products,price,quantity, row_type, product_id, bonus;
    public Date purchase_date;
    public boolean is_selected = false;

    public PastRecordModel(JSONObject jsonObject, String row_type){

        if(jsonObject == null) return;

        try{
            this.row_type = row_type;

            if(row_type.equalsIgnoreCase(Params.type_row)){
                id = jsonObject.getString("purchase_id");
                purchase_date = CalendarDay.from(AppDelegate.getDate(jsonObject.getLong("purchase_date"))).getDate();
                products = jsonObject.getJSONObject("product").getString("product_name");
//                product_id = jsonObject.getJSONObject("product").getString("product_id");
                price = jsonObject.getString("sales");
                bonus = jsonObject.getString("bonus");
                quantity = jsonObject.getString("quantity");
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }*/

}