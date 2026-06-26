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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** Read List + điều hướng sang Edit cho node "categories". */
public class CategoryListActivity extends AppCompatActivity {
    ListView lvList;
    ArrayAdapter<String> adapter;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_list);
        setTitle(getString(R.string.str_fb_categories));

        lvList = findViewById(R.id.lvList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvList.setAdapter(adapter);

        findViewById(R.id.btnAdd).setOnClickListener(v ->
                startActivity(new Intent(this, CategoryEditActivity.class)));

        lvList.setOnItemClickListener((parent, view, position, id) -> {
            String key = adapter.getItem(position).split("\n")[0];
            Intent intent = new Intent(this, CategoryEditActivity.class);
            intent.putExtra("KEY", key);
            startActivity(intent);
        });

        myRef = FirebaseDatabase.getInstance().getReference("categories");
        loadData();
    }

    private void loadData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Category c = data.getValue(Category.class);
                    adapter.add(data.getKey() + "\n" + (c != null ? c.summary() : ""));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FIREBASE", "categories:onCancelled", error.toException());
            }
        });
    }
}
