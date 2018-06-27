package com.takeda.android.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by microlentsystems on 19/03/18.
 */

public class LoginModel implements Serializable {

  @SerializedName("response")
  public LoginResponceModel response = new LoginResponceModel();


  public class LoginResponceModel implements Serializable {

    @SerializedName("status")
    public String status;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("data")
    public UserModel dataUser = new UserModel();


    /**
     * @return The status
     */
    public String getStatus() {
      return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
      this.status = status;
    }

    /**
     * @return The statusMessage
     */
    public String getStatusMessage() {
      return statusMessage;
    }

    /**
     * @param statusMessage The statusMessage
     */
    public void setStatusMessage(String statusMessage) {
      this.statusMessage = statusMessage;
    }


  }

}


