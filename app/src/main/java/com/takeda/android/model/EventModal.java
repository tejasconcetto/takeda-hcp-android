package com.takeda.android.model;

import com.google.gson.annotations.SerializedName;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Bharat Gupta on 9/6/2015.
 */
public class EventModal implements Serializable {

  @SerializedName("response")
  public EventResponceModel response = new EventResponceModel();

  public class EventResponceModel implements Serializable {

    @SerializedName("status")
    public String status;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("data")
    public DataModel data = new DataModel();


  }

  public class DataModel implements Serializable {

    @SerializedName("events")
    public ArrayList<EventsArrDataModel> eventsData = new ArrayList<EventsArrDataModel>();

    @SerializedName("bookmark_events")
    public String bookmark_events;

  }

  public static class EventsArrDataModel implements Serializable {

    @SerializedName("event_id")
    public String id;

    @SerializedName("event_title")
    public String title;

    @SerializedName("event_address")
    public String Address;

    @SerializedName("event_description")
    public String description;

    @SerializedName("event_date")
    public String event_date;

    @SerializedName("event_period")
    public String period;

    @SerializedName("event_organiser")
    public String organiser_name;

    @SerializedName("interested")
    public Boolean interested;

    @SerializedName("event_url")
    public String click_url;

    @SerializedName("bookmarked")
    public Boolean bookmarked;

    @SerializedName("takeda_event")
    public Boolean takeda_event;

    @SerializedName("calViewDate")
    public CalendarDay calViewDate;


    public Boolean is_selected = false;

  }



   /* public String id, title, Address, description, start_date, period, organiser_name, click_url;
    public boolean is_favourite = false, is_selected = false, is_Takeda_event = false, is_interested = false;
    public JSONObject jsonObject;
    public CalendarDay calViewDate;

    public EventModal(JSONObject jsonObject, CalendarDay calDate){

        if(jsonObject == null) return;

        try{
            this.calViewDate = calDate;
            this.jsonObject = jsonObject;
            id = jsonObject.getString("event_id");
            title = jsonObject.getString("event_title");
            Address = jsonObject.getString("event_address");
            description = jsonObject.getString("event_description");
            start_date = ""+CalendarDay.from(AppDelegate.getDate(jsonObject.getLong("event_date"))).getDay();
            period = "Event Period : "+jsonObject.getString("event_period");
            organiser_name = "Event Organiser : "+jsonObject.getString("event_organiser");
            is_interested = jsonObject.getBoolean("interested");

            click_url = jsonObject.getString("event_url");
            if(!URLUtil.isValidUrl(click_url)){
                click_url = "";
            }

            if(jsonObject.getBoolean("bookmarked"))
                is_favourite = true;

            is_Takeda_event = jsonObject.getString("takeda_event").equalsIgnoreCase("Yes") ? true : false;
        }

        catch (Exception e){
            Log.d("ExceptionEventModel",e.toString());
            e.printStackTrace();
        }
    }*/
}
