package com.takeda.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.R;
import com.takeda.android.model.CompanyProfileModel;
import com.takeda.android.rest.ApiInterface;
import com.takeda.android.rest.RestAdapterService;

import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AboutUsActivity extends BaseActivity {

    String company_name;
    TextView titleTextView;
    public int selectedId;
    SupportMapFragment mapFragment;
    private GoogleMap map;
    double latitude = 0;
    double longitude = 0;
    SessionManager session;
    private ProgressDialog dialog;
    AlertDialog alert1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        titleTextView = findViewById(R.id.titleTV);
        session = new SessionManager(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);

        findViewById(R.id.backArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.get_dir_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                } catch (Exception e) {
                    showSBar("No app found to open Map.");
                }
            }
        });

        findViewById(R.id.tandc_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openTandCDialog(new String[]{session.getUserDetails().get(SessionManager.KEY_USER_ID),
                            session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN)}, null, null, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.mobile_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(AboutUsActivity.this);
                final View promptView = layoutInflater.inflate(R.layout.enquiry_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AboutUsActivity.this,
                        R.style.CustomDialog);
                alertDialogBuilder.setView(promptView);

                final Button posBtn = promptView.findViewById(R.id.posBtn);
                final Button negBtn = promptView.findViewById(R.id.negBtn);
                final ImageView dialogIcon = promptView.findViewById(R.id.dialogIcon);
                final TextView contactDetail = promptView.findViewById(R.id.contactDetail);
                final TextView message = promptView.findViewById(R.id.contactMsg);
                final TextView title = promptView.findViewById(R.id.dialog_title);

                String positiveBtnName = "CALL NOW";
                title.setText("Call Us Now");
//            message.setText("Call Us Now");
                message.setVisibility(View.GONE);
                contactDetail
                        .setText(((TextView) findViewById(R.id.company_contact_no)).getText().toString());
                dialogIcon.setImageResource(R.drawable.icn_mobile);
                posBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (alert1 != null) {
                            alert1.dismiss();
                        }
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse(
                                    "tel:" + ((TextView) findViewById(R.id.company_contact_no)).getText()
                                            .toString()));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                posBtn.setText(positiveBtnName);

                alert1 = alertDialogBuilder.create();
                alert1.setCancelable(false);
                alert1.show();

                negBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert1.dismiss();
                    }
                });


            }
        });

        init();
    }

    void init() {
        if (dialog != null) {
            dialog.show();
        }
        try {
            ApiInterface api = RestAdapterService.createService(ApiInterface.class);

            api.CompanyProfile(session.getUserDetails().get(SessionManager.KEY_USER_ID),
                    session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN),
                    new Callback<CompanyProfileModel>() {
                        @Override
                        public void success(final CompanyProfileModel companyProfileModel, Response response) {
                            System.out.println("=======success in CompanyProfile api call=====");
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }

                            try {

                                if (companyProfileModel.response.status.equalsIgnoreCase("success")) {

//                                    final JSONObject jsonObject = jsonResponse.getJSONObject(Params.response_success_data).getJSONObject("company_details");
                                    setTitleText("About Us");
                                    company_name = companyProfileModel.response.dataUser.companyDetailModel.company_name;
                                    ((TextView) findViewById(R.id.company_contact_no)).setText(
                                            companyProfileModel.response.dataUser.companyDetailModel.company_mobile);
                                    ((TextView) findViewById(R.id.company_description)).setText(
                                            companyProfileModel.response.dataUser.companyDetailModel.company_name);
                                    ((TextView) findViewById(R.id.company_email)).setText(
                                            companyProfileModel.response.dataUser.companyDetailModel.company_email);
                                    ((TextView) findViewById(R.id.company_timings)).setText(
                                            companyProfileModel.response.dataUser.companyDetailModel.company_time);
                                    ((TextView) findViewById(R.id.company_website)).setText(
                                            companyProfileModel.response.dataUser.companyDetailModel.company_website);
                                    ((TextView) findViewById(R.id.company_address)).setText(
                                            companyProfileModel.response.dataUser.companyDetailModel.company_address);

                                    findViewById(R.id.company_website).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                openURL(
                                                        companyProfileModel.response.dataUser.companyDetailModel.company_website);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    latitude = Double.parseDouble(
                                            companyProfileModel.response.dataUser.companyDetailModel.latitude);
                                    longitude = Double.parseDouble(
                                            companyProfileModel.response.dataUser.companyDetailModel.longitude);

                                    mapFragment = ((SupportMapFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.map));
                                    if (mapFragment != null) {
                                        findViewById(R.id.map_frame).setVisibility(View.VISIBLE);
                                        findViewById(R.id.map_frame_image).setVisibility(View.GONE);
                                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(GoogleMap map) {
                                                loadMap(map);
                                            }
                                        });
                                    } else {
                                        findViewById(R.id.map_frame).setVisibility(View.GONE);
                                        findViewById(R.id.map_frame_image).setVisibility(View.VISIBLE);
                                        showToast("Error Occurred. Please try again");
                                    }

                                } else {
                                    msgAlertDialog("Error", companyProfileModel.response.statusMessage);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            System.out.println("=======failure in CompanyProfile api call=====");
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }

                        }
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*old code
        try{
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("doctor_id",session.getUserDetails().get(SessionManager.KEY_USER_ID));

            new UserTask().getJsonRequest(new resultInterface() {
                @Override
                public void Success(JSONObject jsonResponse, String requestCall) {
                    try{
                        final JSONObject jsonObject = jsonResponse.getJSONObject(Params.response_success_data).getJSONObject("company_details");
                        setTitleText("About Me");
                        company_name = jsonObject.getString("company_name");
                        ((TextView)findViewById(R.id.company_contact_no)).setText(jsonObject.getString("company_mobile"));
                        ((TextView)findViewById(R.id.company_description)).setText(jsonObject.getString("company_name"));
                        ((TextView)findViewById(R.id.company_email)).setText(jsonObject.getString("company_email"));
                        ((TextView)findViewById(R.id.company_timings)).setText(jsonObject.getString("company_time"));
                        ((TextView)findViewById(R.id.company_website)).setText(jsonObject.getString("company_website"));
                        ((TextView)findViewById(R.id.company_address)).setText(jsonObject.getString("company_address"));

                        ((TextView)findViewById(R.id.company_website)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try{
                                    openURL(jsonObject.getString("company_website"));
                                }

                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });

                        latitude = jsonObject.getDouble("latitude");
                        longitude = jsonObject.getDouble("longitude");

                        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
                        if (mapFragment != null) {
                            findViewById(R.id.map_frame).setVisibility(View.VISIBLE);
                            findViewById(R.id.map_frame_image).setVisibility(View.GONE);
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap map) {
                                    loadMap(map);
                                }
                            });
                        } else {
                            findViewById(R.id.map_frame).setVisibility(View.GONE);
                            findViewById(R.id.map_frame_image).setVisibility(View.VISIBLE);
                            showToast("Error Occurred. Please try again");
                        }
                    }

                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            },jsonParams,true,this,findViewById(R.id.coordinator_layout),Params.request_code_company_profile);

        }

        catch (Exception e){
            e.printStackTrace();
        }*/
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            showLogs("MapFragment", "Map Fragment was loaded properly!");
            Log.d("LatLong", "Latitude:" + latitude + ", Longitude:" + longitude);
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title(company_name);
            googleMap.addMarker(marker);
            moveToCurrentLocation(new LatLng(latitude, longitude));
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveToCurrentLocation(LatLng currentLocation) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setTitleText(String title) {
        titleTextView.setText(title);
    }
}

