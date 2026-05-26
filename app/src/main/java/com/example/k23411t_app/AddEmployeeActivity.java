package com.example.k23411t_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.models.Employee;

public class AddEmployeeActivity extends AppCompatActivity {

    EditText edt_id;
    EditText edt_name;
    EditText edt_phone;
    EditText edt_birthyear;
    AutoCompleteTextView act_birthplace;
    Spinner sp_department_add;
    ImageView img_save;
    ImageView img_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_employee);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();
        addEvents();
    }

    private void addViews() {
        edt_id = findViewById(R.id.edt_id);
        edt_name = findViewById(R.id.edt_name);
        edt_phone = findViewById(R.id.edt_phone);
        edt_birthyear = findViewById(R.id.edt_birthyear);
        act_birthplace = findViewById(R.id.act_birthplace);
        sp_department_add = findViewById(R.id.sp_department_add);
        img_save = findViewById(R.id.img_save);
        img_cancel = findViewById(R.id.img_cancel);

        String[] departments = getIntent().getStringArrayExtra("DEPARTMENTS");
        if (departments != null) {
            ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    departments
            );
            deptAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
            );
            sp_department_add.setAdapter(deptAdapter);
        }
    }

    private void addEvents() {
        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSaveEmployee();
            }
        });

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void processSaveEmployee() {
        String id = edt_id.getText().toString().trim();
        String name = edt_name.getText().toString().trim();
        String phone = edt_phone.getText().toString().trim();
        String birthYearStr = edt_birthyear.getText().toString().trim();
        String birthPlace = act_birthplace.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập ID, Name, Phone", Toast.LENGTH_SHORT).show();
            return;
        }

        int birthYear = 2000;
        if (!birthYearStr.isEmpty()) {
            try {
                birthYear = Integer.parseInt(birthYearStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Năm sinh không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Employee employee = new Employee(id, name, phone, birthYear, birthPlace);

        Intent intent = getIntent();
        employee.processSaveEmployee(intent);
        intent.putExtra("DEPT_POSITION", sp_department_add.getSelectedItemPosition());
        setResult(888, intent);
        finish();
    }
}
