package com.takeda.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.skk.lib.BaseClasses.BaseActivity;
import com.takeda.android.R;
import com.takeda.android.model.PastRecordModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Bharat Gupta on 9/5/2015.
 */
public class PastRecordAdapter extends RecyclerView.Adapter<PastRecordAdapter.ViewHolder1> {


  private BaseActivity activity;
  private ArrayList<PastRecordModel.PurchaseArrDataModel> enquiryList;
  private LayoutInflater inflater = null;

  public PastRecordAdapter(BaseActivity a,
      ArrayList<PastRecordModel.PurchaseArrDataModel> cartData) {

//        System.out.println("=======in PastRecordAdapter=====");

    activity = a;
    this.enquiryList = cartData;
    inflater = (LayoutInflater) activity.
        getSystemService(Context.LAYOUT_INFLATER_SERVICE);

  }

   /* @Override
    public int getCount() {
        return enquiryList.size();
    }

    @Override
    public Object getItem(int pos) {
        return pos;
    }*/

  /*@Override
  public long getItemId(int id) {
      return id;
  }
*/
  public class ViewHolder1 extends RecyclerView.ViewHolder {


    public TextView tv_record_date;
    public TextView tv_product_name;
    public TextView tv_product_qty;
    public TextView tv_product_price;
    public TextView tv_bonus;
    public LinearLayout headerLayout, rowLayout;

    public ViewHolder1(View view) {
      super(view);
//            System.out.println("=======in ViewHolder=====");
      tv_product_price = view.findViewById(R.id.tv_product_price);
      tv_product_name = view.findViewById(R.id.tv_product_record);
      tv_product_qty = view.findViewById(R.id.tv_product_qty);
      tv_record_date = view.findViewById(R.id.tv_date_record);
      tv_bonus = view.findViewById(R.id.tv_bonus);
      headerLayout = view.findViewById(R.id.header_layout);
      rowLayout = view.findViewById(R.id.row_layout);
    }
  }

  @Override
  public ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.past_record_row_item, parent, false);

