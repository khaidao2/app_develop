package com.example.k23411t_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbmodels.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** Read single + Save (Insert/Update) + Delete cho node "products". */
public class ProductEditActivity extends AppCompatActivity {
    EditText edtId, edtProductName, edtPrice, edtStock, edtCategoryId, edtImageUrl;
    CheckBox cbActive;
    DatabaseReference myRef;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_product_edit);

        edtId = findViewById(R.id.edtId);
        edtProductName = findViewById(R.id.edtProductName);
        edtPrice = findViewById(R.id.edtPrice);
        edtStock = findViewById(R.id.edtStock);
        edtCategoryId = findViewById(R.id.edtCategoryId);
        edtImageUrl = findViewById(R.id.edtImageUrl);
        cbActive = findViewById(R.id.cbActive);

        myRef = FirebaseDatabase.getInstance().getReference("products");
        key = getIntent().getStringExtra("KEY");
        if (key != null) {
            edtId.setText(key);
            edtId.setEnabled(false);
            loadDetail();
        }
    }

    private void loadDetail() {
        myRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product p = snapshot.getValue(Product.class);
                if (p != null) {
                    edtProductName.setText(p.getProductName());
                    edtPrice.setText(String.valueOf(p.getPrice()));
                    edtStock.setText(String.valueOf(p.getStock()));
                    edtCategoryId.setText(p.getCategoryId());
                    edtImageUrl.setText(p.getImageUrl());
                    cbActive.setChecked(p.isActive());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FIREBASE", "product detail:onCancelled", error.toException());
            }
        });
    }

    public void onSave(View view) {
        String id = edtId.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, R.string.str_fb_need_id, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double price = parseDouble(edtPrice.getText().toString());
            long stock = parseLong(edtStock.getText().toString());
            Product p = new Product(
                    edtProductName.getText().toString(),
                    price, stock,
                    edtCategoryId.getText().toString(),
                    edtImageUrl.getText().toString(),
                    cbActive.isChecked());
            myRef.child(id).setValue(p);
            finish();
        } catch (Exception ex) {
            Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onDelete(View view) {
        if (key == null) {
            finish();
            return;
        }
        myRef.child(key).removeValue();
        finish();
    }

    private double parseDouble(String s) {
        return TextUtils.isEmpty(s) ? 0 : Double.parseDouble(s);
    }

    private long parseLong(String s) {
        return TextUtils.isEmpty(s) ? 0 : Long.parseLong(s);
    }
}
