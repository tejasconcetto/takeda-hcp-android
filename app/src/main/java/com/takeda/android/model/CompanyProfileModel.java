package com.takeda.android.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by microlentsystems on 25/03/18.
 */

public class CompanyProfileModel implements Serializable {

  @SerializedName("response")
  public CompanyResponceModel response = new CompanyResponceModel();


  public class CompanyResponceModel implements Serializable {

    @SerializedName("status")
    public String status;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("data")
    public CompanyDataModel dataUser = new CompanyDataModel();

  }

  public class CompanyDataModel implements Serializable {

    @SerializedName("company_details")
    public CompanyDetailModel companyDetailModel = new CompanyDetailModel();


  }

  public class CompanyDetailModel implements Serializable {

    @SerializedName("id")
    public String id;

    @SerializedName("company_name")
    public String company_name;

    @SerializedName("company_email")
    public String company_email;

    @SerializedName("company_website")
    public String company_website;

    @SerializedName("company_description")
    public String company_description;

    @SerializedName("company_time")
    public String company_time;

    @SerializedName("company_mobile")
    public String company_mobile;

    @SerializedName("company_address")
    public String company_address;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;

  }
}
