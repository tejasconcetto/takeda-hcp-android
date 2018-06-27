package com.takeda.android.rest;

import com.skk.lib.utils.SessionManager;


/**
 * Created by premgoyal98 on 15.03.18.
 */

public class ApiClient {

  /********
   * URLS
   *******/
  private static final String ROOT_URL = SessionManager.BASE_URL_AWS;

  /**
   * Get Retrofit Instance
   */
    /*private static Retrofit getRetrofitInstance() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }*/

  /**
   * Get API Service
   *
   * @return API Service
   */
    /*public static ApiInterface getApiService() {
        return getRetrofitInstance().create(ApiInterface.class);
    }*/

}