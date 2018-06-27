package com.takeda.android;

import com.takeda.android.model.ProductModel;
import java.util.Comparator;

/**
 * Created by premgoyal98 on 05.02.18.
 */

public class SortBasedOnName implements Comparator {

  public int compare(Object o1, Object o2) {

    ProductModel.ProductsArrDataModel dd1 = (ProductModel.ProductsArrDataModel) o1;
    ProductModel.ProductsArrDataModel dd2 = (ProductModel.ProductsArrDataModel) o2;
    return dd1.product_name.compareToIgnoreCase(dd2.product_name);
//        return 0;
  }
}