package com.takeda.android;

import android.content.Context;
import android.util.Log;

/**
 * Created by premgoyal98 on 06.02.18.
 */

public class Waiter extends Thread {

  private static final String TAG = Waiter.class.getName();
  private long lastUsed;
  private long period;
  private boolean stop = false;
  private Context mContext;

  public Waiter(Context context, long period) {
    this.period = period;
    stop = false;
    mContext = context;
  }

  public void run() {
    long idle = 0;
    this.touch();
    Log.d("Value of stop", String.valueOf(stop));
    do {
      idle = System.currentTimeMillis() - lastUsed;
//            Log.d(TAG, "Application is idle for "+idle +" ms");
      try {
        Thread.sleep(2000); //check every 5 seconds
      } catch (InterruptedException e) {
        Log.d(TAG, "Waiter interrupted!");
      }
      if (idle > period) {
        idle = 0;
        //do something here - e.g. call popup or so

        // Perform Your desired Function like Logout or expire the session for the app.
        stopThread();
      }
    }
    while (!stop);
    Log.d(TAG, "Finishing Waiter thread");
  }

  public synchronized void touch() {
    lastUsed = System.currentTimeMillis();
  }

  public synchronized void forceInterrupt() {
    this.interrupt();
  }

  public synchronized void setPeriod(long period) {
    this.period = period;
  }

  public synchronized void stopThread() {
    stop = true;
  }

  public synchronized void startThread() {
    stop = false;
  }

  public synchronized void closeThread() {
    // Perform Your desired Function like Logout or expire the session for the app.
    stopThread();
  }


}