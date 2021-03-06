package com.takeda.android.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.Interfaces.OnBookMarkClick;
import com.skk.lib.utils.AppDelegate;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.model.EventModal;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;
import java.util.ArrayList;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bharat Gupta on 9/5/2015.
 */
public class EventAdapter extends BaseAdapter {

  private BaseActivity activity;
  private ArrayList<EventModal.EventsArrDataModel> eventList;
  private static LayoutInflater inflater = null;
  private OnBookMarkClick clickListen;
  public static String fav = "Favourite", not_fav = "Unfavourite";
  AlertDialog alert1;

  private ProgressDialog dialog;

  public EventAdapter(BaseActivity a, ArrayList<EventModal.EventsArrDataModel> eventList,
      OnBookMarkClick markClick) {
    activity = a;
    clickListen = markClick;
    this.eventList = eventList;
    inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    dialog = new ProgressDialog(activity);
    dialog.setMessage("Please Wait....");
    dialog.setCancelable(false);

  }

  @Override
  public int getCount() {
    return eventList.size();
  }

  @Override
  public Object getItem(int pos) {
    return pos;
  }

  @Override
  public long getItemId(int id) {
    return id;
  }

  public static class ViewHolder {

    public RelativeLayout selected_layout, not_selected_layout;
    public TextView tv_SelectedTitle, tv_NotSelectedTitle, tv_SelectedDate, tv_NotSelectedDate;
    public TextView tv_Organiser_not_selected, tv_period_not_selected, tv_Event_Description_not_selected, tv_More_register_not_selected;
    public TextView tv_Organiser_selected, tv_period_selected, tv_Event_Description_selected, tv_More_register_selected;
    public ImageView img_FavSelected, img_FavNotSelected;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {

    View vi = convertView;
    final ViewHolder holder;

    if (convertView == null) {

      /****** Inflate tabitem.xml file for each row ( productquantityDefined below ) *******/
      vi = inflater.inflate(R.layout.event_row, null);

      /****** View Holder Object to contain tabitem.xml file elements ******/
      holder = new ViewHolder();
      holder.selected_layout = vi.findViewById(R.id.selected_layout);
      holder.not_selected_layout = vi.findViewById(R.id.not_selected_layout);
      holder.tv_SelectedDate = vi.findViewById(R.id.event_selected_date);
      holder.tv_NotSelectedDate = vi.findViewById(R.id.event_date);
      holder.tv_SelectedTitle = vi.findViewById(R.id.event_title_selected);
      holder.tv_NotSelectedTitle = vi.findViewById(R.id.event_title_not_selected);
      holder.tv_Event_Description_not_selected = vi.findViewById(R.id.event_description);
      holder.tv_Event_Description_selected = vi.findViewById(R.id.event_selected_description);
      holder.tv_period_not_selected = vi.findViewById(R.id.event_period);
      holder.tv_period_selected = vi.findViewById(R.id.event_selected_period);
      holder.tv_Organiser_not_selected = vi.findViewById(R.id.event_organiser_name);
      holder.tv_Organiser_selected = vi.findViewById(R.id.event_selected_organiser_name);
      holder.img_FavSelected = vi.findViewById(R.id.favourite_icon_selected);
      holder.img_FavNotSelected = vi.findViewById(R.id.favourite_icon_not_selected);
      holder.tv_More_register_selected = vi.findViewById(R.id.more_register_selected_text);
      holder.tv_More_register_not_selected = vi.findViewById(R.id.more_register_text);

      /************  Set holder with LayoutInflater ************/
      vi.setTag(holder);
    } else {
      holder = (ViewHolder) vi.getTag();
    }

    // to set data from product class otherwise it will show temperary data stored in respective xml
    if (eventList.size() > 0) {
      final EventModal.EventsArrDataModel eventListModel = eventList.get(position);

//            String[] dateParts = eventListModel.start_date.split("-");
//            Log.d("SplittedDate","Date Field : "+eventListModel.start_date+", Date : "+String.valueOf(dateParts));

      holder.tv_SelectedDate.setText(eventListModel.event_date);
      holder.tv_NotSelectedDate.setText(eventListModel.event_date);
      holder.tv_SelectedTitle.setText(eventListModel.title);
      holder.tv_NotSelectedTitle.setText(eventListModel.title);
      holder.tv_Event_Description_not_selected.setText(eventListModel.description);
      holder.tv_Event_Description_selected.setText(eventListModel.description);
      holder.tv_Organiser_not_selected.setText(eventListModel.organiser_name);
      holder.tv_Organiser_selected.setText(eventListModel.organiser_name);
      holder.tv_period_selected.setText(eventListModel.period);
      holder.tv_period_not_selected.setText(eventListModel.period);

      System.out.println("eventListModel.id========>" + eventListModel.id);
      System.out.println("eventListModel.start_date========>" + eventListModel.event_date);

      if (!URLUtil.isValidUrl(eventListModel.click_url)) {
        System.out.println("eventListModel.is_interested ========>" + eventListModel.interested);
        String tViewText = eventListModel.interested ? "Acknowledged" : "Register";
        holder.tv_More_register_not_selected.setText(tViewText);
        holder.tv_More_register_selected.setText(tViewText);
      } else {
        holder.tv_More_register_not_selected.setText("More Info...");
        holder.tv_More_register_selected.setText("More Info...");
      }

      if (eventListModel.is_selected) {
        holder.selected_layout.setVisibility(View.VISIBLE);
        holder.not_selected_layout.setVisibility(View.GONE);
      } else if (!eventListModel.is_selected) {
        holder.selected_layout.setVisibility(View.GONE);
        holder.not_selected_layout.setVisibility(View.VISIBLE);
      }

      Log.d("EventAdapter", "Pos:" + position + ", isFav : " + eventListModel.bookmarked);
      if (eventListModel.bookmarked) {
        holder.img_FavNotSelected
            .setImageDrawable(AppDelegate.getDrawable(activity, R.drawable.favourite_icon));
        holder.img_FavSelected
            .setImageDrawable(AppDelegate.getDrawable(activity, R.drawable.favourite_icon_white));
      } else if (!eventListModel.bookmarked) {
        holder.img_FavNotSelected
            .setImageDrawable(AppDelegate.getDrawable(activity, R.drawable.favourite_icon_not));
        holder.img_FavSelected.setImageDrawable(
            AppDelegate.getDrawable(activity, R.drawable.favourite_icon_not_white));
      }

      holder.img_FavSelected.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          setFavNotFav(eventListModel, position);
        }
      });

