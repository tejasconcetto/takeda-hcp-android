package com.takeda.android.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Patterns;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.Interfaces.OnBookMarkClick;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.Utilities;
import com.takeda.android.model.EventModal;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.takeda.android.Utilities.convertLongToDate;
import static com.takeda.android.Utilities.convertTimeStampToLong;
import static com.takeda.android.Utilities.openDialogWithOption;
import static com.takeda.android.Utilities.shareOnSocialMedia;

/**
 * Created by Bharat Gupta on 9/5/2015.
 */
public class EventAdapter extends BaseAdapter {

    public static String fav = "Favourite", not_fav = "Unfavourite";
    private static LayoutInflater inflater = null;
    AlertDialog alert1;
    View vi;
    ViewHolder holder;
    private BaseActivity activity;
    private ArrayList<EventModal.EventsArrDataModel> eventList;
    private OnBookMarkClick clickListen;
    private ProgressDialog dialog;
    SessionManager sessionManager;

    public EventAdapter(BaseActivity a, ArrayList<EventModal.EventsArrDataModel> eventList,
                        OnBookMarkClick markClick) {
        activity = a;
        clickListen = markClick;
        this.eventList = eventList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        dialog = new ProgressDialog(activity);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);
        sessionManager = new SessionManager(activity);

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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        vi = convertView;
        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( productquantityDefined below ) *******/
            vi = inflater.inflate(R.layout.event_row, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/
            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new ViewHolder();
            holder.rootLayout = vi.findViewById(R.id.relative_layout_user);
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
            holder.calendarShare = vi.findViewById(R.id.calendarShare);
            holder.calendarFav = vi.findViewById(R.id.calendarFav);
            holder.calendarMoreInfo = vi.findViewById(R.id.calendarMoreInfo);
            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        // to set data from product class otherwise it will show temperary data stored in respective xml
        if (eventList.size() > 0) {

            if (position == 0) {
                holder.rootLayout.setBackgroundColor(ContextCompat.getColor(holder.rootLayout.getContext(), R.color.calendar_bg_light));
            } else if (position % 2 == 0) {
                holder.rootLayout.setBackgroundColor(ContextCompat.getColor(holder.rootLayout.getContext(), R.color.calendar_bg_light));
            } else {
                holder.rootLayout.setBackgroundColor(ContextCompat.getColor(holder.rootLayout.getContext(), R.color.calendar_bg_dark));

            }
            final EventModal.EventsArrDataModel eventListModel = eventList.get(position);

//            String[] dateParts = eventListModel.start_date.split("-");
//            Log.d("SplittedDate","Date Field : "+eventListModel.start_date+", Date : "+String.valueOf(dateParts));
            holder.selected_layout.setVisibility(View.GONE);
            holder.not_selected_layout.setVisibility(View.VISIBLE);
            holder.tv_SelectedDate.setText(eventListModel.event_date);
            holder.tv_NotSelectedDate.setText(eventListModel.event_date);
            holder.tv_SelectedTitle.setText(eventListModel.title);
            holder.tv_NotSelectedTitle.setText(eventListModel.title);
            String desc = removeDate(eventListModel.description, eventListModel.startDate);
            holder.tv_Event_Description_not_selected.setText(desc);
            holder.tv_Event_Description_selected.setText(desc);
            holder.tv_Organiser_not_selected.setText(eventListModel.organiser_name);
            holder.tv_Organiser_selected.setText(eventListModel.organiser_name);
            holder.tv_period_selected.setText(eventListModel.period);
            holder.tv_period_not_selected.setText(eventListModel.period);
            Linkify.addLinks(holder.tv_Event_Description_not_selected, Patterns.PHONE, "tel:", Linkify.sPhoneNumberMatchFilter,
                    Linkify.sPhoneNumberTransformFilter);
            Linkify.addLinks(holder.tv_Event_Description_selected, Patterns.PHONE, "tel:", Linkify.sPhoneNumberMatchFilter,
                    Linkify.sPhoneNumberTransformFilter);
           /* Linkify.addLinks(holder.tv_Event_Description_not_selected, Linkify.PHONE_NUMBERS);
            Linkify.addLinks(holder.tv_Event_Description_selected, Linkify.PHONE_NUMBERS);*/
            holder.tv_Event_Description_not_selected.setLinkTextColor(ContextCompat.getColor(activity, R.color.link_color));
            holder.tv_Event_Description_selected.setLinkTextColor(ContextCompat.getColor(activity, R.color.link_color));
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
/*
      if (eventListModel.is_selected) {
        holder.selected_layout.setVisibility(View.VISIBLE);
        holder.not_selected_layout.setVisibility(View.GONE);
      } else if (!eventListModel.is_selected) {
        holder.selected_layout.setVisibility(View.GONE);
        holder.not_selected_layout.setVisibility(View.VISIBLE);
      }*/

            Log.d("EventAdapter", "Pos:" + position + ", isFav : " + eventListModel.bookmarked);
            if (eventListModel.bookmarked) {
                holder.calendarFav.setImageResource(R.drawable.ic_calendar_with_a_clock_selected);
                /*holder.img_FavNotSelected
                        .setImageDrawable(AppDelegate.getDrawable(activity, R.drawable.favourite_icon));
                holder.img_FavSelected
                        .setImageDrawable(AppDelegate.getDrawable(activity, R.drawable.favourite_icon_white));*/
            } else if (!eventListModel.bookmarked) {
                holder.calendarFav.setImageResource(R.drawable.ic_calendar_with_a_clock_deselected);

                /*holder.img_FavNotSelected
                        .setImageDrawable(AppDelegate.getDrawable(activity, R.drawable.favourite_icon_not));
                holder.img_FavSelected.setImageDrawable(
                        AppDelegate.getDrawable(activity, R.drawable.favourite_icon_not_white));*/
            }

            holder.calendarFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFavNotFav(eventListModel, position);
                }
            });

            holder.calendarMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickFunction(position);
                }
            });

            holder.calendarShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = activity.getString(R.string.share_text,
                            eventList.get(position).title,
                            eventList.get(position).organiser_name,
                            eventList.get(position).period,
                            eventList.get(position).description,
                            eventList.get(position).click_url,
                            eventList.get(position).calendarUrl);
                    shareOnSocialMedia(activity, text);
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
            if (sessionManager.isCalendarSync())
                clickListen.OnRowClick(position, fav);
            else {
                openDialogWithOption(activity,
                        activity.getString(R.string.app_name) + " would like to access your Calendar",
                        activity.getString(R.string.app_name) + " uses your calendar to automatically add events that you are interested in.",
                        "OK", "Don't Allow",
                        new Utilities.OnClickOfButtons() {
                            @Override
                            public void onClickPositiveBtn() {
                                sessionManager.setCalendarSync(true);
                                clickListen.OnRowClick(position, fav);
                            }

                            @Override
                            public void onClickNegativiteBtn() {
                                sessionManager.setCalendarSync(false);
                                clickListen.OnRowClick(position, fav);
                            }
                        });
            }
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

    public static class ViewHolder {
        public LinearLayout rootLayout;
        public RelativeLayout selected_layout, not_selected_layout;
        public TextView tv_SelectedTitle, tv_NotSelectedTitle, tv_SelectedDate, tv_NotSelectedDate;
        public TextView tv_Organiser_not_selected, tv_period_not_selected, tv_Event_Description_not_selected, tv_More_register_not_selected;
        public TextView tv_Organiser_selected, tv_period_selected, tv_Event_Description_selected, tv_More_register_selected;
        public ImageView img_FavSelected, img_FavNotSelected, calendarShare, calendarFav, calendarMoreInfo;
    }

    /*private String removeDate(String desc,String date){
        String weekDay = convertLongToDate(convertTimeStampToLong(date,null),"EEE");
        String convertedDate = convertLongToDate(convertTimeStampToLong(date,null),"dd MMM yyyy");
        String formattedString = convertedDate +" ("+weekDay+")";
        String formattedStringWithNewLine = convertedDate +"\r\n("+weekDay+")";
        if(desc.contains(formattedString) || desc.contains(formattedStringWithNewLine)){
            return desc.replace(formattedString,"");
        }else
            return desc;
    }*/

    private String removeDate(String desc, String date) {
        try {
            String year = convertLongToDate(convertTimeStampToLong(date, null), "yyyy");
            String beforeYear = String.valueOf(Integer.parseInt(year) - 1);
            String afterYear = String.valueOf(Integer.parseInt(year) + 1);

            String updatedDesc[] = desc.split("\r\n");
            if (updatedDesc[0].contains(year) || updatedDesc[0].contains(beforeYear) || updatedDesc[0].contains(afterYear))
                return desc.replace(updatedDesc[0], "");
            else
                return desc;
        } catch (Exception e) {
            return desc;
        }
    }
}