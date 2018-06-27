package com.takeda.android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bharat Gupta on 9/6/2015.
 */
public class CategoryListModel {

  @SerializedName("category_id")
  private String id;

  @SerializedName("category_name")
  private String cat_name;

    /*public String id,cat_name;

    public CategoryListModel(JSONObject jsonObject){

        if(jsonObject == null) return;

        try{
            id = jsonObject.getString("category_id");
            cat_name = jsonObject.getString("category_name");
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }*/
}
