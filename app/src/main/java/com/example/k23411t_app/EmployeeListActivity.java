package com.example.k23411t_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbmodels.Employee;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** Read List + điều hướng sang Edit cho node "employees". */
public class EmployeeListActivity extends AppCompatActivity {
    ListView lvList;
    ArrayAdapter<String> adapter;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_list);
        setTitle(getString(R.string.str_fb_employees));

        lvList = findViewById(R.id.lvList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvList.setAdapter(adapter);

        findViewById(R.id.btnAdd).setOnClickListener(v ->
                startActivity(new Intent(this, EmployeeEditActivity.class)));

        lvList.setOnItemClickListener((parent, view, position, id) -> {
            String key = adapter.getItem(position).split("\n")[0];
            Intent intent = new Intent(this, EmployeeEditActivity.class);
            intent.putExtra("KEY", key);
            startActivity(intent);
        });

        myRef = FirebaseDatabase.getInstance().getReference("employees");
        loadData();
    }

    private void loadData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Employee e = data.getValue(Employee.class);
                    adapter.add(data.getKey() + "\n" + (e != null ? e.summary() : ""));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FIREBASE", "employees:onCancelled", error.toException());
            }
        });
    }
}
