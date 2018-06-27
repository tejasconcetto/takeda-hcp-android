package com.takeda.android.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Bharat Gupta on 9/6/2015.
 */
public class EnquiryModel implements Serializable {

  @SerializedName("response")
  public EnquiryResponceModel response = new EnquiryResponceModel();

  public class EnquiryResponceModel implements Serializable {

    @SerializedName("status")
    public String status;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("data")
    public DataModel data = new DataModel();


  }

  public class DataModel implements Serializable {

    @SerializedName("sales_persons")
    public ArrayList<SalesPersonArrDataModel> salesPersonData = new ArrayList<SalesPersonArrDataModel>();

  }

  public class SalesPersonArrDataModel implements Serializable {

    @SerializedName("sales_id")
    public String id;

    @SerializedName("sales_name")
    public String sales_name;

    @SerializedName("sales_title")
    public String sales_title;

    @SerializedName("sales_email")
    public String enquiry_email;

    @SerializedName("telephone")
    public String enquiry_mobile;

    @SerializedName("sales_image")
    public String salesImage;
  }


    /*public String id,sales_name,enquiry_email,enquiry_mobile, sales_title;

    public EnquiryModel(JSONObject jsonObject){

        if(jsonObject == null) return;

        try{
            id = jsonObject.getString("sales_id");
            sales_name = jsonObject.getString("sales_name").trim();
            sales_title = jsonObject.getString("sales_title").trim();
            enquiry_email = jsonObject.getString("sales_email").trim();
            enquiry_mobile = jsonObject.getString("telephone").trim();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }*/
}
