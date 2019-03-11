package com.takeda.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.Interfaces.OnEnquiryClick;
import com.takeda.android.R;
import com.takeda.android.activities.ImageDisplayActivity;
import com.takeda.android.model.EnquiryModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bharat Gupta on 9/5/2015.
 */
public class EnquiryAdapter extends BaseAdapter {

    private BaseActivity activity;
    private ArrayList<EnquiryModel.SalesPersonArrDataModel> enquiryList;
    private static LayoutInflater inflater = null;
    OnEnquiryClick mPrdClick;

    public EnquiryAdapter(BaseActivity a, ArrayList<EnquiryModel.SalesPersonArrDataModel> cartData,
                          OnEnquiryClick mPrdClick) {

        activity = a;
        this.enquiryList = cartData;
        this.mPrdClick = mPrdClick;
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return enquiryList.size();
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

        public TextView tv_sales_name;
        public TextView tv_email_id;
        public TextView tv_contact_detail;
        public LinearLayout emailLayout, mobileLayout;
        public CircleImageView salesPersonImage;
        public TextView tvProductName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( productquantityDefined below ) *******/
            vi = inflater.inflate(R.layout.enquiry_row_item, null);

            holder = new ViewHolder();
            holder.tv_email_id = vi.findViewById(R.id.tv_email_enquiry);
            holder.tv_contact_detail = vi.findViewById(R.id.tv_mobile_enquiry);
            holder.tv_sales_name = vi.findViewById(R.id.sales_name_tv);
            holder.tvProductName = vi.findViewById(R.id.productName);
            holder.emailLayout = vi.findViewById(R.id.email_details_layout);
            holder.mobileLayout = vi.findViewById(R.id.call_details_layout);
            holder.salesPersonImage = vi.findViewById(R.id.sales_person_image);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        // to set data from product class otherwise it will show temperary data stored in respective xml
        if (enquiryList.size() > 0) {
            final EnquiryModel.SalesPersonArrDataModel enquiryEntity = enquiryList.get(position);
            Glide.with(activity).load(enquiryEntity.salesImage).into(holder.salesPersonImage);
            holder.tv_email_id.setText("" + enquiryEntity.enquiry_email);
            holder.tv_contact_detail.setText(enquiryEntity.enquiry_mobile);
            holder.tv_sales_name
                    .setText("" + enquiryEntity.sales_name);
            holder.tvProductName
                    .setText(enquiryEntity.productName);

            holder.emailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPrdClick.OnEmailClick(position);
                }
            });

            holder.mobileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPrdClick.OnCallClick(position);
                }
            });

            holder.tvProductName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Layout l = holder.tvProductName.getLayout();
                        if (l != null) {
                            int lines = l.getLineCount();
                            if (lines > 0)
                                if (l.getEllipsisCount(lines - 1) > 0)
                                    mPrdClick.OnProductNameClick(position);

                        }
                    }catch (Exception e){
                        mPrdClick.OnProductNameClick(position);
                    }
                }
            });

            holder.salesPersonImage.setOnClickListener(view -> {
                activity.startActivity(new Intent(activity, ImageDisplayActivity.class)
                        .putExtra(activity.getString(R.string.image_url), enquiryEntity.salesImage));
            });
        }
        return vi;
    }
}