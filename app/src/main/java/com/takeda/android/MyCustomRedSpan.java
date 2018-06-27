package com.takeda.android;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

/**
 * Created by microlentsystems on 01/04/18.
 */

public class MyCustomRedSpan implements LineBackgroundSpan {

  int color;
  int radius;


  public MyCustomRedSpan(int radius, int color) {

    this.color = color;
    this.radius = radius;
  }

  @Override
  public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline,
      int bottom, CharSequence text, int start, int end, int lnum) {
    int oldColor = paint.getColor();

    if (color != 0) {
      paint.setColor(color);
    }

    canvas.drawCircle((left + right) / 2, bottom + radius + 10, radius, paint);
    paint.setColor(oldColor);
  }
}
