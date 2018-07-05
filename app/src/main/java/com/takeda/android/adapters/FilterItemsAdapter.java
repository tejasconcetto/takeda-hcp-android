package com.takeda.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.takeda.android.R;
import com.takeda.android.adapters.FilterItemsAdapter.ViewHolder;
import java.util.ArrayList;

public class FilterItemsAdapter extends Adapter<ViewHolder> {


  private final ArrayList<String> district;
  private final ArrayList<String> customerId;
  private OnItemSelected onItemSelectedListener;
  Context context;

  public OnItemSelected getOnItemSelectedListener() {
    return onItemSelectedListener;
  }

  public void setOnItemSelectedListener(
      OnItemSelected onItemSelectedListener) {
    this.onItemSelectedListener = onItemSelectedListener;
  }

  public FilterItemsAdapter(Context context, ArrayList<String> customerId,
      ArrayList<String> district) {
    this.customerId = customerId;
    this.district = district;
    this.context = context;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.itemview_filter_popup, parent, false);

//        System.out.println("=======in onCreateViewHolder=====");

    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.setIsRecyclable(false);

    if (position <= customerId.size()) {
      holder.mSpinnerCol2.setText("#" + customerId.get(position));
      holder.mSpinnerCol2.setTag(context.getString(R.string.customer_id));
    }
    if (position <= district.size()) {
      holder.mSpinnerCol1
          .setText(context.getString(R.string.district) + " " + district.get(position));
      holder.mSpinnerCol1.setTag(context.getString(R.string.district));
    }

    holder.mSpinnerCol1.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (getOnItemSelectedListener() != null) {
          onItemSelectedListener.onItemClickListener((TextView) v, district.get(position));
        }
      }
    });

    holder.mSpinnerCol2.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (getOnItemSelectedListener() != null) {
          onItemSelectedListener.onItemClickListener((TextView) v, customerId.get(position));
        }
      }
    });

  }

  public interface OnItemSelected {

    void onItemClickListener(TextView textView, String selectedItem);
  }

  @Override
  public int getItemCount() {
    if (district.size() > customerId.size()) {
      return district.size();
    } else {
      return customerId.size();
    }
  }


  public class ViewHolder extends RecyclerView.ViewHolder {

    private TextView mSpinnerCol1;
    private TextView mSpinnerCol2;


    public ViewHolder(View view) {
      super(view);
//            System.out.println("=======in ViewHolder=====");
      mSpinnerCol1 = view.findViewById(R.id.spinner_col_1);
      mSpinnerCol2 = view.findViewById(R.id.spinner_col_2);
    }
  }

}
