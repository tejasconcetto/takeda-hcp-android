package com.takeda.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.takeda.android.R;
import com.takeda.android.adapters.FilterAdapter.ViewHolder;
import java.util.ArrayList;

public class FilterAdapter extends Adapter<ViewHolder> {

  private final ArrayList<String> filterList;
  private final Context context;

  public FilterAdapter(ArrayList<String> filterList, Context context) {
    this.filterList = filterList;
    this.context = context;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.filter_item_view, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.mFilterItemText.setText(filterList.get(position));
    holder.setIsRecyclable(false);
  }

  @Override
  public int getItemCount() {
    return filterList.size();
  }


  public class ViewHolder extends RecyclerView.ViewHolder {

    private TextView mFilterItemText;

    public ViewHolder(View itemView) {
      super(itemView);
      mFilterItemText = itemView.findViewById(R.id.filter_item_text);

    }
  }
}
