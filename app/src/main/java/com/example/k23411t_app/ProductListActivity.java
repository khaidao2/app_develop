package com.example.k23411t_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adapters.ProductAdapter;
import com.example.dals.ProductDAO;
import com.example.models.Product;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    private TextView txt_title;
    private ListView lv_product;
    private ProductAdapter productAdapter;
    private ProductDAO productDAO;
    private String cateId;
    private String cateName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy thông tin Category truyền sang từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            cateId = intent.getStringExtra("CateID");
            cateName = intent.getStringExtra("CategoryName");
        }

        initViews();
        loadProducts();
    }

    private void initViews() {
        txt_title = findViewById(R.id.txt_title);
        lv_product = findViewById(R.id.lv_product);
        productDAO = new ProductDAO(this);
        productAdapter = new ProductAdapter(this, R.layout.item_custom_product);
        lv_product.setAdapter(productAdapter);

        // Thiết lập tiêu đề theo tên Category
        if (cateName != null && !cateName.isEmpty()) {
            String titleBase = getString(R.string.str_product_list_title);
            txt_title.setText(titleBase + ": " + cateName);
        }
    }

    private void loadProducts() {
        if (cateId == null || cateId.isEmpty()) return;

        try {
            ArrayList<Product> products = productDAO.getProductsByCategory(cateId);
            productAdapter.clear();
            if (products != null) {
                productAdapter.addAll(products);
            }
            productAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.str_load_product_failed), Toast.LENGTH_SHORT).show();
        }
    }

    public void closeActivity(View view) {
        finish();
    }
}
