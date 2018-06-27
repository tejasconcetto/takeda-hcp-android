package com.takeda.android.rest;

import com.takeda.android.model.BannerModel;
import com.takeda.android.model.CompanyProfileModel;
import com.takeda.android.model.CustomerRecordModel;
import com.takeda.android.model.EnquiryModel;
import com.takeda.android.model.EventModal;
import com.takeda.android.model.LoginModel;
import com.takeda.android.model.PastRecordModel;
import com.takeda.android.model.ProductModel;
import com.takeda.android.model.TermsAndConditionsModel;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;


/**
 * Created by premgoyal98 on 15.03.18.
 */

public interface ApiInterface {

//    @POST("/api/login")
//    @FormUrlEncoded
//    Call<LoginModel> UserLogin(@Field("user_name") String username,
//                          @Field("password") String password);

  @FormUrlEncoded
  @POST("/my_login")
  void UserLogin(@Field("user_name") String username,
      @Field("password") String password,
      Callback<LoginModel> callback);

  @FormUrlEncoded
  @POST("/my_logout")
  void UserLogout(@Field("doctor_id") String doctor_id,
      @Field("access_token") String access_token,
      Callback<LoginModel> callback);

  @FormUrlEncoded
  @POST("/profile/my_update")
  void UserProfileUpdateFirst(@Field("doctor_id") String doctor_id,
      @Field("user_name") String user_name,
      @Field("email") String email,
      @Field("mobile_number") String mobile_number,
      @Field("password") String password,
      @Field("access_token") String access_token,
      Callback<LoginModel> callback);

  @FormUrlEncoded
  @POST("/profile/my_update")
  void UserProfileUpdate(@Field("doctor_id") String doctor_id,
      @Field("email") String email,
      @Field("mobile_number") String mobile_number,
      @Field("access_token") String access_token,
      Callback<LoginModel> callback);

  @FormUrlEncoded
  @POST("/my_change-password")
  void UserChangePassword(@Field("doctor_id") String doctor_id,
      @Field("user_name") String user_name,
      @Field("current_password") String current_password,
      @Field("password") String password,
      @Field("access_token") String access_token,
      Callback<LoginModel> callback);

  @FormUrlEncoded
  @POST("/my_banners")
  void Banners(@Field("doctor_id") String doctor_id,
      @Field("banner_location") String location_id,
      @Field("banner_specialty") String specialty_id,
      @Field("banner_sector") String sector_id,
      @Field("banner_class") String class_id,
      @Field("banner_included") String banner_included,
      @Field("banner_excluded") String banner_excluded,
      @Field("access_token") String access_token,
      Callback<BannerModel> callback);

  @FormUrlEncoded
  @POST("/my_products")
  void Products(@Field("doctor_id") String doctor_id,
      @Field("access_token") String access_token,
      Callback<ProductModel> callback);

  @FormUrlEncoded
  @POST("/my_purchase_history")
  void PurchaseHistory(@Field("doctor_id") String doctor_id,
      @Field("order_by") String order_by,
      @Field("access_token") String access_token,
      Callback<PastRecordModel> callback);

  @FormUrlEncoded
  @POST("/my_purchase_history")
  void FilterPurchaseHistoryByCustomerId(
      @Field("customer_id") String customer_id,
      @Field("doctor_id") String doctor_id,
      @Field("order_by") String order_by,
      @Field("access_token") String access_token,
      Callback<PastRecordModel> callback);

  @FormUrlEncoded
  @POST("/my_purchase_history")
  void FilterPurchaseHistoryByDistrict(
      @Field("district") String district,
      @Field("doctor_id") String doctor_id,
      @Field("order_by") String order_by,
      @Field("access_token") String access_token,
      Callback<PastRecordModel> callback);

  @FormUrlEncoded
  @POST("/my_sales-persons")
  void SalesPersons(@Field("doctor_id") String doctor_id,
      @Field("access_token") String access_token,
      Callback<EnquiryModel> callback);

  @FormUrlEncoded
  @POST("/sales/my_enquiry")
  void SalesEnquiry(@Field("doctor_id") String doctor_id,
      @Field("sales_id") String sales_id,
      @Field("enquiry_type") String enquiry_type,
      @Field("access_token") String access_token,
      Callback<EnquiryModel> callback);

  @FormUrlEncoded
  @POST("/my_get_events")
  void GetEvent(@Field("doctor_id") String doctor_id,
      @Field("event_month") int event_month,
      @Field("event_year") int event_year,
      @Field("access_token") String access_token,
      Callback<EventModal> callback);

  @FormUrlEncoded
  @POST("/my_interested-event")
  void InterestedEvent(@Field("doctor_id") String doctor_id,
      @Field("event_id") String event_id,
      @Field("action") String action,
      @Field("access_token") String access_token,
      Callback<EventModal> callback);

  @FormUrlEncoded
  @POST("/my_bookmark-event")
  void BookmarkEvent(@Field("doctor_id") String doctor_id,
      @Field("event_id") String event_id,
      @Field("action") String action,
      @Field("access_token") String access_token,
      Callback<EventModal> callback);

  @FormUrlEncoded
  @POST("/my_accept-special-offers")
  void AcceptSpecialOffers(@Field("doctor_id") String doctor_id,
      @Field("banner_id") String banner_id,
      @Field("access_token") String access_token,
      Callback<BannerModel> callback);

  @FormUrlEncoded
  @POST("/my_company-profile")
  void CompanyProfile(@Field("doctor_id") String doctor_id,
      @Field("access_token") String access_token,
      Callback<CompanyProfileModel> callback);

  @FormUrlEncoded
  @POST("/termsandconditions/fetch")
  void TermsAndConditions(@Field("doctor_id") String doctor_id,
      @Field("access_token") String access_token,
      Callback<TermsAndConditionsModel> callback);

  @FormUrlEncoded
  @POST("/termsandconditions/my_update")
  void TermsAndConditionsAccept(@Field("doctor_id") String doctor_id,
      @Field("access_token") String access_token,
      @Field("accepted") Boolean accepted,
      @Field("timeStamp") Long timeStamp,
      Callback<TermsAndConditionsModel> callback);

  @FormUrlEncoded
  @POST("/my_customer-record")
  void getCustomerRecord(@Field("doctor_id") String doctor_id,
      @Field("access_token") String access_token, Callback<CustomerRecordModel> callback);

}
