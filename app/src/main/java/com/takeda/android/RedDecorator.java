package com.takeda.android;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import java.util.HashSet;

/**
 * Created by microlentsystems on 01/04/18.
 */

public class RedDecorator implements DayViewDecorator {

  Context mContext;
  HashSet<CalendarDay> dates;

  public RedDecorator(Context mContext, HashSet<CalendarDay> dates) {

    this.mContext = mContext;
    this.dates = dates;

  }

  @Override
  public boolean shouldDecorate(CalendarDay day) {
    return (dates.contains(day));
  }

  @Override
  public void decorate(DayViewFacade view) {

    view.addSpan(new MyCustomRedSpan(5, ContextCompat.getColor(mContext, R.color.red_offers)));

       /* Drawable drawable = ContextCompat.getDrawable(getContext(),R.drawable.circle_counter);
        int horizontalMargin = 5;
        int verticalMargin = 5;
        InsetDrawable insetDrawable = new InsetDrawable(drawable,horizontalMargin,verticalMargin,horizontalMargin,verticalMargin);
        view.setBackgroundDrawable(insetDrawable);
        view.addSpan(new ForegroundColorSpan(Color.WHITE));

*/
  }
}
