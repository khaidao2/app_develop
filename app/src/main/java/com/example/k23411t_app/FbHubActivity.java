package com.example.k23411t_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Bài 15 - Firebase Manager: hub mở 4 màn quản lý entity trên Realtime Database.
 */
public class FbHubActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_hub);

        findViewById(R.id.btnFbProducts).setOnClickListener(v ->
                startActivity(new Intent(this, ProductListActivity2.class)));
        findViewById(R.id.btnFbCategories).setOnClickListener(v ->
                startActivity(new Intent(this, CategoryListActivity.class)));
        findViewById(R.id.btnFbCustomers).setOnClickListener(v ->
                startActivity(new Intent(this, CustomerListActivity.class)));
        findViewById(R.id.btnFbEmployees).setOnClickListener(v ->
                startActivity(new Intent(this, EmployeeListActivity.class)));
    }
}
