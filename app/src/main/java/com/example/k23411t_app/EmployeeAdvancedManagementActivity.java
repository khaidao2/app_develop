package com.example.k23411t_app;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adapters.EmployeeAdapter;
import com.example.models.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAdvancedManagementActivity
        extends AppCompatActivity {

    ListView lv_data;

    ArrayList<Employee> listOfEmployees;

    EmployeeAdapter adapter;

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
    }
}