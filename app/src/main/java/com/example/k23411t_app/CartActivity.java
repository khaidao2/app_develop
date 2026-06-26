package com.example.k23411t_app;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbmodels.Customer;
import com.example.fbmodels.Order;
import com.example.fbmodels.OrderDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Bài 15 - Giỏ hàng + Checkout: tạo orders/{id} + orderDetails + trừ tồn kho lên Firebase.
 */
public class CartActivity extends AppCompatActivity {
    ListView lvCart;
    TextView tvCartTotal;
    Spinner spnCustomer;
    EditText edtEmployeeId;
    Button btnCheckout;

    ArrayAdapter<String> cartAdapter;
    final List<String> cartProductIds = new ArrayList<>();

    final List<String> customerIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle(getString(R.string.str_shop_cart));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvCart = findViewById(R.id.lvCart);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        spnCustomer = findViewById(R.id.spnCustomer);
        edtEmployeeId = findViewById(R.id.edtEmployeeId);
        btnCheckout = findViewById(R.id.btnCheckout);

        cartAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvCart.setAdapter(cartAdapter);

        // Chạm 1 dòng để xóa khỏi giỏ
        lvCart.setOnItemClickListener((parent, view, position, id) -> {
            Cart.remove(cartProductIds.get(position));
            refreshCart();
        });

        btnCheckout.setOnClickListener(v -> checkout());

        loadCustomers();
        refreshCart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void refreshCart() {
        cartAdapter.clear();
        cartProductIds.clear();
        for (Cart.Item it : Cart.items()) {
            cartAdapter.add(it.productName + "\n" + it.qty + " x "
                    + String.format("%,.0f", it.price) + "đ = "
                    + String.format("%,.0f", it.subtotal()) + "đ");
            cartProductIds.add(it.productId);
        }
        tvCartTotal.setText(getString(R.string.str_cart_total,
                String.format("%,.0f", Cart.total())));
    }

    private void loadCustomers() {
        FirebaseDatabase.getInstance().getReference("customers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        customerIds.clear();
                        List<String> labels = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Customer c = ds.getValue(Customer.class);
                            customerIds.add(ds.getKey());
                            labels.add(ds.getKey() + " - "
                                    + (c != null ? c.getFullName() : ""));
                        }
                        ArrayAdapter<String> a = new ArrayAdapter<>(CartActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, labels);
                        spnCustomer.setAdapter(a);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("FIREBASE", "cart customers:onCancelled", error.toException());
                    }
                });
    }

    private void checkout() {
        if (Cart.isEmpty()) {
            Toast.makeText(this, R.string.str_cart_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        int pos = spnCustomer.getSelectedItemPosition();
        if (pos < 0 || pos >= customerIds.size()) {
            Toast.makeText(this, R.string.str_cart_need_customer, Toast.LENGTH_SHORT).show();
            return;
        }
        String customerId = customerIds.get(pos);
        String employeeId = edtEmployeeId.getText().toString().trim();
        String orderId = "ORD" + System.currentTimeMillis();
        String orderDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                .format(new Date());
        double total = Cart.total();

        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        // 1) Tạo order
        root.child("orders").child(orderId)
                .setValue(new Order(customerId, employeeId, orderDate, "Pending", total));

        // 2) Tạo orderDetails + trừ tồn kho
        int i = 1;
        for (Cart.Item it : Cart.items()) {
            String odId = orderId + "_" + i;
            root.child("orderDetails").child(odId)
                    .setValue(new OrderDetail(orderId, it.productId, it.qty, it.price));
            long newStock = Math.max(0, it.stock - it.qty);
            root.child("products").child(it.productId).child("stock").setValue(newStock);
            i++;
        }

        Cart.clear();
        Toast.makeText(this, getString(R.string.str_cart_checkout_ok, orderId),
                Toast.LENGTH_LONG).show();
        finish();
    }
}
