package com.takeda.android.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.BaseClasses.BaseFragment;
import com.skk.lib.Interfaces.OnBookMarkClick;
import com.skk.lib.utils.AppDelegate;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.Utilities;
import com.takeda.android.adapters.EventAdapter;
import com.takeda.android.model.EventModal;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.takeda.android.Utilities.convertTimeStampToLong;
import static com.takeda.android.Utilities.getMonth;
import static com.takeda.android.Utilities.openDialogWithOption;

//
//import com.roomorama.caldroid.CaldroidFragment;
//import com.roomorama.caldroid.CaldroidListener;

public class CalendarFragment extends BaseFragment implements OnBookMarkClick {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    Map<Date, Drawable> dateBg;
    Context mContext;
    SessionManager session;
    View mView;
    ListView addressList;
    TextView noEventTextView;
    ArrayList<EventModal.EventsArrDataModel> events = new ArrayList<>();
    ArrayList<EventModal.EventsArrDataModel> eventDateWise = new ArrayList<>();
    //    CaldroidFragment caldroidFragment = new CaldroidFragment();
    Handler mHandler;
    EventAdapter eventAdapter;
    int color;
    CalendarDay current_date;
    HashSet<CalendarDay> dates, takeda_events, favourite_events;
    MaterialCalendarView calendarView;
    AlertDialog alert1;
    String[] date_notification = {};
    private ProgressDialog dialog;

    CalendarDay selectedDate;

    OnMonthChangedListener onMonthChndListener = null;
    ImageView ivPrevousMonth, ivNextMonth;
    TextView tvCurrentMonth, tvPreviousMonth, tvNextMonth;
    String currentMonth = "";
    String previousMonth = "";
    String nextMonth = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        session = new SessionManager(mContext);

        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_calendar, container, false);
        addressList = mView.findViewById(R.id.events_list);
        noEventTextView = mView.findViewById(R.id.noEventTextView);
        calendarView = mView.findViewById(R.id.calendarView);
        ivNextMonth = mView.findViewById(R.id.nextMonth);
        ivPrevousMonth = mView.findViewById(R.id.previousMonth);
        tvCurrentMonth = mView.findViewById(R.id.monthLabel);
        tvNextMonth = mView.findViewById(R.id.nextMonthName);
        tvPreviousMonth = mView.findViewById(R.id.previousMonthName);
        ViewGroup vg = (ViewGroup) calendarView.getChildAt(0);
        vg.setVisibility(View.GONE);

        setCalendarHeader();
        setHandler();

        onMonthChndListener = new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                int month = date.getMonth();
               /* if(date.getMonth()==0){
                    month = date.getMonth()+1;
                }else{
                    month = date.getMonth();
                }*/
                currentMonth = getMonth(month) + " " + date.getYear();
                if (month - 1 < 0)
                    previousMonth = getMonth(11).substring(0, 3);
                else
                    previousMonth = getMonth(month - 1).substring(0, 3);

                if (month + 1 > 11)
                    nextMonth = getMonth(0).substring(0, 3);
                else
                    nextMonth = getMonth(month + 1).substring(0, 3);
                tvNextMonth.setText(nextMonth);
                tvPreviousMonth.setText(previousMonth);
                tvCurrentMonth.setText(currentMonth);
                loadEvents(date.getMonth() + 1, date.getYear());
                if (events == null) {
                    events = new ArrayList<>();
                }
                events.clear();
                if (eventDateWise == null) {
                    eventDateWise = new ArrayList<>();
                }
                eventDateWise.clear();
                mHandler.sendEmptyMessage(1);
            }
        };

        addressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                for (int i = 0; i < eventDateWise.size(); i++) {
                    eventDateWise.get(i).is_selected = false;
                }
                eventDateWise.get(pos).is_selected = true;

                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).id.equalsIgnoreCase(eventDateWise.get(pos).id)) {
                        events.get(i).bookmarked = eventDateWise.get(pos).bookmarked;
                        showLogs("Favourite",
                                "Id : " + eventDateWise.get(pos).id + ", Fav Value " + events.get(i).bookmarked
                                        + " is updated");
                    }
                }

                mHandler.sendEmptyMessage(1);
            }
        });

        ViewGroup.LayoutParams params1 = calendarView.getLayoutParams();
        Double height1 = layoutsize() * 0.7;
