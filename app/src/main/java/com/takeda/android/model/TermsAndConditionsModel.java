package com.takeda.android.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by microlentsystems on 25/03/18.
 */

public class TermsAndConditionsModel implements Serializable {

  @SerializedName("response")
  public TermsResponceModel response = new TermsResponceModel();


  public class TermsResponceModel implements Serializable {

    @SerializedName("status")
    public String status;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("data")
    public TermsDataModel dataUser = new TermsDataModel();

  }

  public class TermsDataModel implements Serializable {


    @SerializedName("terms_and_conditions")
    public String terms_and_conditions;

  }

}
