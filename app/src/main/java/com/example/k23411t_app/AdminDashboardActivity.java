package com.example.k23411t_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbmodels.Customer;
import com.example.fbmodels.Order;
import com.example.fbmodels.OrderDetail;
import com.example.fbmodels.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Bài 15 - Admin Dashboard nâng cấp: thẻ thống kê, biểu đồ cột, và
 * danh sách khách hàng / sản phẩm có thanh tìm kiếm + bộ lọc.
 * Đọc cả cây DB 1 lần rồi tổng hợp & lọc tại client.
 */
public class AdminDashboardActivity extends AppCompatActivity {

    // Thẻ thống kê
    TextView tvRevenue, tvOrderCount, tvCustomerCount, tvProductCount, tvUpdated;
    // Biểu đồ
    BarChartView chartProducts, chartCustomers, chartStatus;
    // Khu khách hàng
    EditText edtSearchCustomer;
    LinearLayout containerCustomers;
    TextView tvCustomerEmpty;
    // Khu sản phẩm
    EditText edtSearchProduct;
    Spinner spCategory, spStatus;
    LinearLayout containerProducts;
    TextView tvProductEmpty;

    // Dữ liệu đã nạp
    private final List<CustomerRow> customers = new ArrayList<>();
    private final List<ProductRow> products = new ArrayList<>();
    private final List<String> categoryFilter = new ArrayList<>(); // "Tất cả" + các categoryId

    private static final int[] BAR_COLORS = {
            Color.parseColor("#6200EE"), Color.parseColor("#1565C0"),
            Color.parseColor("#2E7D32"), Color.parseColor("#D84315"),
            Color.parseColor("#6A1B9A"), Color.parseColor("#00838F"),
    };

    /** Khách hàng kèm key + tổng chi tiêu. */
    static class CustomerRow {
        String key;
        Customer c;
        double spend;
    }

    /** Sản phẩm kèm key + số lượng đã bán. */
    static class ProductRow {
        String key;
        Product p;
        long sold;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        setTitle(getString(R.string.str_dash_title));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvRevenue = findViewById(R.id.tvRevenue);
        tvOrderCount = findViewById(R.id.tvOrderCount);
        tvCustomerCount = findViewById(R.id.tvCustomerCount);
        tvProductCount = findViewById(R.id.tvProductCount);
        tvUpdated = findViewById(R.id.tvUpdated);

        chartProducts = findViewById(R.id.chartProducts);
        chartCustomers = findViewById(R.id.chartCustomers);
        chartStatus = findViewById(R.id.chartStatus);

        edtSearchCustomer = findViewById(R.id.edtSearchCustomer);
        containerCustomers = findViewById(R.id.containerCustomers);
        tvCustomerEmpty = findViewById(R.id.tvCustomerEmpty);

        edtSearchProduct = findViewById(R.id.edtSearchProduct);
        spCategory = findViewById(R.id.spCategory);
        spStatus = findViewById(R.id.spStatus);
        containerProducts = findViewById(R.id.containerProducts);
        tvProductEmpty = findViewById(R.id.tvProductEmpty);

        setupSearchAndFilters();
        loadStats();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupSearchAndFilters() {
        edtSearchCustomer.addTextChangedListener(new SimpleWatcher(this::renderCustomers));
        edtSearchProduct.addTextChangedListener(new SimpleWatcher(this::renderProducts));

        // Lọc theo trạng thái sản phẩm
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{getString(R.string.str_dash_all),
                        getString(R.string.str_dash_active),
                        getString(R.string.str_dash_inactive)});
        spStatus.setAdapter(statusAdapter);

        AdapterView.OnItemSelectedListener reRender = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderProducts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spStatus.setOnItemSelectedListener(reRender);
        spCategory.setOnItemSelectedListener(reRender);
    }