//        Integer width1 = layoutsize();
        params1.height = height1.intValue();
//        params1.width = width1;
        calendarView.setLayoutParams(params1);

        Calendar cal = Calendar.getInstance();

        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_NONE);
        if (onMonthChndListener != null) {
            calendarView.setOnMonthChangedListener(onMonthChndListener);
        }
        currentMonth = getMonth(cal.get(Calendar.MONTH)) + " " + cal.get(Calendar.YEAR);
        tvCurrentMonth.setText(currentMonth);
        if (cal.get(Calendar.MONTH) - 1 < 0)
            previousMonth = getMonth(11).substring(0, 3);
        else
            previousMonth = getMonth(cal.get(Calendar.MONTH) - 1).substring(0, 3);

        if (cal.get(Calendar.MONTH) + 1 > 11)
            nextMonth = getMonth(0).substring(0, 3);
        else
            nextMonth = getMonth(cal.get(Calendar.MONTH) + 1).substring(0, 3);
        tvNextMonth.setText(nextMonth);
        tvPreviousMonth.setText(previousMonth);
        selectedDate = CalendarDay.today();
        if (getArguments() != null) {
            showLogs("BundleStatus", "Exist - " + String.valueOf(getArguments()));
            Bundle bundle = getArguments();
            date_notification = bundle.getString("event_date").split("-");
            current_date = CalendarDay.from(Integer.parseInt(date_notification[0]),
                    Integer.parseInt(date_notification[1]) - 1, Integer.parseInt(date_notification[2]));
            calendarView.setCurrentDate(current_date);
//            calendarView.setSelectedDate(current_date);

//            if(onMonthChndListener != null){
//                onMonthChndListener.onMonthChanged(calendarView,));
//            }
            loadEvents(Integer.parseInt(date_notification[1]), Integer.parseInt(date_notification[0]));
        } else {
            loadEvents(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
        }
        return mView;
    }

    private void setCalendarHeader() {

        ivNextMonth.setOnClickListener(view -> calendarView.goToNext());

        ivPrevousMonth.setOnClickListener(view -> calendarView.goToPrevious());

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    void loadEvents(int month, int year) {

        try {
            calendarView.removeDecorators();

            if (dialog != null) {
                dialog.show();
            }

            ApiInterface api = RestAdapterService.createService(ApiInterface.class);

            api.GetEvent(new JSONObject(session.getUserDetails().get(SessionManager.KEY_USER_JSON))
                            .getString("doctor_id"), month, year,
                    new JSONObject(session.getUserDetails().get(SessionManager.KEY_USER_JSON))
                            .getString("access_token"),
                    new Callback<EventModal>() {
                        @Override
                        public void success(EventModal eventModal, Response response) {
//                            System.out.println("=======success in GetEvent api call=====");
                            dialog.dismiss();

                            if (eventModal.response.status.equalsIgnoreCase("success")) {

                                try {
                                    ArrayList<EventModal.EventsArrDataModel> resultArr = new ArrayList<EventModal.EventsArrDataModel>();
                                    resultArr = eventModal.response.data.eventsData;

//                                    JSONArray eventsArray = response.getJSONObject(Params.response_success_data).getJSONArray("events");

                                    if (dates == null) {
                                        dates = new HashSet<>();
                                    }

                                    if (takeda_events == null) {
                                        takeda_events = new HashSet<CalendarDay>();
                                    }

                                    if (favourite_events == null) {
                                        favourite_events = new HashSet<CalendarDay>();
                                    }

                                    dates.clear();
                                    takeda_events.clear();
                                    favourite_events.clear();

                                    for (int i = 0; i < resultArr.size(); i++) {
                                        try {
                                            CalendarDay date = CalendarDay
                                                    .from(AppDelegate.getDate(Long.parseLong(resultArr.get(i).event_date)));

                                            EventModal.EventsArrDataModel eventsArrDataModel = new EventModal.EventsArrDataModel();
                                            eventsArrDataModel = resultArr.get(i);
                                            eventsArrDataModel.calViewDate = date;
                                            eventsArrDataModel.event_date = String.valueOf(date.getDay());
                                            events.add(eventsArrDataModel);

                                            dates.add(date);
                                            if (resultArr.get(i).takeda_event) {
                                                takeda_events.add(date);
                                            }

                                            if (resultArr.get(i).bookmarked) {
                                                favourite_events.add(date);
                                            }
                                            showLogs("EventIntStatus", "From JSON - " + resultArr.get(i).interested);
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                    addDecorators();
                                    calendarView.refreshDrawableState();
                                    showLogs("CurrentDate", "Date - " + CalendarDay.today().getDay());
                                    if (getArguments() == null) {
                                        showEventsList("" + CalendarDay.today().getDay());
                                    } else {
                                        showEventsList("" + date_notification[2]);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            } else {
                                msgAlertDialog("Error", eventModal.response.statusMessage);
                            }


                        }

                        @Override
                        public void failure(RetrofitError error) {
//                            System.out.println("=======failure in GetEvent api call=====");
                            dialog.dismiss();

                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addDecorators() {

        System.out.println("========in addDecorators=========");

//        RedDecorator redDecorator = new RedDecorator(getActivity(), dates);
//        calendarView.addDecorator(redDecorator);
        calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return (dates.contains(day));
            }

            @Override
            public void decorate(DayViewFacade view) {

                view.addSpan(new DotSpan(5, ContextCompat.getColor(mContext, R.color.red_offers)));
            }
        });

        /*calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                System.out.println("takeda_events.contains(day)=======>"+takeda_events.contains(day));
                System.out.println("day=======>"+day);
                return takeda_events.contains(day);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(AppDelegate.getDrawable(mContext,R.drawable.block_blue));
            }
        });*/

        calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return (favourite_events.contains(day));
            }

            @Override
            public void decorate(DayViewFacade view) {
                System.out.println("selectedDate========>" + selectedDate);
                System.out.println("favourite_events========>" + favourite_events);
                /*if (favourite_events != null && favourite_events.size() > 0 && favourite_events.contains(selectedDate)) {
                    System.out.println("true=====");
                    view.setBackgroundDrawable(AppDelegate.getDrawable(mContext, R.drawable.circle_counter));
                } else {*/
                view.setBackgroundDrawable(
                        AppDelegate.getDrawable(mContext, R.drawable.circle_counter_green));
                // }
                view.addSpan(new ForegroundColorSpan(Color.WHITE));
            }
        });

        calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return (selectedDate != null && day.equals(CalendarDay.from(selectedDate.getCalendar().get(Calendar.YEAR),
                        selectedDate.getCalendar().get(Calendar.MONTH),
                        selectedDate.getCalendar().get(Calendar.DATE))));
                        /*|| (day.equals(CalendarDay.from(Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DATE)))));*/
            }

            @Override
            public void decorate(DayViewFacade view) {
                /*TextDrawable drawable = TextDrawable.builder().beginConfig()
                        .width(20)
                        .height(20)
                        .endConfig()
                        .buildRound(null, Color.parseColor(color));*/
                view.setBackgroundDrawable(AppDelegate.getDrawable(mContext, R.drawable.circle_counter));
                view.addSpan(new ForegroundColorSpan(Color.WHITE));
            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date,
                                       boolean selected) {
                System.out.println("=======in onDateSelected=========");
                System.out.println("date.getDay()=========>" + date);

                selectedDate = date;
                calendarView.removeDecorators();
                addDecorators();

//                RedDecorator redDecorator = new RedDecorator(getActivity(), dates);
//                calendarView.addDecorator(redDecorator);
//                calendarView.addDecorators(new CurrentDayDecorator(mContext, date));

//                calendarView.setSelectionColor(R.color.red_color_pressed);
                /*calendarView.addDecorator(new DayViewDecorator() {
                    @Override
                    public boolean shouldDecorate(CalendarDay day) {
                        return day.equals(date.getDay());
                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                */
                showEventsList("" + date.getDay());
            }
        });

        calendarView.refreshDrawableState();

    }

    private void setHandler() {
        mHandler = new Handler() {
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);
                if (msg.what == 1) {
                    if (eventDateWise.size() > 0) {
                        addressList.setVisibility(View.VISIBLE);
                        noEventTextView.setVisibility(View.GONE);
                        if (eventAdapter != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    eventAdapter.notifyDataSetChanged();
                                    addressList.invalidate();
                                    //addressList.smoothScrollToPosition(0);
                                    addressList.setSelectionAfterHeaderView();
                                }
                            });
                        } else {
//                            System.out.println("eventDateWise in setHandler=========>" + eventDateWise);
                            eventAdapter = new EventAdapter((BaseActivity) getActivity(), eventDateWise,
                                    CalendarFragment.this);
                            addressList.setAdapter(eventAdapter);
                            //addressList.smoothScrollToPosition(0);
                            addressList.setSelectionAfterHeaderView();

                        }
                    } else {
                        addressList.setVisibility(View.GONE);
                        noEventTextView.setVisibility(View.VISIBLE);
                    }

                    calendarView.refreshDrawableState();
                    addDecorators();
                }
            }
        };
    }

    void showEventsList(String date) {
//        System.out.println("=======in showEventsList======");
//        System.out.println("events in showEventsList======>"+events);
        eventDateWise.clear();
        for (EventModal.EventsArrDataModel event : events) {

            if (event.event_date.equalsIgnoreCase(date)) {
                if (current_date != null && event.event_date.equals(current_date.getDay())) {
                    event.is_selected = true;
                }
                eventDateWise.add(event);
            }
        }
//        System.out.println("eventDateWise in showEventsList======>"+eventDateWise);
//        eventDateWise.clear();
        current_date = null;
        mHandler.sendEmptyMessage(1);
    }

    boolean isAnyFavEvent(CalendarDay date) {
        boolean isFavLeft = false;
        for (EventModal.EventsArrDataModel event : events) {
            if (event.calViewDate.equals(date)) {
                if (event.bookmarked) {
                    isFavLeft = true;
                }
            }
        }
        return isFavLeft;
    }

    @Override
    public void OnRowClick(final int pos, final String setFav) {
        System.out.println("in OnRowClick");
        if (setFav.equalsIgnoreCase(EventAdapter.fav) && session.isCalendarSync())
            askPermissionForLocation(pos, setFav);
        else
            onFavBtnCLick(pos, setFav, false);
    }

    @Override
    public void OnRegiserOrMoreClick(int position, String interestedStr) {
        showLogs("EventIntStatus", "Listener Status - " + eventDateWise.get(position).interested);
        openDialog(position, interestedStr, eventDateWise.get(position).interested, "register");
    }


    public void onFavBtnCLick(final int pos, final String setFav, boolean isPermissionGranted) {
        if (dialog != null) {
            dialog.show();
        }

        try {

            ApiInterface api = RestAdapterService.createService(ApiInterface.class);

            api.BookmarkEvent(session.getUserDetails().get(SessionManager.KEY_USER_ID),
                    eventDateWise.get(pos).id, setFav,
                    new JSONObject(session.getUserDetails().get(SessionManager.KEY_USER_JSON))
                            .getString("access_token"),
                    new Callback<EventModal>() {
                        @Override
                        public void success(EventModal eventModal, Response response) {
//                            System.out.println("=======success in BookmarkEvent api call=====");

                            dialog.dismiss();

                            CalendarDay dateCal = eventDateWise.get(pos).calViewDate;
                            showLogs("DateToChange", "Calendar Day : " + String.valueOf(dateCal));

                            if (eventModal.response.data.bookmark_events.equalsIgnoreCase("success")) {
                                eventDateWise.get(pos).bookmarked = setFav
                                        .equalsIgnoreCase(EventAdapter.fav);
                                eventAdapter.notifyDataSetChanged();
                                if (eventDateWise.get(pos).bookmarked) {
                                    if (session.isCalendarSync() && isPermissionGranted) {
                                        openDialogWithOption(getActivity(), "Event Reminder",
                                                getString(R.string.event_reminder_msg), null, null,
                                                new Utilities.OnClickOfButtons() {
                                                    @Override
                                                    public void onClickPositiveBtn() {
                                                        long startTime;
                                                        long endTime;

                                                        startTime = convertTimeStampToLong(eventDateWise.get(pos).startDate, null);
                                                        endTime = convertTimeStampToLong(eventDateWise.get(pos).endDate, null);
                                                        boolean isAllDay = eventDateWise.get(pos).startTime.isEmpty()
                                                                || eventDateWise.get(pos).startTime.equalsIgnoreCase("00:00:00");

                                                        if (!checkIfEventExists(startTime, endTime, eventDateWise.get(pos).title)) {
                                                            pushAppointmentsToCalender(eventDateWise.get(pos).title,
                                                                    eventDateWise.get(pos).description,
                                                                    eventDateWise.get(pos).Address,
                                                                    eventDateWise.get(pos).click_url,
                                                                    eventDateWise.get(pos).id,
                                                                    isAllDay,
                                                                    0,
                                                                    startTime,
                                                                    endTime, false);
                                                            msgAlertDialog("Success", "The event is synchronized to your phone calendar");
                                                        } else {
                                                            msgAlertDialog("Alert", "The event has already been synchronized to your phone calendar");

                                                        }
                                                    }

                                                    @Override
                                                    public void onClickNegativiteBtn() {

                                                    }
                                                });
                                    } else {
                                        msgAlertDialog("Event Reminder",
                                                "You will receive a pop-up reminder at 10am one day before the event.");
                                    }


                                }
//                            showSBar(responseJSON.getString(Params.response_message));
                                for (int i = 0; i < events.size(); i++) {
                                    if (events.get(i).id.equalsIgnoreCase(eventDateWise.get(pos).id)) {
                                        events.get(i).bookmarked = eventDateWise.get(pos).bookmarked;
                                    }
                                    boolean isAnyFavLeft = isAnyFavEvent(events.get(i).calViewDate);
                                    if (isAnyFavLeft) {

                                        favourite_events.add(events.get(i).calViewDate);

//
                                    } else {
                                        favourite_events.remove(events.get(i).calViewDate);
                                    }
                                }
                            }
                            calendarView.invalidateDecorators();
                            mHandler.sendEmptyMessage(1);

                        }

                        @Override
                        public void failure(RetrofitError error) {

                            dialog.dismiss();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }


        /* old code
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("doctor_id",session.getUserDetails().get(SessionManager.KEY_USER_ID));
            jsonObject.put("event_id",eventDateWise.get(pos).id);
            jsonObject.put("action",setFav);

            new UserTask().getJsonRequest(new resultInterface() {
                @Override
                public void Success(JSONObject responseJSON, String requestCall) {
                    try{
                        JSONObject response = responseJSON.getJSONObject(Params.response_success_data);
                        CalendarDay dateCal = eventDateWise.get(pos).calViewDate;
                        showLogs("DateToChange","Calendar Day : "+String.valueOf(dateCal));

                        if(response.getString("bookmark_events").equalsIgnoreCase("success")){
                            eventDateWise.get(pos).is_favourite = setFav.equalsIgnoreCase(EventAdapter.fav) ? true : false;
                            if (eventDateWise.get(pos).is_favourite)
                            {
                                msgAlertDialog("Event Reminder", "You will receive a pop-up reminder at 10am one day before the event.");
                            }
//                            showSBar(responseJSON.getString(Params.response_message));
                            for(int i=0;i<events.size();i++){
                                if(events.get(i).id.equalsIgnoreCase(eventDateWise.get(pos).id)){
                                    events.get(i).is_favourite = eventDateWise.get(pos).is_favourite;
                                }
                                boolean isAnyFavLeft = isAnyFavEvent(events.get(i).calViewDate);
                                if(isAnyFavLeft){

                                    favourite_events.add(events.get(i).calViewDate);

//
                                }
                                else{
                                    favourite_events.remove(events.get(i).calViewDate);
                                }
                            }
                        }
                        calendarView.invalidateDecorators();
                        mHandler.sendEmptyMessage(1);
                    }

                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            },jsonObject,true,(BaseActivity)mContext,null, Params.request_code_set_fav_not_fav);
        }

        catch (Exception e){
            e.printStackTrace();
        }*/
    }

    void openDialog(final int position, final String interestedStatus, final boolean intStatus,
                    final String callType) {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final View promptView = layoutInflater.inflate(R.layout.enquiry_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(),
                R.style.CustomDialog);
        alertDialogBuilder.setView(promptView);

        final Button posBtn = promptView.findViewById(R.id.posBtn);
        final Button negBtn = promptView.findViewById(R.id.negBtn);
        final ImageView dialogIcon = promptView.findViewById(R.id.dialogIcon);
        final TextView contactDetail = promptView.findViewById(R.id.contactDetail);
        final TextView message = promptView.findViewById(R.id.contactMsg);
        final TextView title = promptView.findViewById(R.id.dialog_title);

//        if(callType.equalsIgnoreCase("selectFavourite")){
//            title.setText("Event Reminder");
//
//            message.setText("You will receive a pop-up reminder at 10am one day before the event.");
//            posBtn.setText("OK");
//
//            posBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alert1.dismiss();
//                }
//            });
//
//            negBtn.setVisibility(View.GONE);
//
//        }
//        else {

        title.setText("Takeda");
        showLogs("Status", "---- " + interestedStatus);

        if (interestedStatus.toLowerCase().contains("not")) {
            message.setText("Do you want to unregister for the event?");
            posBtn.setText("UNREGISTER");
        } else {
            message.setText("Do you want more information for this event?");
//                posBtn.setText("REGISTER");
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
                            new SessionManager(getActivity()).getUserDetails().get(SessionManager.KEY_USER_ID),
                            eventDateWise.get(position).id, interestedStatus,
                            new JSONObject(session.getUserDetails().get(SessionManager.KEY_USER_JSON))
                                    .getString("access_token"),
                            new Callback<EventModal>() {
                                @Override
                                public void success(EventModal eventModal, Response response) {

//                                        System.out.println("=======success in InterestedEvent api call=====");

                                    dialog.dismiss();

                                    try {
                                        eventDateWise.get(position).interested = !intStatus;
                                        for (int i = 0; i < events.size(); i++) {
                                            if (events.get(i).id.equalsIgnoreCase(eventDateWise.get(position).id)) {
                                                events.get(i).interested = eventDateWise.get(position).interested;
//                                                    System.out.println("events.get(i).is_interested=======>"+events.get(i).interested);
                                            }
//                                    boolean isAnyFavLeft = isAnyFavEvent(events.get(i).calViewDate);
//                                    if (isAnyFavLeft) {
//                                        favourite_events.add(events.get(i).calViewDate);
//                                    } else {
//                                        favourite_events.remove(events.get(i).calViewDate);
//                                    }
                                        }
                                        showLogs("EventIntStatus",
                                                "Listener Status - " + eventDateWise.get(position).interested);
                                        if (alert1 != null) {
                                            alert1.dismiss();
                                        }
                                        mHandler.sendEmptyMessage(1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void failure(RetrofitError error) {

//                                        System.out.println("=======failure in InterestedEvent api call=====");
                                    dialog.dismiss();

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }



                    /*old code
                    try {
                        System.out.println("interestedStatus=======>"+interestedStatus);
                        System.out.println("position=======>"+position);

                        System.out.println("eventDateWise.get(position).is_interested=======>"+eventDateWise.get(position).is_interested);
                        System.out.println("eventDateWise.get(position).id=======>"+eventDateWise.get(position).id);
                        System.out.println(" events.get(position).id=======>"+ events.get(position).id);


                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("event_id", eventDateWise.get(position).id);
                        jsonObject.put("doctor_id", new SessionManager(getActivity()).getUserDetails().get(SessionManager.KEY_USER_ID));
                        jsonObject.put("action", interestedStatus);

                        System.out.println("jsonObject=======>"+jsonObject);

                        System.out.println("submit interestedStatus=======>"+interestedStatus);

                        new UserTask().getJsonRequest(new resultInterface() {
                            @Override
                            public void Success(JSONObject response, String requestCall) {
                                System.out.println("response=========>"+response);
                                try {
                                    eventDateWise.get(position).is_interested = !intStatus;
                                    for (int i = 0; i < events.size(); i++) {
                                        if (events.get(i).id.equalsIgnoreCase(eventDateWise.get(position).id)) {
                                            events.get(i).is_interested = eventDateWise.get(position).is_interested;
                                            System.out.println("events.get(i).is_interested=======>"+events.get(i).is_interested);
                                        }
//                                    boolean isAnyFavLeft = isAnyFavEvent(events.get(i).calViewDate);
//                                    if (isAnyFavLeft) {
//                                        favourite_events.add(events.get(i).calViewDate);
//                                    } else {
//                                        favourite_events.remove(events.get(i).calViewDate);
//                                    }
                                    }
                                    showLogs("EventIntStatus", "Listener Status - " + eventDateWise.get(position).is_interested);
                                    if (alert1 != null) {
                                        alert1.dismiss();
                                    }
                                    mHandler.sendEmptyMessage(1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, jsonObject, true, getActivity(), getCoordinatorLayout(), Params.request_code_register_event);
                    } catch (Exception e) {
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

//        }
    }

    public long pushAppointmentsToCalender(String title,
                                           String addInfo,
                                           String place,
                                           String url,
                                           String eventId,
                                           boolean isAllDay,
                                           int status,
                                           long startDate,
                                           long endDate,
                                           boolean needReminder) {
        /***************** Event: note(without alert) *******************/

        Activity curActivity = getActivity();
        String eventUriString = "content://com.android.calendar/events";
        ContentValues eventValues = new ContentValues();

        eventValues.put(CalendarContract.Events.CALENDAR_ID, 1); // id, We need to choose from
        // our mobile for primary
        // its 1
        eventValues.put(CalendarContract.Events.TITLE, title);
        eventValues.put(CalendarContract.Events.DESCRIPTION, addInfo + "\n" + url);
        eventValues.put(CalendarContract.Events.EVENT_LOCATION, place);
        eventValues.put(CalendarContract.Events.ALL_DAY, isAllDay);
        eventValues.put(CalendarContract.Events.DTSTART, startDate);
        eventValues.put(CalendarContract.Events.DTEND, endDate);
        eventValues.put(CalendarContract.Events.CUSTOM_APP_URI, url);
        eventValues.put(CalendarContract.Events._ID, eventId);

        // values.put("allDay", 1); //If it is bithday alarm or such
        // kind (which should remind me for whole day) 0 for false, 1
        // for true
        eventValues.put(CalendarContract.Events.STATUS, status); // This information is
        // sufficient for most
        // entries tentative (0),
        // confirmed (1) or canceled
        // (2):
        TimeZone timeZone = TimeZone.getDefault();
        eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        /*Comment below visibility and transparency  column to avoid java.lang.IllegalArgumentException column visibility is invalid error */

    /*eventValues.put("visibility", 3); // visibility to default (0),
                                        // confidential (1), private
                                        // (2), or public (3):
    eventValues.put("transparency", 0); // You can control whether
                                        // an event consumes time
                                        // opaque (0) or transparent
                                        // (1).
      */
        eventValues.put(CalendarContract.Events.HAS_ALARM, 0); // 0 for false, 1 for true

        Uri eventUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
        assert eventUri != null;
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

        if (needReminder) {
            /***************** Event: Reminder(with alert) Adding reminder to event *******************/

            String reminderUriString = "content://com.android.calendar/reminders";

            ContentValues reminderValues = new ContentValues();

            reminderValues.put("event_id", eventID);
            reminderValues.put("minutes", 5); // Default value of the
            // system. Minutes is a
            // integer
            reminderValues.put("method", 1); // Alert Methods: Default(0),
            // Alert(1), Email(2),
            // SMS(3)

            Uri reminderUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
        }


        return eventID;

    }

    public boolean checkIfEventExists(long begin, long end, String title) {
        String[] proj =
                new String[]{
                        CalendarContract.Instances._ID,
                        CalendarContract.Instances.BEGIN,
                        CalendarContract.Instances.END,
                        CalendarContract.Instances.TITLE};
        Cursor cursor =
                CalendarContract.Instances.query(getActivity().getContentResolver(), proj, begin, end);
        return cursor != null && cursor.moveToFirst() && cursor.getString(cursor.getColumnIndex("title")).equalsIgnoreCase(title);
    }


    private void askPermissionForLocation(final int pos, final String setFav) {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
                .withListener(getCompositeListenerForLocation(pos, setFav)).check();
    }

    private MultiplePermissionsListener getCompositeListenerForLocation(final int pos, final String setFav) {

        MultiplePermissionsListener multiplePermissionsListener = new CompositeMultiplePermissionsListener() {

            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted())
                    onFavBtnCLick(pos, setFav, true);
            }

        };

        MultiplePermissionsListener snackbarPermissionsListener =
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                        .with(mView, getString(R.string.calendar_access_needed))
                        .withOpenSettingsButton("Settings")
                        .withCallback(new Snackbar.Callback() {
                            @Override
                            public void onShown(Snackbar snackbar) {
                                onFavBtnCLick(pos, setFav, false);

                            }

                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                            }
                        })
                        .build();

        return new CompositeMultiplePermissionsListener(snackbarPermissionsListener, multiplePermissionsListener);

    }
}