package com.example.k23411t_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbmodels.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Client xem danh mục (read-only). Chạm 1 danh mục => mở Shop lọc theo category đó.
 */
public class CategoriesViewActivity extends AppCompatActivity {
    ListView lvList;
    ArrayAdapter<String> adapter;
    final List<String> keys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_only);
        setTitle(getString(R.string.str_shop_categories));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvList = findViewById(R.id.lvList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvList.setAdapter(adapter);

        lvList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ShopActivity.class);
            intent.putExtra("categoryId", keys.get(position));
            startActivity(intent);
        });

        FirebaseDatabase.getInstance().getReference("categories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        keys.clear();
                        adapter.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Category c = ds.getValue(Category.class);
                            keys.add(ds.getKey());
                            adapter.add(ds.getKey() + " - "
                                    + (c != null ? c.getCategoryName() : "")
                                    + "\n" + (c != null ? c.getDescription() : ""));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("FIREBASE", "categories view:onCancelled", error.toException());
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
