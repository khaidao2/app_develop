package com.example.k23411t_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbmodels.Customer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** Read single + Save (Insert/Update) + Delete cho node "customers". */
public class CustomerEditActivity extends AppCompatActivity {
    EditText edtId, edtFullName, edtEmail, edtPhone, edtAddress;
    DatabaseReference myRef;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_customer_edit);

        edtId = findViewById(R.id.edtId);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);

        myRef = FirebaseDatabase.getInstance().getReference("customers");
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
                Customer c = snapshot.getValue(Customer.class);
                if (c != null) {
                    edtFullName.setText(c.getFullName());
                    edtEmail.setText(c.getEmail());
                    edtPhone.setText(c.getPhone());
                    edtAddress.setText(c.getAddress());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FIREBASE", "customer detail:onCancelled", error.toException());
            }
        });
    }

    public void onSave(View view) {
        String id = edtId.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, R.string.str_fb_need_id, Toast.LENGTH_SHORT).show();
            return;
        }
        Customer c = new Customer(
                edtFullName.getText().toString(),
                edtEmail.getText().toString(),
                edtPhone.getText().toString(),
                edtAddress.getText().toString());
        myRef.child(id).setValue(c);
        finish();
    }

    public void onDelete(View view) {
        if (key == null) {
            finish();
            return;
        }
        myRef.child(key).removeValue();
        finish();
    }
}
