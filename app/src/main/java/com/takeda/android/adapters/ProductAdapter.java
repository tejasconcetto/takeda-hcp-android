package com.takeda.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.Interfaces.OnEnquiryClick;
import com.skk.lib.utils.AppDelegate;
import com.takeda.android.R;
import com.takeda.android.model.ProductModel;
import java.util.ArrayList;

/**
 * Created by Bharat Gupta on 9/5/2015.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {


  private BaseActivity activity;
  private ArrayList<ProductModel.ProductsArrDataModel> productList;
  private static LayoutInflater inflater = null;
  private String categoryName;
  OnEnquiryClick mPrdClick;
  OnCardClickListner onCardClickListner;

  public ProductAdapter(BaseActivity a, ArrayList<ProductModel.ProductsArrDataModel> cartData,
      String categoryName, OnEnquiryClick mPrdClick) {

    activity = a;
    this.productList = cartData;
    this.categoryName = categoryName;
    this.mPrdClick = mPrdClick;
    inflater = (LayoutInflater) activity.
        getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    //Fresco.initialize(activity);   /* by johnny

  }

    /*@Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int pos) {
        return pos;
    }

    @Override
    public long getItemId(int id) {
        return id;
    }*/

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public SimpleDraweeView tv_product_image;
    public TextView tv_product_name;
    public TextView tv_description;
    public TextView tv_category;
    public LinearLayout relative_layout_user;


    public ViewHolder(View itemView) {
      super(itemView);

      tv_description = itemView.findViewById(R.id.tv_description);
      tv_category = itemView.findViewById(R.id.tv_category);
      tv_product_name = itemView.findViewById(R.id.product_name);
      tv_product_image = itemView.findViewById(R.id.product_image);
      relative_layout_user = itemView.findViewById(R.id.relative_layout_user);


    }


  }

  public interface OnCardClickListner {

    void OnCardClicked(View view, int position);
  }

  public void setOnCardClickListner(OnCardClickListner onCardClickListner) {
    this.onCardClickListner = onCardClickListner;
  }


  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.product_row_item, parent, false);

    ViewHolder vh = new ViewHolder(view);

    return vh;

  }

  @Override
  public void onBindViewHolder(ViewHolder holder, final int position) {

    // to set data from product class otherwise it will show temperary data stored in respective xml
    if (productList.size() > 0) {
      final ProductModel.ProductsArrDataModel enquiryEntity = productList.get(position);

      holder.tv_category
          .setText("Category : " + AppDelegate.checkString(enquiryEntity.category_name));

      System.out.println("position======>" + position);
//            System.out.println("enquiryEntity======>"+enquiryEntity);
//            System.out.println("enquiryEntity.productsData.size()======>"+enquiryEntity.productsData.size());
//            System.out.println("enquiryEntity.productsData======>"+enquiryEntity.productsData);

//            for (int i = 0; i < enquiryEntity.productsData.size(); i++)
//            {
      holder.tv_description.setText("" + AppDelegate.checkString(enquiryEntity.ingredient));

      holder.tv_product_name.setText("" + enquiryEntity.product_name);
//            System.out.println("enquiryEntity.productsData.get(i).product_name======>"+enquiryEntity.product_name);
//            }

      if (enquiryEntity.product_images.size() > 0) {
        activity.showLogs("ProductURL",
            enquiryEntity.product_images.get(0).product_image_url + ", STatus - " + URLUtil
                .isValidUrl(enquiryEntity.product_images.get(0).product_image_url));
        AppDelegate.loadImageFromPicasaRactangle(activity, holder.tv_product_image,
            enquiryEntity.product_images.get(0).product_image_url);
        //holder.tv_product_image.setImageURI("enquiryEntity.product_images.get(0).product_image_url");
        //holder.setIsRecyclable(false);

      }

      holder.relative_layout_user.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//                    System.out.println("onCardClickListner=======>"+onCardClickListner);
//                    System.out.println("view=======>"+view);
//                    System.out.println("position=======>"+position);
//                    System.out.println("onCardClickListner=======>"+onCardClickListner);
          onCardClickListner.OnCardClicked(view, position);
        }
      });
    }


  }


  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
  }

  @Override
  public int getItemCount() {
    return productList.size();
  }

    /*@Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            *//****** Inflate tabitem.xml file for each row ( productquantityDefined below ) *******//*
            vi = inflater.inflate(R.layout.product_row_item, null);

            holder = new ViewHolder();
            holder.tv_description = (TextView) vi.findViewById(R.id.tv_description);
            holder.tv_category = (TextView) vi.findViewById(R.id.tv_category);
            holder.tv_product_name = (TextView) vi.findViewById(R.id.product_name);
            holder.tv_product_image = (SimpleDraweeView) vi.findViewById(R.id.product_image);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        // to set data from product class otherwise it will show temperary data stored in respective xml
        if (productList.size() > 0) {
            final ProductModel.ProductsArrDataModel enquiryEntity = productList.get(position);

            holder.tv_category.setText("Category : "+AppDelegate.checkString(enquiryEntity.category_name));

            System.out.println("position======>"+position);
            System.out.println("enquiryEntity======>"+enquiryEntity);
//            System.out.println("enquiryEntity.productsData.size()======>"+enquiryEntity.productsData.size());
//            System.out.println("enquiryEntity.productsData======>"+enquiryEntity.productsData);

//            for (int i = 0; i < enquiryEntity.productsData.size(); i++)
//            {
                holder.tv_description.setText("" + AppDelegate.checkString(enquiryEntity.indication));

                holder.tv_product_name.setText("" + enquiryEntity.product_name);
                System.out.println("enquiryEntity.productsData.get(i).product_name======>"+enquiryEntity.product_name);
//            }



            activity.showLogs("ProductURL",enquiryEntity.product_images.get(0).product_image_url+", STatus - "+ URLUtil.isValidUrl(enquiryEntity.product_images.get(0).product_image_url));
            AppDelegate.loadImageFromPicasaRactangle(activity,holder.tv_product_image,
                                                     enquiryEntity.product_images.get(0).product_image_url);
        }
        return vi;
    }*/
}