    private void loadStats() {
        FirebaseDatabase.getInstance().getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot root) {
                        try {
                            computeAndShow(root);
                        } catch (Exception ex) {
                            Log.e("FIREBASE", "dashboard error", ex);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("FIREBASE", "dashboard:onCancelled", error.toException());
                    }
                });
    }

    private void computeAndShow(DataSnapshot root) {
        // 1) Doanh số + số đơn + chi tiêu theo customer + doanh số theo trạng thái
        double revenue = 0;
        int orderCount = 0;
        Map<String, Double> spendByCustomer = new HashMap<>();
        Map<String, Double> revenueByStatus = new LinkedHashMap<>();
        for (DataSnapshot ds : root.child("orders").getChildren()) {
            Order o = ds.getValue(Order.class);
            if (o == null) continue;
            revenue += o.getTotalAmount();
            orderCount++;
            spendByCustomer.put(o.getCustomerId(),
                    spendByCustomer.getOrDefault(o.getCustomerId(), 0d) + o.getTotalAmount());
            String st = o.getStatus() == null ? "?" : o.getStatus();
            revenueByStatus.put(st, revenueByStatus.getOrDefault(st, 0d) + o.getTotalAmount());
        }

        // 2) Số lượng bán theo product
        Map<String, Long> qtyByProduct = new HashMap<>();
        for (DataSnapshot ds : root.child("orderDetails").getChildren()) {
            OrderDetail d = ds.getValue(OrderDetail.class);
            if (d == null) continue;
            qtyByProduct.put(d.getProductId(),
                    qtyByProduct.getOrDefault(d.getProductId(), 0L) + d.getQuantity());
        }

        // 3) Nạp customers
        customers.clear();
        for (DataSnapshot ds : root.child("customers").getChildren()) {
            Customer c = ds.getValue(Customer.class);
            if (c == null) continue;
            CustomerRow row = new CustomerRow();
            row.key = ds.getKey();
            row.c = c;
            row.spend = spendByCustomer.getOrDefault(ds.getKey(), 0d);
            customers.add(row);
        }
        customers.sort((a, b) -> Double.compare(b.spend, a.spend));

        // 4) Nạp products
        products.clear();
        for (DataSnapshot ds : root.child("products").getChildren()) {
            Product p = ds.getValue(Product.class);
            if (p == null) continue;
            ProductRow row = new ProductRow();
            row.key = ds.getKey();
            row.p = p;
            row.sold = qtyByProduct.getOrDefault(ds.getKey(), 0L);
            products.add(row);
        }
        products.sort((a, b) -> Long.compare(b.sold, a.sold));

        // 5) Thẻ thống kê
        tvRevenue.setText(String.format("%,.0fđ", revenue));
        tvOrderCount.setText(String.valueOf(orderCount));
        tvCustomerCount.setText(String.valueOf(customers.size()));
        tvProductCount.setText(String.valueOf(products.size()));
        tvUpdated.setText(getString(R.string.str_dash_updated,
                new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault()).format(new Date())));

        // 6) Biểu đồ
        buildProductChart();
        buildCustomerChart();
        buildStatusChart(revenueByStatus);

        // 7) Spinner danh mục (từ products thực tế)
        buildCategorySpinner();

        // 8) Danh sách
        renderCustomers();
        renderProducts();
    }

    // ---------- Biểu đồ ----------

    private void buildProductChart() {
        List<BarChartView.Bar> bars = new ArrayList<>();
        int i = 0;
        for (ProductRow r : products) {
            if (i >= 5 || r.sold <= 0) break;
            bars.add(new BarChartView.Bar(name(r.p.getProductName(), r.key),
                    r.sold, r.sold + " sp", BAR_COLORS[i % BAR_COLORS.length]));
            i++;
        }
        chartProducts.setBars(bars);
    }

    private void buildCustomerChart() {
        List<BarChartView.Bar> bars = new ArrayList<>();
        int i = 0;
        for (CustomerRow r : customers) {
            if (i >= 5 || r.spend <= 0) break;
            bars.add(new BarChartView.Bar(name(r.c.getFullName(), r.key),
                    r.spend, String.format("%,.0fđ", r.spend), BAR_COLORS[i % BAR_COLORS.length]));
            i++;
        }
        chartCustomers.setBars(bars);
    }

    private void buildStatusChart(Map<String, Double> revenueByStatus) {
        List<BarChartView.Bar> bars = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Double> e : revenueByStatus.entrySet()) {
            bars.add(new BarChartView.Bar(e.getKey(), e.getValue(),
                    String.format("%,.0fđ", e.getValue()), BAR_COLORS[i % BAR_COLORS.length]));
            i++;
        }
        chartStatus.setBars(bars);
    }

    private void buildCategorySpinner() {
        categoryFilter.clear();
        categoryFilter.add(getString(R.string.str_dash_all));
        List<String> seen = new ArrayList<>();
        for (ProductRow r : products) {
            String cat = r.p.getCategoryId();
            if (cat != null && !cat.isEmpty() && !seen.contains(cat)) {
                seen.add(cat);
            }
        }
        seen.sort(Comparator.naturalOrder());
        categoryFilter.addAll(seen);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categoryFilter);
        spCategory.setAdapter(adapter);
    }

    // ---------- Danh sách + lọc ----------

    private void renderCustomers() {
        String q = norm(edtSearchCustomer.getText().toString());
        containerCustomers.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        int shown = 0;
        for (CustomerRow r : customers) {
            Customer c = r.c;
            String hay = norm((c.getFullName() == null ? "" : c.getFullName()) + " "
                    + (c.getPhone() == null ? "" : c.getPhone()) + " "
                    + (c.getEmail() == null ? "" : c.getEmail()));
            if (!q.isEmpty() && !hay.contains(q)) continue;

            View item = inflater.inflate(R.layout.item_dash_customer, containerCustomers, false);
            TextView avatar = item.findViewById(R.id.tvAvatar);
            TextView name = item.findViewById(R.id.tvName);
            TextView sub = item.findViewById(R.id.tvSub);
            TextView spend = item.findViewById(R.id.tvSpend);

            String fullName = name(c.getFullName(), r.key);
            avatar.setText(initial(fullName));
            name.setText(fullName);
            sub.setText((c.getPhone() == null ? "" : c.getPhone())
                    + (c.getEmail() == null || c.getEmail().isEmpty() ? "" : " · " + c.getEmail()));
            spend.setText(r.spend > 0 ? String.format("%,.0fđ", r.spend) : "—");

            item.setOnClickListener(v -> openEdit(CustomerEditActivity.class, r.key));
            containerCustomers.addView(item);
            shown++;
        }
        tvCustomerEmpty.setVisibility(shown == 0 ? View.VISIBLE : View.GONE);
    }

    private void renderProducts() {
        String q = norm(edtSearchProduct.getText().toString());
        String catSel = spCategory.getSelectedItem() == null ? null : spCategory.getSelectedItem().toString();
        boolean catAll = catSel == null || catSel.equals(getString(R.string.str_dash_all));
        int statusPos = spStatus.getSelectedItemPosition(); // 0 all, 1 active, 2 inactive

        containerProducts.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        int shown = 0;
        for (ProductRow r : products) {
            Product p = r.p;
            if (!q.isEmpty() && !norm(name(p.getProductName(), r.key)).contains(q)) continue;
            if (!catAll && !catSel.equals(p.getCategoryId())) continue;
            if (statusPos == 1 && !p.isActive()) continue;
            if (statusPos == 2 && p.isActive()) continue;

            View item = inflater.inflate(R.layout.item_dash_product, containerProducts, false);
            TextView name = item.findViewById(R.id.tvName);
            TextView sub = item.findViewById(R.id.tvSub);
            TextView badge = item.findViewById(R.id.tvBadge);
            TextView price = item.findViewById(R.id.tvPrice);

            name.setText(name(p.getProductName(), r.key));
            sub.setText("SL: " + p.getStock()
                    + (p.getCategoryId() == null || p.getCategoryId().isEmpty() ? "" : " · " + p.getCategoryId())
                    + (r.sold > 0 ? " · đã bán " + r.sold : ""));
            price.setText(String.format("%,.0fđ", p.getPrice()));

            if (p.isActive()) {
                badge.setText(getString(R.string.str_dash_active));
                tintBadge(badge, "#E8F5E9", "#2E7D32");
            } else {
                badge.setText(getString(R.string.str_dash_inactive));
                tintBadge(badge, "#FFEBEE", "#C62828");
            }

            item.setOnClickListener(v -> openEdit(ProductEditActivity.class, r.key));
            containerProducts.addView(item);
            shown++;
        }
        tvProductEmpty.setVisibility(shown == 0 ? View.VISIBLE : View.GONE);
    }

    // ---------- Tiện ích ----------

    private void tintBadge(TextView badge, String bg, String fg) {
        GradientDrawable d = new GradientDrawable();
        d.setCornerRadius(40f);
        d.setColor(Color.parseColor(bg));
        badge.setBackground(d);
        badge.setTextColor(Color.parseColor(fg));
    }

    private void openEdit(Class<?> target, String key) {
        Intent intent = new Intent(this, target);
        intent.putExtra("KEY", key);
        startActivity(intent);
    }

    private String name(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private String initial(String s) {
        return s == null || s.trim().isEmpty() ? "?" : s.trim().substring(0, 1).toUpperCase(Locale.getDefault());
    }

    private String norm(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.getDefault());
    }

    /** TextWatcher rút gọn chỉ dùng afterTextChanged. */
    private static class SimpleWatcher implements TextWatcher {
        private final Runnable onChange;

        SimpleWatcher(Runnable onChange) {
            this.onChange = onChange;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int a, int b, int c) {
        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            onChange.run();
        }
    }
}
