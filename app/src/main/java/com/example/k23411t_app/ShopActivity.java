package com.example.k23411t_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbmodels.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Bài 15 - Client Shop: xem sản phẩm (realtime), search theo tên,
 * lọc theo category (qua intent extra), chạm sản phẩm = thêm vào giỏ.
 */
public class ShopActivity extends AppCompatActivity {
    EditText edtSearch;
    ListView lvProducts;
    Button btnCart;
    TextView tvFilter;
    ArrayAdapter<String> adapter;

    static class Row {
        String key;
        Product product;
    }

    final List<Row> allRows = new ArrayList<>();
    final List<Row> shown = new ArrayList<>();
    String categoryFilter; // null = tất cả

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        setTitle(getString(R.string.str_shop_title));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtSearch = findViewById(R.id.edtSearch);
        lvProducts = findViewById(R.id.lvProducts);
        btnCart = findViewById(R.id.btnCart);
        tvFilter = findViewById(R.id.tvFilter);
        Button btnCategories = findViewById(R.id.btnCategories);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvProducts.setAdapter(adapter);

        categoryFilter = getIntent().getStringExtra("categoryId");
        if (categoryFilter != null) {
            tvFilter.setVisibility(TextView.VISIBLE);
            tvFilter.setText(getString(R.string.str_shop_filter_by, categoryFilter));
        }

        btnCategories.setOnClickListener(v ->
                startActivity(new Intent(this, CategoriesViewActivity.class)));
        btnCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Row r = shown.get(position);
            Cart.add(r.key, r.product.getProductName(), r.product.getPrice(), r.product.getStock());
            Toast.makeText(this, getString(R.string.str_shop_added, r.product.getProductName()),
                    Toast.LENGTH_SHORT).show();
            updateCartButton();
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {
            }

            public void onTextChanged(CharSequence s, int a, int b, int c) {
                applyFilter();
            }

            public void afterTextChanged(Editable s) {
            }
        });

        loadProducts();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartButton();
    }

    private void updateCartButton() {
        btnCart.setText(getString(R.string.str_shop_cart) + " (" + Cart.count() + ")");
    }

    private void loadProducts() {
        FirebaseDatabase.getInstance().getReference("products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allRows.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Product p = ds.getValue(Product.class);
                            if (p == null) continue;
                            Row r = new Row();
                            r.key = ds.getKey();
                            r.product = p;
                            allRows.add(r);
                        }
                        applyFilter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("FIREBASE", "shop:onCancelled", error.toException());
                    }
                });
    }

    private void applyFilter() {
        String q = edtSearch.getText().toString().trim().toLowerCase();
        shown.clear();
        adapter.clear();
        for (Row r : allRows) {
            if (categoryFilter != null && !categoryFilter.equals(r.product.getCategoryId())) {
                continue;
            }
            String name = r.product.getProductName() == null ? "" : r.product.getProductName();
            if (!q.isEmpty() && !name.toLowerCase().contains(q)) {
                continue;
            }
            shown.add(r);
            adapter.add(r.product.summary());
        }
    }
}
