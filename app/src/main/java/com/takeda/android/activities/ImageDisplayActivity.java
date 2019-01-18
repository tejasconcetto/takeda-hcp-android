package com.takeda.android.activities;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.skk.lib.BaseClasses.BaseActivity;
import com.takeda.android.R;

public class ImageDisplayActivity extends BaseActivity {

    ImageView ivClose, ivSalesPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        initViews();
        getIntentData();
        setOnClickOfButton();

    }

    private void setOnClickOfButton() {
        ivClose.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void getIntentData() {
        try {
            String imageUrl = getIntent().getStringExtra(getString(R.string.image_url));
            if (imageUrl != null && !imageUrl.isEmpty())
                Glide.with(this)
                        .load(imageUrl)
                        .into(ivSalesPerson);
        } catch (Exception e) {

        }
    }

    private void initViews() {
        ivClose = findViewById(R.id.ivCloseBtn);
        ivSalesPerson = findViewById(R.id.salesPersonImage);
    }
}
