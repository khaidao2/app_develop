package com.example.k23411t_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adapters.EmployeeAdapter;
import com.example.models.Department;
import com.example.models.Employee;

import java.util.ArrayList;

public class EmployeeAdvancedManagementActivity
        extends AppCompatActivity {

    private static final int REQUEST_ADD_EMPLOYEE = 1;

    ListView lv_data;

    ArrayList<Employee> listOfEmployees;

    EmployeeAdapter adapter;
    Spinner sp_department;
    ArrayList<Department> listofDepartment;
    ArrayAdapter<Department> adapterDepartment;
    ImageView img_addemp, img_updateemp, img_delemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_employee_advanced_management
        );

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });

        addViews();

        sampleData();
        addEvents();
    }

    private void addViews() {

        lv_data =
                findViewById(R.id.lv_data);

        listOfEmployees =
                new ArrayList<>();

        adapter =
                new EmployeeAdapter(
                        this,
                        R.layout.item_custom_employee
                );

        lv_data.setAdapter(adapter);
        sp_department = findViewById(R.id.sp_department);

        listofDepartment = new ArrayList<>();

        adapterDepartment = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                listofDepartment
        );

        adapterDepartment.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        sp_department.setAdapter(adapterDepartment);
        img_addemp = findViewById(R.id.img_addemp);
        img_updateemp = findViewById(R.id.img_updateemp);
        img_delemp = findViewById(R.id.img_delemp);
    }

    private void sampleData() {

        Employee e1 = new Employee();
        e1.setId("E01");
        e1.setName("Nguyen Van A");
        e1.setPhone("0981111111");

        Employee e2 = new Employee();
        e2.setId("E02");
        e2.setName("Tran Thi B");
        e2.setPhone("0972222222");

        Employee e3 = new Employee();
        e3.setId("E03");
        e3.setName("Le Van C");
        e3.setPhone("0963333333");

        listOfEmployees.add(e1);
        listOfEmployees.add(e2);
        listOfEmployees.add(e3);

        adapter.addAll(listOfEmployees);

        adapterDepartment.clear();

        Department allDept = new Department();
        allDept.setDepartmentID("All");
        allDept.setDepartmentName("All");
        allDept.addListEmployee(listOfEmployees);
        listofDepartment.add(allDept);

        Department d1 = new Department();
        d1.setDepartmentID("D01");
        d1.setDepartmentName("IT");

        Department d2 = new Department();
        d2.setDepartmentID("D02");
        d2.setDepartmentName("HR");

        Department d3 = new Department();
        d3.setDepartmentID("D03");
        d3.setDepartmentName("Marketing");

        d1.addEmployee(e1);
        d1.addEmployee(e2);

        d2.addEmployee(e3);

        listofDepartment.add(d1);
        listofDepartment.add(d2);
        listofDepartment.add(d3);

        adapterDepartment.notifyDataSetChanged();
    }

    public void addEvents() {
        img_addemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        EmployeeAdvancedManagementActivity.this,
                        AddEmployeeActivity.class
                );
                String[] deptNames = new String[listofDepartment.size()];
                for (int i = 0; i < listofDepartment.size(); i++) {
                    deptNames[i] = listofDepartment.get(i).getDepartmentName();
                }
                intent.putExtra("DEPARTMENTS", deptNames);
                startActivityForResult(intent, REQUEST_ADD_EMPLOYEE);
            }
        });

        sp_department.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        adapter.clear();

                        if (position == 0) {
                            adapter.addAll(listOfEmployees);
                        } else {
                            Department d = listofDepartment.get(position);
                            adapter.addAll(d.getListofEmployee());
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {

                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_EMPLOYEE && resultCode == 888 && data != null) {
            Employee employee = (Employee) data.getSerializableExtra("Employee");
            if (employee != null) {
                listOfEmployees.add(employee);

                int deptPos = data.getIntExtra("DEPT_POSITION", 2);
                if (deptPos == 0) {
                    deptPos = 2;
                }
                Department dns = listofDepartment.get(deptPos);
                dns.addEmployee(employee);

                adapter.clear();
                adapter.addAll(listOfEmployees);
                adapter.notifyDataSetChanged();

                Toast.makeText(this, "Added " + employee.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
