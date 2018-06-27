package com.takeda.android;

import android.app.ActivityManager;
import android.os.Build;
import android.util.Log;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.imagepipeline.cache.MemoryCacheParams;

/**
 * Created by microlentsystems on 03/04/18.
 */

public class FrescoCacheParams implements Supplier<MemoryCacheParams> {


  private ActivityManager activityManager;

  public FrescoCacheParams(ActivityManager activityManager) {
    this.activityManager = activityManager;
  }

  @Override
  public MemoryCacheParams get() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

      int cacheSize = getMaxCacheSize();
      Log.d("####", "fresco cache size = " + cacheSize);

      return new MemoryCacheParams(cacheSize, 1, 1, 1, 1);
    } else {
      return new MemoryCacheParams(
          getMaxCacheSize(),
          256,
          Integer.MAX_VALUE,
          Integer.MAX_VALUE,
          Integer.MAX_VALUE);
    }
  }

  private int getMaxCacheSize() {
    final int maxMemory = Math.min(activityManager.getMemoryClass()
        * ByteConstants.MB, Integer.MAX_VALUE);

    if (maxMemory < 32 * ByteConstants.MB) {
      return 4 * ByteConstants.MB;
    } else if (maxMemory < 64 * ByteConstants.MB) {
      return 6 * ByteConstants.MB;
    } else {

      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD) {
        return 8 * ByteConstants.MB;
      } else {
        return maxMemory / 6;
      }
    }
  }
}

