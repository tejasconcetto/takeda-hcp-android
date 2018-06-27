package com.takeda.android;

import android.app.Application;

/**
 * Created by premgoyal98 on 06.02.18.
 */

public class AppLockManager {

  private static AppLockManager instance;
  private DefaultApplock currentAppLocker;

  public static AppLockManager getInstance() {
    if (instance == null) {
      instance = new AppLockManager();
    }
    return instance;
  }

  public void enableDefaultAppLockIfAvailable(Application currentApp) {

    currentAppLocker = new DefaultApplock(currentApp);

  }

  public void updateTouch() {

    currentAppLocker.updateTouch();
  }
}
