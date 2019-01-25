package com.takeda.android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utilities {


    public static void openDialogWithOption(Activity activity, String titleMsg, String msg, String positiveBtnText, String negativeBtnText, OnClickOfButtons onClickOfButtons) {
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


        title.setText(titleMsg);
        title.setGravity(Gravity.CENTER);

        message.setText(msg);
        message.setGravity(Gravity.CENTER);

        promptView.findViewById(R.id.email_details_layout).setVisibility(View.GONE);

        AlertDialog alert1 = alertDialogBuilder.create();
        dialogIcon.setImageResource(R.drawable.icn_mobile);
        AlertDialog finalAlert = alert1;
        posBtn.setOnClickListener(v -> {
            finalAlert.dismiss();
            if (onClickOfButtons != null) {
                onClickOfButtons.onClickPositiveBtn();
            }


        });

        if (positiveBtnText != null)
            posBtn.setText(positiveBtnText);
        else
            posBtn.setText("YES");


        if (negativeBtnText != null)
            negBtn.setText(negativeBtnText);
        else
            negBtn.setText("NO");


        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalAlert.dismiss();
                if (onClickOfButtons != null) {
                    onClickOfButtons.onClickNegativiteBtn();
                }
            }
        });

        alert1.setCancelable(false);
        alert1.show();
    }

    public interface OnClickOfButtons {

        void onClickPositiveBtn();

        void onClickNegativiteBtn();
    }


    public static long convertdatetoLong(String date, String sourcePattern) {

        SimpleDateFormat df = new SimpleDateFormat(sourcePattern, Locale.ENGLISH);
        try {
            Date newDate = df.parse(date);
            return newDate.getTime();

        } catch (ParseException e) {
            return 0;
        }
    }

    public static long convertTimeStampToLong(String date, String time) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        /* debug: is it local time? */
        Log.d("Time zone: ", tz.getDisplayName());

        /* date formatter in local timezone */
        String applyPattern;
        if (time != null)
            applyPattern = "dd/MM/yyyy";
        else
            applyPattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(applyPattern);
        sdf.setTimeZone(tz);

        /* print your timestamp and double check it's the date you expect */
        long timestamp = Long.parseLong(date);
        String localTime = sdf.format(new Date(timestamp * 1000));
        if (time != null) {
            localTime = localTime + " " + time;
        }
        return convertdatetoLong(localTime, "dd/MM/yyyy HH:mm:ss");
    }

    public static void shareOnSocialMedia(Activity activity, String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        activity.startActivity(Intent.createChooser(intent, "Share"));
    }

    public static String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    public static String convertDateFormat(String myDate, String inputFormat, String outputFormat) {
        if (!myDate.equals("")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(outputFormat, Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat(inputFormat, Locale.getDefault());
            Date date = null;
            try {
                date = sdf.parse(myDate);
                return dateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return myDate;
            }
        } else
            return myDate;
    }

    public static String convertLongToDate(long dateInLong, String outputFormat) {
        return DateFormat.format(outputFormat, new Date(dateInLong)).toString();
    }
}
