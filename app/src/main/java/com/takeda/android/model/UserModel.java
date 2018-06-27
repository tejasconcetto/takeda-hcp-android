package com.takeda.android.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by microlentsystems on 20/03/18.
 */

public class UserModel implements Serializable {

  @SerializedName("doctor_id")
  public String doctor_id;

  @SerializedName("doctor_name")
  public String doctor_name;

  @SerializedName("user_name")
  public String user_name;

  @SerializedName("account_number")
  public String account_number;

  @SerializedName("mID")
  public String mID;

  @SerializedName("address")
  public String address;

  @SerializedName("fax_number")
  public String fax_number;

  @SerializedName("email")
  public String email;

  @SerializedName("mobile_number")
  public String mobile_number;

  @SerializedName("term_condition")
  public String term_condition;

  @SerializedName("status")
  public String status;

  @SerializedName("first_login")
  public String first_login;

  @SerializedName("access_token")
  public String access_token;

  @SerializedName("locality")
  public LocalityModel locality = new LocalityModel();

  @SerializedName("specialty")
  public SpecialtyModel specialty = new SpecialtyModel();

  @SerializedName("sector")
  public SectorModel sector = new SectorModel();

  @SerializedName("class")
  public ClassModel classModel = new ClassModel();

  public class LocalityModel implements Serializable {

    @SerializedName("location_id")
    public String location_id;

    @SerializedName("location_name")
    public String location_name;

    public String toJSON() {

      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("location_id", location_id);
        jsonObject.put("location_name", location_name);

        return jsonObject.toString();
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return "";
      }

    }
  }

  public class SpecialtyModel implements Serializable {

    @SerializedName("specialty_id")
    public String specialty_id;

    @SerializedName("specialty_title")
    public String specialty_title;

    public String toJSON() {

      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("specialty_id", specialty_id);
        jsonObject.put("specialty_title", specialty_title);

        return jsonObject.toString();
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return "";
      }

    }
  }

  public class SectorModel implements Serializable {

    @SerializedName("sector_id")
    public String sector_id;

    @SerializedName("sector_name")
    public String sector_name;

    public String toJSON() {

      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("sector_id", sector_id);
        jsonObject.put("sector_name", sector_name);

        return jsonObject.toString();
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return "";
      }

    }
  }

  public class ClassModel implements Serializable {

    @SerializedName("class_id")
    public String class_id;

    @SerializedName("class_name")
    public String class_name;

    public String toJSON() {

      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("class_id", class_id);
        jsonObject.put("class_name", class_name);

        return jsonObject.toString();
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return "";
      }

    }
  }

  public String toJSON() {

    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("doctor_id", doctor_id);
      jsonObject.put("doctor_name", doctor_name);
      jsonObject.put("user_name", user_name);
      jsonObject.put("account_number", account_number);
      jsonObject.put("mID", mID);
      jsonObject.put("address", address);
      jsonObject.put("fax_number", fax_number);
      jsonObject.put("email", email);
      jsonObject.put("mobile_number", mobile_number);
      jsonObject.put("term_condition", term_condition);
      jsonObject.put("status", status);
      jsonObject.put("first_login", first_login);
      jsonObject.put("access_token", access_token);
      jsonObject.put("locality", new JSONObject(locality.toJSON()));
      jsonObject.put("specialty", new JSONObject(specialty.toJSON()));
      jsonObject.put("sector", new JSONObject(sector.toJSON()));
      jsonObject.put("class", new JSONObject(classModel.toJSON()));

      return jsonObject.toString();
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "";
    }

  }

}

