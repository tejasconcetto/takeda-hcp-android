package com.takeda.android.adapters;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.skk.lib.utils.AppDelegate;
import com.takeda.android.R;
import java.util.ArrayList;

/**
 * Created by bharat on 23/10/15.
 */
public class ViewPagerAdapter extends android.support.v4.view.PagerAdapter implements
    ViewPager.OnPageChangeListener {

  // Declare Variables
  Context context;
  ArrayList<String> arrayList;
  LayoutInflater inflater;
  int type;

  public ViewPagerAdapter(Context context, ArrayList<String> arrayList) {
    this.context = context;
    this.arrayList = arrayList;
    this.type = 0;
  }


  @Override
  public int getCount() {
    return arrayList.size();
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {

// Declare Variables
    SimpleDraweeView imgflag;

    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View itemView;

    itemView = inflater.inflate(R.layout.viewpager_item, container, false);
    imgflag = itemView.findViewById(R.id.flag);

    System.out.println("arrayList.get(position)=======>" + arrayList.get(position));

    AppDelegate.loadImageFromPicasaRactangle(context, imgflag, arrayList.get(position));
    container.addView(itemView);
    return itemView;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    // Remove viewpager_item.xml from ViewPager
    container.removeView((LinearLayout) object);
  }

  @Override
  public void onPageScrollStateChanged(int arg0) {
    // TODO Auto-generated method stub
    int myint = 9;
    myint = myint * 2;
  }

  @Override
  public void onPageScrolled(int arg0, float arg1, int arg2) {
    // TODO Auto-generated method stub
    int myint = 9;
    myint = myint * 2;
  }

  @Override
  public void onPageSelected(int arg0) {
    // TODO Auto-generated method stub
    int myint = 9;
    myint = myint * 2;
  }//    @Override
}