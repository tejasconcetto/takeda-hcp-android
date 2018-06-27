package com.takeda.android.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;


public class CustomerRecordModel {

  @SerializedName("response")
  @Expose
  private Response response;

  public Response getResponse() {
    return response;
  }

  public void setResponse(Response response) {
    this.response = response;
  }

  public class Data {

    @SerializedName("result")
    @Expose
    private Result result;

    public Result getResult() {
      return result;
    }

    public void setResult(Result result) {
      this.result = result;
    }

  }

  public class Response {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("statusMessage")
    @Expose
    private String statusMessage;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

    public String getStatusMessage() {
      return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
      this.statusMessage = statusMessage;
    }

    public Data getData() {
      return data;
    }

    public void setData(Data data) {
      this.data = data;
    }

  }

  public class Result {

    @SerializedName("customer_ids")
    @Expose
    private ArrayList<String> customerIds = null;
    @SerializedName("districts")
    @Expose
    private ArrayList<String> districts = null;

    public ArrayList<String> getCustomerIds() {
      return customerIds;
    }

    public void setCustomerIds(ArrayList<String> customerIds) {
      this.customerIds = customerIds;
    }

    public ArrayList<String> getDistricts() {
      return districts;
    }

    public void setDistricts(ArrayList<String> districts) {
      this.districts = districts;
    }
  }
}


