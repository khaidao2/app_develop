package com.example.k23411t_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbmodels.Employee;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** Read single + Save (Insert/Update) + Delete cho node "employees". */
public class EmployeeEditActivity extends AppCompatActivity {
    EditText edtId, edtFullName, edtEmail, edtDepartment, edtPosition;
    DatabaseReference myRef;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_employee_edit);

        edtId = findViewById(R.id.edtId);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtDepartment = findViewById(R.id.edtDepartment);
        edtPosition = findViewById(R.id.edtPosition);

        myRef = FirebaseDatabase.getInstance().getReference("employees");
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
                Employee e = snapshot.getValue(Employee.class);
                if (e != null) {
                    edtFullName.setText(e.getFullName());
                    edtEmail.setText(e.getEmail());
                    edtDepartment.setText(e.getDepartment());
                    edtPosition.setText(e.getPosition());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FIREBASE", "employee detail:onCancelled", error.toException());
            }
        });
    }

    public void onSave(View view) {
        String id = edtId.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, R.string.str_fb_need_id, Toast.LENGTH_SHORT).show();
            return;
        }
        Employee e = new Employee(
                edtFullName.getText().toString(),
                edtEmail.getText().toString(),
                edtDepartment.getText().toString(),
                edtPosition.getText().toString());
        myRef.child(id).setValue(e);
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