      holder.img_FavNotSelected.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          setFavNotFav(eventListModel, position);
        }
      });

      holder.tv_More_register_selected.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          clickFunction(position);
        }
      });

      holder.tv_More_register_not_selected.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          clickFunction(position);
        }
      });
    }
    return vi;
  }

  void clickFunction(final int position) {
    if (URLUtil.isValidUrl(eventList.get(position).click_url)) {
      System.out.println("Click on more information");

      LayoutInflater layoutInflater = LayoutInflater.from(activity);
      final View promptView = layoutInflater.inflate(R.layout.enquiry_dialog, null);
      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity,
          R.style.CustomDialog);
      alertDialogBuilder.setView(promptView);

      final Button posBtn = promptView.findViewById(R.id.posBtn);
      final Button negBtn = promptView.findViewById(R.id.negBtn);
      final ImageView dialogIcon = promptView.findViewById(R.id.dialogIcon);
      final TextView contactDetail = promptView.findViewById(R.id.contactDetail);
      final TextView message = promptView.findViewById(R.id.contactMsg);
      final TextView title = promptView.findViewById(R.id.dialog_title);

      title.setText("Takeda");
      title.setGravity(Gravity.CENTER);

      message.setText("This will open the link in Google Chrome. Do you wish to continue?");
      message.setGravity(Gravity.CENTER);

      promptView.findViewById(R.id.email_details_layout).setVisibility(View.GONE);

      dialogIcon.setImageResource(R.drawable.icn_mobile);
      posBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          alert1.dismiss();
          try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(eventList.get(position).click_url));
            activity.startActivity(i);
          } catch (Exception e) {
            activity.showSBar("No app found to open link.");
          }

        }
      });
      posBtn.setText("YES");
      negBtn.setText("NO");

      alert1 = alertDialogBuilder.create();
      alert1.setCancelable(false);
      alert1.show();

      negBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          alert1.dismiss();
        }
      });


    } else {
      activity.showLogs("EventIntStatus", "Adapter - " + eventList.get(position).interested);
      String interestedStr = eventList.get(position).interested ? "Notinterested" : "Interested";
      if (clickListen != null) {
        clickListen.OnRegiserOrMoreClick(position, interestedStr);
      }
    }
  }

  void setFavNotFav(EventModal.EventsArrDataModel eventListModel, int position) {
    if (eventListModel.bookmarked) {
      clickListen.OnRowClick(position, not_fav);
    } else if (!eventListModel.bookmarked) {
      clickListen.OnRowClick(position, fav);
    }
  }

  Integer layoutsize() {
    WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;
    //int height = size.y;
    return width;
  }

  void openDialog(final int position, final String interestedStatus, final boolean intStatus) {

    LayoutInflater layoutInflater = LayoutInflater.from(activity);
    final View promptView = layoutInflater.inflate(R.layout.enquiry_dialog, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity,
        R.style.CustomDialog);
    alertDialogBuilder.setView(promptView);

    final Button posBtn = promptView.findViewById(R.id.posBtn);
    final Button negBtn = promptView.findViewById(R.id.negBtn);
    final ImageView dialogIcon = promptView.findViewById(R.id.dialogIcon);
    final TextView contactDetail = promptView.findViewById(R.id.contactDetail);
    final TextView message = promptView.findViewById(R.id.contactMsg);
    final TextView title = promptView.findViewById(R.id.dialog_title);

    title.setText("Takeda");

    if (interestedStatus.toLowerCase().contains("not")) {
      message.setText("Do you want to unregister for the event?");
    } else {
      message.setText("Do you want more information for this event?");
    }
    promptView.findViewById(R.id.email_details_layout).setVisibility(View.GONE);

    dialogIcon.setImageResource(R.drawable.icn_mobile);
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (dialog != null) {
          dialog.show();
        }

        try {
          ApiInterface api = RestAdapterService.createService(ApiInterface.class);

          api.InterestedEvent(
              new SessionManager(activity).getUserDetails().get(SessionManager.KEY_USER_ID),
              eventList.get(position).id, interestedStatus, new JSONObject(
                  new SessionManager(activity).getUserDetails().get(SessionManager.KEY_USER_JSON))
                  .getString("access_token"),
              new Callback<EventModal>() {
                @Override
                public void success(EventModal eventModal, Response response) {

                  System.out.println("=======success in InterestedEvent api call=====");

                  try {
                    eventList.get(position).interested = intStatus;
                    if (alert1 != null) {
                      alert1.dismiss();
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }

                }

                @Override
                public void failure(RetrofitError error) {

                  System.out.println("=======failure in InterestedEvent api call=====");

                }
              });
        } catch (Exception e) {
          e.printStackTrace();
        }


                /*old code
                try{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("event_id",eventList.get(position).id);
                    jsonObject.put("doctor_id",new SessionManager(activity).getUserDetails().get(SessionManager.KEY_USER_ID));
                    jsonObject.put("action",interestedStatus);

                    new UserTask().getJsonRequest(new resultInterface() {
                        @Override
                        public void Success(JSONObject response, String requestCall) {
                            try{
                                eventList.get(position).is_interested = intStatus;
                                if(alert1!= null){
                                    alert1.dismiss();
                                }
                            }

                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    },jsonObject,true,activity,activity.getCoordinatorLayout(), Params.request_code_register_event);
                }

                catch (Exception e){
                    e.printStackTrace();
                }*/
      }
    });
    posBtn.setText("CONFIRM");

    alert1 = alertDialogBuilder.create();
    alert1.setCancelable(false);
    alert1.show();

    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        alert1.dismiss();
      }
    });
  }

}