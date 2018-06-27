package com.takeda.android.model.request_model;

/**
 * Created by microlentsystems on 20/03/18.
 */

public class LoginRequestModel {

  private String username;
  private String password;

  public LoginRequestModel(String username, String password) {
    this.username = username;
    this.password = password;

  }

  public String getUserName() {
    return username;
  }

  public void setUserName(String username) {
    this.username = username;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
