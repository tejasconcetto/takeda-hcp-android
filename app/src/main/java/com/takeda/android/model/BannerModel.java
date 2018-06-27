package com.takeda.android.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Bharat Gupta on 9/6/2015.
 */
public class BannerModel implements Serializable {

//    public String id,banner_type,name,description,effective_date,expiry_date,status,imageURL;
//    public boolean accept_status;

  @SerializedName("response")
  public BannerResponceModel response = new BannerResponceModel();

  public class BannerResponceModel implements Serializable {

    @SerializedName("status")
    public String status;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("data")
    public DataModel data = new DataModel();


  }

  public class DataModel implements Serializable {

    @SerializedName("banners")
    public ArrayList<BannerDataModel> bannerData = new ArrayList<BannerDataModel>();

  }

  public class BannerDataModel implements Serializable {

    @SerializedName("banner_id")
    public String id;

    @SerializedName("banner_type")
    public String banner_type;

    @SerializedName("banner_name")
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("effective_date")
    public String effective_date;

    @SerializedName("expiry_date")
    public String expiry_date;

    @SerializedName("status")
    public String status;

    @SerializedName("banner_image")
    public String imageURL;

    @SerializedName("special_offer_accepted")
    public boolean accept_status;
  }





    /*public BannerModel(JSONObject jsonObject){

        if(jsonObject == null) return;

        try{
            id = jsonObject.getString("banner_id");
            banner_type = jsonObject.getString("banner_type");
            name = jsonObject.getString("banner_name");
            description = AppDelegate.checkString(jsonObject.getString("description"));
//            effective_date = jsonObject.getString("effective_date");
//            expiry_date = jsonObject.getString("expiry_date");

            effective_date = ""+new SimpleDateFormat("dd-MM-yyyy").format(AppDelegate.getDate(jsonObject.getLong("effective_date")));
            expiry_date = ""+new SimpleDateFormat("dd-MM-yyyy").format(AppDelegate.getDate(jsonObject.getLong("expiry_date")));
            status = jsonObject.getString("status");
            imageURL = jsonObject.getString("banner_image");
            accept_status = jsonObject.getBoolean("special_offer_accepted");
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }*/
}
