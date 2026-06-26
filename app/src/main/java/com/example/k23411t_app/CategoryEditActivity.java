package com.example.k23411t_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbmodels.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** Read single + Save (Insert/Update) + Delete cho node "categories". */
public class CategoryEditActivity extends AppCompatActivity {
    EditText edtId, edtCategoryName, edtDescription;
    DatabaseReference myRef;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_category_edit);

        edtId = findViewById(R.id.edtId);
        edtCategoryName = findViewById(R.id.edtCategoryName);
        edtDescription = findViewById(R.id.edtDescription);

        myRef = FirebaseDatabase.getInstance().getReference("categories");
        key = getIntent().getStringExtra("KEY");
        if (key != null) {
            edtId.setText(key);
            edtId.setEnabled(false); // không cho đổi ID khi sửa
            loadDetail();
        }
    }

    private void loadDetail() {
        myRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Category c = snapshot.getValue(Category.class);
                if (c != null) {
                    edtCategoryName.setText(c.getCategoryName());
                    edtDescription.setText(c.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FIREBASE", "category detail:onCancelled", error.toException());
            }
        });
    }

    public void onSave(View view) {
        String id = edtId.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, R.string.str_fb_need_id, Toast.LENGTH_SHORT).show();
            return;
        }
        Category c = new Category(
                edtCategoryName.getText().toString(),
                edtDescription.getText().toString());
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
