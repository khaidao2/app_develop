package com.example.models;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.example.k23411t_app.R;

import java.io.Serializable;

public class Employee implements Serializable {
    private ImageView img_save, img_cancel;
    private String id;
    private String name;
    private String phone;
    private int birthYear;
    private String birthPlace;

    public Employee() {
    }

    public Employee(String id,
                    String name,
                    String phone,
                    int birthYear,
                    String birthPlace) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.birthYear = birthYear;
        this.birthPlace = birthPlace;
    }

    public Employee(String id, String name, String phone) {
        this(id, name, phone, 2000, "Ho Chi Minh");
    }

    public void addViews(View rootView) {
        img_save = rootView.findViewById(R.id.img_save);
        img_cancel = rootView.findViewById(R.id.img_cancel);
    }

    public void addEvents() {
        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEmployee();
            }
        });

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEmployee();
            }
        });
    }

    private void saveEmployee() {
        // TODO: handle save via addEvents flow
    }

    private void cancelEmployee() {
        // TODO: handle cancel action
    }

    public void processSaveEmployee(Intent intent) {
        if (id == null || id.isEmpty() ||
                name == null || name.isEmpty() ||
                phone == null || phone.isEmpty()) {
            return;
        }
        intent.putExtra("Employee", this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }
}
