package com.example.k23411t_app;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

public class EmployeeManagementActivity
        extends AppCompatActivity {

    EditText edt_id, edt_name, edt_phone;

    Button btn_save,
            btn_clear,
            btn_delete,
            btn_exit;

    ListView lv_data;

    ArrayAdapter<String> adapter;

    ArrayList<String> employeeList;

    SharedPreferences preferences;

    String name_share_ref;

    int selectedPosition = -1;

    private void addViews() {

        edt_id = findViewById(R.id.edt_id);

        edt_name = findViewById(R.id.edt_name);

        edt_phone = findViewById(R.id.edt_phone);

        btn_save = findViewById(R.id.btn_save);

        btn_clear = findViewById(R.id.btn_clear);

        btn_delete = findViewById(R.id.btn_delete);

        btn_exit = findViewById(R.id.btn_exit);

        lv_data = findViewById(R.id.lv_data);

        employeeList = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_activated_1,
                employeeList
        );

        lv_data.setAdapter(adapter);

        lv_data.setChoiceMode(
                ListView.CHOICE_MODE_SINGLE
        );
    }

    private void sampleData() {

        Random random = new Random();

        for (int i = 1; i <= 10; i++) {

            String id = "E" + i;

            String name = "Name " + i;

            String phone = "09";

            int provider = random.nextInt(3);

            if (provider == 0)
                phone += "8";
            else if (provider == 1)
                phone += "7";
            else
                phone += "6";

            for (int p = 1; p <= 7; p++) {

                phone += random.nextInt(10);
            }

            String employee =
                    id + ";" +
                            name + ";" +
                            phone;

            employeeList.add(employee);
        }

        adapter.notifyDataSetChanged();
    }

    private void addEvents() {

        lv_data.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        selectedPosition = position;

                        String employee =
                                employeeList.get(position);

                        String[] arr =
                                employee.split(";");

                        edt_id.setText(arr[0]);

                        edt_name.setText(arr[1]);

                        edt_phone.setText(arr[2]);

                        saveSelectedEmployee();
                    }
                });

        btn_clear.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        edt_id.setText("");

                        edt_name.setText("");

                        edt_phone.setText("");

                        edt_id.requestFocus();

                        lv_data.clearChoices();

                        adapter.notifyDataSetChanged();

                        selectedPosition = -1;
                    }
                });

        btn_save.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        saveEmployee();
                    }
                });

        btn_exit.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        closeActivity(v);
                    }
                });
    }

    private void saveEmployee() {

        String id =
                edt_id.getText()
                        .toString()
                        .trim();

        String name =
                edt_name.getText()
                        .toString()
                        .trim();

        String phone =
                edt_phone.getText()
                        .toString()
                        .trim();

        if (id.isEmpty() ||
                name.isEmpty() ||
                phone.isEmpty()) {

            return;
        }

        String employee =
                id + ";" +
                        name + ";" +
                        phone;

        int existingIndex = -1;

        for (int i = 0; i < employeeList.size(); i++) {

            String existing = employeeList.get(i);

            String[] arr = existing.split(";");

            if (arr[0].equals(id)) {

                existingIndex = i;

                break;
            }
        }

        if (existingIndex != -1) {

            employeeList.set(
                    existingIndex,
                    employee
            );
        }
        else {

            employeeList.add(employee);
        }

        adapter.notifyDataSetChanged();

        clearInput();
    }

    private void clearInput() {

        edt_id.setText("");

        edt_name.setText("");

        edt_phone.setText("");

        edt_id.requestFocus();

        selectedPosition = -1;

        lv_data.clearChoices();
    }

    private void saveSelectedEmployee() {

        SharedPreferences.Editor editor =
                preferences.edit();

        editor.putInt(
                "POSITION",
                selectedPosition);

        editor.putString(
                "EMP_ID",
                edt_id.getText().toString());

        editor.putString(
                "EMP_NAME",
                edt_name.getText().toString());

        editor.putString(
                "EMP_PHONE",
                edt_phone.getText().toString());

        editor.apply();
    }

    private void restoreSelectedEmployee() {

        selectedPosition =
                preferences.getInt(
                        "POSITION",
                        -1);

        String id =
                preferences.getString(
                        "EMP_ID",
                        "");

        String name =
                preferences.getString(
                        "EMP_NAME",
                        "");

        String phone =
                preferences.getString(
                        "EMP_PHONE",
                        "");

        if (selectedPosition != -1 &&
                selectedPosition < employeeList.size()) {

            edt_id.setText(id);

            edt_name.setText(name);

            edt_phone.setText(phone);

            lv_data.setItemChecked(
                    selectedPosition,
                    true);

            lv_data.smoothScrollToPosition(
                    selectedPosition);
        }
    }

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_employee_management
        );

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat
                                            .Type
                                            .systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });

        addViews();

        name_share_ref = "EMPLOYEE_INFO";

        preferences =
                getSharedPreferences(
                        name_share_ref,
                        MODE_PRIVATE);

        sampleData();

        addEvents();

        restoreSelectedEmployee();
    }

    public void closeActivity(View view) {

        Dialog dialog = new Dialog(this);

        dialog.setContentView(
                R.layout.custom_dialog);

        dialog.setCanceledOnTouchOutside(
                false);

        ImageView img_yes =
                dialog.findViewById(R.id.ic_yes);

        ImageView img_no =
                dialog.findViewById(R.id.ic_no);

        img_yes.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        finish();
                    }
                });

        img_no.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

        dialog.show();
    }

    public void deleteEmployee(View v) {

        if (selectedPosition == -1) {

            String id = edt_id.getText()
                    .toString()
                    .trim();

            if (id.isEmpty()) {

                Toast.makeText(
                        this,
                        R.string.str_select_employee_to_delete,
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }
        }

        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(
                        this
                );

        builder.setTitle(
                R.string.str_delete_confirm_title
        );

        builder.setMessage(
                R.string.str_delete_confirm_message
        );

        builder.setPositiveButton(
                R.string.str_yes,
                (dialog, which) -> {

                    if (selectedPosition != -1) {

                        employeeList.remove(
                                selectedPosition
                        );
                    }

                    adapter.notifyDataSetChanged();

                    edt_id.setText("");

                    edt_name.setText("");

                    edt_phone.setText("");

                    selectedPosition = -1;

                    lv_data.clearChoices();
                }
        );

        builder.setNegativeButton(
                R.string.str_no,
                (dialog, which) -> {

                    dialog.dismiss();
                }
        );

        builder.show();
    }
}