//        System.out.println("=======in onCreateViewHolder=====");

    return new ViewHolder1(itemView);
  }

  @Override
  public void onBindViewHolder(ViewHolder1 holder, int position) {
//        System.out.println("=======in onBindViewHolder=====");
    if (enquiryList.size() > 0) {

      if (position == 0) {
        holder.headerLayout.setVisibility(View.VISIBLE);
        holder.rowLayout.setVisibility(View.GONE);
      } else {
        final PastRecordModel.PurchaseArrDataModel pastRecordEntity = enquiryList.get(position - 1);

        /*            if(pastRecordEntity.row_type.equalsIgnoreCase(Params.type_row)){
         */

        String number = pastRecordEntity.price;
        double amount = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("HK$#,###");
        String formattedPrice = formatter.format(amount);

//                System.out.println("pastRecordEntity.purchase_date=========>"+pastRecordEntity.purchase_date);

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(pastRecordEntity.purchase_date) * 1000);
        String date = DateFormat.format("d MMM yyyy", cal).toString();

                /*Calendar cal = Calendar.getInstance();
                TimeZone tz = cal.getTimeZone();//get your local time zone.
                SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");
//                sdf.setTimeZone(tz);//set time zone.
                String localTime = sdf.format(new Date(Long.parseLong(pastRecordEntity.purchase_date) * 1000));
                Date date = new Date();
                try {
                    date = sdf.parse(localTime);//get local date
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/

//                System.out.println(date);

        holder.headerLayout.setVisibility(View.GONE);
        holder.rowLayout.setVisibility(View.VISIBLE);
        holder.tv_product_name.setText("" + pastRecordEntity.products.product_name);
        holder.tv_product_qty.setText(pastRecordEntity.quantity);
        holder.tv_record_date.setText("" + date);
        holder.tv_product_price.setText("" + formattedPrice);
        holder.tv_bonus.setText("" + pastRecordEntity.bonus);

        //Your color logic
        if (position % 2 == 0) {
          holder.rowLayout.setBackgroundColor(Color.LTGRAY);
        } else {
          holder.rowLayout.setBackgroundColor(Color.WHITE);
        }
      }
    } else {
      holder.headerLayout.setVisibility(View.VISIBLE);
      holder.rowLayout.setVisibility(View.GONE);
    }
  }

  @Override
  public int getItemCount() {
//        System.out.println("=======in getItemCount=====");

    return enquiryList.size() + 1;

  }

    /*@Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            *//****** Inflate tabitem.xml file for each row ( productquantityDefined below ) *******//*
            vi = inflater.inflate(R.layout.past_record_row_item, null);

            holder = new ViewHolder();
            holder.tv_product_price = (TextView) vi.findViewById(R.id.tv_product_price);
            holder.tv_product_name = (TextView) vi.findViewById(R.id.tv_product_record);
            holder.tv_product_qty = (TextView) vi.findViewById(R.id.tv_product_qty);
            holder.tv_record_date = (TextView) vi.findViewById(R.id.tv_date_record);
            holder.tv_bonus = (TextView) vi.findViewById(R.id.tv_bonus);
            holder.headerLayout = (LinearLayout) vi.findViewById(R.id.header_layout);
            holder.rowLayout = (LinearLayout) vi.findViewById(R.id.row_layout);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

//        holder.rowLayout.setBackgroundColor(Color.BLUE);
        //Your color logic
//        if ( position % 2 == 0) {
//            holder.rowLayout.setBackgroundColor(Color.RED);
//        } else {
//            holder.rowLayout.setBackgroundColor(Color.YELLOW);
//        }

//        View view = super.getView(position, convertView, parent);
//        if (position % 2 == 1) {
//            vi.setBackgroundColor(Color.GRAY);
//        } else {
//            vi.setBackgroundColor(Color.WHITE);
//        }

        // to set data from product class otherwise it will show temperary data stored in respective xml
        if (enquiryList.size() > 0) {

            if (position == 0)
            {
                holder.headerLayout.setVisibility(View.VISIBLE);
                holder.rowLayout.setVisibility(View.GONE);
            }
            else {
                final PastRecordModel.PurchaseArrDataModel pastRecordEntity = enquiryList.get(position-1);

*//*            if(pastRecordEntity.row_type.equalsIgnoreCase(Params.type_row)){
   *//*


                String number = pastRecordEntity.price;
                double amount = Double.parseDouble(number);
                DecimalFormat formatter = new DecimalFormat("HK$#,###");
                String formattedPrice = formatter.format(amount);

//                System.out.println("pastRecordEntity.purchase_date=========>"+pastRecordEntity.purchase_date);

                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(Long.parseLong(pastRecordEntity.purchase_date));
                String date = DateFormat.format("d-MMM-yyyy", cal).toString();
//            System.out.println(date);

                holder.headerLayout.setVisibility(View.GONE);
                holder.rowLayout.setVisibility(View.VISIBLE);
                holder.tv_product_name.setText("" + pastRecordEntity.products.product_name);
                holder.tv_product_qty.setText(pastRecordEntity.quantity);
                holder.tv_record_date.setText("" + date);
                holder.tv_product_price.setText("" + formattedPrice);
                holder.tv_bonus.setText("" + pastRecordEntity.bonus);

                //Your color logic
                if (position % 2 == 0) {
                    holder.rowLayout.setBackgroundColor(Color.LTGRAY);
                } else {
                    holder.rowLayout.setBackgroundColor(Color.WHITE);
                }
            }
//                if(position%2 == 0){
//                    holder.rowLayout.setBackgroundColor(AppDelegate.getColorRes(activity,Color.GRAY));
//                }
//
//                else{
//                    holder.rowLayout.setBackgroundColor(AppDelegate.getColorRes(activity,R.color.white));
//                }
*//*            }

            else{
                holder.headerLayout.setVisibility(View.VISIBLE);
                holder.rowLayout.setVisibility(View.GONE);
            }*//*

//            if(pastRecordEntity.is_selected){
//                holder.rowLayout.setBackgroundColor(AppDelegate.getColorRes(activity,R.color.headerColor));
//            }
//
//            if(!pastRecordEntity.is_selected){
//                holder.rowLayout.setBackgroundColor(AppDelegate.getColorRes(activity,R.color.white));
//            }
        }
        return vi;
    }*/
}
