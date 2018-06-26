package com.takeda.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;


/**
 * Created by amitkumar on 9/8/16.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

  private ArrayList<Fragment> list = new ArrayList<Fragment>();
  private ArrayList<String> titles = new ArrayList<String>();

  public MyPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  public void addFragment(Fragment fragment) {
    list.add(fragment);
  }

  public void addFragment(String title, Fragment fragment) {
    titles.add(title);
    list.add(fragment);
  }

  @Override
  public Fragment getItem(int position) {
    return list.get(position);
  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public CharSequence getPageTitle(int position) {
    if (position < titles.size()) {
      return titles.get(position);
    }
    return super.getPageTitle(position);
  }
}//class